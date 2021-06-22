package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DamagingProjectileEntity extends Entity {
   public LivingEntity field_70235_a;
   private int field_70236_j;
   private int field_70234_an;
   public double xPower;
   public double yPower;
   public double zPower;

   protected DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50173_1_, World p_i50173_2_) {
      super(p_i50173_1_, p_i50173_2_);
   }

   public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50174_1_, double p_i50174_2_, double p_i50174_4_, double p_i50174_6_, double p_i50174_8_, double p_i50174_10_, double p_i50174_12_, World p_i50174_14_) {
      this(p_i50174_1_, p_i50174_14_);
      this.moveTo(p_i50174_2_, p_i50174_4_, p_i50174_6_, this.yRot, this.xRot);
      this.setPos(p_i50174_2_, p_i50174_4_, p_i50174_6_);
      double d0 = (double)MathHelper.sqrt(p_i50174_8_ * p_i50174_8_ + p_i50174_10_ * p_i50174_10_ + p_i50174_12_ * p_i50174_12_);
      this.xPower = p_i50174_8_ / d0 * 0.1D;
      this.yPower = p_i50174_10_ / d0 * 0.1D;
      this.zPower = p_i50174_12_ / d0 * 0.1D;
   }

   public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50175_1_, LivingEntity p_i50175_2_, double p_i50175_3_, double p_i50175_5_, double p_i50175_7_, World p_i50175_9_) {
      this(p_i50175_1_, p_i50175_9_);
      this.field_70235_a = p_i50175_2_;
      this.moveTo(p_i50175_2_.getX(), p_i50175_2_.getY(), p_i50175_2_.getZ(), p_i50175_2_.yRot, p_i50175_2_.xRot);
      this.reapplyPosition();
      this.setDeltaMovement(Vec3d.ZERO);
      p_i50175_3_ = p_i50175_3_ + this.random.nextGaussian() * 0.4D;
      p_i50175_5_ = p_i50175_5_ + this.random.nextGaussian() * 0.4D;
      p_i50175_7_ = p_i50175_7_ + this.random.nextGaussian() * 0.4D;
      double d0 = (double)MathHelper.sqrt(p_i50175_3_ * p_i50175_3_ + p_i50175_5_ * p_i50175_5_ + p_i50175_7_ * p_i50175_7_);
      this.xPower = p_i50175_3_ / d0 * 0.1D;
      this.yPower = p_i50175_5_ / d0 * 0.1D;
      this.zPower = p_i50175_7_ / d0 * 0.1D;
   }

   protected void defineSynchedData() {
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   public void tick() {
      if (this.level.isClientSide || (this.field_70235_a == null || !this.field_70235_a.removed) && this.level.hasChunkAt(new BlockPos(this))) {
         super.tick();
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         ++this.field_70234_an;
         RayTraceResult raytraceresult = ProjectileHelper.func_221266_a(this, true, this.field_70234_an >= 25, this.field_70235_a, RayTraceContext.BlockMode.COLLIDER);
         if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
         }

         Vec3d vec3d = this.getDeltaMovement();
         double d0 = this.getX() + vec3d.x;
         double d1 = this.getY() + vec3d.y;
         double d2 = this.getZ() + vec3d.z;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         float f = this.getInertia();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }

            f = 0.8F;
         }

         this.setDeltaMovement(vec3d.add(this.xPower, this.yPower, this.zPower).scale((double)f));
         this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
         this.setPos(d0, d1, d2);
      } else {
         this.remove();
      }
   }

   protected boolean shouldBurn() {
      return true;
   }

   protected IParticleData getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   protected float getInertia() {
      return 0.95F;
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      RayTraceResult.Type raytraceresult$type = p_70227_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_70227_1_;
         BlockState blockstate = this.level.getBlockState(blockraytraceresult.getBlockPos());
         blockstate.onProjectileHit(this.level, blockstate, blockraytraceresult, this);
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      Vec3d vec3d = this.getDeltaMovement();
      p_213281_1_.put("direction", this.newDoubleList(new double[]{vec3d.x, vec3d.y, vec3d.z}));
      p_213281_1_.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
      p_213281_1_.putInt("life", this.field_70236_j);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("power", 9)) {
         ListNBT listnbt = p_70037_1_.getList("power", 6);
         if (listnbt.size() == 3) {
            this.xPower = listnbt.getDouble(0);
            this.yPower = listnbt.getDouble(1);
            this.zPower = listnbt.getDouble(2);
         }
      }

      this.field_70236_j = p_70037_1_.getInt("life");
      if (p_70037_1_.contains("direction", 9) && p_70037_1_.getList("direction", 6).size() == 3) {
         ListNBT listnbt1 = p_70037_1_.getList("direction", 6);
         this.setDeltaMovement(listnbt1.getDouble(0), listnbt1.getDouble(1), listnbt1.getDouble(2));
      } else {
         this.remove();
      }

   }

   public boolean isPickable() {
      return true;
   }

   public float getPickRadius() {
      return 1.0F;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markHurt();
         if (p_70097_1_.getEntity() != null) {
            Vec3d vec3d = p_70097_1_.getEntity().getLookAngle();
            this.setDeltaMovement(vec3d);
            this.xPower = vec3d.x * 0.1D;
            this.yPower = vec3d.y * 0.1D;
            this.zPower = vec3d.z * 0.1D;
            if (p_70097_1_.getEntity() instanceof LivingEntity) {
               this.field_70235_a = (LivingEntity)p_70097_1_.getEntity();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   public IPacket<?> getAddEntityPacket() {
      int i = this.field_70235_a == null ? 0 : this.field_70235_a.getId();
      return new SSpawnObjectPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.xRot, this.yRot, this.getType(), i, new Vec3d(this.xPower, this.yPower, this.zPower));
   }
}
