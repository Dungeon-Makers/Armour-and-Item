package net.minecraft.entity.passive.horse;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MuleEntity extends AbstractChestedHorseEntity {
   public MuleEntity(EntityType<? extends MuleEntity> p_i50236_1_, World p_i50236_2_) {
      super(p_i50236_1_, p_i50236_2_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.MULE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.MULE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.MULE_HURT;
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public AgeableEntity func_90011_a(AgeableEntity p_90011_1_) {
      return EntityType.MULE.create(this.level);
   }
}