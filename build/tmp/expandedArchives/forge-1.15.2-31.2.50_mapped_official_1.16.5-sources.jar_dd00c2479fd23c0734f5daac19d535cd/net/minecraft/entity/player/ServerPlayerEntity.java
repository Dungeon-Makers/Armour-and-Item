package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String field_71148_cg = "en_US";
   public ServerPlayNetHandler connection;
   public final MinecraftServer server;
   public final PlayerInteractionManager gameMode;
   private final List<Integer> entitiesToRemove = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatisticsManager stats;
   private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
   private int lastRecordedFoodLevel = Integer.MIN_VALUE;
   private int lastRecordedAirLevel = Integer.MIN_VALUE;
   private int lastRecordedArmor = Integer.MIN_VALUE;
   private int lastRecordedLevel = Integer.MIN_VALUE;
   private int lastRecordedExperience = Integer.MIN_VALUE;
   private float lastSentHealth = -1.0E8F;
   private int lastSentFood = -99999999;
   private boolean lastFoodSaturationZero = true;
   private int lastSentExp = -99999999;
   private int spawnInvulnerableTime = 60;
   private ChatVisibility chatVisibility;
   private boolean canChatColor = true;
   private long lastActionTime = Util.getMillis();
   private Entity camera;
   private boolean isChangingDimension;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3d levitationStartPos;
   private int levitationStartTime;
   private boolean disconnected;
   @Nullable
   private Vec3d enteredNetherPosition;
   private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
   public int containerCounter;
   public boolean ignoreSlotUpdateHack;
   public int latency;
   public boolean wonGame;

   public ServerPlayerEntity(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_) {
      super(p_i45285_2_, p_i45285_3_);
      p_i45285_4_.player = this;
      this.gameMode = p_i45285_4_;
      this.server = p_i45285_1_;
      this.recipeBook = new ServerRecipeBook(p_i45285_1_.getRecipeManager());
      this.stats = p_i45285_1_.getPlayerList().getPlayerStats(this);
      this.advancements = p_i45285_1_.getPlayerList().getPlayerAdvancements(this);
      this.maxUpStep = 1.0F;
      this.fudgeSpawnLocation(p_i45285_2_);
   }

   private void fudgeSpawnLocation(ServerWorld p_205734_1_) {
      BlockPos blockpos = p_205734_1_.func_175694_M();
      if (p_205734_1_.dimension.func_191066_m() && p_205734_1_.getLevelData().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(p_205734_1_));
         int j = MathHelper.floor(p_205734_1_.getWorldBorder().getDistanceToBorder((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         long k = (long)(i * 2 + 1);
         long l = k * k;
         int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int)l;
         int j1 = this.getCoprime(i1);
         int k1 = (new Random()).nextInt(i1);

         for(int l1 = 0; l1 < i1; ++l1) {
            int i2 = (k1 + j1 * l1) % i1;
            int j2 = i2 % (i * 2 + 1);
            int k2 = i2 / (i * 2 + 1);
            BlockPos blockpos1 = p_205734_1_.func_201675_m().func_206921_a(blockpos.getX() + j2 - i, blockpos.getZ() + k2 - i, false);
            if (blockpos1 != null) {
               this.moveTo(blockpos1, 0.0F, 0.0F);
               if (p_205734_1_.noCollision(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveTo(blockpos, 0.0F, 0.0F);

         while(!p_205734_1_.noCollision(this) && this.getY() < 255.0D) {
            this.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
         }
      }

   }

   private int getCoprime(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("playerGameType", 99)) {
         if (this.getServer().getForceGameType()) {
            this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType());
         } else {
            this.gameMode.setGameModeForPlayer(GameType.byId(p_70037_1_.getInt("playerGameType")));
         }
      }

      if (p_70037_1_.contains("enteredNetherPosition", 10)) {
         CompoundNBT compoundnbt = p_70037_1_.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3d(compoundnbt.getDouble("x"), compoundnbt.getDouble("y"), compoundnbt.getDouble("z"));
      }

      this.seenCredits = p_70037_1_.getBoolean("seenCredits");
      if (p_70037_1_.contains("recipeBook", 10)) {
         this.recipeBook.fromNbt(p_70037_1_.getCompound("recipeBook"));
      }

      if (this.isSleeping()) {
         this.stopSleeping();
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
      p_213281_1_.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putDouble("x", this.enteredNetherPosition.x);
         compoundnbt.putDouble("y", this.enteredNetherPosition.y);
         compoundnbt.putDouble("z", this.enteredNetherPosition.z);
         p_213281_1_.put("enteredNetherPosition", compoundnbt);
      }

      Entity entity1 = this.getRootVehicle();
      Entity entity = this.getVehicle();
      if (entity != null && entity1 != this && entity1.hasOnePlayerPassenger()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         CompoundNBT compoundnbt2 = new CompoundNBT();
         entity1.save(compoundnbt2);
         compoundnbt1.putUUID("Attach", entity.getUUID());
         compoundnbt1.put("Entity", compoundnbt2);
         p_213281_1_.put("RootVehicle", compoundnbt1);
      }

      p_213281_1_.put("recipeBook", this.recipeBook.toNbt());
   }

   public void setExperiencePoints(int p_195394_1_) {
      float f = (float)this.getXpNeededForNextLevel();
      float f1 = (f - 1.0F) / f;
      this.experienceProgress = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastSentExp = -1;
   }

   public void setExperienceLevels(int p_195399_1_) {
      this.experienceLevel = p_195399_1_;
      this.lastSentExp = -1;
   }

   public void giveExperienceLevels(int p_82242_1_) {
      super.giveExperienceLevels(p_82242_1_);
      this.lastSentExp = -1;
   }

   public void onEnchantmentPerformed(ItemStack p_192024_1_, int p_192024_2_) {
      super.onEnchantmentPerformed(p_192024_1_, p_192024_2_);
      this.lastSentExp = -1;
   }

   public void initMenu() {
      this.containerMenu.addSlotListener(this);
   }

   public void onEnterCombat() {
      super.onEnterCombat();
      this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTER_COMBAT));
   }

   public void onLeaveCombat() {
      super.onLeaveCombat();
      this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createItemCooldowns() {
      return new ServerCooldownTracker(this);
   }

   public void tick() {
      this.gameMode.tick();
      --this.spawnInvulnerableTime;
      if (this.invulnerableTime > 0) {
         --this.invulnerableTime;
      }

      this.containerMenu.broadcastChanges();
      if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      while(!this.entitiesToRemove.isEmpty()) {
         int i = Math.min(this.entitiesToRemove.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entitiesToRemove.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = iterator.next();
            iterator.remove();
         }

         this.connection.send(new SDestroyEntitiesPacket(aint));
      }

      Entity entity = this.getCamera();
      if (entity != this) {
         if (entity.isAlive()) {
            this.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
            this.getLevel().getChunkSource().move(this);
            if (this.wantsToStopRiding()) {
               this.setCamera(this);
            }
         } else {
            this.setCamera(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
      }

      this.advancements.flushDirty(this);
   }

   public void doTick() {
      try {
         if (!this.isSpectator() || this.level.hasChunkAt(new BlockPos(this))) {
            super.tick();
         }

         for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.getItem().isComplex()) {
               IPacket<?> ipacket = ((AbstractMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.level, this);
               if (ipacket != null) {
                  this.connection.send(ipacket);
               }
            }
         }

         if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
            this.connection.send(new SUpdateHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
            this.lastSentHealth = this.getHealth();
            this.lastSentFood = this.foodData.getFoodLevel();
            this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
            this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
            this.updateScoreForCriteria(ScoreCriteria.HEALTH, MathHelper.ceil(this.lastRecordedHealthAndAbsorption));
         }

         if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
            this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
            this.updateScoreForCriteria(ScoreCriteria.FOOD, MathHelper.ceil((float)this.lastRecordedFoodLevel));
         }

         if (this.getAirSupply() != this.lastRecordedAirLevel) {
            this.lastRecordedAirLevel = this.getAirSupply();
            this.updateScoreForCriteria(ScoreCriteria.AIR, MathHelper.ceil((float)this.lastRecordedAirLevel));
         }

         if (this.getArmorValue() != this.lastRecordedArmor) {
            this.lastRecordedArmor = this.getArmorValue();
            this.updateScoreForCriteria(ScoreCriteria.ARMOR, MathHelper.ceil((float)this.lastRecordedArmor));
         }

         if (this.totalExperience != this.lastRecordedExperience) {
            this.lastRecordedExperience = this.totalExperience;
            this.updateScoreForCriteria(ScoreCriteria.EXPERIENCE, MathHelper.ceil((float)this.lastRecordedExperience));
         }

         if (this.experienceLevel != this.lastRecordedLevel) {
            this.lastRecordedLevel = this.experienceLevel;
            this.updateScoreForCriteria(ScoreCriteria.LEVEL, MathHelper.ceil((float)this.lastRecordedLevel));
         }

         if (this.totalExperience != this.lastSentExp) {
            this.lastSentExp = this.totalExperience;
            this.connection.send(new SSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
         }

         if (this.tickCount % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Player being ticked");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void updateScoreForCriteria(ScoreCriteria p_184849_1_, int p_184849_2_) {
      this.getScoreboard().forAllObjectives(p_184849_1_, this.getScoreboardName(), (p_195397_1_) -> {
         p_195397_1_.setScore(p_184849_2_);
      });
   }

   public void die(DamageSource p_70645_1_) {
      if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, p_70645_1_)) return;
      boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
      if (flag) {
         ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
         this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {
            if (!p_212356_2_.isSuccess()) {
               int i = 256;
               String s = itextcomponent.getString(256);
               ITextComponent itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long", (new StringTextComponent(s)).func_211708_a(TextFormatting.YELLOW));
               ITextComponent itextcomponent2 = (new TranslationTextComponent("death.attack.even_more_magic", this.getDisplayName())).func_211710_a((p_212357_1_) -> {
                  p_212357_1_.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
               });
               this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
            }

         });
         Team team = this.getTeam();
         if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().broadcastToTeam(this, itextcomponent);
            } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().broadcastToAllExceptTeam(this, itextcomponent);
            }
         } else {
            this.server.getPlayerList().func_148539_a(itextcomponent);
         }
      } else {
         this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED));
      }

      this.removeEntitiesOnShoulder();
      if (!this.isSpectator()) {
         this.dropAllDeathLoot(p_70645_1_);
      }

      this.getScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, this.getScoreboardName(), Score::increment);
      LivingEntity livingentity = this.getKillCredit();
      if (livingentity != null) {
         this.awardStat(Stats.ENTITY_KILLED_BY.get(livingentity.getType()));
         livingentity.awardKillScore(this, this.deathScore, p_70645_1_);
         this.createWitherRose(livingentity);
      }

      this.level.broadcastEntityEvent(this, (byte)3);
      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
      this.getCombatTracker().recheckStatus();
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.increaseScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getScoreboard().forAllObjectives(ScoreCriteria.KILL_COUNT_ALL, s, Score::increment);
         if (p_191956_1_ instanceof PlayerEntity) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ScoreCriteria.KILL_COUNT_PLAYERS, s, Score::increment);
         } else {
            this.awardStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(s, s1, ScoreCriteria.TEAM_KILL);
         this.handleTeamKill(s1, s, ScoreCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }
   }

   private void handleTeamKill(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getId();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getScoreboard().forAllObjectives(p_195398_3_[i], p_195398_1_, Score::increment);
         }
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(p_70097_1_.msgId);
         if (!flag && this.spawnInvulnerableTime > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (p_70097_1_ instanceof EntityDamageSource) {
               Entity entity = p_70097_1_.getEntity();
               if (entity instanceof PlayerEntity && !this.canHarmPlayer((PlayerEntity)entity)) {
                  return false;
               }

               if (entity instanceof AbstractArrowEntity) {
                  AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
                  Entity entity1 = abstractarrowentity.func_212360_k();
                  if (entity1 instanceof PlayerEntity && !this.canHarmPlayer((PlayerEntity)entity1)) {
                     return false;
                  }
               }
            }

            return super.hurt(p_70097_1_, p_70097_2_);
         }
      }
   }

   public boolean canHarmPlayer(PlayerEntity p_96122_1_) {
      return !this.isPvpAllowed() ? false : super.canHarmPlayer(p_96122_1_);
   }

   private boolean isPvpAllowed() {
      return this.server.isPvpAllowed();
   }

   @Override
   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_212321_1_)) return null;
      this.isChangingDimension = true;
      DimensionType dimensiontype = this.field_71093_bK;
      if (dimensiontype == DimensionType.field_223229_c_ && p_212321_1_ == DimensionType.field_223227_a_ && teleporter instanceof net.minecraft.world.Teleporter) { //Forge: Fix non-vanilla teleporters triggering end credits
         this.unRide();
         this.getLevel().removePlayer(this, true); //Forge: The player entity is cloned so keep the data until after cloning calls copyFrom
         if (!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new SChangeGameStatePacket(4, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         ServerWorld serverworld = this.server.getLevel(dimensiontype);
         this.field_71093_bK = p_212321_1_;
         ServerWorld serverworld1 = this.server.getLevel(p_212321_1_);
         WorldInfo worldinfo = serverworld1.getLevelData();
         net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(this.connection.connection, this);
         this.connection.send(new SRespawnPacket(p_212321_1_, WorldInfo.func_227498_c_(worldinfo.func_76063_b()), worldinfo.func_76067_t(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
         PlayerList playerlist = this.server.getPlayerList();
         playerlist.sendPlayerPermissionLevel(this);
         serverworld.removeEntity(this, true); //Forge: the player entity is moved to the new world, NOT cloned. So keep the data alive with no matching invalidate call.
         this.revive();
         Entity e = teleporter.placeEntity(this, serverworld, serverworld1, this.yRot, spawnPortal -> {//Forge: Start vanilla logic
         double d0 = this.getX();
         double d1 = this.getY();
         double d2 = this.getZ();
         float f = this.xRot;
         float f1 = this.yRot;
         double d3 = 8.0D;
         float f2 = f1;
         serverworld.getProfiler().push("moving");
         double moveFactor = serverworld.func_201675_m().getMovementFactor() / serverworld1.func_201675_m().getMovementFactor();
         d0 *= moveFactor;
         d2 *= moveFactor;
         if (dimensiontype == DimensionType.field_223227_a_ && p_212321_1_ == DimensionType.field_223228_b_) {
            this.enteredNetherPosition = this.position();
         } else if (dimensiontype == DimensionType.field_223227_a_ && p_212321_1_ == DimensionType.field_223229_c_) {
            BlockPos blockpos = serverworld1.func_180504_m();
            d0 = (double)blockpos.getX();
            d1 = (double)blockpos.getY();
            d2 = (double)blockpos.getZ();
            f1 = 90.0F;
            f = 0.0F;
         }

         this.moveTo(d0, d1, d2, f1, f);
         serverworld.getProfiler().pop();
         serverworld.getProfiler().push("placing");
         double d7 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().getMinX() + 16.0D);
         double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().getMinZ() + 16.0D);
         double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().getMaxX() - 16.0D);
         double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().getMaxZ() - 16.0D);
         d0 = MathHelper.clamp(d0, d7, d5);
         d2 = MathHelper.clamp(d2, d4, d6);
         this.moveTo(d0, d1, d2, f1, f);
         if (p_212321_1_ == DimensionType.field_223229_c_) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY()) - 1;
            int k = MathHelper.floor(this.getZ());
            int l = 1;
            int i1 = 0;

            for(int j1 = -2; j1 <= 2; ++j1) {
               for(int k1 = -2; k1 <= 2; ++k1) {
                  for(int l1 = -1; l1 < 3; ++l1) {
                     int i2 = i + k1 * 1 + j1 * 0;
                     int j2 = j + l1;
                     int k2 = k + k1 * 0 - j1 * 1;
                     boolean flag = l1 < 0;
                     serverworld1.setBlockAndUpdate(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
                  }
               }
            }

            this.moveTo((double)i, (double)j, (double)k, f1, 0.0F);
            this.setDeltaMovement(Vec3d.ZERO);
         } else if (spawnPortal && !serverworld1.getPortalForcer().func_222268_a(this, f2)) {
            serverworld1.getPortalForcer().func_85188_a(this);
            serverworld1.getPortalForcer().func_222268_a(this, f2);
         }

         serverworld.getProfiler().pop();
         this.setLevel(serverworld1);
         serverworld1.addDuringPortalTeleport(this);
         this.triggerDimensionChangeTriggers(serverworld);
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), f1, f);
         return this;//forge: this is part of the ITeleporter patch
         });//Forge: End vanilla logic
         if (e != this) throw new java.lang.IllegalArgumentException(String.format("Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
         this.gameMode.setLevel(serverworld1);
         this.connection.send(new SPlayerAbilitiesPacket(this.abilities));
         playerlist.sendLevelInfo(this, serverworld1);
         playerlist.sendAllPlayerInfo(this);

         for(EffectInstance effectinstance : this.getActiveEffects()) {
            this.connection.send(new SPlayEntityEffectPacket(this.getId(), effectinstance));
         }

         this.connection.send(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
         this.lastSentExp = -1;
         this.lastSentHealth = -1.0F;
         this.lastSentFood = -1;
         net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, dimensiontype, p_212321_1_);
         return this;
      }
   }

   private void triggerDimensionChangeTriggers(ServerWorld p_213846_1_) {
      DimensionType dimensiontype = p_213846_1_.dimension.func_186058_p();
      DimensionType dimensiontype1 = this.level.dimension.func_186058_p();
      CriteriaTriggers.CHANGED_DIMENSION.func_193143_a(this, dimensiontype, dimensiontype1);
      if (dimensiontype == DimensionType.field_223228_b_ && dimensiontype1 == DimensionType.field_223227_a_ && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (dimensiontype1 != DimensionType.field_223228_b_) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean broadcastToPlayer(ServerPlayerEntity p_174827_1_) {
      if (p_174827_1_.isSpectator()) {
         return this.getCamera() == this;
      } else {
         return this.isSpectator() ? false : super.broadcastToPlayer(p_174827_1_);
      }
   }

   private void broadcast(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = p_147097_1_.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.connection.send(supdatetileentitypacket);
         }
      }

   }

   public void take(Entity p_71001_1_, int p_71001_2_) {
      super.take(p_71001_1_, p_71001_2_);
      this.containerMenu.broadcastChanges();
   }

   public Either<PlayerEntity.SleepResult, Unit> startSleepInBed(BlockPos p_213819_1_) {
      return super.startSleepInBed(p_213819_1_).ifRight((p_213849_1_) -> {
         this.awardStat(Stats.SLEEP_IN_BED);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      });
   }

   public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
      if (this.isSleeping()) {
         this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(this, 2));
      }

      super.stopSleepInBed(p_225652_1_, p_225652_2_);
      if (this.connection != null) {
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      Entity entity = this.getVehicle();
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         Entity entity1 = this.getVehicle();
         if (entity1 != entity && this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
         }

         return true;
      }
   }

   public void stopRiding() {
      Entity entity = this.getVehicle();
      super.stopRiding();
      Entity entity1 = this.getVehicle();
      if (entity1 != entity && this.connection != null) {
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return super.isInvulnerableTo(p_180431_1_) || this.isChangingDimension() || this.abilities.invulnerable && p_180431_1_ == DamageSource.WITHER;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   protected void onChangedBlock(BlockPos p_184594_1_) {
      if (!this.isSpectator()) {
         super.onChangedBlock(p_184594_1_);
      }

   }

   public void doCheckFallDamage(double p_71122_1_, boolean p_71122_3_) {
      BlockPos blockpos = this.getOnPos();
      if (this.level.hasChunkAt(blockpos)) {
         BlockState blockstate = this.level.getBlockState(blockpos);
         super.checkFallDamage(p_71122_1_, p_71122_3_, blockstate, blockpos);
      }
   }

   public void openTextEdit(SignTileEntity p_175141_1_) {
      p_175141_1_.setAllowedPlayerEditor(this);
      this.connection.send(new SOpenSignMenuPacket(p_175141_1_.getBlockPos()));
   }

   public void nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
   }

   public OptionalInt openMenu(@Nullable INamedContainerProvider p_213829_1_) {
      if (p_213829_1_ == null) {
         return OptionalInt.empty();
      } else {
         if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
         }

         this.nextContainerCounter();
         Container container = p_213829_1_.createMenu(this.containerCounter, this.inventory, this);
         if (container == null) {
            if (this.isSpectator()) {
               this.displayClientMessage((new TranslationTextComponent("container.spectatorCantOpen")).func_211708_a(TextFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.send(new SOpenWindowPacket(container.containerId, container.getType(), p_213829_1_.getDisplayName()));
            container.addSlotListener(this);
            this.containerMenu = container;
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
            return OptionalInt.of(this.containerCounter);
         }
      }
   }

   public void sendMerchantOffers(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
      this.connection.send(new SMerchantOffersPacket(p_213818_1_, p_213818_2_, p_213818_3_, p_213818_4_, p_213818_5_, p_213818_6_));
   }

   public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
      if (this.containerMenu != this.inventoryMenu) {
         this.closeContainer();
      }

      this.nextContainerCounter();
      this.connection.send(new SOpenHorseWindowPacket(this.containerCounter, p_184826_2_.getContainerSize(), p_184826_1_.getId()));
      this.containerMenu = new HorseInventoryContainer(this.containerCounter, this.inventory, p_184826_2_, p_184826_1_);
      this.containerMenu.addSlotListener(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
   }

   public void openItemGui(ItemStack p_184814_1_, Hand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITTEN_BOOK) {
         if (WrittenBookItem.resolveBookComponents(p_184814_1_, this.createCommandSourceStack(), this)) {
            this.containerMenu.broadcastChanges();
         }

         this.connection.send(new SOpenBookWindowPacket(p_184814_2_));
      }

   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
      p_184824_1_.setSendToClient(true);
      this.broadcast(p_184824_1_);
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (!(p_71111_1_.getSlot(p_71111_2_) instanceof CraftingResultSlot)) {
         if (p_71111_1_ == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.func_192208_a(this, this.inventory);
         }

         if (!this.ignoreSlotUpdateHack) {
            this.connection.send(new SSetSlotPacket(p_71111_1_.containerId, p_71111_2_, p_71111_3_));
         }
      }
   }

   public void refreshContainer(Container p_71120_1_) {
      this.refreshContainer(p_71120_1_, p_71120_1_.getItems());
   }

   public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.connection.send(new SWindowItemsPacket(p_71110_1_.containerId, p_71110_2_));
      this.connection.send(new SSetSlotPacket(-1, -1, this.inventory.getCarried()));
   }

   public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
      this.connection.send(new SWindowPropertyPacket(p_71112_1_.containerId, p_71112_2_, p_71112_3_));
   }

   public void closeContainer() {
      this.connection.send(new SCloseWindowPacket(this.containerMenu.containerId));
      this.doCloseContainer();
   }

   public void broadcastCarriedItem() {
      if (!this.ignoreSlotUpdateHack) {
         this.connection.send(new SSetSlotPacket(-1, -1, this.inventory.getCarried()));
      }
   }

   public void doCloseContainer() {
      this.containerMenu.removed(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Close(this, this.containerMenu));
      this.containerMenu = this.inventoryMenu;
   }

   public void setPlayerInput(float p_110430_1_, float p_110430_2_, boolean p_110430_3_, boolean p_110430_4_) {
      if (this.isPassenger()) {
         if (p_110430_1_ >= -1.0F && p_110430_1_ <= 1.0F) {
            this.xxa = p_110430_1_;
         }

         if (p_110430_2_ >= -1.0F && p_110430_2_ <= 1.0F) {
            this.zza = p_110430_2_;
         }

         this.jumping = p_110430_3_;
         this.setShiftKeyDown(p_110430_4_);
      }

   }

   public void awardStat(Stat<?> p_71064_1_, int p_71064_2_) {
      this.stats.increment(this, p_71064_1_, p_71064_2_);
      this.getScoreboard().forAllObjectives(p_71064_1_, this.getScoreboardName(), (p_195396_1_) -> {
         p_195396_1_.add(p_71064_2_);
      });
   }

   public void resetStat(Stat<?> p_175145_1_) {
      this.stats.setValue(this, p_175145_1_, 0);
      this.getScoreboard().forAllObjectives(p_175145_1_, this.getScoreboardName(), Score::reset);
   }

   public int awardRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return this.recipeBook.addRecipes(p_195065_1_, this);
   }

   public void awardRecipesByKey(ResourceLocation[] p_193102_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : p_193102_1_) {
         this.server.getRecipeManager().byKey(resourcelocation).ifPresent(list::add);
      }

      this.awardRecipes(list);
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return this.recipeBook.removeRecipes(p_195069_1_, this);
   }

   public void giveExperiencePoints(int p_195068_1_) {
      super.giveExperiencePoints(p_195068_1_);
      this.lastSentExp = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.ejectPassengers();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void resetSentInfo() {
      this.lastSentHealth = -1.0E8F;
   }

   public void displayClientMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      this.connection.send(new SChatPacket(p_146105_1_, p_146105_2_ ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   protected void completeUsingItem() {
      if (!this.useItem.isEmpty() && this.isUsingItem()) {
         this.connection.send(new SEntityStatusPacket(this, (byte)9));
         super.completeUsingItem();
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.connection.send(new SPlayerLookPacket(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void lookAt(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vec3d vec3d = p_200618_3_.apply(p_200618_2_);
      super.lookAt(p_200618_1_, vec3d);
      this.connection.send(new SPlayerLookPacket(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void restoreFrom(ServerPlayerEntity p_193104_1_, boolean p_193104_2_) {
      if (p_193104_2_) {
         this.inventory.replaceWith(p_193104_1_.inventory);
         this.setHealth(p_193104_1_.getHealth());
         this.foodData = p_193104_1_.foodData;
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.totalExperience = p_193104_1_.totalExperience;
         this.experienceProgress = p_193104_1_.experienceProgress;
         this.setScore(p_193104_1_.getScore());
         this.field_181016_an = p_193104_1_.field_181016_an;
         this.field_181017_ao = p_193104_1_.field_181017_ao;
         this.field_181018_ap = p_193104_1_.field_181018_ap;
      } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || p_193104_1_.isSpectator()) {
         this.inventory.replaceWith(p_193104_1_.inventory);
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.totalExperience = p_193104_1_.totalExperience;
         this.experienceProgress = p_193104_1_.experienceProgress;
         this.setScore(p_193104_1_.getScore());
      }

      this.enchantmentSeed = p_193104_1_.enchantmentSeed;
      this.enderChestInventory = p_193104_1_.enderChestInventory;
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, p_193104_1_.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
      this.lastSentExp = -1;
      this.lastSentHealth = -1.0F;
      this.lastSentFood = -1;
      this.recipeBook.copyOverData(p_193104_1_.recipeBook);
      this.entitiesToRemove.addAll(p_193104_1_.entitiesToRemove);
      this.seenCredits = p_193104_1_.seenCredits;
      this.enteredNetherPosition = p_193104_1_.enteredNetherPosition;
      this.setShoulderEntityLeft(p_193104_1_.getShoulderEntityLeft());
      this.setShoulderEntityRight(p_193104_1_.getShoulderEntityRight());

      this.spawnPosMap = p_193104_1_.spawnPosMap;
      this.spawnForcedMap = p_193104_1_.spawnForcedMap;
      if(p_193104_1_.field_71093_bK != DimensionType.field_223227_a_) {
          this.field_71077_c = p_193104_1_.field_71077_c;
          this.field_82248_d = p_193104_1_.field_82248_d;
      }

      //Copy over a section of the Entity Data from the old player.
      //Allows mods to specify data that persists after players respawn.
      CompoundNBT old = p_193104_1_.getPersistentData();
      if (old.contains(PERSISTED_NBT_TAG))
          getPersistentData().put(PERSISTED_NBT_TAG, old.get(PERSISTED_NBT_TAG));
      net.minecraftforge.event.ForgeEventFactory.onPlayerClone(this, p_193104_1_, !p_193104_2_);
   }

   protected void onEffectAdded(EffectInstance p_70670_1_) {
      super.onEffectAdded(p_70670_1_);
      this.connection.send(new SPlayEntityEffectPacket(this.getId(), p_70670_1_));
      if (p_70670_1_.getEffect() == Effects.LEVITATION) {
         this.levitationStartTime = this.tickCount;
         this.levitationStartPos = this.position();
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectUpdated(EffectInstance p_70695_1_, boolean p_70695_2_) {
      super.onEffectUpdated(p_70695_1_, p_70695_2_);
      this.connection.send(new SPlayEntityEffectPacket(this.getId(), p_70695_1_));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectRemoved(EffectInstance p_70688_1_) {
      super.onEffectRemoved(p_70688_1_);
      this.connection.send(new SRemoveEntityEffectPacket(this.getId(), p_70688_1_.getEffect()));
      if (p_70688_1_.getEffect() == Effects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      this.connection.teleport(p_70634_1_, p_70634_3_, p_70634_5_, this.yRot, this.xRot);
   }

   public void moveTo(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.connection.teleport(p_225653_1_, p_225653_3_, p_225653_5_, this.yRot, this.xRot);
      this.connection.resetPosition();
   }

   public void crit(Entity p_71009_1_) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(p_71009_1_, 4));
   }

   public void magicCrit(Entity p_71047_1_) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(p_71047_1_, 5));
   }

   public void onUpdateAbilities() {
      if (this.connection != null) {
         this.connection.send(new SPlayerAbilitiesPacket(this.abilities));
         this.updateInvisibilityStatus();
      }
   }

   public ServerWorld getLevel() {
      return (ServerWorld)this.level;
   }

   public void setGameMode(GameType p_71033_1_) {
      this.gameMode.setGameModeForPlayer(p_71033_1_);
      this.connection.send(new SChangeGameStatePacket(3, (float)p_71033_1_.getId()));
      if (p_71033_1_ == GameType.SPECTATOR) {
         this.removeEntitiesOnShoulder();
         this.stopRiding();
      } else {
         this.setCamera(this);
      }

      this.onUpdateAbilities();
      this.updateEffectVisibility();
   }

   public boolean isSpectator() {
      return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.func_195395_a(p_145747_1_, ChatType.SYSTEM);
   }

   public void func_195395_a(ITextComponent p_195395_1_, ChatType p_195395_2_) {
      this.connection.send(new SChatPacket(p_195395_1_, p_195395_2_), (p_211144_3_) -> {
         if (!p_211144_3_.isSuccess() && (p_195395_2_ == ChatType.GAME_INFO || p_195395_2_ == ChatType.SYSTEM)) {
            int i = 256;
            String s = p_195395_1_.getString(256);
            ITextComponent itextcomponent = (new StringTextComponent(s)).func_211708_a(TextFormatting.YELLOW);
            this.connection.send(new SChatPacket((new TranslationTextComponent("multiplayer.message_not_delivered", itextcomponent)).func_211708_a(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String getIpAddress() {
      String s = this.connection.connection.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      s = s.substring(0, s.indexOf(":"));
      return s;
   }

   public void updateOptions(CClientSettingsPacket p_147100_1_) {
      this.field_71148_cg = p_147100_1_.getLanguage();
      this.chatVisibility = p_147100_1_.getChatVisibility();
      this.canChatColor = p_147100_1_.getChatColors();
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)p_147100_1_.getModelCustomisation());
      this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)(p_147100_1_.getMainHand() == HandSide.LEFT ? 0 : 1));
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void sendTexturePack(String p_175397_1_, String p_175397_2_) {
      this.connection.send(new SSendResourcePackPacket(p_175397_1_, p_175397_2_));
   }

   protected int getPermissionLevel() {
      return this.server.getProfilePermissions(this.getGameProfile());
   }

   public void resetLastActionTime() {
      this.lastActionTime = Util.getMillis();
   }

   public ServerStatisticsManager getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void sendRemoveEntity(Entity p_152339_1_) {
      if (p_152339_1_ instanceof PlayerEntity) {
         this.connection.send(new SDestroyEntitiesPacket(p_152339_1_.getId()));
      } else {
         this.entitiesToRemove.add(p_152339_1_.getId());
      }

   }

   public void cancelRemoveEntity(Entity p_184848_1_) {
      this.entitiesToRemove.remove(Integer.valueOf(p_184848_1_.getId()));
   }

   protected void updateInvisibilityStatus() {
      if (this.isSpectator()) {
         this.removeEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateInvisibilityStatus();
      }

   }

   public Entity getCamera() {
      return (Entity)(this.camera == null ? this : this.camera);
   }

   public void setCamera(Entity p_175399_1_) {
      Entity entity = this.getCamera();
      this.camera = (Entity)(p_175399_1_ == null ? this : p_175399_1_);
      if (entity != this.camera) {
         this.connection.send(new SCameraPacket(this.camera));
         this.teleportTo(this.camera.getX(), this.camera.getY(), this.camera.getZ());
      }

   }

   protected void processPortalCooldown() {
      if (this.field_71088_bW > 0 && !this.isChangingDimension) {
         --this.field_71088_bW;
      }

   }

   public void attack(Entity p_71059_1_) {
      if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
         this.setCamera(p_71059_1_);
      } else {
         super.attack(p_71059_1_);
      }

   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swing(Hand p_184609_1_) {
      super.swing(p_184609_1_);
      this.resetAttackStrengthTicker();
   }

   public boolean isChangingDimension() {
      return this.isChangingDimension;
   }

   public void hasChangedDimension() {
      this.isChangingDimension = false;
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleportTo(ServerWorld p_200619_1_, double p_200619_2_, double p_200619_4_, double p_200619_6_, float p_200619_8_, float p_200619_9_) {
      this.setCamera(this);
      this.stopRiding();
      if (p_200619_1_ == this.level) {
         this.connection.teleport(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
      } else if (net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_200619_1_.dimension.func_186058_p())) {
         DimensionType oldDimension = this.field_71093_bK;
         ServerWorld serverworld = this.getLevel();
         this.field_71093_bK = p_200619_1_.dimension.func_186058_p();
         WorldInfo worldinfo = p_200619_1_.getLevelData();
         net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(this.connection.connection, this);
         this.connection.send(new SRespawnPacket(this.field_71093_bK, WorldInfo.func_227498_c_(worldinfo.func_76063_b()), worldinfo.func_76067_t(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
         this.server.getPlayerList().sendPlayerPermissionLevel(this);
         serverworld.removePlayer(this, true); //Forge: The player entity itself is moved, and not cloned. So we need to keep the data alive with no matching invalidate call later.
         this.revive();
         this.moveTo(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.setLevel(p_200619_1_);
         p_200619_1_.addDuringCommandTeleport(this);
         this.triggerDimensionChangeTriggers(serverworld);
         this.connection.teleport(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.gameMode.setLevel(p_200619_1_);
         this.server.getPlayerList().sendLevelInfo(this, p_200619_1_);
         this.server.getPlayerList().sendAllPlayerInfo(this);
         net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, oldDimension, this.field_71093_bK);
      }

   }

   public void trackChunk(ChunkPos p_213844_1_, IPacket<?> p_213844_2_, IPacket<?> p_213844_3_) {
      this.connection.send(p_213844_3_);
      this.connection.send(p_213844_2_);
   }

   public void untrackChunk(ChunkPos p_213845_1_) {
      if (this.isAlive()) {
         this.connection.send(new SUnloadChunkPacket(p_213845_1_.x, p_213845_1_.z));
      }

   }

   public SectionPos getLastSectionPos() {
      return this.lastSectionPos;
   }

   public void setLastSectionPos(SectionPos p_213850_1_) {
      this.lastSectionPos = p_213850_1_;
   }

   public void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.connection.send(new SPlaySoundEffectPacket(p_213823_1_, p_213823_2_, this.getX(), this.getY(), this.getZ(), p_213823_3_, p_213823_4_));
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnPlayerPacket(this);
   }

   public ItemEntity drop(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
      ItemEntity itementity = super.drop(p_146097_1_, p_146097_2_, p_146097_3_);
      if (itementity == null) {
         return null;
      } else {
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.level.addFreshEntity(itementity);
         ItemStack itemstack = itementity.getItem();
         if (p_146097_3_) {
            if (!itemstack.isEmpty()) {
               this.awardStat(Stats.ITEM_DROPPED.get(itemstack.getItem()), p_146097_1_.getCount());
            }

            this.awardStat(Stats.DROP);
         }

         return itementity;
      }
   }
}
