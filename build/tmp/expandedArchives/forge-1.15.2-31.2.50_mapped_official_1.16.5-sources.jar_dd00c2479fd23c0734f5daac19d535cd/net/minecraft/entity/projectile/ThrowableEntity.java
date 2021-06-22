package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ThrowableEntity extends Entity implements IProjectile {
   private int field_145788_c = -1;
   private int field_145786_d = -1;
   private int field_145787_e = -1;
   protected boolean field_174854_a;
   public int field_70191_b;
   protected LivingEntity field_70192_c;
   private UUID field_200218_h;
   private Entity field_184539_c;
   private int field_184540_av;

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48540_1_, World p_i48540_2_) {
      super(p_i48540_1_, p_i48540_2_);
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48541_1_, double p_i48541_2_, double p_i48541_4_, double p_i48541_6_, World p_i48541_8_) {
      this(p_i48541_1_, p_i48541_8_);
      this.setPos(p_i48541_2_, p_i48541_4_, p_i48541_6_);
   }

   protected ThrowableEntity(EntityType<? extends ThrowableEntity> p_i48542_1_, LivingEntity p_i48542_2_, World p_i48542_3_) {
      this(p_i48542_1_, p_i48542_2_.getX(), p_i48542_2_.getEyeY() - (double)0.1F, p_i48542_2_.getZ(), p_i48542_3_);
      this.field_70192_c = p_i48542_2_;
      this.field_200218_h = p_i48542_2_.getUUID();
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

   public void func_184538_a(Entity p_184538_1_, float p_184538_2_, float p_184538_3_, float p_184538_4_, float p_184538_5_, float p_184538_6_) {
      float f = -MathHelper.sin(p_184538_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184538_2_ * ((float)Math.PI / 180F));
      float f1 = -MathHelper.sin((p_184538_2_ + p_184538_4_) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(p_184538_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184538_2_ * ((float)Math.PI / 180F));
      this.shoot((double)f, (double)f1, (double)f2, p_184538_5_, p_184538_6_);
      Vec3d vec3d = p_184538_1_.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, p_184538_1_.onGround ? 0.0D : vec3d.y, vec3d.z));
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setDeltaMovement(vec3d);
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
      this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.yRot = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.xRot = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

   }

   public void tick() {
      super.tick();
      if (this.field_70191_b > 0) {
         --this.field_70191_b;
      }

      if (this.field_174854_a) {
         this.field_174854_a = false;
         this.setDeltaMovement(this.getDeltaMovement().multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      }

      AxisAlignedBB axisalignedbb = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D);

      for(Entity entity : this.level.getEntities(this, axisalignedbb, (p_213881_0_) -> {
         return !p_213881_0_.isSpectator() && p_213881_0_.isPickable();
      })) {
         if (entity == this.field_184539_c) {
            ++this.field_184540_av;
            break;
         }

         if (this.field_70192_c != null && this.tickCount < 2 && this.field_184539_c == null) {
            this.field_184539_c = entity;
            this.field_184540_av = 3;
            break;
         }
      }

      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, axisalignedbb, (p_213880_1_) -> {
         return !p_213880_1_.isSpectator() && p_213880_1_.isPickable() && p_213880_1_ != this.field_184539_c;
      }, RayTraceContext.BlockMode.OUTLINE, true);
      if (this.field_184539_c != null && this.field_184540_av-- <= 0) {
         this.field_184539_c = null;
      }

      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && this.level.getBlockState(((BlockRayTraceResult)raytraceresult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.handleInsidePortal(((BlockRayTraceResult)raytraceresult).getBlockPos());
         } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)){
            this.func_70184_a(raytraceresult);
         }
      }

      Vec3d vec3d = this.getDeltaMovement();
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
      float f1;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            float f2 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
         }

         f1 = 0.8F;
      } else {
         f1 = 0.99F;
      }

      this.setDeltaMovement(vec3d.scale((double)f1));
      if (!this.isNoGravity()) {
         Vec3d vec3d1 = this.getDeltaMovement();
         this.setDeltaMovement(vec3d1.x, vec3d1.y - (double)this.getGravity(), vec3d1.z);
      }

      this.setPos(d0, d1, d2);
   }

   protected float getGravity() {
      return 0.03F;
   }

   protected abstract void func_70184_a(RayTraceResult p_70184_1_);

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("xTile", this.field_145788_c);
      p_213281_1_.putInt("yTile", this.field_145786_d);
      p_213281_1_.putInt("zTile", this.field_145787_e);
      p_213281_1_.putByte("shake", (byte)this.field_70191_b);
      p_213281_1_.putBoolean("inGround", this.field_174854_a);
      if (this.field_200218_h != null) {
         p_213281_1_.put("owner", NBTUtil.func_186862_a(this.field_200218_h));
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.field_145788_c = p_70037_1_.getInt("xTile");
      this.field_145786_d = p_70037_1_.getInt("yTile");
      this.field_145787_e = p_70037_1_.getInt("zTile");
      this.field_70191_b = p_70037_1_.getByte("shake") & 255;
      this.field_174854_a = p_70037_1_.getBoolean("inGround");
      this.field_70192_c = null;
      if (p_70037_1_.contains("owner", 10)) {
         this.field_200218_h = NBTUtil.loadUUID(p_70037_1_.getCompound("owner"));
      }

   }

   @Nullable
   public LivingEntity func_85052_h() {
      if ((this.field_70192_c == null || this.field_70192_c.removed) && this.field_200218_h != null && this.level instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.level).getEntity(this.field_200218_h);
         if (entity instanceof LivingEntity) {
            this.field_70192_c = (LivingEntity)entity;
         } else {
            this.field_70192_c = null;
         }
      }

      return this.field_70192_c;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
