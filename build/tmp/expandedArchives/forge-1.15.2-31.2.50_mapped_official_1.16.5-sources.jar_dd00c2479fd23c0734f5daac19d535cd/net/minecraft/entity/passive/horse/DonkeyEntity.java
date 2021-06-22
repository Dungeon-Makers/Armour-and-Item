package net.minecraft.entity.passive.horse;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity extends AbstractChestedHorseEntity {
   public DonkeyEntity(EntityType<? extends DonkeyEntity> p_i50239_1_, World p_i50239_2_) {
      super(p_i50239_1_, p_i50239_2_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.DONKEY_HURT;
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof DonkeyEntity) && !(p_70878_1_ instanceof HorseEntity)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorseEntity)p_70878_1_).canParent();
      }
   }

   public AgeableEntity func_90011_a(AgeableEntity p_90011_1_) {
      EntityType<? extends AbstractHorseEntity> entitytype = p_90011_1_ instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorseEntity abstracthorseentity = entitytype.create(this.level);
      this.setOffspringAttributes(p_90011_1_, abstracthorseentity);
      return abstracthorseentity;
   }
}