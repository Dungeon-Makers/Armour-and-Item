package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BegGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WolfEntity extends TameableEntity {
   private static final DataParameter<Boolean> DATA_INTERESTED_ID = EntityDataManager.defineId(WolfEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DATA_COLLAR_COLOR = EntityDataManager.defineId(WolfEntity.class, DataSerializers.INT);
   public static final Predicate<LivingEntity> PREY_SELECTOR = (p_213440_0_) -> {
      EntityType<?> entitytype = p_213440_0_.getType();
      return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
   };
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private float shakeAnim;
   private float shakeAnimO;

   public WolfEntity(EntityType<? extends WolfEntity> p_i50240_1_, World p_i50240_2_) {
      super(p_i50240_1_, p_i50240_2_);
      this.setTame(false);
   }

   protected void registerGoals() {
      this.field_70911_d = new SitGoal(this);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.field_70911_d);
      this.goalSelector.addGoal(3, new WolfEntity.AvoidEntityGoal(this, LlamaEntity.class, 24.0F, 1.5D, 1.5D));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(4, new NonTamedTargetGoal<>(this, AnimalEntity.class, false, PREY_SELECTOR));
      this.targetSelector.addGoal(4, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.3F);
      if (this.isTame()) {
         this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(8.0D);
      }

      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).setBaseValue(2.0D);
   }

   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      super.setTarget(p_70624_1_);
      if (p_70624_1_ == null) {
         this.func_70916_h(false);
      } else if (!this.isTame()) {
         this.func_70916_h(true);
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_INTERESTED_ID, false);
      this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("Angry", this.func_70919_bu());
      p_213281_1_.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.func_70916_h(p_70037_1_.getBoolean("Angry"));
      if (p_70037_1_.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(p_70037_1_.getInt("CollarColor")));
      }

   }

   protected SoundEvent getAmbientSound() {
      if (this.func_70919_bu()) {
         return SoundEvents.WOLF_GROWL;
      } else if (this.random.nextInt(3) == 0) {
         return this.isTame() && this.getHealth() < 10.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
      } else {
         return SoundEvents.WOLF_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WOLF_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
         this.level.broadcastEntityEvent(this, (byte)8);
      }

      if (!this.level.isClientSide && this.getTarget() == null && this.func_70919_bu()) {
         this.func_70916_h(false);
      }

   }

   public void tick() {
      super.tick();
      if (this.isAlive()) {
         this.interestedAngleO = this.interestedAngle;
         if (this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
         } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
         }

         if (this.isInWaterRainOrBubble()) {
            this.isWet = true;
            this.isShaking = false;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
         } else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.shakeAnim == 0.0F) {
               this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;
            if (this.shakeAnimO >= 2.0F) {
               this.isWet = false;
               this.isShaking = false;
               this.shakeAnimO = 0.0F;
               this.shakeAnim = 0.0F;
            }

            if (this.shakeAnim > 0.4F) {
               float f = (float)this.getY();
               int i = (int)(MathHelper.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
               Vec3d vec3d = this.getDeltaMovement();

               for(int j = 0; j < i; ++j) {
                  float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)f1, (double)(f + 0.8F), this.getZ() + (double)f2, vec3d.x, vec3d.y, vec3d.z);
               }
            }
         }

      }
   }

   public void die(DamageSource p_70645_1_) {
      this.isWet = false;
      this.isShaking = false;
      this.shakeAnimO = 0.0F;
      this.shakeAnim = 0.0F;
      super.die(p_70645_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWet() {
      return this.isWet;
   }

   @OnlyIn(Dist.CLIENT)
   public float getWetShade(float p_70915_1_) {
      return 0.75F + MathHelper.lerp(p_70915_1_, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getBodyRollAngle(float p_70923_1_, float p_70923_2_) {
      float f = (MathHelper.lerp(p_70923_1_, this.shakeAnimO, this.shakeAnim) + p_70923_2_) / 1.8F;
      if (f < 0.0F) {
         f = 0.0F;
      } else if (f > 1.0F) {
         f = 1.0F;
      }

      return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRollAngle(float p_70917_1_) {
      return MathHelper.lerp(p_70917_1_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.8F;
   }

   public int getMaxHeadXRot() {
      return this.func_70906_o() ? 20 : super.getMaxHeadXRot();
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getEntity();
         if (this.field_70911_d != null) {
            this.field_70911_d.func_75270_a(false);
         }

         if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
            p_70097_2_ = (p_70097_2_ + 1.0F) / 2.0F;
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttribute(SharedMonsterAttributes.field_111264_e).getValue()));
      if (flag) {
         this.doEnchantDamageEffects(this, p_70652_1_);
      }

      return flag;
   }

   public void setTame(boolean p_70903_1_) {
      super.setTame(p_70903_1_);
      if (p_70903_1_) {
         this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(20.0D);
         this.setHealth(20.0F);
      } else {
         this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(8.0D);
      }

      this.getAttribute(SharedMonsterAttributes.field_111264_e).setBaseValue(4.0D);
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      Item item = itemstack.getItem();
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      } else if (this.level.isClientSide) {
         return this.isOwnedBy(p_184645_1_) || item == Items.BONE && !this.func_70919_bu();
      } else {
         if (this.isTame()) {
            if (item.isEdible() && item.getFoodProperties().isMeat() && this.getHealth() < this.getMaxHealth()) {
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               this.heal((float)item.getFoodProperties().getNutrition());
               return true;
            }

            if (!(item instanceof DyeItem)) {
               boolean flag = super.func_184645_a(p_184645_1_, p_184645_2_);
               if (!flag || this.isBaby()) {
                  this.field_70911_d.func_75270_a(!this.func_70906_o());
               }

               return flag;
            }

            DyeColor dyecolor = ((DyeItem)item).getDyeColor();
            if (dyecolor != this.getCollarColor()) {
               this.setCollarColor(dyecolor);
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (this.isOwnedBy(p_184645_1_) && !this.isFood(itemstack)) {
               this.field_70911_d.func_75270_a(!this.func_70906_o());
               this.jumping = false;
               this.navigation.stop();
               this.setTarget((LivingEntity)null);
            }
         } else if (item == Items.BONE && !this.func_70919_bu()) {
            if (!p_184645_1_.abilities.instabuild) {
               itemstack.shrink(1);
            }

            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_184645_1_)) {
               this.tame(p_184645_1_);
               this.navigation.stop();
               this.setTarget((LivingEntity)null);
               this.field_70911_d.func_75270_a(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.level.broadcastEntityEvent(this, (byte)6);
            }

            return true;
         }

         return super.func_184645_a(p_184645_1_, p_184645_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 8) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getTailAngle() {
      if (this.func_70919_bu()) {
         return 1.5393804F;
      } else {
         return this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
      }
   }

   public boolean isFood(ItemStack p_70877_1_) {
      Item item = p_70877_1_.getItem();
      return item.isEdible() && item.getFoodProperties().isMeat();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean func_70919_bu() {
      return (this.entityData.get(DATA_FLAGS_ID) & 2) != 0;
   }

   public void func_70916_h(boolean p_70916_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_70916_1_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 2));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -3));
      }

   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
   }

   public void setCollarColor(DyeColor p_175547_1_) {
      this.entityData.set(DATA_COLLAR_COLOR, p_175547_1_.getId());
   }

   public WolfEntity func_90011_a(AgeableEntity p_90011_1_) {
      WolfEntity wolfentity = EntityType.WOLF.create(this.level);
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         wolfentity.setOwnerUUID(uuid);
         wolfentity.setTame(true);
      }

      return wolfentity;
   }

   public void setIsInterested(boolean p_70918_1_) {
      this.entityData.set(DATA_INTERESTED_ID, p_70918_1_);
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!this.isTame()) {
         return false;
      } else if (!(p_70878_1_ instanceof WolfEntity)) {
         return false;
      } else {
         WolfEntity wolfentity = (WolfEntity)p_70878_1_;
         if (!wolfentity.isTame()) {
            return false;
         } else if (wolfentity.func_70906_o()) {
            return false;
         } else {
            return this.isInLove() && wolfentity.isInLove();
         }
      }
   }

   public boolean isInterested() {
      return this.entityData.get(DATA_INTERESTED_ID);
   }

   public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
      if (!(p_142018_1_ instanceof CreeperEntity) && !(p_142018_1_ instanceof GhastEntity)) {
         if (p_142018_1_ instanceof WolfEntity) {
            WolfEntity wolfentity = (WolfEntity)p_142018_1_;
            return !wolfentity.isTame() || wolfentity.getOwner() != p_142018_2_;
         } else if (p_142018_1_ instanceof PlayerEntity && p_142018_2_ instanceof PlayerEntity && !((PlayerEntity)p_142018_2_).canHarmPlayer((PlayerEntity)p_142018_1_)) {
            return false;
         } else if (p_142018_1_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_142018_1_).isTamed()) {
            return false;
         } else {
            return !(p_142018_1_ instanceof TameableEntity) || !((TameableEntity)p_142018_1_).isTame();
         }
      } else {
         return false;
      }
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return !this.func_70919_bu() && super.canBeLeashed(p_184652_1_);
   }

   class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
      private final WolfEntity wolf;

      public AvoidEntityGoal(WolfEntity p_i47251_2_, Class<T> p_i47251_3_, float p_i47251_4_, double p_i47251_5_, double p_i47251_7_) {
         super(p_i47251_2_, p_i47251_3_, p_i47251_4_, p_i47251_5_, p_i47251_7_);
         this.wolf = p_i47251_2_;
      }

      public boolean canUse() {
         if (super.canUse() && this.toAvoid instanceof LlamaEntity) {
            return !this.wolf.isTame() && this.avoidLlama((LlamaEntity)this.toAvoid);
         } else {
            return false;
         }
      }

      private boolean avoidLlama(LlamaEntity p_190854_1_) {
         return p_190854_1_.getStrength() >= WolfEntity.this.random.nextInt(5);
      }

      public void start() {
         WolfEntity.this.setTarget((LivingEntity)null);
         super.start();
      }

      public void tick() {
         WolfEntity.this.setTarget((LivingEntity)null);
         super.tick();
      }
   }
}
