package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.WhiteList;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.test.TestCollection;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerMultiWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.CommandStorage;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USERID_CACHE_FILE = new File("usercache.json");
   private static final CompletableFuture<Unit> field_223713_i = CompletableFuture.completedFuture(Unit.INSTANCE);
   public static final WorldSettings DEMO_SETTINGS = (new WorldSettings((long)"North Carolina".hashCode(), GameType.SURVIVAL, true, false, WorldType.field_77137_b)).func_77159_a();
   private final SaveFormat storageSource;
   private final Snooper snooper = new Snooper("server", this, Util.getMillis());
   private final File field_71308_o;
   private final List<Runnable> tickables = Lists.newArrayList();
   private final DebugProfiler profiler = new DebugProfiler(this::getTickCount);
   private final NetworkSystem connection;
   protected final IChunkStatusListenerFactory progressListenerFactory;
   private final ServerStatusResponse status = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer fixerUpper;
   private String localIp;
   private int port = -1;
   private final Map<DimensionType, ServerWorld> levels = Maps.newIdentityHashMap();
   private PlayerList playerList;
   private volatile boolean running = true;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean field_71324_y;
   private boolean field_71323_z;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int maxBuildHeight;
   private int playerIdleTimeout;
   public final long[] tickTimes = new long[100];
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private String singleplayerName;
   private final String field_71294_K;
   @Nullable
   @OnlyIn(Dist.CLIENT)
   private String field_71287_L;
   private boolean isDemo;
   private boolean field_71289_N;
   private String resourcePack = "";
   private String resourcePackHash = "";
   private volatile boolean isReady;
   private long lastOverloadWarning;
   @Nullable
   private ITextComponent field_71298_S;
   private boolean delayProfilerStart;
   private boolean forceGameType;
   @Nullable
   private final YggdrasilAuthenticationService field_152364_T;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepository;
   private final PlayerProfileCache profileCache;
   private long lastServerStatus;
   protected final Thread serverThread = Util.make(new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, this, "Server thread"), (p_213187_0_) -> {
      p_213187_0_.setUncaughtExceptionHandler((p_213206_0_, p_213206_1_) -> {
         LOGGER.error(p_213206_1_);
      });
   });
   protected long nextTickTime = Util.getMillis();
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   @OnlyIn(Dist.CLIENT)
   private boolean hasWorldScreenshot;
   private final IReloadableResourceManager resources = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA, this.serverThread);
   private final ResourcePackList<ResourcePackInfo> packRepository = new ResourcePackList<>(ResourcePackInfo::new);
   @Nullable
   private FolderPackFinder field_195578_ae;
   private final Commands field_195579_af;
   private final RecipeManager field_199530_ag = new RecipeManager();
   private final NetworkTagManager field_199736_ah = new NetworkTagManager();
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   @Nullable
   private CommandStorage commandStorage;
   private final CustomServerBossInfoManager customBossEvents = new CustomServerBossInfoManager(this);
   private final LootPredicateManager field_229734_an_ = new LootPredicateManager();
   private final LootTableManager field_200256_aj = new LootTableManager(this.field_229734_an_);
   private final AdvancementManager field_200257_ak = new AdvancementManager();
   private final FunctionManager functionManager = new FunctionManager(this);
   private final net.minecraftforge.common.loot.LootModifierManager lootManager = new net.minecraftforge.common.loot.LootModifierManager();
   private final FrameTimer frameTimer = new FrameTimer();
   private boolean enforceWhitelist;
   private boolean field_212205_ao;
   private boolean field_213216_as;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;

   public MinecraftServer(File p_i50590_1_, Proxy p_i50590_2_, DataFixer p_i50590_3_, Commands p_i50590_4_, YggdrasilAuthenticationService p_i50590_5_, MinecraftSessionService p_i50590_6_, GameProfileRepository p_i50590_7_, PlayerProfileCache p_i50590_8_, IChunkStatusListenerFactory p_i50590_9_, String p_i50590_10_) {
      super("Server");
      this.proxy = p_i50590_2_;
      this.field_195579_af = p_i50590_4_;
      this.field_152364_T = p_i50590_5_;
      this.sessionService = p_i50590_6_;
      this.profileRepository = p_i50590_7_;
      this.profileCache = p_i50590_8_;
      this.field_71308_o = p_i50590_1_;
      this.connection = new NetworkSystem(this);
      this.progressListenerFactory = p_i50590_9_;
      this.storageSource = new SaveFormat(p_i50590_1_.toPath(), p_i50590_1_.toPath().resolve("../backups"), p_i50590_3_);
      this.fixerUpper = p_i50590_3_;
      this.resources.registerReloadListener(this.field_199736_ah);
      this.resources.registerReloadListener(this.field_229734_an_);
      this.resources.registerReloadListener(this.field_199530_ag);
      this.resources.registerReloadListener(this.field_200256_aj);
      this.resources.registerReloadListener(this.functionManager);
      this.resources.registerReloadListener(this.field_200257_ak);
      resources.registerReloadListener(lootManager);
      this.executor = Util.backgroundExecutor();
      this.field_71294_K = p_i50590_10_;
   }

   private void readScoreboard(DimensionSavedDataManager p_213204_1_) {
      ScoreboardSaveData scoreboardsavedata = p_213204_1_.computeIfAbsent(ScoreboardSaveData::new, "scoreboard");
      scoreboardsavedata.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyListener(new WorldSavedDataCallableSave(scoreboardsavedata));
   }

   protected abstract boolean initServer() throws IOException;

   protected void func_71237_c(String p_71237_1_) {
      if (this.func_71254_M().func_75801_b(p_71237_1_)) {
         LOGGER.info("Converting map!");
         this.func_200245_b(new TranslationTextComponent("menu.convertingLevel"));
         this.func_71254_M().func_75805_a(p_71237_1_, new IProgressUpdate() {
            private long timeStamp = Util.getMillis();

            public void progressStartNoAbort(ITextComponent p_200210_1_) {
            }

            @OnlyIn(Dist.CLIENT)
            public void progressStart(ITextComponent p_200211_1_) {
            }

            public void progressStagePercentage(int p_73718_1_) {
               if (Util.getMillis() - this.timeStamp >= 1000L) {
                  this.timeStamp = Util.getMillis();
                  MinecraftServer.LOGGER.info("Converting... {}%", (int)p_73718_1_);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void stop() {
            }

            public void progressStage(ITextComponent p_200209_1_) {
            }
         });
      }

      if (this.field_212205_ao) {
         LOGGER.info("Forcing world upgrade!");
         WorldInfo worldinfo = this.func_71254_M().func_75803_c(this.func_71270_I());
         if (worldinfo != null) {
            WorldOptimizer worldoptimizer = new WorldOptimizer(this.func_71270_I(), this.func_71254_M(), worldinfo, this.field_213216_as);
            ITextComponent itextcomponent = null;

            while(!worldoptimizer.isFinished()) {
               ITextComponent itextcomponent1 = worldoptimizer.getStatus();
               if (itextcomponent != itextcomponent1) {
                  itextcomponent = itextcomponent1;
                  LOGGER.info(worldoptimizer.getStatus().getString());
               }

               int i = worldoptimizer.getTotalChunks();
               if (i > 0) {
                  int j = worldoptimizer.getConverted() + worldoptimizer.getSkipped();
                  LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
               }

               if (this.isStopped()) {
                  worldoptimizer.cancel();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                     ;
                  }
               }
            }
         }
      }

   }

   protected synchronized void func_200245_b(ITextComponent p_200245_1_) {
      this.field_71298_S = p_200245_1_;
   }

   protected void func_71247_a(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.func_71237_c(p_71247_1_);
      this.func_200245_b(new TranslationTextComponent("menu.loadingLevel"));
      SaveHandler savehandler = this.func_71254_M().func_197715_a(p_71247_1_, this);
      this.detectBundledResources(this.func_71270_I(), savehandler);
      // Move factory creation earlier to prevent startupquery deadlock
      IChunkStatusListener ichunkstatuslistener = this.progressListenerFactory.create(11);
      WorldInfo worldinfo = savehandler.func_75757_d();
      WorldSettings worldsettings;
      if (worldinfo == null) {
         if (this.isDemo()) {
            worldsettings = DEMO_SETTINGS;
         } else {
            worldsettings = new WorldSettings(p_71247_3_, this.getDefaultGameType(), this.func_71225_e(), this.isHardcore(), p_71247_5_);
            worldsettings.func_205390_a(p_71247_6_);
            if (this.field_71289_N) {
               worldsettings.func_77159_a();
            }
         }

         worldinfo = new WorldInfo(worldsettings, p_71247_2_);
      } else {
         worldinfo.func_76062_a(p_71247_2_);
         worldsettings = new WorldSettings(worldinfo);
      }

      worldinfo.func_230145_a_(this.getServerModName(), this.getModdedStatus().isPresent());
      this.func_195560_a(savehandler.func_75765_b(), worldinfo);
      this.func_213194_a(savehandler, worldinfo, worldsettings, ichunkstatuslistener);
      this.setDifficulty(this.func_147135_j(), true);
      this.prepareLevels(ichunkstatuslistener);
   }

   protected void func_213194_a(SaveHandler p_213194_1_, WorldInfo p_213194_2_, WorldSettings p_213194_3_, IChunkStatusListener p_213194_4_) {
      net.minecraftforge.common.DimensionManager.fireRegister();
      if (this.isDemo()) {
         p_213194_2_.func_176127_a(DEMO_SETTINGS);
      }

      ServerWorld serverworld = new ServerWorld(this, this.executor, p_213194_1_, p_213194_2_, DimensionType.field_223227_a_, this.profiler, p_213194_4_);
      this.levels.put(DimensionType.field_223227_a_, serverworld);
      DimensionSavedDataManager dimensionsaveddatamanager = serverworld.getDataStorage();
      this.readScoreboard(dimensionsaveddatamanager);
      this.commandStorage = new CommandStorage(dimensionsaveddatamanager);
      serverworld.getWorldBorder().func_222519_b(p_213194_2_);
      ServerWorld serverworld1 = this.getLevel(DimensionType.field_223227_a_);
      if (!p_213194_2_.isInitialized()) {
         try {
            serverworld1.func_73052_b(p_213194_3_);
            if (p_213194_2_.func_76067_t() == WorldType.field_180272_g) {
               this.func_213188_a(p_213194_2_);
            }

            p_213194_2_.setInitialized(true);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception initializing level");

            try {
               serverworld1.fillReportDetails(crashreport);
            } catch (Throwable var11) {
               ;
            }

            throw new ReportedException(crashreport);
         }

         p_213194_2_.setInitialized(true);
      }

      this.getPlayerList().setLevel(serverworld1);
      if (p_213194_2_.func_201357_P() != null) {
         this.getCustomBossEvents().load(p_213194_2_.func_201357_P());
      }

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         if (dimensiontype != DimensionType.field_223227_a_) {
            this.levels.put(dimensiontype, new ServerMultiWorld(serverworld1, this, this.executor, p_213194_1_, dimensiontype, this.profiler, p_213194_4_));
         }
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(levels.get(dimensiontype)));
      }

   }

   private void func_213188_a(WorldInfo p_213188_1_) {
      p_213188_1_.func_176128_f(false);
      p_213188_1_.func_176121_c(true);
      p_213188_1_.setRaining(false);
      p_213188_1_.setThundering(false);
      p_213188_1_.func_176142_i(1000000000);
      p_213188_1_.setDayTime(6000L);
      p_213188_1_.func_76060_a(GameType.SPECTATOR);
      p_213188_1_.func_176119_g(false);
      p_213188_1_.func_176144_a(Difficulty.PEACEFUL);
      p_213188_1_.func_180783_e(true);
      p_213188_1_.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, this);
   }

   protected void func_195560_a(File p_195560_1_, WorldInfo p_195560_2_) {
      this.packRepository.func_198982_a(new ServerPackFinder());
      this.field_195578_ae = new FolderPackFinder(new File(p_195560_1_, "datapacks"));
      this.packRepository.func_198982_a(this.field_195578_ae);
      this.packRepository.reload();
      List<ResourcePackInfo> list = Lists.newArrayList();

      for(String s : p_195560_2_.func_197720_O()) {
         ResourcePackInfo resourcepackinfo = this.packRepository.getPack(s);
         if (resourcepackinfo != null) {
            list.add(resourcepackinfo);
         } else {
            LOGGER.warn("Missing data pack {}", (Object)s);
         }
      }

      this.packRepository.setSelected(list);
      this.func_195568_a(p_195560_2_);
      this.func_229737_ba_();
   }

   protected void prepareLevels(IChunkStatusListener p_213186_1_) {
      this.func_200245_b(new TranslationTextComponent("menu.generatingTerrain"));
      ServerWorld serverworld = this.getLevel(DimensionType.field_223227_a_);
      LOGGER.info("Preparing start region for dimension " + DimensionType.func_212678_a(serverworld.dimension.func_186058_p()));
      BlockPos blockpos = serverworld.func_175694_M();
      p_213186_1_.updateSpawnPos(new ChunkPos(blockpos));
      ServerChunkProvider serverchunkprovider = serverworld.getChunkSource();
      serverchunkprovider.getLightEngine().setTaskPerBatch(500);
      this.nextTickTime = Util.getMillis();
      serverchunkprovider.addRegionTicket(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

      while(serverchunkprovider.getTickingGenerated() != 441) {
         this.nextTickTime = Util.getMillis() + 10L;
         this.waitUntilNextTick();
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         ForcedChunksSaveData forcedchunkssavedata = this.getLevel(dimensiontype).getDataStorage().get(ForcedChunksSaveData::new, "chunks");
         if (forcedchunkssavedata != null) {
            ServerWorld serverworld1 = this.getLevel(dimensiontype);
            LongIterator longiterator = forcedchunkssavedata.getChunks().iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(i);
               serverworld1.getChunkSource().updateChunkForced(chunkpos, true);
            }
         }
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();
      p_213186_1_.stop();
      serverchunkprovider.getLightEngine().setTaskPerBatch(5);
   }

   protected void detectBundledResources(String p_175584_1_, SaveHandler p_175584_2_) {
      File file1 = new File(p_175584_2_.func_75765_b(), "resources.zip");
      if (file1.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(p_175584_1_, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", (Object)p_175584_1_);
         }
      }

   }

   public abstract boolean func_71225_e();

   public abstract GameType getDefaultGameType();

   public abstract Difficulty func_147135_j();

   public abstract boolean isHardcore();

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public boolean saveAllChunks(boolean p_213211_1_, boolean p_213211_2_, boolean p_213211_3_) {
      boolean flag = false;

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (!p_213211_1_) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverworld.getLevelData().getLevelName(), DimensionType.func_212678_a(serverworld.dimension.func_186058_p()));
         }

         try {
            serverworld.save((IProgressUpdate)null, p_213211_2_, serverworld.noSave && !p_213211_3_);
         } catch (SessionLockException sessionlockexception) {
            LOGGER.warn(sessionlockexception.getMessage());
         }

         flag = true;
      }

      ServerWorld serverworld1 = this.getLevel(DimensionType.field_223227_a_);
      WorldInfo worldinfo = serverworld1.getLevelData();
      serverworld1.getWorldBorder().func_222520_a(worldinfo);
      worldinfo.func_201356_c(this.getCustomBossEvents().save());
      serverworld1.func_217485_w().func_75755_a(worldinfo, this.getPlayerList().getSingleplayerData());
      return flag;
   }

   public void close() {
      this.stopServer();
   }

   protected void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getConnection() != null) {
         this.getConnection().stop();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAll();
         this.playerList.removeAll();
      }

      LOGGER.info("Saving worlds");

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (serverworld != null) {
            serverworld.noSave = false;
         }
      }

      this.saveAllChunks(false, true, false);

      for(ServerWorld serverworld1 : this.getAllLevels()) {
         if (serverworld1 != null) {
            try {
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(serverworld1));
               serverworld1.close();
            } catch (IOException ioexception) {
               LOGGER.error("Exception closing the level", (Throwable)ioexception);
            }
         }
      }

      if (this.snooper.isStarted()) {
         this.snooper.interrupt();
      }

   }

   public String getLocalIp() {
      return this.localIp;
   }

   public void setLocalIp(String p_71189_1_) {
      this.localIp = p_71189_1_;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void halt(boolean p_71263_1_) {
      this.running = false;
      if (p_71263_1_) {
         try {
            this.serverThread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.error("Error while shutting down", (Throwable)interruptedexception);
         }
      }

   }

   public void run() {
      try {
         if (this.initServer()) {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarted(this);
            this.nextTickTime = Util.getMillis();
            this.status.setDescription(new StringTextComponent(this.motd));
            this.status.setVersion(new ServerStatusResponse.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
            this.updateStatusIcon(this.status);

            while(this.running) {
               long i = Util.getMillis() - this.nextTickTime;
               if (i > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.nextTickTime += j * 50L;
                  this.lastOverloadWarning = this.nextTickTime;
               }

               this.nextTickTime += 50L;
               if (this.delayProfilerStart) {
                  this.delayProfilerStart = false;
                  this.profiler.func_219899_d().func_219939_d();
               }

               this.profiler.startTick();
               this.profiler.push("tick");
               this.tickServer(this::haveTime);
               this.profiler.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
               this.waitUntilNextTick();
               this.profiler.pop();
               this.profiler.endTick();
               this.isReady = true;
            }
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopping(this);
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         } else {
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.onServerCrash((CrashReport)null);
         }
      } catch (net.minecraftforge.fml.StartupQuery.AbortedException e) {
         // ignore silently
         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport;
         if (throwable1 instanceof ReportedException) {
            crashreport = this.fillReport(((ReportedException)throwable1).getReport());
         } else {
            crashreport = this.fillReport(new CrashReport("Exception in server tick loop", throwable1));
         }

         File file1 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         this.onServerCrash(crashreport);
      } finally {
         try {
            this.stopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopped(this);
            this.onServerExit();
         }

      }

   }

   private boolean haveTime() {
      return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> {
         return !this.haveTime();
      });
   }

   protected TickDelayedTask wrapRunnable(Runnable p_212875_1_) {
      return new TickDelayedTask(this.tickCount, p_212875_1_);
   }

   protected boolean shouldRun(TickDelayedTask p_212874_1_) {
      return p_212874_1_.getTick() + 3 < this.tickCount || this.haveTime();
   }

   public boolean pollTask() {
      boolean flag = this.pollTaskInternal();
      this.mayHaveDelayedTasks = flag;
      return flag;
   }

   private boolean pollTaskInternal() {
      if (super.pollTask()) {
         return true;
      } else {
         if (this.haveTime()) {
            for(ServerWorld serverworld : this.getAllLevels()) {
               if (serverworld.getChunkSource().pollTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void doRunTask(TickDelayedTask p_213166_1_) {
      this.getProfiler().incrementCounter("runTask");
      super.doRunTask(p_213166_1_);
   }

   public void updateStatusIcon(ServerStatusResponse p_184107_1_) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.func_71254_M().func_186352_b(this.func_71270_I(), "icon.png");
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            p_184107_1_.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", (Throwable)exception);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasWorldScreenshot() {
      this.hasWorldScreenshot = this.hasWorldScreenshot || this.getWorldScreenshotFile().isFile();
      return this.hasWorldScreenshot;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldScreenshotFile() {
      return this.func_71254_M().func_186352_b(this.func_71270_I(), "icon.png");
   }

   public File getServerDirectory() {
      return new File(".");
   }

   protected void onServerCrash(CrashReport p_71228_1_) {
   }

   protected void onServerExit() {
   }

   protected void tickServer(BooleanSupplier p_71217_1_) {
      long i = Util.getNanos();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPreServerTick();
      ++this.tickCount;
      this.tickChildren(p_71217_1_);
      if (i - this.lastServerStatus >= 5000000000L) {
         this.lastServerStatus = i;
         this.status.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.status.getPlayers().setSample(agameprofile);
         this.status.invalidateJson();
      }

      if (this.tickCount % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.playerList.saveAll();
         this.saveAllChunks(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("snooper");
      if (!this.snooper.isStarted() && this.tickCount > 100) {
         this.snooper.start();
      }

      if (this.tickCount % 6000 == 0) {
         this.snooper.prepare();
      }

      this.profiler.pop();
      this.profiler.push("tallying");
      long l = this.tickTimes[this.tickCount % 100] = Util.getNanos() - i;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      long i1 = Util.getNanos();
      this.frameTimer.logFrameDuration(i1 - i);
      this.profiler.pop();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPostServerTick();
   }

   protected void tickChildren(BooleanSupplier p_71190_1_) {
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");

      for(ServerWorld serverworld : this.getWorldArray()) {
         long tickStart = Util.getNanos();
         if (serverworld.dimension.func_186058_p() == DimensionType.field_223227_a_ || this.isNetherEnabled()) {
            this.profiler.push(() -> {
               return serverworld.getLevelData().getLevelName() + " " + Registry.field_212622_k.getKey(serverworld.dimension.func_186058_p());
            });
            if (this.tickCount % 20 == 0) {
               this.profiler.push("timeSync");
               this.playerList.func_148537_a(new SUpdateTimePacket(serverworld.getGameTime(), serverworld.getDayTime(), serverworld.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), serverworld.dimension.func_186058_p());
               this.profiler.pop();
            }

            this.profiler.push("tick");
            net.minecraftforge.fml.hooks.BasicEventHooks.onPreWorldTick(serverworld);

            try {
               serverworld.tick(p_71190_1_);
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception ticking world");
               serverworld.fillReportDetails(crashreport);
               throw new ReportedException(crashreport);
            }
            net.minecraftforge.fml.hooks.BasicEventHooks.onPostWorldTick(serverworld);

            this.profiler.pop();
            this.profiler.pop();
         }
         perWorldTickTimes.computeIfAbsent(serverworld.func_201675_m().func_186058_p(), k -> new long[100])[this.tickCount % 100] = Util.getNanos() - tickStart;
      }

      this.profiler.popPush("dim_unloading");
      net.minecraftforge.common.DimensionManager.unloadWorlds(this, this.tickCount % 200 == 0);
      this.profiler.popPush("connection");
      this.getConnection().tick();
      this.profiler.popPush("players");
      this.playerList.tick();
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         TestCollection.singleton.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int i = 0; i < this.tickables.size(); ++i) {
         this.tickables.get(i).run();
      }

      this.profiler.pop();
   }

   public boolean isNetherEnabled() {
      return true;
   }

   public void addTickable(Runnable p_82010_1_) {
      this.tickables.add(p_82010_1_);
   }

   public static void main(String[] p_main_0_) {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("nogui");
      OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
      OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
      OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
      OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
      OptionSpec<Void> optionspec6 = optionparser.accepts("help").forHelp();
      OptionSpec<String> optionspec7 = optionparser.accepts("singleplayer").withRequiredArg();
      OptionSpec<String> optionspec8 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".");
      OptionSpec<String> optionspec9 = optionparser.accepts("world").withRequiredArg();
      OptionSpec<Integer> optionspec10 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
      OptionSpec<String> optionspec11 = optionparser.accepts("serverId").withRequiredArg();
      OptionSpec<String> optionspec12 = optionparser.nonOptions();
      optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File(".")); //Forge: Consume this argument, we use it in the launcher, and the client side.

      try {
         OptionSet optionset = optionparser.parse(p_main_0_);
         if (optionset.has(optionspec6)) {
            optionparser.printHelpOn(System.err);
            return;
         }

         Path path = Paths.get("server.properties");
         ServerPropertiesProvider serverpropertiesprovider = new ServerPropertiesProvider(path);
         if (optionset.has(optionspec1) || !Files.exists(path)) serverpropertiesprovider.forceSave();
         Path path1 = Paths.get("eula.txt");
         ServerEula servereula = new ServerEula(path1);
         if (optionset.has(optionspec1)) {
            LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path1.toAbsolutePath().toString() + "'");
            return;
         }

         if (!servereula.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         CrashReport.preload();
         Bootstrap.bootStrap();
         Bootstrap.validate();
         String s = optionset.valueOf(optionspec8);
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(s, USERID_CACHE_FILE.getName()));
         String s1 = Optional.ofNullable(optionset.valueOf(optionspec9)).orElse(serverpropertiesprovider.getProperties().levelName);
         if (s1 == null || s1.isEmpty() || new File(s, s1).getAbsolutePath().equals(new File(s).getAbsolutePath())) {
            LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! " + s1);
            return;
         }
         final DedicatedServer dedicatedserver = new DedicatedServer(new File(s), serverpropertiesprovider, DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache, LoggingChunkStatusListener::new, s1);
         dedicatedserver.setSingleplayerName(optionset.valueOf(optionspec7));
         dedicatedserver.setPort(optionset.valueOf(optionspec10));
         dedicatedserver.setDemo(optionset.has(optionspec2));
         dedicatedserver.func_71194_c(optionset.has(optionspec3));
         dedicatedserver.func_212204_b(optionset.has(optionspec4));
         dedicatedserver.func_213197_c(optionset.has(optionspec5));
         dedicatedserver.setId(optionset.valueOf(optionspec11));
         boolean flag = !optionset.has(optionspec) && !optionset.valuesOf(optionspec12).contains("nogui");
         if (flag && !GraphicsEnvironment.isHeadless()) {
            dedicatedserver.showGui();
         }

         dedicatedserver.func_71256_s();
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.halt(true);
               LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception exception) {
         LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
      }

   }

   protected void setId(String p_213208_1_) {
      this.serverId = p_213208_1_;
   }

   protected void func_212204_b(boolean p_212204_1_) {
      this.field_212205_ao = p_212204_1_;
   }

   protected void func_213197_c(boolean p_213197_1_) {
      this.field_213216_as = p_213197_1_;
   }

   public void func_71256_s() {
      this.serverThread.start();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isShutdown() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String p_71209_1_) {
      return new File(this.getServerDirectory(), p_71209_1_);
   }

   public void func_71244_g(String p_71244_1_) {
      LOGGER.info(p_71244_1_);
   }

   public void func_71236_h(String p_71236_1_) {
      LOGGER.warn(p_71236_1_);
   }

   public ServerWorld getLevel(DimensionType p_71218_1_) {
      return net.minecraftforge.common.DimensionManager.getWorld(this, p_71218_1_, true, true);
   }

   public Iterable<ServerWorld> getAllLevels() {
      return this.levels.values();
   }

   public String getServerVersion() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public int getPlayerCount() {
      return this.playerList.getPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getPlayerNames() {
      return this.playerList.getPlayerNamesArray();
   }

   public boolean func_71239_B() {
      return false;
   }

   public void func_71201_j(String p_71201_1_) {
      LOGGER.error(p_71201_1_);
   }

   public void func_71198_k(String p_71198_1_) {
      if (this.func_71239_B()) {
         LOGGER.info(p_71198_1_);
      }

   }

   public String getServerModName() {
      return net.minecraftforge.fml.BrandingControl.getServerBranding();
   }

   public CrashReport fillReport(CrashReport p_71230_1_) {
      if (this.playerList != null) {
         p_71230_1_.getSystemDetails().setDetail("Player Count", () -> {
            return this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      p_71230_1_.getSystemDetails().setDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(ResourcePackInfo resourcepackinfo : this.packRepository.getSelectedPacks()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getId());
            if (!resourcepackinfo.getCompatibility().isCompatible()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      if (this.serverId != null) {
         p_71230_1_.getSystemDetails().setDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return p_71230_1_;
   }

   public abstract Optional<String> getModdedStatus();

   public boolean func_175578_N() {
      return this.field_71308_o != null;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      LOGGER.info(p_145747_1_.getString());
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int p_71208_1_) {
      this.port = p_71208_1_;
   }

   public String getSingleplayerName() {
      return this.singleplayerName;
   }

   public void setSingleplayerName(String p_71224_1_) {
      this.singleplayerName = p_71224_1_;
   }

   public boolean isSingleplayer() {
      return this.singleplayerName != null;
   }

   public String func_71270_I() {
      return this.field_71294_K;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_71246_n(String p_71246_1_) {
      this.field_71287_L = p_71246_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String func_71221_J() {
      return this.field_71287_L;
   }

   public void func_71253_a(KeyPair p_71253_1_) {
      this.keyPair = p_71253_1_;
   }

   public void setDifficulty(Difficulty p_147139_1_, boolean p_147139_2_) {
      for(ServerWorld serverworld : this.getAllLevels()) {
         WorldInfo worldinfo = serverworld.getLevelData();
         if (p_147139_2_ || !worldinfo.isDifficultyLocked()) {
            if (worldinfo.isHardcore()) {
               worldinfo.func_176144_a(Difficulty.HARD);
               serverworld.setSpawnSettings(true, true);
            } else if (this.isSingleplayer()) {
               worldinfo.func_176144_a(p_147139_1_);
               serverworld.setSpawnSettings(serverworld.getDifficulty() != Difficulty.PEACEFUL, true);
            } else {
               worldinfo.func_176144_a(p_147139_1_);
               serverworld.setSpawnSettings(this.func_71193_K(), this.field_71324_y);
            }
         }
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   public void setDifficultyLocked(boolean p_213209_1_) {
      for(ServerWorld serverworld : this.getAllLevels()) {
         WorldInfo worldinfo = serverworld.getLevelData();
         worldinfo.func_180783_e(p_213209_1_);
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayerEntity p_213189_1_) {
      WorldInfo worldinfo = p_213189_1_.getLevel().getLevelData();
      p_213189_1_.connection.send(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
   }

   protected boolean func_71193_K() {
      return true;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean p_71204_1_) {
      this.isDemo = p_71204_1_;
   }

   public void func_71194_c(boolean p_71194_1_) {
      this.field_71289_N = p_71194_1_;
   }

   public SaveFormat func_71254_M() {
      return this.storageSource;
   }

   public String getResourcePack() {
      return this.resourcePack;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String p_180507_1_, String p_180507_2_) {
      this.resourcePack = p_180507_1_;
      this.resourcePackHash = p_180507_2_;
   }

   public void populateSnooper(Snooper p_70000_1_) {
      p_70000_1_.setDynamicData("whitelist_enabled", false);
      p_70000_1_.setDynamicData("whitelist_count", 0);
      if (this.playerList != null) {
         p_70000_1_.setDynamicData("players_current", this.getPlayerCount());
         p_70000_1_.setDynamicData("players_max", this.getMaxPlayers());
         p_70000_1_.setDynamicData("players_seen", this.getLevel(DimensionType.field_223227_a_).func_217485_w().func_215771_d().length);
      }

      p_70000_1_.setDynamicData("uses_auth", this.onlineMode);
      p_70000_1_.setDynamicData("gui_state", this.hasGui() ? "enabled" : "disabled");
      p_70000_1_.setDynamicData("run_time", (Util.getMillis() - p_70000_1_.getStartupTime()) / 60L * 1000L);
      p_70000_1_.setDynamicData("avg_tick_ms", (int)(MathHelper.average(this.tickTimes) * 1.0E-6D));
      int i = 0;

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (serverworld != null) {
            WorldInfo worldinfo = serverworld.getLevelData();
            p_70000_1_.setDynamicData("world[" + i + "][dimension]", serverworld.dimension.func_186058_p());
            p_70000_1_.setDynamicData("world[" + i + "][mode]", worldinfo.getGameType());
            p_70000_1_.setDynamicData("world[" + i + "][difficulty]", serverworld.getDifficulty());
            p_70000_1_.setDynamicData("world[" + i + "][hardcore]", worldinfo.isHardcore());
            p_70000_1_.setDynamicData("world[" + i + "][generator_name]", worldinfo.func_76067_t().func_211888_a());
            p_70000_1_.setDynamicData("world[" + i + "][generator_version]", worldinfo.func_76067_t().func_77131_c());
            p_70000_1_.setDynamicData("world[" + i + "][height]", this.maxBuildHeight);
            p_70000_1_.setDynamicData("world[" + i + "][chunks_loaded]", serverworld.getChunkSource().getLoadedChunksCount());
            ++i;
         }
      }

      p_70000_1_.setDynamicData("worlds", i);
   }

   public abstract boolean isDedicatedServer();

   public boolean usesAuthentication() {
      return this.onlineMode;
   }

   public void setUsesAuthentication(boolean p_71229_1_) {
      this.onlineMode = p_71229_1_;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean func_71268_U() {
      return this.field_71324_y;
   }

   public void func_71251_e(boolean p_71251_1_) {
      this.field_71324_y = p_71251_1_;
   }

   public boolean func_71220_V() {
      return this.field_71323_z;
   }

   public abstract boolean isEpollEnabled();

   public void func_71257_f(boolean p_71257_1_) {
      this.field_71323_z = p_71257_1_;
   }

   public boolean isPvpAllowed() {
      return this.pvp;
   }

   public void setPvpAllowed(boolean p_71188_1_) {
      this.pvp = p_71188_1_;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setFlightAllowed(boolean p_71245_1_) {
      this.allowFlight = p_71245_1_;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(String p_71205_1_) {
      this.motd = p_71205_1_;
   }

   public int getMaxBuildHeight() {
      return this.maxBuildHeight;
   }

   public void setMaxBuildHeight(int p_71191_1_) {
      this.maxBuildHeight = p_71191_1_;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList p_184105_1_) {
      this.playerList = p_184105_1_;
   }

   public abstract boolean isPublished();

   public void setDefaultGameType(GameType p_71235_1_) {
      for(ServerWorld serverworld : this.getAllLevels()) {
         serverworld.getLevelData().func_76060_a(p_71235_1_);
      }

   }

   @Nullable
   public NetworkSystem getConnection() {
      return this.connection;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public abstract boolean publishServer(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_);

   public int getTickCount() {
      return this.tickCount;
   }

   public void func_71223_ag() {
      this.delayProfilerStart = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(World p_175579_1_, BlockPos p_175579_2_, PlayerEntity p_175579_3_) {
      return false;
   }

   public void setForceGameType(boolean p_104055_1_) {
      this.forceGameType = p_104055_1_;
   }

   public boolean getForceGameType() {
      return this.forceGameType;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      this.playerIdleTimeout = p_143006_1_;
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getProfileRepository() {
      return this.profileRepository;
   }

   public PlayerProfileCache getProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getStatus() {
      return this.status;
   }

   public void invalidateStatus() {
      this.lastServerStatus = 0L;
   }

   public int getAbsoluteMaxWorldSize() {
      return 29999984;
   }

   public boolean scheduleExecutables() {
      return super.scheduleExecutables() && !this.isStopped();
   }

   public Thread getRunningThread() {
      return this.serverThread;
   }

   public int getCompressionThreshold() {
      return 256;
   }

   public long getNextTickTime() {
      return this.nextTickTime;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public int getSpawnRadius(@Nullable ServerWorld p_184108_1_) {
      return p_184108_1_ != null ? p_184108_1_.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS) : 10;
   }

   public AdvancementManager getAdvancements() {
      return this.field_200257_ak;
   }

   public FunctionManager getFunctions() {
      return this.functionManager;
   }
   
   public net.minecraftforge.common.loot.LootModifierManager getLootModifierManager() {
	   return lootManager;
   }

   public void func_193031_aM() {
      if (!this.isSameThread()) {
         this.execute(this::func_193031_aM);
      } else {
         this.getPlayerList().saveAll();
         this.packRepository.reload();
         this.func_195568_a(this.getLevel(DimensionType.field_223227_a_).getLevelData());
         this.getPlayerList().reloadResources();
         this.func_229737_ba_();
      }
   }

   private void func_195568_a(WorldInfo p_195568_1_) {
      List<ResourcePackInfo> list = Lists.newArrayList(this.packRepository.getSelectedPacks());

      for(ResourcePackInfo resourcepackinfo : this.packRepository.getAvailablePacks()) {
         if (!p_195568_1_.func_197719_N().contains(resourcepackinfo.getId()) && !list.contains(resourcepackinfo)) {
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)resourcepackinfo.getId());
            resourcepackinfo.getDefaultPosition().insert(list, resourcepackinfo, (p_200247_0_) -> {
               return p_200247_0_;
            }, false);
         }
      }

      this.packRepository.setSelected(list);
      List<IResourcePack> list1 = Lists.newArrayList();
      this.packRepository.getSelectedPacks().forEach((p_200244_1_) -> {
         list1.add(p_200244_1_.open());
      });
      CompletableFuture<Unit> completablefuture = this.resources.reload(this.executor, this, list1, field_223713_i);
      this.managedBlock(completablefuture::isDone);

      try {
         completablefuture.get();
      } catch (Exception exception) {
         LOGGER.error("Failed to reload data packs", (Throwable)exception);
      }

      p_195568_1_.func_197720_O().clear();
      p_195568_1_.func_197719_N().clear();
      this.packRepository.getSelectedPacks().forEach((p_195562_1_) -> {
         p_195568_1_.func_197720_O().add(p_195562_1_.getId());
      });
      this.packRepository.getAvailablePacks().forEach((p_200248_2_) -> {
         if (!this.packRepository.getSelectedPacks().contains(p_200248_2_)) {
            p_195568_1_.func_197719_N().add(p_200248_2_.getId());
         }

      });
   }

   public void kickUnlistedPlayers(CommandSource p_205743_1_) {
      if (this.isEnforceWhitelist()) {
         PlayerList playerlist = p_205743_1_.getServer().getPlayerList();
         WhiteList whitelist = playerlist.getWhiteList();
         if (whitelist.func_152689_b()) {
            for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(playerlist.getPlayers())) {
               if (!whitelist.isWhiteListed(serverplayerentity.getGameProfile())) {
                  serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_whitelisted"));
               }
            }

         }
      }
   }

   public IReloadableResourceManager func_195570_aG() {
      return this.resources;
   }

   public ResourcePackList<ResourcePackInfo> getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.field_195579_af;
   }

   public CommandSource createCommandSourceStack() {
      return new CommandSource(this, this.getLevel(DimensionType.field_223227_a_) == null ? Vec3d.ZERO : new Vec3d(this.getLevel(DimensionType.field_223227_a_).func_175694_M()), Vec2f.ZERO, this.getLevel(DimensionType.field_223227_a_), 4, "Server", new StringTextComponent("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.field_199530_ag;
   }

   public NetworkTagManager func_199731_aO() {
      return this.field_199736_ah;
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage getCommandStorage() {
      if (this.commandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.commandStorage;
      }
   }

   public LootTableManager getLootTables() {
      return this.field_200256_aj;
   }

   public LootPredicateManager getPredicateManager() {
      return this.field_229734_an_;
   }

   public GameRules getGameRules() {
      return this.getLevel(DimensionType.field_223227_a_).getGameRules();
   }

   public CustomServerBossInfoManager getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean p_205741_1_) {
      this.enforceWhitelist = p_205741_1_;
   }

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public int getProfilePermissions(GameProfile p_211833_1_) {
      if (this.getPlayerList().isOp(p_211833_1_)) {
         OpEntry opentry = this.getPlayerList().getOps().get(p_211833_1_);
         if (opentry != null) {
            return opentry.getLevel();
         } else if (this.isSingleplayerOwner(p_211833_1_)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
         } else {
            return this.getOperatorUserPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public DebugProfiler getProfiler() {
      return this.profiler;
   }

   public Executor getBackgroundTaskExecutor() {
      return this.executor;
   }

   public abstract boolean isSingleplayerOwner(GameProfile p_213199_1_);

   private Map<DimensionType, long[]> perWorldTickTimes = Maps.newIdentityHashMap();
   @Nullable
   public long[] getTickTime(DimensionType dim) {
      return perWorldTickTimes.get(dim);
   }

   @Deprecated //Forge Internal use Only, You can screw up a lot of things if you mess with this map.
   public synchronized Map<DimensionType, ServerWorld> forgeGetWorldMap() {
      return this.levels;
   }
   private int worldArrayMarker = 0;
   private int worldArrayLast = -1;
   private ServerWorld[] worldArray;
   @Deprecated //Forge Internal use Only, use to protect against concurrent modifications in the world tick loop.
   public synchronized void markWorldsDirty() {
      worldArrayMarker++;
   }
   private ServerWorld[] getWorldArray() {
      if (worldArrayMarker == worldArrayLast && worldArray != null)
         return worldArray;
      worldArray = this.levels.values().stream().toArray(x -> new ServerWorld[x]);
      worldArrayLast = worldArrayMarker;
      return worldArray;
   }

   public void saveDebugReport(Path p_223711_1_) throws IOException {
      Path path = p_223711_1_.resolve("levels");

      for(Entry<DimensionType, ServerWorld> entry : this.levels.entrySet()) {
         ResourceLocation resourcelocation = DimensionType.func_212678_a(entry.getKey());
         Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
         Files.createDirectories(path1);
         entry.getValue().saveDebugReport(path1);
      }

      this.dumpGameRules(p_223711_1_.resolve("gamerules.txt"));
      this.dumpClasspath(p_223711_1_.resolve("classpath.txt"));
      this.dumpCrashCategory(p_223711_1_.resolve("example_crash.txt"));
      this.dumpMiscStats(p_223711_1_.resolve("stats.txt"));
      this.dumpThreads(p_223711_1_.resolve("threads.txt"));
   }

   private void dumpMiscStats(Path p_223710_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223710_1_)) {
         writer.write(String.format("pending_tasks: %d\n", this.getPendingTasksCount()));
         writer.write(String.format("average_tick_time: %f\n", this.getAverageTickTime()));
         writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimes)));
         writer.write(String.format("queue: %s\n", Util.backgroundExecutor()));
      }

   }

   private void dumpCrashCategory(Path p_223709_1_) throws IOException {
      CrashReport crashreport = new CrashReport("Server dump", new Exception("dummy"));
      this.fillReport(crashreport);

      try (Writer writer = Files.newBufferedWriter(p_223709_1_)) {
         writer.write(crashreport.getFriendlyReport());
      }

   }

   private void dumpGameRules(Path p_223708_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223708_1_)) {
         final List<String> list = Lists.newArrayList();
         final GameRules gamerules = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
               list.add(String.format("%s=%s\n", p_223481_1_.getId(), gamerules.<T>getRule(p_223481_1_).toString()));
            }
         });

         for(String s : list) {
            writer.write(s);
         }
      }

   }

   private void dumpClasspath(Path p_223706_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223706_1_)) {
         String s = System.getProperty("java.class.path");
         String s1 = System.getProperty("path.separator");

         for(String s2 : Splitter.on(s1).split(s)) {
            writer.write(s2);
            writer.write("\n");
         }
      }

   }

   private void dumpThreads(Path p_223712_1_) throws IOException {
      ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
      Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));

      try (Writer writer = Files.newBufferedWriter(p_223712_1_)) {
         for(ThreadInfo threadinfo : athreadinfo) {
            writer.write(threadinfo.toString());
            writer.write(10);
         }
      }

   }

   private void func_229737_ba_() {
      Block.BLOCK_STATE_REGISTRY.forEach(BlockState::initCache);
   }
}
