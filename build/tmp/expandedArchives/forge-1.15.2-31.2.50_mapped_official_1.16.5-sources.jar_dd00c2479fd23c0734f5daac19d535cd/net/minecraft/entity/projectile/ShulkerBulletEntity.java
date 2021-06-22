package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBulletEntity extends Entity {
   private LivingEntity field_184570_a;
   private Entity finalTarget;
   @Nullable
   private Direction currentMoveDirection;
   private int flightSteps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID field_184580_h;
   private BlockPos field_184572_as;
   @Nullable
   private UUID targetId;
   private BlockPos field_184576_au;

   public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> p_i50161_1_, World p_i50161_2_) {
      super(p_i50161_1_, p_i50161_2_);
      this.noPhysics = true;
   }

   @OnlyIn(Dist.CLIENT)
   public ShulkerBulletEntity(World p_i46771_1_, double p_i46771_2_, double p_i46771_4_, double p_i46771_6_, double p_i46771_8_, double p_i46771_10_, double p_i46771_12_) {
      this(EntityType.SHULKER_BULLET, p_i46771_1_);
      this.moveTo(p_i46771_2_, p_i46771_4_, p_i46771_6_, this.yRot, this.xRot);
      this.setDeltaMovement(p_i46771_8_, p_i46771_10_, p_i46771_12_);
   }

   public ShulkerBulletEntity(World p_i46772_1_, LivingEntity p_i46772_2_, Entity p_i46772_3_, Direction.Axis p_i46772_4_) {
      this(EntityType.SHULKER_BULLET, p_i46772_1_);
      this.field_184570_a = p_i46772_2_;
      BlockPos blockpos = new BlockPos(p_i46772_2_);
      double d0 = (double)blockpos.getX() + 0.5D;
      double d1 = (double)blockpos.getY() + 0.5D;
      double d2 = (double)blockpos.getZ() + 0.5D;
      this.moveTo(d0, d1, d2, this.yRot, this.xRot);
      this.finalTarget = p_i46772_3_;
      this.currentMoveDirection = Direction.UP;
      this.selectNextMoveDirection(p_i46772_4_);
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.HOSTILE;
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      if (this.field_184570_a != null) {
         BlockPos blockpos = new BlockPos(this.field_184570_a);
         CompoundNBT compoundnbt = NBTUtil.func_186862_a(this.field_184570_a.getUUID());
         compoundnbt.putInt("X", blockpos.getX());
         compoundnbt.putInt("Y", blockpos.getY());
         compoundnbt.putInt("Z", blockpos.getZ());
         p_213281_1_.put("Owner", compoundnbt);
      }

      if (this.finalTarget != null) {
         BlockPos blockpos1 = new BlockPos(this.finalTarget);
         CompoundNBT compoundnbt1 = NBTUtil.func_186862_a(this.finalTarget.getUUID());
         compoundnbt1.putInt("X", blockpos1.getX());
         compoundnbt1.putInt("Y", blockpos1.getY());
         compoundnbt1.putInt("Z", blockpos1.getZ());
         p_213281_1_.put("Target", compoundnbt1);
      }

      if (this.currentMoveDirection != null) {
         p_213281_1_.putInt("Dir", this.currentMoveDirection.get3DDataValue());
      }

      p_213281_1_.putInt("Steps", this.flightSteps);
      p_213281_1_.putDouble("TXD", this.targetDeltaX);
      p_213281_1_.putDouble("TYD", this.targetDeltaY);
      p_213281_1_.putDouble("TZD", this.targetDeltaZ);
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.flightSteps = p_70037_1_.getInt("Steps");
      this.targetDeltaX = p_70037_1_.getDouble("TXD");
      this.targetDeltaY = p_70037_1_.getDouble("TYD");
      this.targetDeltaZ = p_70037_1_.getDouble("TZD");
      if (p_70037_1_.contains("Dir", 99)) {
         this.currentMoveDirection = Direction.from3DDataValue(p_70037_1_.getInt("Dir"));
      }

      if (p_70037_1_.contains("Owner", 10)) {
         CompoundNBT compoundnbt = p_70037_1_.getCompound("Owner");
         this.field_184580_h = NBTUtil.loadUUID(compoundnbt);
         this.field_184572_as = new BlockPos(compoundnbt.getInt("X"), compoundnbt.getInt("Y"), compoundnbt.getInt("Z"));
      }

      if (p_70037_1_.contains("Target", 10)) {
         CompoundNBT compoundnbt1 = p_70037_1_.getCompound("Target");
         this.targetId = NBTUtil.loadUUID(compoundnbt1);
         this.field_184576_au = new BlockPos(compoundnbt1.getInt("X"), compoundnbt1.getInt("Y"), compoundnbt1.getInt("Z"));
      }

   }

   protected void defineSynchedData() {
   }

   private void setMoveDirection(@Nullable Direction p_184568_1_) {
      this.currentMoveDirection = p_184568_1_;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis p_184569_1_) {
      double d0 = 0.5D;
      BlockPos blockpos;
      if (this.finalTarget == null) {
         blockpos = (new BlockPos(this)).below();
      } else {
         d0 = (double)this.finalTarget.getBbHeight() * 0.5D;
         blockpos = new BlockPos(this.finalTarget.getX(), this.finalTarget.getY() + d0, this.finalTarget.getZ());
      }

      double d1 = (double)blockpos.getX() + 0.5D;
      double d2 = (double)blockpos.getY() + d0;
      double d3 = (double)blockpos.getZ() + 0.5D;
      Direction direction = null;
      if (!blockpos.closerThan(this.position(), 2.0D)) {
         BlockPos blockpos1 = new BlockPos(this);
         List<Direction> list = Lists.newArrayList();
         if (p_184569_1_ != Direction.Axis.X) {
            if (blockpos1.getX() < blockpos.getX() && this.level.isEmptyBlock(blockpos1.east())) {
               list.add(Direction.EAST);
            } else if (blockpos1.getX() > blockpos.getX() && this.level.isEmptyBlock(blockpos1.west())) {
               list.add(Direction.WEST);
            }
         }

         if (p_184569_1_ != Direction.Axis.Y) {
            if (blockpos1.getY() < blockpos.getY() && this.level.isEmptyBlock(blockpos1.above())) {
               list.add(Direction.UP);
            } else if (blockpos1.getY() > blockpos.getY() && this.level.isEmptyBlock(blockpos1.below())) {
               list.add(Direction.DOWN);
            }
         }

         if (p_184569_1_ != Direction.Axis.Z) {
            if (blockpos1.getZ() < blockpos.getZ() && this.level.isEmptyBlock(blockpos1.south())) {
               list.add(Direction.SOUTH);
            } else if (blockpos1.getZ() > blockpos.getZ() && this.level.isEmptyBlock(blockpos1.north())) {
               list.add(Direction.NORTH);
            }
         }

         direction = Direction.func_176741_a(this.random);
         if (list.isEmpty()) {
            for(int i = 5; !this.level.isEmptyBlock(blockpos1.relative(direction)) && i > 0; --i) {
               direction = Direction.func_176741_a(this.random);
            }
         } else {
            direction = list.get(this.random.nextInt(list.size()));
         }

         d1 = this.getX() + (double)direction.getStepX();
         d2 = this.getY() + (double)direction.getStepY();
         d3 = this.getZ() + (double)direction.getStepZ();
      }

      this.setMoveDirection(direction);
      double d6 = d1 - this.getX();
      double d7 = d2 - this.getY();
      double d4 = d3 - this.getZ();
      double d5 = (double)MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
      if (d5 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = d6 / d5 * 0.15D;
         this.targetDeltaY = d7 / d5 * 0.15D;
         this.targetDeltaZ = d4 / d5 * 0.15D;
      }

      this.hasImpulse = true;
      this.flightSteps = 10 + this.random.nextInt(5) * 10;
   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         if (this.finalTarget == null && this.targetId != null) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(this.field_184576_au.offset(-2, -2, -2), this.field_184576_au.offset(2, 2, 2)))) {
               if (livingentity.getUUID().equals(this.targetId)) {
                  this.finalTarget = livingentity;
                  break;
               }
            }

            this.targetId = null;
         }

         if (this.field_184570_a == null && this.field_184580_h != null) {
            for(LivingEntity livingentity1 : this.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(this.field_184572_as.offset(-2, -2, -2), this.field_184572_as.offset(2, 2, 2)))) {
               if (livingentity1.getUUID().equals(this.field_184580_h)) {
                  this.field_184570_a = livingentity1;
                  break;
               }
            }

            this.field_184580_h = null;
         }

         if (this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof PlayerEntity && ((PlayerEntity)this.finalTarget).isSpectator()) {
            if (!this.isNoGravity()) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
         } else {
            this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
            this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
            this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
            Vec3d vec3d = this.getDeltaMovement();
            this.setDeltaMovement(vec3d.add((this.targetDeltaX - vec3d.x) * 0.2D, (this.targetDeltaY - vec3d.y) * 0.2D, (this.targetDeltaZ - vec3d.z) * 0.2D));
         }

         RayTraceResult raytraceresult = ProjectileHelper.func_221266_a(this, true, false, this.field_184570_a, RayTraceContext.BlockMode.COLLIDER);
            if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.func_184567_a(raytraceresult);
         }
      }

      Vec3d vec3d1 = this.getDeltaMovement();
      this.setPos(this.getX() + vec3d1.x, this.getY() + vec3d1.y, this.getZ() + vec3d1.z);
      ProjectileHelper.rotateTowardsMovement(this, 0.5F);
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.END_ROD, this.getX() - vec3d1.x, this.getY() - vec3d1.y + 0.15D, this.getZ() - vec3d1.z, 0.0D, 0.0D, 0.0D);
      } else if (this.finalTarget != null && !this.finalTarget.removed) {
         if (this.flightSteps > 0) {
            --this.flightSteps;
            if (this.flightSteps == 0) {
               this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
            }
         }

         if (this.currentMoveDirection != null) {
            BlockPos blockpos1 = new BlockPos(this);
            Direction.Axis direction$axis = this.currentMoveDirection.getAxis();
            if (this.level.loadedAndEntityCanStandOn(blockpos1.relative(this.currentMoveDirection), this)) {
               this.selectNextMoveDirection(direction$axis);
            } else {
               BlockPos blockpos = new BlockPos(this.finalTarget);
               if (direction$axis == Direction.Axis.X && blockpos1.getX() == blockpos.getX() || direction$axis == Direction.Axis.Z && blockpos1.getZ() == blockpos.getZ() || direction$axis == Direction.Axis.Y && blockpos1.getY() == blockpos.getY()) {
                  this.selectNextMoveDirection(direction$axis);
               }
            }
         }
      }

   }

   public boolean isOnFire() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      return p_70112_1_ < 16384.0D;
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected void func_184567_a(RayTraceResult p_184567_1_) {
      if (p_184567_1_.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)p_184567_1_).getEntity();
         boolean flag = entity.hurt(DamageSource.indirectMobAttack(this, this.field_184570_a).setProjectile(), 4.0F);
         if (flag) {
            this.doEnchantDamageEffects(this.field_184570_a, entity);
            if (entity instanceof LivingEntity) {
               ((LivingEntity)entity).addEffect(new EffectInstance(Effects.LEVITATION, 200));
            }
         }
      } else {
         ((ServerWorld)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
      }

      this.remove();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.level.isClientSide) {
         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerWorld)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.remove();
      }

      return true;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
