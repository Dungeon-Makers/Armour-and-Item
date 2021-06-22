package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ZombieHorseEntity extends AbstractHorseEntity {
   public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> p_i50233_1_, World p_i50233_2_) {
      super(p_i50233_1_, p_i50233_2_);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.2F);
      this.getAttribute(field_110271_bv).setBaseValue(this.generateRandomJumpStrength());
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public AgeableEntity func_90011_a(AgeableEntity p_90011_1_) {
      return EntityType.ZOMBIE_HORSE.create(this.level);
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
            if (!this.isSaddled() && itemstack.getItem() == Items.SADDLE) {
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

   protected void addBehaviourGoals() {
   }
}