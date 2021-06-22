package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity extends AbstractFireballEntity {
   public SmallFireballEntity(EntityType<? extends SmallFireballEntity> p_i50160_1_, World p_i50160_2_) {
      super(p_i50160_1_, p_i50160_2_);
   }

   public SmallFireballEntity(World p_i1771_1_, LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
      super(EntityType.SMALL_FIREBALL, p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_);
   }

   public SmallFireballEntity(World p_i1772_1_, double p_i1772_2_, double p_i1772_4_, double p_i1772_6_, double p_i1772_8_, double p_i1772_10_, double p_i1772_12_) {
      super(EntityType.SMALL_FIREBALL, p_i1772_2_, p_i1772_4_, p_i1772_6_, p_i1772_8_, p_i1772_10_, p_i1772_12_, p_i1772_1_);
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         if (p_70227_1_.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)p_70227_1_).getEntity();
            if (!entity.func_70045_F()) {
               int i = entity.getRemainingFireTicks();
               entity.setSecondsOnFire(5);
               boolean flag = entity.hurt(DamageSource.func_76362_a(this, this.field_70235_a), 5.0F);
               if (flag) {
                  this.doEnchantDamageEffects(this.field_70235_a, entity);
               } else {
                  entity.func_223308_g(i);
               }
            }
         } else if (this.field_70235_a == null || !(this.field_70235_a instanceof MobEntity) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.field_70235_a)) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_70227_1_;
            BlockPos blockpos = blockraytraceresult.getBlockPos().relative(blockraytraceresult.getDirection());
            if (this.level.isEmptyBlock(blockpos)) {
               this.level.setBlockAndUpdate(blockpos, Blocks.FIRE.defaultBlockState());
            }
         }

         this.remove();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }
}
