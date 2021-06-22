package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.CryptManager;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final WorldSettings field_71350_m;
   private boolean paused;
   private int publishedPort = -1;
   private LanServerPingThread lanPinger;
   private UUID uuid;

   public IntegratedServer(Minecraft p_i50895_1_, String p_i50895_2_, String p_i50895_3_, WorldSettings p_i50895_4_, YggdrasilAuthenticationService p_i50895_5_, MinecraftSessionService p_i50895_6_, GameProfileRepository p_i50895_7_, PlayerProfileCache p_i50895_8_, IChunkStatusListenerFactory p_i50895_9_) {
      super(new File(p_i50895_1_.gameDirectory, "saves"), p_i50895_1_.getProxy(), p_i50895_1_.getFixerUpper(), new Commands(false), p_i50895_5_, p_i50895_6_, p_i50895_7_, p_i50895_8_, p_i50895_9_, p_i50895_2_);
      this.setSingleplayerName(p_i50895_1_.getUser().getName());
      this.func_71246_n(p_i50895_3_);
      this.setDemo(p_i50895_1_.isDemo());
      this.func_71194_c(p_i50895_4_.func_77167_c());
      this.setMaxBuildHeight(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.minecraft = p_i50895_1_;
      this.field_71350_m = this.isDemo() ? MinecraftServer.DEMO_SETTINGS : p_i50895_4_;
   }

   public void func_71247_a(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.func_71237_c(p_71247_1_);
      SaveHandler savehandler = this.func_71254_M().func_197715_a(p_71247_1_, this);
      this.detectBundledResources(this.func_71270_I(), savehandler);
      // Move factory creation earlier to prevent startupquery deadlock
      IChunkStatusListener ichunkstatuslistener = this.progressListenerFactory.create(11);
      WorldInfo worldinfo = savehandler.func_75757_d();
      if (worldinfo == null) {
         worldinfo = new WorldInfo(this.field_71350_m, p_71247_2_);
      } else {
         worldinfo.func_76062_a(p_71247_2_);
      }

      worldinfo.func_230145_a_(this.getServerModName(), this.getModdedStatus().isPresent());
      this.func_195560_a(savehandler.func_75765_b(), worldinfo);
      this.func_213194_a(savehandler, worldinfo, this.field_71350_m, ichunkstatuslistener);
      if (this.getLevel(DimensionType.field_223227_a_).getLevelData().getDifficulty() == null) {
         this.setDifficulty(this.minecraft.options.difficulty, true);
      }

      this.prepareLevels(ichunkstatuslistener);
   }

   public boolean initServer() throws IOException {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.func_71251_e(true);
      this.func_71257_f(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      LOGGER.info("Generating keypair");
      this.func_71253_a(CryptManager.generateKeyPair());
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
      this.func_71247_a(this.func_71270_I(), this.func_71221_J(), this.field_71350_m.func_77160_d(), this.field_71350_m.func_77165_h(), this.field_71350_m.func_205391_j());
      this.setMotd(this.getSingleplayerName() + " - " + this.getLevel(DimensionType.field_223227_a_).getLevelData().getLevelName());
      return net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarting(this);
   }

   public void tickServer(BooleanSupplier p_71217_1_) {
      boolean flag = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      DebugProfiler debugprofiler = this.getProfiler();
      if (!flag && this.paused) {
         debugprofiler.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         debugprofiler.pop();
      }

      if (!this.paused) {
         super.tickServer(p_71217_1_);
         int i = Math.max(2, this.minecraft.options.renderDistance + -1);
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }

      }
   }

   public boolean func_71225_e() {
      return false;
   }

   public GameType getDefaultGameType() {
      return this.field_71350_m.func_77162_e();
   }

   public Difficulty func_147135_j() {
      if (this.minecraft.level == null) return this.minecraft.options.difficulty; // Fix NPE just in case.
      return this.minecraft.level.getLevelData().getDifficulty();
   }

   public boolean isHardcore() {
      return this.field_71350_m.func_77158_f();
   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public void onServerCrash(CrashReport p_71228_1_) {
      this.minecraft.delayCrash(p_71228_1_);
   }

   public CrashReport fillReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.fillReport(p_71230_1_);
      p_71230_1_.getSystemDetails().setDetail("Type", "Integrated Server (map_client.txt)");
      p_71230_1_.getSystemDetails().setDetail("Is Modded", () -> {
         return this.getModdedStatus().orElse("Probably not. Jar signature remains and both client + server brands are untouched.");
      });
      return p_71230_1_;
   }

   public Optional<String> getModdedStatus() {
      String s = ClientBrandRetriever.getClientModName();
      if (!s.equals("vanilla")) {
         return Optional.of("Definitely; Client brand changed to '" + s + "'");
      } else {
         s = this.getServerModName();
         if (!"vanilla".equals(s)) {
            return Optional.of("Definitely; Server brand changed to '" + s + "'");
         } else {
            return Minecraft.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
         }
      }
   }

   public void populateSnooper(Snooper p_70000_1_) {
      super.populateSnooper(p_70000_1_);
      p_70000_1_.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean publishServer(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, p_195565_3_);
         LOGGER.info("Started serving on {}", (int)p_195565_3_);
         this.publishedPort = p_195565_3_;
         this.lanPinger = new LanServerPingThread(this.getMotd(), p_195565_3_ + "");
         this.lanPinger.start();
         this.getPlayerList().setOverrideGameMode(p_195565_1_);
         this.getPlayerList().setAllowCheatsForAllPlayers(p_195565_2_);
         int i = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(i);

         for(ServerPlayerEntity serverplayerentity : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(serverplayerentity);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean p_71263_1_) {
      if (isRunning())
      this.executeBlocking(() -> {
         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!serverplayerentity.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(serverplayerentity);
            }
         }

      });
      super.halt(p_71263_1_);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameType(GameType p_71235_1_) {
      super.setDefaultGameType(p_71235_1_);
      this.getPlayerList().setOverrideGameMode(p_71235_1_);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID p_211527_1_) {
      this.uuid = p_211527_1_;
   }

   public boolean isSingleplayerOwner(GameProfile p_213199_1_) {
      return p_213199_1_.getName().equalsIgnoreCase(this.getSingleplayerName());
   }
}
