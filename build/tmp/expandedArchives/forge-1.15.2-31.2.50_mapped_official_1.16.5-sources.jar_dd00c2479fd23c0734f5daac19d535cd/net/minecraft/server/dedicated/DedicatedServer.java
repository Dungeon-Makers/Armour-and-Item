package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.MainThread;
import net.minecraft.network.rcon.QueryThread;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   public final List<PendingCommand> consoleInput = Collections.synchronizedList(Lists.newArrayList());
   private QueryThread queryThreadGs4;
   private final RConConsoleSource rconConsoleSource;
   private MainThread rconThread;
   private final ServerPropertiesProvider settings;
   private GameType field_71337_q;
   @Nullable
   private MinecraftServerGui gui;

   public DedicatedServer(File p_i50720_1_, ServerPropertiesProvider p_i50720_2_, DataFixer p_i50720_3_, YggdrasilAuthenticationService p_i50720_4_, MinecraftSessionService p_i50720_5_, GameProfileRepository p_i50720_6_, PlayerProfileCache p_i50720_7_, IChunkStatusListenerFactory p_i50720_8_, String p_i50720_9_) {
      super(p_i50720_1_, Proxy.NO_PROXY, p_i50720_3_, new Commands(true), p_i50720_4_, p_i50720_5_, p_i50720_6_, p_i50720_7_, p_i50720_8_, p_i50720_9_);
      this.settings = p_i50720_2_;
      this.rconConsoleSource = new RConConsoleSource(this);
      Thread thread = new Thread("Server Infinisleeper") {
         {
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
            this.start();
         }

         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
                  ;
               }
            }
         }
      };
   }

   public boolean initServer() throws IOException {
      Thread thread = new Thread("Server console handler") {
         public void run() {
            if (net.minecraftforge.server.console.TerminalHandler.handleCommands(DedicatedServer.this)) return;
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            String s3;
            try {
               while(!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (s3 = bufferedreader.readLine()) != null) {
                  DedicatedServer.this.handleConsoleInput(s3, DedicatedServer.this.createCommandSourceStack());
               }
            } catch (IOException ioexception1) {
               DedicatedServer.LOGGER.error("Exception handling console input", (Throwable)ioexception1);
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
      LOGGER.info("Starting minecraft server version " + SharedConstants.getCurrentVersion().getName());
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      net.minecraftforge.fml.server.ServerModLoader.begin(this);
      LOGGER.info("Loading properties");
      this.settings.update(properties -> ServerProperties.func_218985_a(java.nio.file.Paths.get("server.properties")));
      ServerProperties serverproperties = this.settings.getProperties();
      if (this.isSingleplayer()) {
         this.setLocalIp("127.0.0.1");
      } else {
         this.setUsesAuthentication(serverproperties.onlineMode);
         this.setPreventProxyConnections(serverproperties.preventProxyConnections);
         this.setLocalIp(serverproperties.serverIp);
      }

      this.func_71251_e(serverproperties.spawnAnimals);
      this.func_71257_f(serverproperties.spawnNpcs);
      this.setPvpAllowed(serverproperties.pvp);
      this.setFlightAllowed(serverproperties.allowFlight);
      this.setResourcePack(serverproperties.resourcePack, this.getPackHash());
      this.setMotd(serverproperties.motd);
      this.setForceGameType(serverproperties.forceGameMode);
      super.setPlayerIdleTimeout(serverproperties.playerIdleTimeout.get());
      this.setEnforceWhitelist(serverproperties.enforceWhitelist);
      this.field_71337_q = serverproperties.gamemode;
      LOGGER.info("Default game type: {}", (Object)this.field_71337_q);
      InetAddress inetaddress = null;
      if (!this.getLocalIp().isEmpty()) {
         inetaddress = InetAddress.getByName(this.getLocalIp());
      }

      if (this.getPort() < 0) {
         this.setPort(serverproperties.serverPort);
      }

      LOGGER.info("Generating keypair");
      this.func_71253_a(CryptManager.generateKeyPair());
      LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

      try {
         this.getConnection().startTcpServerListener(inetaddress, this.getPort());
      } catch (IOException ioexception) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: {}", (Object)ioexception.toString());
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.usesAuthentication()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.getProfileCache().save();
      }

      if (!PreYggdrasilConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         net.minecraftforge.fml.server.ServerModLoader.end();
         this.setPlayerList(new DedicatedPlayerList(this));
         long i = Util.getNanos();
         String s = serverproperties.field_219022_p;
         String s1 = serverproperties.field_219024_r;
         long j = (new Random()).nextLong();
         if (!s.isEmpty()) {
            try {
               long k = Long.parseLong(s);
               if (k != 0L) {
                  j = k;
               }
            } catch (NumberFormatException var16) {
               j = (long)s.hashCode();
            }
         }

         WorldType worldtype = serverproperties.field_219023_q;
         this.setMaxBuildHeight(serverproperties.maxBuildHeight);
         SkullTileEntity.setProfileCache(this.getProfileCache());
         SkullTileEntity.setSessionService(this.getSessionService());
         PlayerProfileCache.setUsesAuthentication(this.usesAuthentication());
         if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
         LOGGER.info("Preparing level \"{}\"", (Object)this.func_71270_I());
         JsonObject jsonobject = new JsonObject();
         if (worldtype == WorldType.field_77138_c) {
            jsonobject.addProperty("flat_world_options", s1);
         } else if (!s1.isEmpty()) {
            jsonobject = JSONUtils.parse(s1);
         }

         this.func_71247_a(this.func_71270_I(), this.func_71270_I(), j, worldtype, jsonobject);
         long l = Util.getNanos() - i;
         String s2 = String.format(Locale.ROOT, "%.3fs", (double)l / 1.0E9D);
         LOGGER.info("Done ({})! For help, type \"help\"", (Object)s2);
         this.nextTickTime = Util.getMillis(); //Forge: Update server time to prevent watchdog/spaming during long load.
         if (serverproperties.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(serverproperties.announcePlayerAchievements, this);
         }

         if (serverproperties.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = new QueryThread(this);
            this.queryThreadGs4.func_72602_a();
         }

         if (serverproperties.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = new MainThread(this);
            this.rconThread.func_72602_a();
         }

         if (this.getMaxTickLength() > 0L) {
            Thread thread1 = new Thread(new ServerHangWatchdog(this));
            thread1.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
            thread1.setName("Server Watchdog");
            thread1.setDaemon(true);
            thread1.start();
         }

         Items.AIR.fillItemCategory(ItemGroup.TAB_SEARCH, NonNullList.create());
         // <3 you Grum for this, saves us ~30 patch files! --^
         return net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarting(this);
      }
   }

   public String getPackHash() {
      ServerProperties serverproperties = this.settings.getProperties();
      String s;
      if (!serverproperties.resourcePackSha1.isEmpty()) {
         s = serverproperties.resourcePackSha1;
         if (!Strings.isNullOrEmpty(serverproperties.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
         }
      } else if (!Strings.isNullOrEmpty(serverproperties.resourcePackHash)) {
         LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
         s = serverproperties.resourcePackHash;
      } else {
         s = "";
      }

      if (!s.isEmpty() && !SHA1.matcher(s).matches()) {
         LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
      }

      if (!serverproperties.resourcePack.isEmpty() && s.isEmpty()) {
         LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
      }

      return s;
   }

   public void setDefaultGameType(GameType p_71235_1_) {
      super.setDefaultGameType(p_71235_1_);
      this.field_71337_q = p_71235_1_;
   }

   public ServerProperties getProperties() {
      return this.settings.getProperties();
   }

   public boolean func_71225_e() {
      return this.getProperties().field_219018_l;
   }

   public GameType getDefaultGameType() {
      return this.field_71337_q;
   }

   public Difficulty func_147135_j() {
      return this.getProperties().difficulty;
   }

   public boolean isHardcore() {
      return this.getProperties().hardcore;
   }

   public CrashReport fillReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.fillReport(p_71230_1_);
      p_71230_1_.getSystemDetails().setDetail("Is Modded", () -> {
         return this.getModdedStatus().orElse("Unknown (can't tell)");
      });
      p_71230_1_.getSystemDetails().setDetail("Type", () -> {
         return "Dedicated Server (map_server.txt)";
      });
      return p_71230_1_;
   }

   public Optional<String> getModdedStatus() {
      String s = this.getServerModName();
      return !"vanilla".equals(s) ? Optional.of("Definitely; Server brand changed to '" + s + "'") : Optional.empty();
   }

   public void onServerExit() {
      if (this.gui != null) {
         this.gui.close();
      }

      if (this.rconThread != null) {
         this.rconThread.stop();
      }

      if (this.queryThreadGs4 != null) {
         this.queryThreadGs4.stop();
      }

   }

   public void tickChildren(BooleanSupplier p_71190_1_) {
      super.tickChildren(p_71190_1_);
      this.handleConsoleInputs();
   }

   public boolean isNetherEnabled() {
      return this.getProperties().allowNether;
   }

   public boolean func_71193_K() {
      return this.getProperties().spawnMonsters;
   }

   public void populateSnooper(Snooper p_70000_1_) {
      p_70000_1_.setDynamicData("whitelist_enabled", this.getPlayerList().isUsingWhitelist());
      p_70000_1_.setDynamicData("whitelist_count", this.getPlayerList().getWhiteListNames().length);
      super.populateSnooper(p_70000_1_);
   }

   public void handleConsoleInput(String p_195581_1_, CommandSource p_195581_2_) {
      this.consoleInput.add(new PendingCommand(p_195581_1_, p_195581_2_));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         PendingCommand pendingcommand = this.consoleInput.remove(0);
         this.getCommands().performCommand(pendingcommand.source, pendingcommand.msg);
      }

   }

   public boolean isDedicatedServer() {
      return true;
   }

   public boolean isEpollEnabled() {
      return this.getProperties().useNativeTransport;
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   public boolean isPublished() {
      return true;
   }

   public String getServerIp() {
      return this.getLocalIp();
   }

   public int getServerPort() {
      return this.getPort();
   }

   public String getServerName() {
      return this.getMotd();
   }

   public void showGui() {
      if (this.gui == null) {
         this.gui = MinecraftServerGui.showFrameFor(this);
      }

   }

   public boolean hasGui() {
      return this.gui != null;
   }

   public boolean publishServer(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      return false;
   }

   public boolean isCommandBlockEnabled() {
      return this.getProperties().enableCommandBlock;
   }

   public int getSpawnProtectionRadius() {
      return this.getProperties().spawnProtection;
   }

   public boolean isUnderSpawnProtection(World p_175579_1_, BlockPos p_175579_2_, PlayerEntity p_175579_3_) {
      if (p_175579_1_.dimension.func_186058_p() != DimensionType.field_223227_a_) {
         return false;
      } else if (this.getPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerList().isOp(p_175579_3_.getGameProfile())) {
         return false;
      } else if (this.getSpawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos blockpos = p_175579_1_.func_175694_M();
         int i = MathHelper.abs(p_175579_2_.getX() - blockpos.getX());
         int j = MathHelper.abs(p_175579_2_.getZ() - blockpos.getZ());
         int k = Math.max(i, j);
         return k <= this.getSpawnProtectionRadius();
      }
   }

   public int getOperatorUserPermissionLevel() {
      return this.getProperties().opPermissionLevel;
   }

   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      super.setPlayerIdleTimeout(p_143006_1_);
      this.settings.update((p_213224_1_) -> {
         return p_213224_1_.playerIdleTimeout.func_219038_a(p_143006_1_);
      });
   }

   public boolean shouldRconBroadcast() {
      return this.getProperties().broadcastRconToOps;
   }

   public boolean shouldInformAdmins() {
      return this.getProperties().broadcastConsoleToOps;
   }

   public int getAbsoluteMaxWorldSize() {
      return this.getProperties().maxWorldSize;
   }

   public int getCompressionThreshold() {
      return this.getProperties().networkCompressionThreshold;
   }

   protected boolean convertOldUsers() {
      boolean flag = false;

      for(int i = 0; !flag && i <= 2; ++i) {
         if (i > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag = PreYggdrasilConverter.convertUserBanlist(this);
      }

      boolean flag1 = false;

      for(int j = 0; !flag1 && j <= 2; ++j) {
         if (j > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag1 = PreYggdrasilConverter.convertIpBanlist(this);
      }

      boolean flag2 = false;

      for(int k = 0; !flag2 && k <= 2; ++k) {
         if (k > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.waitForRetry();
         }

         flag2 = PreYggdrasilConverter.convertOpsList(this);
      }

      boolean flag3 = false;

      for(int l = 0; !flag3 && l <= 2; ++l) {
         if (l > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.waitForRetry();
         }

         flag3 = PreYggdrasilConverter.convertWhiteList(this);
      }

      boolean flag4 = false;

      for(int i1 = 0; !flag4 && i1 <= 2; ++i1) {
         if (i1 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.waitForRetry();
         }

         flag4 = PreYggdrasilConverter.convertPlayers(this);
      }

      return flag || flag1 || flag2 || flag3 || flag4;
   }

   private void waitForRetry() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
         ;
      }
   }

   public long getMaxTickLength() {
      return this.getProperties().maxTickTime;
   }

   public String getPluginNames() {
      return "";
   }

   public String runCommand(String p_71252_1_) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> {
         this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), p_71252_1_);
      });
      return this.rconConsoleSource.getCommandResponse();
   }

   public void storeUsingWhiteList(boolean p_213223_1_) {
      this.settings.update((p_213222_1_) -> {
         return p_213222_1_.whiteList.func_219038_a(p_213223_1_);
      });
   }

   public void stopServer() {
      super.stopServer();
      Util.func_215082_f();
   }

   public boolean isSingleplayerOwner(GameProfile p_213199_1_) {
      return false;
   }

   @Override //Forge: Enable formated text for colors in console.
   public void sendMessage(net.minecraft.util.text.ITextComponent message) {
      LOGGER.info(message.func_150254_d());
   }
}
