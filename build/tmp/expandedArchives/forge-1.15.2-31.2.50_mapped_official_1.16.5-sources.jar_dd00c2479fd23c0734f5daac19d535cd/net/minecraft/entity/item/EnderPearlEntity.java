package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderPearlEntity extends ProjectileItemEntity {
   private LivingEntity field_181555_c;

   public EnderPearlEntity(EntityType<? extends EnderPearlEntity> p_i50153_1_, World p_i50153_2_) {
      super(p_i50153_1_, p_i50153_2_);
   }

   public EnderPearlEntity(World p_i1783_1_, LivingEntity p_i1783_2_) {
      super(EntityType.ENDER_PEARL, p_i1783_2_, p_i1783_1_);
      this.field_181555_c = p_i1783_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public EnderPearlEntity(World p_i1784_1_, double p_i1784_2_, double p_i1784_4_, double p_i1784_6_) {
      super(EntityType.ENDER_PEARL, p_i1784_2_, p_i1784_4_, p_i1784_6_, p_i1784_1_);
   }

   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   protected void func_70184_a(RayTraceResult p_70184_1_) {
      LivingEntity livingentity = this.func_85052_h();
      if (p_70184_1_.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)p_70184_1_).getEntity();
         if (entity == this.field_181555_c) {
            return;
         }

         entity.hurt(DamageSource.thrown(this, livingentity), 0.0F);
      }

      if (p_70184_1_.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)p_70184_1_).getBlockPos();
         TileEntity tileentity = this.level.getBlockEntity(blockpos);
         if (tileentity instanceof EndGatewayTileEntity) {
            EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
            if (livingentity != null) {
               if (livingentity instanceof ServerPlayerEntity) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayerEntity)livingentity, this.level.getBlockState(blockpos));
               }

               endgatewaytileentity.teleportEntity(livingentity);
               this.remove();
               return;
            }

            endgatewaytileentity.teleportEntity(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
      }

      if (!this.level.isClientSide) {
         if (livingentity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
            if (serverplayerentity.connection.getConnection().isConnected() && serverplayerentity.level == this.level && !serverplayerentity.isSleeping()) {
               net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(serverplayerentity, this.getX(), this.getY(), this.getZ(), 5.0F);
               if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
               if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.level);
                  endermiteentity.setPlayerSpawned(true);
                  endermiteentity.moveTo(livingentity.getX(), livingentity.getY(), livingentity.getZ(), livingentity.yRot, livingentity.xRot);
                  this.level.addFreshEntity(endermiteentity);
               }

               if (livingentity.isPassenger()) {
                  livingentity.stopRiding();
               }

               livingentity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               livingentity.fallDistance = 0.0F;
               livingentity.hurt(DamageSource.FALL, event.getAttackDamage());
               } //Forge: End
            }
         } else if (livingentity != null) {
            livingentity.teleportTo(this.getX(), this.getY(), this.getZ());
            livingentity.fallDistance = 0.0F;
         }

         this.remove();
      }

   }

   public void tick() {
      LivingEntity livingentity = this.func_85052_h();
      if (livingentity != null && livingentity instanceof PlayerEntity && !livingentity.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (this.field_70192_c.field_71093_bK != p_212321_1_) {
         this.field_70192_c = null;
      }

      return super.changeDimension(p_212321_1_, teleporter);
   }
}
