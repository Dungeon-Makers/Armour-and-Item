package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
   public CowEntity(EntityType<? extends CowEntity> p_i48567_1_, World p_i48567_2_) {
      super(p_i48567_1_, p_i48567_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.2F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.COW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (itemstack.getItem() == Items.BUCKET && !p_184645_1_.abilities.instabuild && !this.isBaby()) {
         p_184645_1_.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         itemstack.shrink(1);
         if (itemstack.isEmpty()) {
            p_184645_1_.setItemInHand(p_184645_2_, new ItemStack(Items.MILK_BUCKET));
         } else if (!p_184645_1_.inventory.add(new ItemStack(Items.MILK_BUCKET))) {
            p_184645_1_.drop(new ItemStack(Items.MILK_BUCKET), false);
         }

         return true;
      } else {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      }
   }

   public CowEntity func_90011_a(AgeableEntity p_90011_1_) {
      return EntityType.COW.create(this.level);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isBaby() ? p_213348_2_.height * 0.95F : 1.3F;
   }
}