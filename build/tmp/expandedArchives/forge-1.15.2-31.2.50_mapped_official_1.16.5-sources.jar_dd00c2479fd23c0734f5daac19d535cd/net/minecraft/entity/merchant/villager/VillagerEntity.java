package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTasks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.GossipManager;
import net.minecraft.village.GossipType;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VillagerEntity extends AbstractVillagerEntity implements IReputationTracking, IVillagerDataHolder {
   private static final DataParameter<VillagerData> DATA_VILLAGER_DATA = EntityDataManager.defineId(VillagerEntity.class, DataSerializers.VILLAGER_DATA);
   public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
   private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
   private int updateMerchantTimer;
   private boolean increaseProfessionLevelOnUpdate;
   @Nullable
   private PlayerEntity lastTradedPlayer;
   private byte foodLevel;
   private final GossipManager gossips = new GossipManager();
   private long lastGossipTime;
   private long lastGossipDecayTime;
   private int villagerXp;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.field_223542_x);
   private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.field_221000_d, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.field_223547_j);
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (p_213769_0_, p_213769_1_) -> {
      return p_213769_1_ == PointOfInterestType.HOME;
   }, MemoryModuleType.JOB_SITE, (p_213771_0_, p_213771_1_) -> {
      return p_213771_0_.getVillagerData().getProfession().getJobPoiType() == p_213771_1_;
   }, MemoryModuleType.MEETING_POINT, (p_213772_0_, p_213772_1_) -> {
      return p_213772_1_ == PointOfInterestType.MEETING;
   });

   public VillagerEntity(EntityType<? extends VillagerEntity> p_i50182_1_, World p_i50182_2_) {
      this(p_i50182_1_, p_i50182_2_, IVillagerType.PLAINS);
   }

   public VillagerEntity(EntityType<? extends VillagerEntity> p_i50183_1_, World p_i50183_2_, IVillagerType p_i50183_3_) {
      super(p_i50183_1_, p_i50183_2_);
      ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
      this.getNavigation().setCanFloat(true);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().setType(p_i50183_3_).setProfession(VillagerProfession.NONE));
      this.brain = this.makeBrain(new Dynamic<>(NBTDynamicOps.INSTANCE, new CompoundNBT()));
   }

   public Brain<VillagerEntity> getBrain() {
      return (Brain<VillagerEntity>) super.getBrain();
   }

   protected Brain<?> makeBrain(Dynamic<?> p_213364_1_) {
      Brain<VillagerEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, p_213364_1_);
      this.registerBrainGoals(brain);
      return brain;
   }

   public void refreshBrain(ServerWorld p_213770_1_) {
      Brain<VillagerEntity> brain = this.getBrain();
      brain.stopAll(p_213770_1_, this);
      this.brain = brain.copyWithoutBehaviors();
      this.registerBrainGoals(this.getBrain());
   }

   private void registerBrainGoals(Brain<VillagerEntity> p_213744_1_) {
      VillagerProfession villagerprofession = this.getVillagerData().getProfession();
      float f = (float)this.getAttribute(SharedMonsterAttributes.field_111263_d).getValue();
      if (this.isBaby()) {
         p_213744_1_.setSchedule(Schedule.VILLAGER_BABY);
         p_213744_1_.addActivity(Activity.PLAY, VillagerTasks.getPlayPackage(f));
      } else {
         p_213744_1_.setSchedule(Schedule.VILLAGER_DEFAULT);
         p_213744_1_.func_218224_a(Activity.WORK, VillagerTasks.getWorkPackage(villagerprofession, f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
      }

      p_213744_1_.addActivity(Activity.CORE, VillagerTasks.getCorePackage(villagerprofession, f));
      p_213744_1_.func_218224_a(Activity.MEET, VillagerTasks.getMeetPackage(villagerprofession, f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
      p_213744_1_.addActivity(Activity.REST, VillagerTasks.getRestPackage(villagerprofession, f));
      p_213744_1_.addActivity(Activity.IDLE, VillagerTasks.getIdlePackage(villagerprofession, f));
      p_213744_1_.addActivity(Activity.PANIC, VillagerTasks.getPanicPackage(villagerprofession, f));
      p_213744_1_.addActivity(Activity.PRE_RAID, VillagerTasks.getPreRaidPackage(villagerprofession, f));
      p_213744_1_.addActivity(Activity.RAID, VillagerTasks.getRaidPackage(villagerprofession, f));
      p_213744_1_.addActivity(Activity.HIDE, VillagerTasks.getHidePackage(villagerprofession, f));
      p_213744_1_.setCoreActivities(ImmutableSet.of(Activity.CORE));
      p_213744_1_.setDefaultActivity(Activity.IDLE);
      p_213744_1_.setActiveActivityIfPossible(Activity.IDLE);
      p_213744_1_.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (this.level instanceof ServerWorld) {
         this.refreshBrain((ServerWorld)this.level);
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.field_111265_b).setBaseValue(48.0D);
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("brain");
      this.getBrain().tick((ServerWorld)this.level, this);
      this.level.getProfiler().pop();
      if (!this.isTrading() && this.updateMerchantTimer > 0) {
         --this.updateMerchantTimer;
         if (this.updateMerchantTimer <= 0) {
            if (this.increaseProfessionLevelOnUpdate) {
               this.increaseMerchantCareer();
               this.increaseProfessionLevelOnUpdate = false;
            }

            this.addEffect(new EffectInstance(Effects.REGENERATION, 200, 0));
         }
      }

      if (this.lastTradedPlayer != null && this.level instanceof ServerWorld) {
         ((ServerWorld)this.level).onReputationEvent(IReputationType.TRADE, this.lastTradedPlayer, this);
         this.level.broadcastEntityEvent(this, (byte)14);
         this.lastTradedPlayer = null;
      }

      if (!this.isNoAi() && this.random.nextInt(100) == 0) {
         Raid raid = ((ServerWorld)this.level).getRaidAt(new BlockPos(this));
         if (raid != null && raid.isActive() && !raid.isOver()) {
            this.level.broadcastEntityEvent(this, (byte)42);
         }
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
         this.stopTrading();
      }

      super.customServerAiStep();
   }

   public void tick() {
      super.tick();
      if (this.getUnhappyCounter() > 0) {
         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      this.maybeDecayGossip();
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      boolean flag = itemstack.getItem() == Items.NAME_TAG;
      if (flag) {
         itemstack.func_111282_a(p_184645_1_, this, p_184645_2_);
         return true;
      } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping() && !p_184645_1_.isSecondaryUseActive()) {
         if (this.isBaby()) {
            this.setUnhappy();
            return super.func_184645_a(p_184645_1_, p_184645_2_);
         } else {
            boolean flag1 = this.getOffers().isEmpty();
            if (p_184645_2_ == Hand.MAIN_HAND) {
               if (flag1 && !this.level.isClientSide) {
                  this.setUnhappy();
               }

               p_184645_1_.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (flag1) {
               return super.func_184645_a(p_184645_1_, p_184645_2_);
            } else {
               if (!this.level.isClientSide && !this.offers.isEmpty()) {
                  this.startTrading(p_184645_1_);
               }

               return true;
            }
         }
      } else {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      }
   }

   private void setUnhappy() {
      this.setUnhappyCounter(40);
      if (!this.level.isClientSide()) {
         this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   private void startTrading(PlayerEntity p_213740_1_) {
      this.updateSpecialPrices(p_213740_1_);
      this.setTradingPlayer(p_213740_1_);
      this.openTradingScreen(p_213740_1_, this.getDisplayName(), this.getVillagerData().getLevel());
   }

   public void setTradingPlayer(@Nullable PlayerEntity p_70932_1_) {
      boolean flag = this.getTradingPlayer() != null && p_70932_1_ == null;
      super.setTradingPlayer(p_70932_1_);
      if (flag) {
         this.stopTrading();
      }

   }

   protected void stopTrading() {
      super.stopTrading();
      this.resetSpecialPrices();
   }

   private void resetSpecialPrices() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetSpecialPriceDiff();
      }

   }

   public boolean canRestock() {
      return true;
   }

   public void restock() {
      this.updateDemand();

      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetUses();
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
         this.func_223359_eB();
      }

      this.lastRestockGameTime = this.level.getGameTime();
      ++this.numberOfRestocksToday;
   }

   private boolean needsToRestock() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         if (merchantoffer.needsRestock()) {
            return true;
         }
      }

      return false;
   }

   private boolean allowedToRestock() {
      return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
   }

   public boolean shouldRestock() {
      long i = this.lastRestockGameTime + 12000L;
      long j = this.level.getGameTime();
      boolean flag = j > i;
      long k = this.level.getDayTime();
      if (this.lastRestockCheckDayTime > 0L) {
         long l = this.lastRestockCheckDayTime / 24000L;
         long i1 = k / 24000L;
         flag |= i1 > l;
      }

      this.lastRestockCheckDayTime = k;
      if (flag) {
         this.lastRestockGameTime = j;
         this.resetNumberOfRestocks();
      }

      return this.allowedToRestock() && this.needsToRestock();
   }

   private void catchUpDemand() {
      int i = 2 - this.numberOfRestocksToday;
      if (i > 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.resetUses();
         }
      }

      for(int j = 0; j < i; ++j) {
         this.updateDemand();
      }

   }

   private void updateDemand() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.updateDemand();
      }

   }

   private void updateSpecialPrices(PlayerEntity p_213762_1_) {
      int i = this.getPlayerReputation(p_213762_1_);
      if (i != 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.addToSpecialPriceDiff(-MathHelper.floor((float)i * merchantoffer.getPriceMultiplier()));
         }
      }

      if (p_213762_1_.hasEffect(Effects.HERO_OF_THE_VILLAGE)) {
         EffectInstance effectinstance = p_213762_1_.getEffect(Effects.HERO_OF_THE_VILLAGE);
         int k = effectinstance.getAmplifier();

         for(MerchantOffer merchantoffer1 : this.getOffers()) {
            double d0 = 0.3D + 0.0625D * (double)k;
            int j = (int)Math.floor(d0 * (double)merchantoffer1.getBaseCostA().getCount());
            merchantoffer1.addToSpecialPriceDiff(-Math.max(j, 1));
         }
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(IVillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.put("VillagerData", this.getVillagerData().func_221131_a(NBTDynamicOps.INSTANCE));
      p_213281_1_.putByte("FoodLevel", this.foodLevel);
      p_213281_1_.put("Gossips", this.gossips.func_220914_a(NBTDynamicOps.INSTANCE).getValue());
      p_213281_1_.putInt("Xp", this.villagerXp);
      p_213281_1_.putLong("LastRestock", this.lastRestockGameTime);
      p_213281_1_.putLong("LastGossipDecay", this.lastGossipDecayTime);
      p_213281_1_.putInt("RestocksToday", this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("VillagerData", 10)) {
         this.setVillagerData(new VillagerData(new Dynamic<>(NBTDynamicOps.INSTANCE, p_70037_1_.get("VillagerData"))));
      }

      if (p_70037_1_.contains("Offers", 10)) {
         this.offers = new MerchantOffers(p_70037_1_.getCompound("Offers"));
      }

      if (p_70037_1_.contains("FoodLevel", 1)) {
         this.foodLevel = p_70037_1_.getByte("FoodLevel");
      }

      ListNBT listnbt = p_70037_1_.getList("Gossips", 10);
      this.gossips.func_220918_a(new Dynamic<>(NBTDynamicOps.INSTANCE, listnbt));
      if (p_70037_1_.contains("Xp", 3)) {
         this.villagerXp = p_70037_1_.getInt("Xp");
      }

      this.lastRestockGameTime = p_70037_1_.getLong("LastRestock");
      this.lastGossipDecayTime = p_70037_1_.getLong("LastGossipDecay");
      this.setCanPickUpLoot(true);
      if (this.level instanceof ServerWorld) {
         this.refreshBrain((ServerWorld)this.level);
      }

      this.numberOfRestocksToday = p_70037_1_.getInt("RestocksToday");
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
   }

   public void playWorkSound() {
      SoundEvent soundevent = this.getVillagerData().getProfession().getWorkSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public void setVillagerData(VillagerData p_213753_1_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != p_213753_1_.getProfession()) {
         this.offers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, p_213753_1_);
   }

   public VillagerData getVillagerData() {
      return this.entityData.get(DATA_VILLAGER_DATA);
   }

   protected void rewardTradeXp(MerchantOffer p_213713_1_) {
      int i = 3 + this.random.nextInt(4);
      this.villagerXp += p_213713_1_.getXp();
      this.lastTradedPlayer = this.getTradingPlayer();
      if (this.shouldIncreaseLevel()) {
         this.updateMerchantTimer = 40;
         this.increaseProfessionLevelOnUpdate = true;
         i += 5;
      }

      if (p_213713_1_.shouldRewardExp()) {
         this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
      }

   }

   public void setLastHurtByMob(@Nullable LivingEntity p_70604_1_) {
      if (p_70604_1_ != null && this.level instanceof ServerWorld) {
         ((ServerWorld)this.level).onReputationEvent(IReputationType.VILLAGER_HURT, p_70604_1_, this);
         if (this.isAlive() && p_70604_1_ instanceof PlayerEntity) {
            this.level.broadcastEntityEvent(this, (byte)13);
         }
      }

      super.setLastHurtByMob(p_70604_1_);
   }

   public void die(DamageSource p_70645_1_) {
      LOGGER.info("Villager {} died, message: '{}'", this, p_70645_1_.getLocalizedDeathMessage(this).getString());
      Entity entity = p_70645_1_.getEntity();
      if (entity != null) {
         this.tellWitnessesThatIWasMurdered(entity);
      }

      this.releasePoi(MemoryModuleType.HOME);
      this.releasePoi(MemoryModuleType.JOB_SITE);
      this.releasePoi(MemoryModuleType.MEETING_POINT);
      super.die(p_70645_1_);
   }

   private void tellWitnessesThatIWasMurdered(Entity p_223361_1_) {
      if (this.level instanceof ServerWorld) {
         Optional<List<LivingEntity>> optional = this.brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
         if (optional.isPresent()) {
            ServerWorld serverworld = (ServerWorld)this.level;
            optional.get().stream().filter((p_223349_0_) -> {
               return p_223349_0_ instanceof IReputationTracking;
            }).forEach((p_223342_2_) -> {
               serverworld.onReputationEvent(IReputationType.VILLAGER_KILLED, p_223361_1_, (IReputationTracking)p_223342_2_);
            });
         }
      }
   }

   public void releasePoi(MemoryModuleType<GlobalPos> p_213742_1_) {
      if (this.level instanceof ServerWorld) {
         MinecraftServer minecraftserver = ((ServerWorld)this.level).getServer();
         this.brain.getMemory(p_213742_1_).ifPresent((p_213752_3_) -> {
            ServerWorld serverworld = minecraftserver.getLevel(p_213752_3_.func_218177_a());
            PointOfInterestManager pointofinterestmanager = serverworld.getPoiManager();
            Optional<PointOfInterestType> optional = pointofinterestmanager.getType(p_213752_3_.pos());
            BiPredicate<VillagerEntity, PointOfInterestType> bipredicate = POI_MEMORIES.get(p_213742_1_);
            if (optional.isPresent() && bipredicate.test(this, optional.get())) {
               pointofinterestmanager.release(p_213752_3_.pos());
               DebugPacketSender.sendPoiTicketCountPacket(serverworld, p_213752_3_.pos());
            }

         });
      }
   }

   public boolean canBreed() {
      return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
   }

   private boolean hungry() {
      return this.foodLevel < 12;
   }

   private void eatUntilFull() {
      if (this.hungry() && this.countFoodPointsInInventory() != 0) {
         for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = this.getInventory().getItem(i);
            if (!itemstack.isEmpty()) {
               Integer integer = FOOD_POINTS.get(itemstack.getItem());
               if (integer != null) {
                  int j = itemstack.getCount();

                  for(int k = j; k > 0; --k) {
                     this.foodLevel = (byte)(this.foodLevel + integer);
                     this.getInventory().removeItem(i, 1);
                     if (!this.hungry()) {
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public int getPlayerReputation(PlayerEntity p_223107_1_) {
      return this.gossips.getReputation(p_223107_1_.getUUID(), (p_223103_0_) -> {
         return true;
      });
   }

   private void digestFood(int p_213758_1_) {
      this.foodLevel = (byte)(this.foodLevel - p_213758_1_);
   }

   public void eatAndDigestFood() {
      this.eatUntilFull();
      this.digestFood(12);
   }

   public void setOffers(MerchantOffers p_213768_1_) {
      this.offers = p_213768_1_;
   }

   private boolean shouldIncreaseLevel() {
      int i = this.getVillagerData().getLevel();
      return VillagerData.canLevelUp(i) && this.villagerXp >= VillagerData.getMaxXpPerLevel(i);
   }

   private void increaseMerchantCareer() {
      this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
      this.updateTrades();
   }

   protected ITextComponent getTypeName() {
      net.minecraft.util.ResourceLocation profName = this.getVillagerData().getProfession().getRegistryName();
      return new TranslationTextComponent(this.getType().getDescriptionId() + '.' + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 12) {
         this.addParticlesAroundSelf(ParticleTypes.HEART);
      } else if (p_70103_1_ == 13) {
         this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
      } else if (p_70103_1_ == 14) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else if (p_70103_1_ == 42) {
         this.addParticlesAroundSelf(ParticleTypes.SPLASH);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_3_ == SpawnReason.BREEDING) {
         this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
      }

      if (p_213386_3_ == SpawnReason.COMMAND || p_213386_3_ == SpawnReason.SPAWN_EGG || p_213386_3_ == SpawnReason.SPAWNER || p_213386_3_ == SpawnReason.DISPENSER) {
         this.setVillagerData(this.getVillagerData().setType(IVillagerType.func_221170_a(p_213386_1_.getBiome(new BlockPos(this)))));
      }

      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public VillagerEntity func_90011_a(AgeableEntity p_90011_1_) {
      double d0 = this.random.nextDouble();
      IVillagerType ivillagertype;
      if (d0 < 0.5D) {
         ivillagertype = IVillagerType.func_221170_a(this.level.getBiome(new BlockPos(this)));
      } else if (d0 < 0.75D) {
         ivillagertype = this.getVillagerData().getType();
      } else {
         ivillagertype = ((VillagerEntity)p_90011_1_).getVillagerData().getType();
      }

      VillagerEntity villagerentity = new VillagerEntity(EntityType.VILLAGER, this.level, ivillagertype);
      villagerentity.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(villagerentity)), SpawnReason.BREEDING, (ILivingEntityData)null, (CompoundNBT)null);
      return villagerentity;
   }

   public void func_70077_a(LightningBoltEntity p_70077_1_) {
      WitchEntity witchentity = EntityType.WITCH.create(this.level);
      witchentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      witchentity.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(witchentity)), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
      witchentity.setNoAi(this.isNoAi());
      if (this.hasCustomName()) {
         witchentity.setCustomName(this.getCustomName());
         witchentity.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.level.addFreshEntity(witchentity);
      this.remove();
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      Item item = itemstack.getItem();
      if (this.func_223717_b(item)) {
         Inventory inventory = this.getInventory();
         boolean flag = false;

         for(int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventory.getItem(i);
            if (itemstack1.isEmpty() || itemstack1.getItem() == item && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return;
         }

         int j = inventory.countItem(item);
         if (j == 256) {
            return;
         }

         if (j > 256) {
            inventory.removeItemType(item, j - 256);
            return;
         }

         this.take(p_175445_1_, itemstack.getCount());
         ItemStack itemstack2 = inventory.addItem(itemstack);
         if (itemstack2.isEmpty()) {
            p_175445_1_.remove();
         } else {
            itemstack.setCount(itemstack2.getCount());
         }
      }

   }

   public boolean func_223717_b(Item p_223717_1_) {
      return WANTED_ITEMS.contains(p_223717_1_) || this.getVillagerData().getProfession().getRequestedItems().contains(p_223717_1_);
   }

   public boolean hasExcessFood() {
      return this.countFoodPointsInInventory() >= 24;
   }

   public boolean wantsMoreFood() {
      return this.countFoodPointsInInventory() < 12;
   }

   private int countFoodPointsInInventory() {
      Inventory inventory = this.getInventory();
      return FOOD_POINTS.entrySet().stream().mapToInt((p_226553_1_) -> {
         return inventory.countItem(p_226553_1_.getKey()) * p_226553_1_.getValue();
      }).sum();
   }

   private void func_223359_eB() {
      Inventory inventory = this.getInventory();
      int i = inventory.countItem(Items.WHEAT);
      int j = i / 3;
      if (j != 0) {
         int k = j * 3;
         inventory.removeItemType(Items.WHEAT, k);
         ItemStack itemstack = inventory.addItem(new ItemStack(Items.BREAD, j));
         if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack, 0.5F);
         }

      }
   }

   public boolean hasFarmSeeds() {
      Inventory inventory = this.getInventory();
      return inventory.hasAnyOf(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
   }

   protected void updateTrades() {
      VillagerData villagerdata = this.getVillagerData();
      Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = VillagerTrades.TRADES.get(villagerdata.getProfession());
      if (int2objectmap != null && !int2objectmap.isEmpty()) {
         VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(villagerdata.getLevel());
         if (avillagertrades$itrade != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addOffersFromItemListings(merchantoffers, avillagertrades$itrade, 2);
         }
      }
   }

   public void func_213746_a(VillagerEntity p_213746_1_, long p_213746_2_) {
      if ((p_213746_2_ < this.lastGossipTime || p_213746_2_ >= this.lastGossipTime + 1200L) && (p_213746_2_ < p_213746_1_.lastGossipTime || p_213746_2_ >= p_213746_1_.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(p_213746_1_.gossips, this.random, 10);
         this.lastGossipTime = p_213746_2_;
         p_213746_1_.lastGossipTime = p_213746_2_;
         this.func_223358_a(p_213746_2_, 5);
      }
   }

   private void maybeDecayGossip() {
      long i = this.level.getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = i;
      } else if (i >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = i;
      }
   }

   public void func_223358_a(long p_223358_1_, int p_223358_3_) {
      if (this.wantsToSpawnGolem(p_223358_1_)) {
         AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
         List<VillagerEntity> list = this.level.getEntitiesOfClass(VillagerEntity.class, axisalignedbb);
         List<VillagerEntity> list1 = list.stream().filter((p_226554_2_) -> {
            return p_226554_2_.wantsToSpawnGolem(p_223358_1_);
         }).limit(5L).collect(Collectors.toList());
         if (list1.size() >= p_223358_3_) {
            IronGolemEntity irongolementity = this.trySpawnGolem();
            if (irongolementity != null) {
               list.forEach((p_226552_2_) -> {
                  p_226552_2_.func_223347_b(p_223358_1_);
               });
            }
         }
      }
   }

   private void func_223347_b(long p_223347_1_) {
      this.brain.setMemory(MemoryModuleType.field_223542_x, p_223347_1_);
   }

   private boolean func_223354_c(long p_223354_1_) {
      Optional<Long> optional = this.brain.getMemory(MemoryModuleType.field_223542_x);
      if (!optional.isPresent()) {
         return false;
      } else {
         Long olong = optional.get();
         return p_223354_1_ - olong <= 600L;
      }
   }

   public boolean wantsToSpawnGolem(long p_223350_1_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT) {
         if (!this.golemSpawnConditionsMet(this.level.getGameTime())) {
            return false;
         } else {
            return !this.func_223354_c(p_223350_1_);
         }
      } else {
         return false;
      }
   }

   @Nullable
   private IronGolemEntity trySpawnGolem() {
      BlockPos blockpos = new BlockPos(this);

      for(int i = 0; i < 10; ++i) {
         double d0 = (double)(this.level.random.nextInt(16) - 8);
         double d1 = (double)(this.level.random.nextInt(16) - 8);
         double d2 = 6.0D;

         for(int j = 0; j >= -12; --j) {
            BlockPos blockpos1 = blockpos.offset(d0, d2 + (double)j, d1);
            if ((this.level.getBlockState(blockpos1).isAir() || this.level.getBlockState(blockpos1).getMaterial().isLiquid()) && this.level.getBlockState(blockpos1.below()).getMaterial().isSolidBlocking()) {
               d2 += (double)j;
               break;
            }
         }

         BlockPos blockpos2 = blockpos.offset(d0, d2, d1);
         IronGolemEntity irongolementity = EntityType.IRON_GOLEM.create(this.level, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos2, SpawnReason.MOB_SUMMONED, false, false);
         if (irongolementity != null) {
            if (irongolementity.checkSpawnRules(this.level, SpawnReason.MOB_SUMMONED) && irongolementity.checkSpawnObstruction(this.level)) {
               this.level.addFreshEntity(irongolementity);
               return irongolementity;
            }

            irongolementity.remove();
         }
      }

      return null;
   }

   public void onReputationEventFrom(IReputationType p_213739_1_, Entity p_213739_2_) {
      if (p_213739_1_ == IReputationType.ZOMBIE_VILLAGER_CURED) {
         this.gossips.add(p_213739_2_.getUUID(), GossipType.MAJOR_POSITIVE, 20);
         this.gossips.add(p_213739_2_.getUUID(), GossipType.MINOR_POSITIVE, 25);
      } else if (p_213739_1_ == IReputationType.TRADE) {
         this.gossips.add(p_213739_2_.getUUID(), GossipType.TRADING, 2);
      } else if (p_213739_1_ == IReputationType.VILLAGER_HURT) {
         this.gossips.add(p_213739_2_.getUUID(), GossipType.MINOR_NEGATIVE, 25);
      } else if (p_213739_1_ == IReputationType.VILLAGER_KILLED) {
         this.gossips.add(p_213739_2_.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
      }

   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public void setVillagerXp(int p_213761_1_) {
      this.villagerXp = p_213761_1_;
   }

   private void resetNumberOfRestocks() {
      this.catchUpDemand();
      this.numberOfRestocksToday = 0;
   }

   public GossipManager getGossips() {
      return this.gossips;
   }

   public void setGossips(INBT p_223716_1_) {
      this.gossips.func_220918_a(new Dynamic<>(NBTDynamicOps.INSTANCE, p_223716_1_));
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPacketSender.sendEntityBrain(this);
   }

   public void startSleeping(BlockPos p_213342_1_) {
      super.startSleeping(p_213342_1_);
      this.brain.setMemory(MemoryModuleType.LAST_SLEPT, LongSerializable.func_223463_a(this.level.getGameTime()));
   }

   public void stopSleeping() {
      super.stopSleeping();
      this.brain.setMemory(MemoryModuleType.LAST_WOKEN, LongSerializable.func_223463_a(this.level.getGameTime()));
   }

   private boolean golemSpawnConditionsMet(long p_223352_1_) {
      Optional<LongSerializable> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
      Optional<LongSerializable> optional1 = this.brain.getMemory(MemoryModuleType.LAST_WORKED_AT_POI);
      if (optional.isPresent() && optional1.isPresent()) {
         return p_223352_1_ - optional.get().func_223461_a() < 24000L && p_223352_1_ - optional1.get().func_223461_a() < 36000L;
      } else {
         return false;
      }
   }
}
