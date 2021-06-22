package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.TriggerSkeletonTrapGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends AbstractHorseEntity {
   private final TriggerSkeletonTrapGoal skeletonTrapGoal = new TriggerSkeletonTrapGoal(this);
   private boolean isTrap;
   private int trapTime;

   public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> p_i50235_1_, World p_i50235_2_) {
      super(p_i50235_1_, p_i50235_2_);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.2F);
      this.getAttribute(field_110271_bv).setBaseValue(this.generateRandomJumpStrength());
   }

   protected void addBehaviourGoals() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.isEyeInFluid(FluidTags.WATER) ? SoundEvents.SKELETON_HORSE_AMBIENT_WATER : SoundEvents.SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround) {
         if (!this.isVehicle()) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopSoundCounter;
         if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
            return SoundEvents.SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopSoundCounter <= 5) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float p_203006_1_) {
      if (this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, p_203006_1_ * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.UNDEAD;
   }

   public double getPassengersRidingOffset() {
      return super.getPassengersRidingOffset() - 0.1875D;
   }

   public void aiStep() {
      super.aiStep();
      if (this.isTrap() && this.trapTime++ >= 18000) {
         this.remove();
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("SkeletonTrap", this.isTrap());
      p_213281_1_.putInt("SkeletonTrapTime", this.trapTime);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setTrap(p_70037_1_.getBoolean("SkeletonTrap"));
      this.trapTime = p_70037_1_.getInt("SkeletonTrapTime");
   }

   public boolean rideableUnderWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.isTrap;
   }

   public void setTrap(boolean p_190691_1_) {
      if (p_190691_1_ != this.isTrap) {
         this.isTrap = p_190691_1_;
         if (p_190691_1_) {
            this.goalSelector.addGoal(1, this.skeletonTrapGoal);
         } else {
            this.goalSelector.removeGoal(this.skeletonTrapGoal);
         }

      }
   }

   @Nullable
   public AgeableEntity func_90011_a(AgeableEntity p_90011_1_) {
      return EntityType.SKELETON_HORSE.create(this.level);
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (itemstack.getItem() instanceof SpawnEggItem) {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      } else if (!this.isTamed()) {
         return false;
      } else if (this.isBaby()) {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      } else if (p_184645_1_.isSecondaryUseActive()) {
         this.openInventory(p_184645_1_);
         return true;
      } else if (this.isVehicle()) {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      } else {
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.SADDLE && !this.isSaddled()) {
               this.openInventory(p_184645_1_);
               return true;
            }

            if (itemstack.func_111282_a(p_184645_1_, this, p_184645_2_)) {
               return true;
            }
         }

         this.doPlayerRide(p_184645_1_);
         return true;
      }
   }
}