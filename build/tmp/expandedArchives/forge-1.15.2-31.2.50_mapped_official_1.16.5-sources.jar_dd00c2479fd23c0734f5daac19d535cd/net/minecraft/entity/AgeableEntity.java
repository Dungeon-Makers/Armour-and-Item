package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AgeableEntity extends CreatureEntity {
   private static final DataParameter<Boolean> DATA_BABY_ID = EntityDataManager.defineId(AgeableEntity.class, DataSerializers.BOOLEAN);
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgeableEntity(EntityType<? extends AgeableEntity> p_i48581_1_, World p_i48581_2_) {
      super(p_i48581_1_, p_i48581_2_);
   }

   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
      }

      AgeableEntity.AgeableData ageableentity$ageabledata = (AgeableEntity.AgeableData)p_213386_4_;
      if (ageableentity$ageabledata.isShouldSpawnBaby() && ageableentity$ageabledata.getGroupSize() > 0 && this.random.nextFloat() <= ageableentity$ageabledata.getBabySpawnChance()) {
         this.setAge(-24000);
      }

      ageableentity$ageabledata.increaseGroupSizeByOne();
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   @Nullable
   public abstract AgeableEntity func_90011_a(AgeableEntity p_90011_1_);

   protected void onOffspringSpawnedFromEgg(PlayerEntity p_213406_1_, AgeableEntity p_213406_2_) {
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      Item item = itemstack.getItem();
      if (item instanceof SpawnEggItem && ((SpawnEggItem)item).spawnsEntity(itemstack.getTag(), this.getType())) {
         if (!this.level.isClientSide) {
            AgeableEntity ageableentity = this.func_90011_a(this);
            if (ageableentity != null) {
               ageableentity.setAge(-24000);
               ageableentity.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
               this.level.addFreshEntity(ageableentity);
               if (itemstack.hasCustomHoverName()) {
                  ageableentity.setCustomName(itemstack.getHoverName());
               }

               this.onOffspringSpawnedFromEgg(p_184645_1_, ageableentity);
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
   }

   public int getAge() {
      if (this.level.isClientSide) {
         return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
      } else {
         return this.age;
      }
   }

   public void ageUp(int p_175501_1_, boolean p_175501_2_) {
      int i = this.getAge();
      i = i + p_175501_1_ * 20;
      if (i > 0) {
         i = 0;
      }

      int j = i - i;
      this.setAge(i);
      if (p_175501_2_) {
         this.forcedAge += j;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getAge() == 0) {
         this.setAge(this.forcedAge);
      }

   }

   public void ageUp(int p_110195_1_) {
      this.ageUp(p_110195_1_, false);
   }

   public void setAge(int p_70873_1_) {
      int i = this.age;
      this.age = p_70873_1_;
      if (i < 0 && p_70873_1_ >= 0 || i >= 0 && p_70873_1_ < 0) {
         this.entityData.set(DATA_BABY_ID, p_70873_1_ < 0);
         this.ageBoundaryReached();
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Age", this.getAge());
      p_213281_1_.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setAge(p_70037_1_.getInt("Age"));
      this.forcedAge = p_70037_1_.getInt("ForcedAge");
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_BABY_ID.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else if (this.isAlive()) {
         int i = this.getAge();
         if (i < 0) {
            ++i;
            this.setAge(i);
         } else if (i > 0) {
            --i;
            this.setAge(i);
         }
      }

   }

   protected void ageBoundaryReached() {
   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }

   public static class AgeableData implements ILivingEntityData {
      private int groupSize;
      private boolean shouldSpawnBaby = true;
      private float babySpawnChance = 0.05F;

      public int getGroupSize() {
         return this.groupSize;
      }

      public void increaseGroupSizeByOne() {
         ++this.groupSize;
      }

      public boolean isShouldSpawnBaby() {
         return this.shouldSpawnBaby;
      }

      public void func_226259_a_(boolean p_226259_1_) {
         this.shouldSpawnBaby = p_226259_1_;
      }

      public float getBabySpawnChance() {
         return this.babySpawnChance;
      }

      public void func_226258_a_(float p_226258_1_) {
         this.babySpawnChance = p_226258_1_;
      }
   }
}