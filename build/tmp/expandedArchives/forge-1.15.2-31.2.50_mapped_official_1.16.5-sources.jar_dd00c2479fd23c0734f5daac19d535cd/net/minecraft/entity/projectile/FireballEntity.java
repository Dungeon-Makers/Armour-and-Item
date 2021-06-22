package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireballEntity extends AbstractFireballEntity {
   public int explosionPower = 1;

   public FireballEntity(EntityType<? extends FireballEntity> p_i50163_1_, World p_i50163_2_) {
      super(p_i50163_1_, p_i50163_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public FireballEntity(World p_i1768_1_, double p_i1768_2_, double p_i1768_4_, double p_i1768_6_, double p_i1768_8_, double p_i1768_10_, double p_i1768_12_) {
      super(EntityType.FIREBALL, p_i1768_2_, p_i1768_4_, p_i1768_6_, p_i1768_8_, p_i1768_10_, p_i1768_12_, p_i1768_1_);
   }

   public FireballEntity(World p_i1769_1_, LivingEntity p_i1769_2_, double p_i1769_3_, double p_i1769_5_, double p_i1769_7_) {
      super(EntityType.FIREBALL, p_i1769_2_, p_i1769_3_, p_i1769_5_, p_i1769_7_, p_i1769_1_);
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         if (p_70227_1_.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)p_70227_1_).getEntity();
            entity.hurt(DamageSource.func_76362_a(this, this.field_70235_a), 6.0F);
            this.doEnchantDamageEffects(this.field_70235_a, entity);
         }

         boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.field_70235_a);
         this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
         this.remove();
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("ExplosionPower", 99)) {
         this.explosionPower = p_70037_1_.getInt("ExplosionPower");
      }

   }
}
