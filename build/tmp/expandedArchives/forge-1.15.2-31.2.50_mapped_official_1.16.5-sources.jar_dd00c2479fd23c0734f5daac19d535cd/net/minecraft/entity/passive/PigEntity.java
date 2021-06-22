package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
   private static final DataParameter<Boolean> DATA_SADDLE_ID = EntityDataManager.defineId(PigEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DATA_BOOST_TIME = EntityDataManager.defineId(PigEntity.class, DataSerializers.INT);
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
   private boolean field_184765_bx;
   private int field_184766_bz;
   private int field_184767_bA;

   public PigEntity(EntityType<? extends PigEntity> p_i50250_1_, World p_i50250_2_) {
      super(p_i50250_1_, p_i50250_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.CARROT_ON_A_STICK), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, FOOD_ITEMS));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue(0.25D);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
   }

   public boolean canBeControlledByRider() {
      Entity entity = this.getControllingPassenger();
      if (!(entity instanceof PlayerEntity)) {
         return false;
      } else {
         PlayerEntity playerentity = (PlayerEntity)entity;
         return playerentity.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK || playerentity.getOffhandItem().getItem() == Items.CARROT_ON_A_STICK;
      }
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_BOOST_TIME.equals(p_184206_1_) && this.level.isClientSide) {
         this.field_184765_bx = true;
         this.field_184766_bz = 0;
         this.field_184767_bA = this.entityData.get(DATA_BOOST_TIME);
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SADDLE_ID, false);
      this.entityData.define(DATA_BOOST_TIME, 0);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("Saddle", this.func_70901_n());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.func_70900_e(p_70037_1_.getBoolean("Saddle"));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIG_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      if (super.func_184645_a(p_184645_1_, p_184645_2_)) {
         return true;
      } else {
         ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
         if (itemstack.getItem() == Items.NAME_TAG) {
            itemstack.func_111282_a(p_184645_1_, this, p_184645_2_);
            return true;
         } else if (this.func_70901_n() && !this.isVehicle()) {
            if (!this.level.isClientSide) {
               p_184645_1_.startRiding(this);
            }

            return true;
         } else {
            return itemstack.getItem() == Items.SADDLE && itemstack.func_111282_a(p_184645_1_, this, p_184645_2_);
         }
      }
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.func_70901_n()) {
         this.spawnAtLocation(Items.SADDLE);
      }

   }

   public boolean func_70901_n() {
      return this.entityData.get(DATA_SADDLE_ID);
   }

   public void func_70900_e(boolean p_70900_1_) {
      if (p_70900_1_) {
         this.entityData.set(DATA_SADDLE_ID, true);
      } else {
         this.entityData.set(DATA_SADDLE_ID, false);
      }

   }

   public void func_70077_a(LightningBoltEntity p_70077_1_) {
      ZombiePigmanEntity zombiepigmanentity = EntityType.field_200785_Y.create(this.level);
      zombiepigmanentity.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
      zombiepigmanentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      zombiepigmanentity.setNoAi(this.isNoAi());
      if (this.hasCustomName()) {
         zombiepigmanentity.setCustomName(this.getCustomName());
         zombiepigmanentity.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.level.addFreshEntity(zombiepigmanentity);
      this.remove();
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.isAlive()) {
         Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
         if (this.isVehicle() && this.canBeControlledByRider()) {
            this.yRot = entity.yRot;
            this.yRotO = this.yRot;
            this.xRot = entity.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yRot;
            this.maxUpStep = 1.0F;
            this.flyingSpeed = this.getSpeed() * 0.1F;
            if (this.field_184765_bx && this.field_184766_bz++ > this.field_184767_bA) {
               this.field_184765_bx = false;
            }

            if (this.isControlledByLocalInstance()) {
               float f = (float)this.getAttribute(SharedMonsterAttributes.field_111263_d).getValue() * 0.225F;
               if (this.field_184765_bx) {
                  f += f * 1.15F * MathHelper.sin((float)this.field_184766_bz / (float)this.field_184767_bA * (float)Math.PI);
               }

               this.setSpeed(f);
               super.travel(new Vec3d(0.0D, 0.0D, 1.0D));
               this.lerpSteps = 0;
            } else {
               this.setDeltaMovement(Vec3d.ZERO);
            }

            this.animationSpeedOld = this.animationSpeed;
            double d1 = this.getX() - this.xo;
            double d0 = this.getZ() - this.zo;
            float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
            if (f1 > 1.0F) {
               f1 = 1.0F;
            }

            this.animationSpeed += (f1 - this.animationSpeed) * 0.4F;
            this.animationPosition += this.animationSpeed;
         } else {
            this.maxUpStep = 0.5F;
            this.flyingSpeed = 0.02F;
            super.travel(p_213352_1_);
         }
      }
   }

   public boolean boost() {
      if (this.field_184765_bx) {
         return false;
      } else {
         this.field_184765_bx = true;
         this.field_184766_bz = 0;
         this.field_184767_bA = this.getRandom().nextInt(841) + 140;
         this.getEntityData().set(DATA_BOOST_TIME, this.field_184767_bA);
         return true;
      }
   }

   public PigEntity func_90011_a(AgeableEntity p_90011_1_) {
      return EntityType.PIG.create(this.level);
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return FOOD_ITEMS.test(p_70877_1_);
   }
}