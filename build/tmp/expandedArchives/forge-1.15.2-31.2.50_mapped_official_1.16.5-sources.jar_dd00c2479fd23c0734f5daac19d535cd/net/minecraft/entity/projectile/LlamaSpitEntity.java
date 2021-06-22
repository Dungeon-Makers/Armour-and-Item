package net.minecraft.entity.projectile;

import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LlamaSpitEntity extends Entity implements IProjectile {
   public LlamaEntity field_190539_a;
   private CompoundNBT field_190540_b;

   public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> p_i50162_1_, World p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public LlamaSpitEntity(World p_i47273_1_, LlamaEntity p_i47273_2_) {
      this(EntityType.LLAMA_SPIT, p_i47273_1_);
      this.field_190539_a = p_i47273_2_;
      this.setPos(p_i47273_2_.getX() - (double)(p_i47273_2_.getBbWidth() + 1.0F) * 0.5D * (double)MathHelper.sin(p_i47273_2_.yBodyRot * ((float)Math.PI / 180F)), p_i47273_2_.getEyeY() - (double)0.1F, p_i47273_2_.getZ() + (double)(p_i47273_2_.getBbWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(p_i47273_2_.yBodyRot * ((float)Math.PI / 180F)));
   }

   @OnlyIn(Dist.CLIENT)
   public LlamaSpitEntity(World p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(EntityType.LLAMA_SPIT, p_i47274_1_);
      this.setPos(p_i47274_2_, p_i47274_4_, p_i47274_6_);

      for(int i = 0; i < 7; ++i) {
         double d0 = 0.4D + 0.1D * (double)i;
         p_i47274_1_.addParticle(ParticleTypes.SPIT, p_i47274_2_, p_i47274_4_, p_i47274_6_, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
      }

      this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
   }

   public void tick() {
      super.tick();
      if (this.field_190540_b != null) {
         this.func_190537_j();
      }

      Vec3d vec3d = this.getDeltaMovement();
      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, this.getBoundingBox().expandTowards(vec3d).inflate(1.0D), (p_213879_1_) -> {
         return !p_213879_1_.isSpectator() && p_213879_1_ != this.field_190539_a;
      }, RayTraceContext.BlockMode.OUTLINE, true);
      if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
         this.func_190536_a(raytraceresult);
      }

      double d0 = this.getX() + vec3d.x;
      double d1 = this.getY() + vec3d.y;
      double d2 = this.getZ() + vec3d.z;
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
      this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));

      for(this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI)); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
         ;
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = MathHelper.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = MathHelper.lerp(0.2F, this.yRotO, this.yRot);
      float f1 = 0.99F;
      float f2 = 0.06F;
      if (!this.level.func_72875_a(this.getBoundingBox(), Material.AIR)) {
         this.remove();
      } else if (this.isInWaterOrBubble()) {
         this.remove();
      } else {
         this.setDeltaMovement(vec3d.scale((double)0.99F));
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06F, 0.0D));
         }

         this.setPos(d0, d1, d2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.xRot = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.yRot = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setDeltaMovement(vec3d);
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
      this.yRot = (float)(MathHelper.atan2(vec3d.x, p_70186_5_) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void func_190536_a(RayTraceResult p_190536_1_) {
      RayTraceResult.Type raytraceresult$type = p_190536_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.ENTITY && this.field_190539_a != null) {
         ((EntityRayTraceResult)p_190536_1_).getEntity().hurt(DamageSource.indirectMobAttack(this, this.field_190539_a).setProjectile(), 1.0F);
      } else if (raytraceresult$type == RayTraceResult.Type.BLOCK && !this.level.isClientSide) {
         this.remove();
      }

   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("Owner", 10)) {
         this.field_190540_b = p_70037_1_.getCompound("Owner");
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      if (this.field_190539_a != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         UUID uuid = this.field_190539_a.getUUID();
         compoundnbt.putUUID("OwnerUUID", uuid);
         p_213281_1_.put("Owner", compoundnbt);
      }

   }

   private void func_190537_j() {
      if (this.field_190540_b != null && this.field_190540_b.hasUUID("OwnerUUID")) {
         UUID uuid = this.field_190540_b.getUUID("OwnerUUID");

         for(LlamaEntity llamaentity : this.level.getEntitiesOfClass(LlamaEntity.class, this.getBoundingBox().inflate(15.0D))) {
            if (llamaentity.getUUID().equals(uuid)) {
               this.field_190539_a = llamaentity;
               break;
            }
         }
      }

      this.field_190540_b = null;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
