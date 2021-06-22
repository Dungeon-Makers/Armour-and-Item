package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AnimalEntity extends AgeableEntity {
   private int inLove;
   private UUID loveCause;

   protected AnimalEntity(EntityType<? extends AnimalEntity> p_i48568_1_, World p_i48568_2_) {
      super(p_i48568_1_, p_i48568_2_);
   }

   protected void customServerAiStep() {
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep();
   }

   public void aiStep() {
      super.aiStep();
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
         }
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.inLove = 0;
         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getBlockState(p_205022_1_.below()).getBlock() == Blocks.GRASS_BLOCK ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("InLove", this.inLove);
      if (this.loveCause != null) {
         p_213281_1_.putUUID("LoveCause", this.loveCause);
      }

   }

   public double getMyRidingOffset() {
      return 0.14D;
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.inLove = p_70037_1_.getInt("InLove");
      this.loveCause = p_70037_1_.hasUUID("LoveCause") ? p_70037_1_.getUUID("LoveCause") : null;
   }

   public static boolean checkAnimalSpawnRules(EntityType<? extends AnimalEntity> p_223316_0_, IWorld p_223316_1_, SpawnReason p_223316_2_, BlockPos p_223316_3_, Random p_223316_4_) {
      return p_223316_1_.getBlockState(p_223316_3_.below()).getBlock() == Blocks.GRASS_BLOCK && p_223316_1_.getRawBrightness(p_223316_3_, 0) > 8;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return false;
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      return 1 + this.level.random.nextInt(3);
   }

   public boolean isFood(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.WHEAT;
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (this.isFood(itemstack)) {
         if (!this.level.isClientSide && this.getAge() == 0 && this.canFallInLove()) {
            this.usePlayerItem(p_184645_1_, itemstack);
            this.setInLove(p_184645_1_);
            p_184645_1_.swing(p_184645_2_, true);
            return true;
         }

         if (this.isBaby()) {
            this.usePlayerItem(p_184645_1_, itemstack);
            this.ageUp((int)((float)(-this.getAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.func_184645_a(p_184645_1_, p_184645_2_);
   }

   protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (!p_175505_1_.abilities.instabuild) {
         p_175505_2_.shrink(1);
      }

   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable PlayerEntity p_146082_1_) {
      this.inLove = 600;
      if (p_146082_1_ != null) {
         this.loveCause = p_146082_1_.getUUID();
      }

      this.level.broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int p_204700_1_) {
      this.inLove = p_204700_1_;
   }

   @Nullable
   public ServerPlayerEntity getLoveCause() {
      if (this.loveCause == null) {
         return null;
      } else {
         PlayerEntity playerentity = this.level.getPlayerByUUID(this.loveCause);
         return playerentity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerentity : null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetLove() {
      this.inLove = 0;
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (p_70878_1_.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && p_70878_1_.isInLove();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 18) {
         for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
         }
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }
}