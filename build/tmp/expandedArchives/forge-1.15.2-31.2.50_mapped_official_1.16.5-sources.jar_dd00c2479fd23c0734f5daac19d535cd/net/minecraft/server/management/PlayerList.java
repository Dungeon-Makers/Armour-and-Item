package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayerEntity> playersByUUID = Maps.newHashMap();
   private final BanList bans = new BanList(USERBANLIST_FILE);
   private final IPBanList ipBans = new IPBanList(IPBANLIST_FILE);
   private final OpList ops = new OpList(OPLIST_FILE);
   private final WhiteList whitelist = new WhiteList(WHITELIST_FILE);
   private final Map<UUID, ServerStatisticsManager> stats = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private IPlayerFileData playerIo;
   private boolean doWhiteList;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType overrideGameMode;
   private boolean allowCheatsForAllPlayers;
   private int sendAllPlayerInfoIn;
   private final List<ServerPlayerEntity> playersView = java.util.Collections.unmodifiableList(players);

   public PlayerList(MinecraftServer p_i50688_1_, int p_i50688_2_) {
      this.server = p_i50688_1_;
      this.maxPlayers = p_i50688_2_;
      this.getBans().func_152686_a(true);
      this.getIpBans().func_152686_a(true);
   }

   public void placeNewPlayer(NetworkManager p_72355_1_, ServerPlayerEntity p_72355_2_) {
      GameProfile gameprofile = p_72355_2_.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getProfileCache();
      GameProfile gameprofile1 = playerprofilecache.get(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.add(gameprofile);
      CompoundNBT compoundnbt = this.load(p_72355_2_);

      //Forge: Make sure the dimension hasn't been deleted, if so stick them in the overworld.
      ServerWorld serverworld = p_72355_2_.field_71093_bK != null ? this.server.getLevel(p_72355_2_.field_71093_bK) : null ;
      if (serverworld == null) {
         p_72355_2_.field_71093_bK = DimensionType.field_223227_a_;
         serverworld = this.server.getLevel(p_72355_2_.field_71093_bK);
         p_72355_2_.setPos(serverworld.getLevelData().getXSpawn(), serverworld.getLevelData().getYSpawn(), serverworld.getLevelData().getZSpawn());
      }

      p_72355_2_.setLevel(serverworld);
      p_72355_2_.gameMode.setLevel((ServerWorld)p_72355_2_.level);
      String s1 = "local";
      if (p_72355_1_.getRemoteAddress() != null) {
         s1 = p_72355_1_.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", p_72355_2_.getName().getString(), s1, p_72355_2_.getId(), p_72355_2_.getX(), p_72355_2_.getY(), p_72355_2_.getZ());
      WorldInfo worldinfo = serverworld.getLevelData();
      this.updatePlayerGameMode(p_72355_2_, (ServerPlayerEntity)null, serverworld);
      ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, p_72355_1_, p_72355_2_);
      net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(p_72355_1_, "PLAY_TO_CLIENT");
      net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(p_72355_1_, p_72355_2_);
      GameRules gamerules = serverworld.getGameRules();
      boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      serverplaynethandler.send(new SJoinGamePacket(p_72355_2_.getId(), p_72355_2_.gameMode.getGameModeForPlayer(), WorldInfo.func_227498_c_(worldinfo.func_76063_b()), worldinfo.isHardcore(), serverworld.dimension.func_186058_p(), this.getMaxPlayers(), worldinfo.func_76067_t(), this.viewDistance, flag1, !flag));
      serverplaynethandler.send(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      serverplaynethandler.send(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplaynethandler.send(new SPlayerAbilitiesPacket(p_72355_2_.abilities));
      serverplaynethandler.send(new SHeldItemChangePacket(p_72355_2_.inventory.selected));
      serverplaynethandler.send(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      serverplaynethandler.send(new STagsListPacket(this.server.func_199731_aO()));
      this.sendPlayerPermissionLevel(p_72355_2_);
      p_72355_2_.getStats().markAllDirty();
      p_72355_2_.getRecipeBook().sendInitialRecipeBook(p_72355_2_);
      this.updateEntireScoreboard(serverworld.getScoreboard(), p_72355_2_);
      this.server.invalidateStatus();
      ITextComponent itextcomponent;
      if (p_72355_2_.getGameProfile().getName().equalsIgnoreCase(s)) {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined", p_72355_2_.getDisplayName());
      } else {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", p_72355_2_.getDisplayName(), s);
      }

      this.func_148539_a(itextcomponent.func_211708_a(TextFormatting.YELLOW));
      serverplaynethandler.teleport(p_72355_2_.getX(), p_72355_2_.getY(), p_72355_2_.getZ(), p_72355_2_.yRot, p_72355_2_.xRot);
      this.addPlayer(p_72355_2_);
      this.playersByUUID.put(p_72355_2_.getUUID(), p_72355_2_);
      this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, p_72355_2_));

      for(int i = 0; i < this.players.size(); ++i) {
         p_72355_2_.connection.send(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
      }

      serverworld.addNewPlayer(p_72355_2_);
      this.server.getCustomBossEvents().onPlayerConnect(p_72355_2_);
      this.sendLevelInfo(p_72355_2_, serverworld);
      if (!this.server.getResourcePack().isEmpty()) {
         p_72355_2_.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      for(EffectInstance effectinstance : p_72355_2_.getActiveEffects()) {
         serverplaynethandler.send(new SPlayEntityEffectPacket(p_72355_2_.getId(), effectinstance));
      }

      if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
         final ServerWorld worldf = serverworld;
         Entity entity1 = EntityType.loadEntityRecursive(compoundnbt1.getCompound("Entity"), serverworld, (p_217885_1_) -> {
            return !worldf.addWithUUID(p_217885_1_) ? null : p_217885_1_;
         });
         if (entity1 != null) {
            UUID uuid = compoundnbt1.getUUID("Attach");
            if (entity1.getUUID().equals(uuid)) {
               p_72355_2_.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getIndirectPassengers()) {
                  if (entity.getUUID().equals(uuid)) {
                     p_72355_2_.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!p_72355_2_.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               serverworld.despawn(entity1);

               for(Entity entity2 : entity1.getIndirectPassengers()) {
                  serverworld.despawn(entity2);
               }
            }
         }
      }

      p_72355_2_.initMenu();
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedIn( p_72355_2_ );
   }

   protected void updateEntireScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_) {
      Set<ScoreObjective> set = Sets.newHashSet();

      for(ScorePlayerTeam scoreplayerteam : p_96456_1_.getPlayerTeams()) {
         p_96456_2_.connection.send(new STeamsPacket(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = p_96456_1_.getDisplayObjective(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            for(IPacket<?> ipacket : p_96456_1_.getStartTrackingPackets(scoreobjective)) {
               p_96456_2_.connection.send(ipacket);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void setLevel(ServerWorld p_212504_1_) {
      this.playerIo = p_212504_1_.func_217485_w();
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onBorderSizeSet(WorldBorder p_177694_1_, double p_177694_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177694_1_, SWorldBorderPacket.Action.SET_SIZE));
         }

         public void onBorderSizeLerping(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177692_1_, SWorldBorderPacket.Action.LERP_SIZE));
         }

         public void onBorderCenterSet(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177693_1_, SWorldBorderPacket.Action.SET_CENTER));
         }

         public void onBorderSetWarningTime(WorldBorder p_177691_1_, int p_177691_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177691_1_, SWorldBorderPacket.Action.SET_WARNING_TIME));
         }

         public void onBorderSetWarningBlocks(WorldBorder p_177690_1_, int p_177690_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177690_1_, SWorldBorderPacket.Action.SET_WARNING_BLOCKS));
         }

         public void onBorderSetDamagePerBlock(WorldBorder p_177696_1_, double p_177696_2_) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder p_177695_1_, double p_177695_2_) {
         }
      });
   }

   @Nullable
   public CompoundNBT load(ServerPlayerEntity p_72380_1_) {
      CompoundNBT compoundnbt = this.server.getLevel(DimensionType.field_223227_a_).getLevelData().func_76072_h();
      CompoundNBT compoundnbt1;
      if (p_72380_1_.getName().getString().equals(this.server.getSingleplayerName()) && compoundnbt != null) {
         compoundnbt1 = compoundnbt;
         p_72380_1_.load(compoundnbt);
         LOGGER.debug("loading single player");
         net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(p_72380_1_, this.playerIo, p_72380_1_.getUUID().toString());
      } else {
         compoundnbt1 = this.playerIo.func_75752_b(p_72380_1_);
      }

      return compoundnbt1;
   }

   protected void save(ServerPlayerEntity p_72391_1_) {
      if (p_72391_1_.connection == null) return;
      this.playerIo.func_75753_a(p_72391_1_);
      ServerStatisticsManager serverstatisticsmanager = this.stats.get(p_72391_1_.getUUID());
      if (serverstatisticsmanager != null) {
         serverstatisticsmanager.save();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(p_72391_1_.getUUID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   public void remove(ServerPlayerEntity p_72367_1_) {
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedOut(p_72367_1_);
      ServerWorld serverworld = p_72367_1_.getLevel();
      p_72367_1_.awardStat(Stats.LEAVE_GAME);
      this.save(p_72367_1_);
      if (p_72367_1_.isPassenger()) {
         Entity entity = p_72367_1_.getRootVehicle();
         if (entity.hasOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            p_72367_1_.stopRiding();
            serverworld.despawn(entity);

            for(Entity entity1 : entity.getIndirectPassengers()) {
               serverworld.despawn(entity1);
            }

            serverworld.getChunk(p_72367_1_.xChunk, p_72367_1_.zChunk).markUnsaved();
         }
      }

      p_72367_1_.unRide();
      serverworld.removePlayerImmediately(p_72367_1_);
      p_72367_1_.getAdvancements().stopListening();
      this.removePlayer(p_72367_1_);
      this.server.getCustomBossEvents().onPlayerDisconnect(p_72367_1_);
      UUID uuid = p_72367_1_.getUUID();
      ServerPlayerEntity serverplayerentity = this.playersByUUID.get(uuid);
      if (serverplayerentity == p_72367_1_) {
         this.playersByUUID.remove(uuid);
         this.stats.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, p_72367_1_));
   }

   @Nullable
   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      if (this.bans.isBanned(p_206258_2_)) {
         ProfileBanEntry profilebanentry = this.bans.get(p_206258_2_);
         ITextComponent itextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.banned.reason", profilebanentry.getReason());
         if (profilebanentry.getExpires() != null) {
            itextcomponent1.func_150257_a(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(profilebanentry.getExpires())));
         }

         return itextcomponent1;
      } else if (!this.isWhiteListed(p_206258_2_)) {
         return new TranslationTextComponent("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(p_206258_1_)) {
         IPBanEntry ipbanentry = this.ipBans.get(p_206258_1_);
         ITextComponent itextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", ipbanentry.getReason());
         if (ipbanentry.getExpires() != null) {
            itextcomponent.func_150257_a(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(ipbanentry.getExpires())));
         }

         return itextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(p_206258_2_) ? new TranslationTextComponent("multiplayer.disconnect.server_full") : null;
      }
   }

   public ServerPlayerEntity getPlayerForLogin(GameProfile p_148545_1_) {
      UUID uuid = PlayerEntity.createPlayerUUID(p_148545_1_);
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.getUUID().equals(uuid)) {
            list.add(serverplayerentity);
         }
      }

      ServerPlayerEntity serverplayerentity2 = this.playersByUUID.get(p_148545_1_.getId());
      if (serverplayerentity2 != null && !list.contains(serverplayerentity2)) {
         list.add(serverplayerentity2);
      }

      for(ServerPlayerEntity serverplayerentity1 : list) {
         serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
      }

      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getLevel(DimensionType.field_223227_a_));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getLevel(DimensionType.field_223227_a_));
      }

      return new ServerPlayerEntity(this.server, this.server.getLevel(DimensionType.field_223227_a_), p_148545_1_, playerinteractionmanager);
   }

   public ServerPlayerEntity func_72368_a(ServerPlayerEntity p_72368_1_, DimensionType p_72368_2_, boolean p_72368_3_) {
      ServerWorld world = server.getLevel(p_72368_2_);
      if (world == null)
         p_72368_2_ = p_72368_1_.getSpawnDimension();
      else if (!world.func_201675_m().func_76567_e())
         p_72368_2_ = world.func_201675_m().getRespawnDimension(p_72368_1_);
      if (server.getLevel(p_72368_2_) == null)
         p_72368_2_ = DimensionType.field_223227_a_;

      this.removePlayer(p_72368_1_);
      p_72368_1_.getLevel().removePlayer(p_72368_1_, true); // Forge: keep data until copyFrom called
      BlockPos blockpos = p_72368_1_.getBedLocation(p_72368_2_);
      boolean flag = p_72368_1_.isSpawnForced(p_72368_2_);
      p_72368_1_.field_71093_bK = p_72368_2_;
      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getLevel(p_72368_1_.field_71093_bK));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getLevel(p_72368_1_.field_71093_bK));
      }

      ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, this.server.getLevel(p_72368_1_.field_71093_bK), p_72368_1_.getGameProfile(), playerinteractionmanager);
      serverplayerentity.connection = p_72368_1_.connection;
      serverplayerentity.restoreFrom(p_72368_1_, p_72368_3_);
      p_72368_1_.remove(false); // Forge: clone event had a chance to see old data, now discard it
      serverplayerentity.field_71093_bK = p_72368_2_;
      serverplayerentity.setId(p_72368_1_.getId());
      serverplayerentity.setMainArm(p_72368_1_.getMainArm());

      for(String s : p_72368_1_.getTags()) {
         serverplayerentity.addTag(s);
      }

      ServerWorld serverworld = this.server.getLevel(p_72368_1_.field_71093_bK);
      this.updatePlayerGameMode(serverplayerentity, p_72368_1_, serverworld);
      if (blockpos != null) {
         Optional<Vec3d> optional = PlayerEntity.func_213822_a(this.server.getLevel(p_72368_1_.field_71093_bK), blockpos, flag);
         if (optional.isPresent()) {
            Vec3d vec3d = optional.get();
            serverplayerentity.moveTo(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
            serverplayerentity.setSpawnPoint(blockpos, flag, false, p_72368_2_);
         } else {
            serverplayerentity.connection.send(new SChangeGameStatePacket(0, 0.0F));
         }
      }

      while(!serverworld.noCollision(serverplayerentity) && serverplayerentity.getY() < 256.0D) {
         serverplayerentity.setPos(serverplayerentity.getX(), serverplayerentity.getY() + 1.0D, serverplayerentity.getZ());
      }

      WorldInfo worldinfo = serverplayerentity.level.getLevelData();
      net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(serverplayerentity.connection.connection, serverplayerentity);
      serverplayerentity.connection.send(new SRespawnPacket(serverplayerentity.field_71093_bK, WorldInfo.func_227498_c_(worldinfo.func_76063_b()), worldinfo.func_76067_t(), serverplayerentity.gameMode.getGameModeForPlayer()));
      BlockPos blockpos1 = serverworld.func_175694_M();
      serverplayerentity.connection.teleport(serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), serverplayerentity.yRot, serverplayerentity.xRot);
      serverplayerentity.connection.send(new SSpawnPositionPacket(blockpos1));
      serverplayerentity.connection.send(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplayerentity.connection.send(new SSetExperiencePacket(serverplayerentity.experienceProgress, serverplayerentity.totalExperience, serverplayerentity.experienceLevel));
      this.sendLevelInfo(serverplayerentity, serverworld);
      this.sendPlayerPermissionLevel(serverplayerentity);
      serverworld.addRespawnedPlayer(serverplayerentity);
      this.addPlayer(serverplayerentity);
      this.playersByUUID.put(serverplayerentity.getUUID(), serverplayerentity);
      serverplayerentity.initMenu();
      serverplayerentity.setHealth(serverplayerentity.getHealth());
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, p_72368_3_);
      return serverplayerentity;
   }

   public void sendPlayerPermissionLevel(ServerPlayerEntity p_187243_1_) {
      GameProfile gameprofile = p_187243_1_.getGameProfile();
      int i = this.server.getProfilePermissions(gameprofile);
      this.sendPlayerPermissionLevel(p_187243_1_, i);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_LATENCY, this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(IPacket<?> p_148540_1_) {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.send(p_148540_1_);
      }

   }

   public void func_148537_a(IPacket<?> p_148537_1_, DimensionType p_148537_2_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.field_71093_bK == p_148537_2_) {
            serverplayerentity.connection.send(p_148537_1_);
         }
      }

   }

   public void broadcastToTeam(PlayerEntity p_177453_1_, ITextComponent p_177453_2_) {
      Team team = p_177453_1_.getTeam();
      if (team != null) {
         for(String s : team.getPlayers()) {
            ServerPlayerEntity serverplayerentity = this.getPlayerByName(s);
            if (serverplayerentity != null && serverplayerentity != p_177453_1_) {
               serverplayerentity.sendMessage(p_177453_2_);
            }
         }

      }
   }

   public void broadcastToAllExceptTeam(PlayerEntity p_177452_1_, ITextComponent p_177452_2_) {
      Team team = p_177452_1_.getTeam();
      if (team == null) {
         this.func_148539_a(p_177452_2_);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverplayerentity = this.players.get(i);
            if (serverplayerentity.getTeam() != team) {
               serverplayerentity.sendMessage(p_177452_2_);
            }
         }

      }
   }

   public String[] getPlayerNamesArray() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public BanList getBans() {
      return this.bans;
   }

   public IPBanList getIpBans() {
      return this.ipBans;
   }

   public void op(GameProfile p_152605_1_) {
      this.ops.add(new OpEntry(p_152605_1_, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(p_152605_1_)));
      ServerPlayerEntity serverplayerentity = this.getPlayer(p_152605_1_.getId());
      if (serverplayerentity != null) {
         this.sendPlayerPermissionLevel(serverplayerentity);
      }

   }

   public void deop(GameProfile p_152610_1_) {
      this.ops.remove(p_152610_1_);
      ServerPlayerEntity serverplayerentity = this.getPlayer(p_152610_1_.getId());
      if (serverplayerentity != null) {
         this.sendPlayerPermissionLevel(serverplayerentity);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayerEntity p_187245_1_, int p_187245_2_) {
      if (p_187245_1_.connection != null) {
         byte b0;
         if (p_187245_2_ <= 0) {
            b0 = 24;
         } else if (p_187245_2_ >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + p_187245_2_);
         }

         p_187245_1_.connection.send(new SEntityStatusPacket(p_187245_1_, b0));
      }

      this.server.getCommands().sendCommands(p_187245_1_);
   }

   public boolean isWhiteListed(GameProfile p_152607_1_) {
      return !this.doWhiteList || this.ops.contains(p_152607_1_) || this.whitelist.contains(p_152607_1_);
   }

   public boolean isOp(GameProfile p_152596_1_) {
      return this.ops.contains(p_152596_1_) || this.server.isSingleplayerOwner(p_152596_1_) && this.server.getLevel(DimensionType.field_223227_a_).getLevelData().getAllowCommands() || this.allowCheatsForAllPlayers;
   }

   @Nullable
   public ServerPlayerEntity getPlayerByName(String p_152612_1_) {
      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getGameProfile().getName().equalsIgnoreCase(p_152612_1_)) {
            return serverplayerentity;
         }
      }

      return null;
   }

   public void broadcast(@Nullable PlayerEntity p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, DimensionType p_148543_10_, IPacket<?> p_148543_11_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity != p_148543_1_ && serverplayerentity.field_71093_bK == p_148543_10_) {
            double d0 = p_148543_2_ - serverplayerentity.getX();
            double d1 = p_148543_4_ - serverplayerentity.getY();
            double d2 = p_148543_6_ - serverplayerentity.getZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < p_148543_8_ * p_148543_8_) {
               serverplayerentity.connection.send(p_148543_11_);
            }
         }
      }

   }

   public void saveAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.save(this.players.get(i));
      }

   }

   public WhiteList getWhiteList() {
      return this.whitelist;
   }

   public String[] getWhiteListNames() {
      return this.whitelist.getUserList();
   }

   public OpList getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getUserList();
   }

   public void reloadWhiteList() {
   }

   public void sendLevelInfo(ServerPlayerEntity p_72354_1_, ServerWorld p_72354_2_) {
      WorldBorder worldborder = this.server.getLevel(DimensionType.field_223227_a_).getWorldBorder();
      p_72354_1_.connection.send(new SWorldBorderPacket(worldborder, SWorldBorderPacket.Action.INITIALIZE));
      p_72354_1_.connection.send(new SUpdateTimePacket(p_72354_2_.getGameTime(), p_72354_2_.getDayTime(), p_72354_2_.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      BlockPos blockpos = p_72354_2_.func_175694_M();
      p_72354_1_.connection.send(new SSpawnPositionPacket(blockpos));
      if (p_72354_2_.isRaining()) {
         p_72354_1_.connection.send(new SChangeGameStatePacket(1, 0.0F));
         p_72354_1_.connection.send(new SChangeGameStatePacket(7, p_72354_2_.getRainLevel(1.0F)));
         p_72354_1_.connection.send(new SChangeGameStatePacket(8, p_72354_2_.getThunderLevel(1.0F)));
      }

   }

   public void sendAllPlayerInfo(ServerPlayerEntity p_72385_1_) {
      p_72385_1_.refreshContainer(p_72385_1_.inventoryMenu);
      p_72385_1_.resetSentInfo();
      p_72385_1_.connection.send(new SHeldItemChangePacket(p_72385_1_.inventory.selected));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isUsingWhitelist() {
      return this.doWhiteList;
   }

   public void setUsingWhiteList(boolean p_72371_1_) {
      this.doWhiteList = p_72371_1_;
   }

   public List<ServerPlayerEntity> getPlayersWithAddress(String p_72382_1_) {
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getIpAddress().equals(p_72382_1_)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public CompoundNBT getSingleplayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setOverrideGameMode(GameType p_152604_1_) {
      this.overrideGameMode = p_152604_1_;
   }

   private void updatePlayerGameMode(ServerPlayerEntity p_72381_1_, ServerPlayerEntity p_72381_2_, IWorld p_72381_3_) {
      if (p_72381_2_ != null) {
         p_72381_1_.gameMode.setGameModeForPlayer(p_72381_2_.gameMode.getGameModeForPlayer());
      } else if (this.overrideGameMode != null) {
         p_72381_1_.gameMode.setGameModeForPlayer(this.overrideGameMode);
      }

      p_72381_1_.gameMode.updateGameMode(p_72381_3_.getLevelData().getGameType());
   }

   @OnlyIn(Dist.CLIENT)
   public void setAllowCheatsForAllPlayers(boolean p_72387_1_) {
      this.allowCheatsForAllPlayers = p_72387_1_;
   }

   public void removeAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void func_148544_a(ITextComponent p_148544_1_, boolean p_148544_2_) {
      this.server.sendMessage(p_148544_1_);
      ChatType chattype = p_148544_2_ ? ChatType.SYSTEM : ChatType.CHAT;
      this.broadcastAll(new SChatPacket(p_148544_1_, chattype));
   }

   public void func_148539_a(ITextComponent p_148539_1_) {
      this.func_148544_a(p_148539_1_, true);
   }

   public ServerStatisticsManager getPlayerStats(PlayerEntity p_152602_1_) {
      UUID uuid = p_152602_1_.getUUID();
      ServerStatisticsManager serverstatisticsmanager = uuid == null ? null : this.stats.get(uuid);
      if (serverstatisticsmanager == null) {
         File file1 = new File(this.server.getLevel(DimensionType.field_223227_a_).func_217485_w().func_75765_b(), "stats");
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, p_152602_1_.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         serverstatisticsmanager = new ServerStatisticsManager(this.server, file2);
         this.stats.put(uuid, serverstatisticsmanager);
      }

      return serverstatisticsmanager;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayerEntity p_192054_1_) {
      UUID uuid = p_192054_1_.getUUID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = new File(this.server.getLevel(DimensionType.field_223227_a_).func_217485_w().func_75765_b(), "advancements");
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server, file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int p_217884_1_) {
      this.viewDistance = p_217884_1_;
      this.broadcastAll(new SUpdateViewDistancePacket(p_217884_1_));

      for(ServerWorld serverworld : this.server.getAllLevels()) {
         if (serverworld != null) {
            serverworld.getChunkSource().setViewDistance(p_217884_1_);
         }
      }

   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.playersView; //Unmodifiable view, we don't want people removing things without us knowing.
   }

   @Nullable
   public ServerPlayerEntity getPlayer(UUID p_177451_1_) {
      return this.playersByUUID.get(p_177451_1_);
   }

   public boolean canBypassPlayerLimit(GameProfile p_183023_1_) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.func_193766_b();
      }

      this.broadcastAll(new STagsListPacket(this.server.func_199731_aO()));
      SUpdateRecipesPacket supdaterecipespacket = new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

      for(ServerPlayerEntity serverplayerentity : this.players) {
         serverplayerentity.connection.send(supdaterecipespacket);
         serverplayerentity.getRecipeBook().sendInitialRecipeBook(serverplayerentity);
      }

   }

   public boolean isAllowCheatsForAllPlayers() {
      return this.allowCheatsForAllPlayers;
   }

   public boolean addPlayer(ServerPlayerEntity player) {
      return net.minecraftforge.common.DimensionManager.rebuildPlayerMap(this, this.players.add(player));
   }

   public boolean removePlayer(ServerPlayerEntity player) {
       return net.minecraftforge.common.DimensionManager.rebuildPlayerMap(this, this.players.remove(player));
   }
}
