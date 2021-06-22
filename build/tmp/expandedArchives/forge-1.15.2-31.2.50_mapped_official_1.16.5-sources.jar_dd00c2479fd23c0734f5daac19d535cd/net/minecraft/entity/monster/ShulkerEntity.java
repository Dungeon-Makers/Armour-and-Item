package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerEntity extends GolemEntity implements IMob {
   private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_MODIFIER = (new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION)).func_111168_a(false);
   protected static final DataParameter<Direction> DATA_ATTACH_FACE_ID = EntityDataManager.defineId(ShulkerEntity.class, DataSerializers.DIRECTION);
   protected static final DataParameter<Optional<BlockPos>> DATA_ATTACH_POS_ID = EntityDataManager.defineId(ShulkerEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   protected static final DataParameter<Byte> DATA_PEEK_ID = EntityDataManager.defineId(ShulkerEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Byte> DATA_COLOR_ID = EntityDataManager.defineId(ShulkerEntity.class, DataSerializers.BYTE);
   private float currentPeekAmountO;
   private float currentPeekAmount;
   private BlockPos oldAttachPosition;
   private int clientSideTeleportInterpolation;

   public ShulkerEntity(EntityType<? extends ShulkerEntity> p_i50196_1_, World p_i50196_2_) {
      super(p_i50196_1_, p_i50196_2_);
      this.yBodyRotO = 180.0F;
      this.yBodyRot = 180.0F;
      this.oldAttachPosition = null;
      this.xpReward = 5;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.yBodyRot = 180.0F;
      this.yBodyRotO = 180.0F;
      this.yRot = 180.0F;
      this.yRotO = 180.0F;
      this.yHeadRot = 180.0F;
      this.yHeadRotO = 180.0F;
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(4, new ShulkerEntity.AttackGoal());
      this.goalSelector.addGoal(7, new ShulkerEntity.PeekGoal());
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(2, new ShulkerEntity.AttackNearestGoal(this));
      this.targetSelector.addGoal(3, new ShulkerEntity.DefenseAttackGoal(this));
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isClosed() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.SHULKER_HURT;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
      this.entityData.define(DATA_ATTACH_POS_ID, Optional.empty());
      this.entityData.define(DATA_PEEK_ID, (byte)0);
      this.entityData.define(DATA_COLOR_ID, (byte)16);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(30.0D);
   }

   protected BodyController createBodyControl() {
      return new ShulkerEntity.BodyHelperController(this);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.entityData.set(DATA_ATTACH_FACE_ID, Direction.from3DDataValue(p_70037_1_.getByte("AttachFace")));
      this.entityData.set(DATA_PEEK_ID, p_70037_1_.getByte("Peek"));
      this.entityData.set(DATA_COLOR_ID, p_70037_1_.getByte("Color"));
      if (p_70037_1_.contains("APX")) {
         int i = p_70037_1_.getInt("APX");
         int j = p_70037_1_.getInt("APY");
         int k = p_70037_1_.getInt("APZ");
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.empty());
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putByte("AttachFace", (byte)this.entityData.get(DATA_ATTACH_FACE_ID).get3DDataValue());
      p_213281_1_.putByte("Peek", this.entityData.get(DATA_PEEK_ID));
      p_213281_1_.putByte("Color", this.entityData.get(DATA_COLOR_ID));
      BlockPos blockpos = this.getAttachPosition();
      if (blockpos != null) {
         p_213281_1_.putInt("APX", blockpos.getX());
         p_213281_1_.putInt("APY", blockpos.getY());
         p_213281_1_.putInt("APZ", blockpos.getZ());
      }

   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.entityData.get(DATA_ATTACH_POS_ID).orElse((BlockPos)null);
      if (blockpos == null && !this.level.isClientSide) {
         blockpos = new BlockPos(this);
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockpos));
      }

      if (this.isPassenger()) {
         blockpos = null;
         float f = this.getVehicle().yRot;
         this.yRot = f;
         this.yBodyRot = f;
         this.yBodyRotO = f;
         this.clientSideTeleportInterpolation = 0;
      } else if (!this.level.isClientSide) {
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (!blockstate.isAir(this.level, blockpos)) {
            if (blockstate.getBlock() == Blocks.MOVING_PISTON) {
               Direction direction = blockstate.getValue(PistonBlock.FACING);
               if (this.level.isEmptyBlock(blockpos.relative(direction))) {
                  blockpos = blockpos.relative(direction);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockpos));
               } else {
                  this.teleportSomewhere();
               }
            } else if (blockstate.getBlock() == Blocks.PISTON_HEAD) {
               Direction direction2 = blockstate.getValue(PistonHeadBlock.FACING);
               if (this.level.isEmptyBlock(blockpos.relative(direction2))) {
                  blockpos = blockpos.relative(direction2);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockpos));
               } else {
                  this.teleportSomewhere();
               }
            } else {
               this.teleportSomewhere();
            }
         }

         BlockPos blockpos1 = blockpos.relative(this.getAttachFace());
         if (!this.level.loadedAndEntityCanStandOn(blockpos1, this)) {
            boolean flag = false;

            for(Direction direction1 : Direction.values()) {
               blockpos1 = blockpos.relative(direction1);
               if (this.level.loadedAndEntityCanStandOn(blockpos1, this)) {
                  this.entityData.set(DATA_ATTACH_FACE_ID, direction1);
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.teleportSomewhere();
            }
         }

         BlockPos blockpos2 = blockpos.relative(this.getAttachFace().getOpposite());
         if (this.level.loadedAndEntityCanStandOn(blockpos2, this)) {
            this.teleportSomewhere();
         }
      }

      float f1 = (float)this.getRawPeekAmount() * 0.01F;
      this.currentPeekAmountO = this.currentPeekAmount;
      if (this.currentPeekAmount > f1) {
         this.currentPeekAmount = MathHelper.clamp(this.currentPeekAmount - 0.05F, f1, 1.0F);
      } else if (this.currentPeekAmount < f1) {
         this.currentPeekAmount = MathHelper.clamp(this.currentPeekAmount + 0.05F, 0.0F, f1);
      }

      if (blockpos != null) {
         if (this.level.isClientSide) {
            if (this.clientSideTeleportInterpolation > 0 && this.oldAttachPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.oldAttachPosition = blockpos;
            }
         }

         this.setPosAndOldPos((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         double d0 = 0.5D - (double)MathHelper.sin((0.5F + this.currentPeekAmount) * (float)Math.PI) * 0.5D;
         double d1 = 0.5D - (double)MathHelper.sin((0.5F + this.currentPeekAmountO) * (float)Math.PI) * 0.5D;
         if (this.isAddedToWorld() && this.level instanceof net.minecraft.world.server.ServerWorld) ((net.minecraft.world.server.ServerWorld)this.level).updateChunkPos(this); // Forge - Process chunk registration after moving.
         Direction direction3 = this.getAttachFace().getOpposite();
         this.setBoundingBox((new AxisAlignedBB(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D, this.getX() + 0.5D, this.getY() + 1.0D, this.getZ() + 0.5D)).expandTowards((double)direction3.getStepX() * d0, (double)direction3.getStepY() * d0, (double)direction3.getStepZ() * d0));
         double d2 = d0 - d1;
         if (d2 > 0.0D) {
            List<Entity> list = this.level.getEntities(this, this.getBoundingBox());
            if (!list.isEmpty()) {
               for(Entity entity : list) {
                  if (!(entity instanceof ShulkerEntity) && !entity.noPhysics) {
                     entity.move(MoverType.SHULKER, new Vec3d(d2 * (double)direction3.getStepX(), d2 * (double)direction3.getStepY(), d2 * (double)direction3.getStepZ()));
                  }
               }
            }
         }
      }

   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      if (p_213315_1_ == MoverType.SHULKER_BOX) {
         this.teleportSomewhere();
      } else {
         super.move(p_213315_1_, p_213315_2_);
      }

   }

   public void setPos(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      super.setPos(p_70107_1_, p_70107_3_, p_70107_5_);
      if (this.entityData != null && this.tickCount != 0) {
         Optional<BlockPos> optional = this.entityData.get(DATA_ATTACH_POS_ID);
         Optional<BlockPos> optional1 = Optional.of(new BlockPos(p_70107_1_, p_70107_3_, p_70107_5_));
         if (!optional1.equals(optional)) {
            this.entityData.set(DATA_ATTACH_POS_ID, optional1);
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.hasImpulse = true;
         }

      }
   }

   protected boolean teleportSomewhere() {
      if (!this.isNoAi() && this.isAlive()) {
         BlockPos blockpos = new BlockPos(this);

         for(int i = 0; i < 5; ++i) {
            BlockPos blockpos1 = blockpos.offset(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (blockpos1.getY() > 0 && this.level.isEmptyBlock(blockpos1) && this.level.getWorldBorder().isWithinBounds(blockpos1) && this.level.noCollision(this, new AxisAlignedBB(blockpos1))) {
               boolean flag = false;

               for(Direction direction : Direction.values()) {
                  if (this.level.loadedAndEntityCanStandOn(blockpos1.relative(direction), this)) {
                     this.entityData.set(DATA_ATTACH_FACE_ID, direction);
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), 0);
                  if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) flag = false;
                  blockpos1 = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               }

               if (flag) {
                  this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(blockpos1));
                  this.entityData.set(DATA_PEEK_ID, (byte)0);
                  this.setTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void aiStep() {
      super.aiStep();
      this.setDeltaMovement(Vec3d.ZERO);
      this.yBodyRotO = 180.0F;
      this.yBodyRot = 180.0F;
      this.yRot = 180.0F;
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_ATTACH_POS_ID.equals(p_184206_1_) && this.level.isClientSide && !this.isPassenger()) {
         BlockPos blockpos = this.getAttachPosition();
         if (blockpos != null) {
            if (this.oldAttachPosition == null) {
               this.oldAttachPosition = blockpos;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.setPosAndOldPos((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         }
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.lerpSteps = 0;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isClosed()) {
         Entity entity = p_70097_1_.getDirectEntity();
         if (entity instanceof AbstractArrowEntity) {
            return false;
         }
      }

      if (super.hurt(p_70097_1_, p_70097_2_)) {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
            this.teleportSomewhere();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isClosed() {
      return this.getRawPeekAmount() == 0;
   }

   @Nullable
   public AxisAlignedBB func_70046_E() {
      return this.isAlive() ? this.getBoundingBox() : null;
   }

   public Direction getAttachFace() {
      return this.entityData.get(DATA_ATTACH_FACE_ID);
   }

   @Nullable
   public BlockPos getAttachPosition() {
      return this.entityData.get(DATA_ATTACH_POS_ID).orElse((BlockPos)null);
   }

   public void setAttachPosition(@Nullable BlockPos p_184694_1_) {
      this.entityData.set(DATA_ATTACH_POS_ID, Optional.ofNullable(p_184694_1_));
   }

   public int getRawPeekAmount() {
      return this.entityData.get(DATA_PEEK_ID);
   }

   public void setRawPeekAmount(int p_184691_1_) {
      if (!this.level.isClientSide) {
         this.getAttribute(SharedMonsterAttributes.field_188791_g).removeModifier(COVERED_ARMOR_MODIFIER);
         if (p_184691_1_ == 0) {
            this.getAttribute(SharedMonsterAttributes.field_188791_g).addModifier(COVERED_ARMOR_MODIFIER);
            this.playSound(SoundEvents.SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.entityData.set(DATA_PEEK_ID, (byte)p_184691_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getClientPeekAmount(float p_184688_1_) {
      return MathHelper.lerp(p_184688_1_, this.currentPeekAmountO, this.currentPeekAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public int getClientSideTeleportInterpolation() {
      return this.clientSideTeleportInterpolation;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOldAttachPosition() {
      return this.oldAttachPosition;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.5F;
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public int getMaxHeadYRot() {
      return 180;
   }

   public void push(Entity p_70108_1_) {
   }

   public float getPickRadius() {
      return 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasValidInterpolationPositions() {
      return this.oldAttachPosition != null && this.getAttachPosition() != null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      Byte obyte = this.entityData.get(DATA_COLOR_ID);
      return obyte != 16 && obyte <= 15 ? DyeColor.byId(obyte) : null;
   }

   class AttackGoal extends Goal {
      private int attackTime;

      public AttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = ShulkerEntity.this.getTarget();
         if (livingentity != null && livingentity.isAlive()) {
            return ShulkerEntity.this.level.getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void start() {
         this.attackTime = 20;
         ShulkerEntity.this.setRawPeekAmount(100);
      }

      public void stop() {
         ShulkerEntity.this.setRawPeekAmount(0);
      }

      public void tick() {
         if (ShulkerEntity.this.level.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity livingentity = ShulkerEntity.this.getTarget();
            ShulkerEntity.this.getLookControl().setLookAt(livingentity, 180.0F, 180.0F);
            double d0 = ShulkerEntity.this.distanceToSqr(livingentity);
            if (d0 < 400.0D) {
               if (this.attackTime <= 0) {
                  this.attackTime = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
                  ShulkerEntity.this.level.addFreshEntity(new ShulkerBulletEntity(ShulkerEntity.this.level, ShulkerEntity.this, livingentity, ShulkerEntity.this.getAttachFace().getAxis()));
                  ShulkerEntity.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               ShulkerEntity.this.setTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   class AttackNearestGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public AttackNearestGoal(ShulkerEntity p_i47060_2_) {
         super(p_i47060_2_, PlayerEntity.class, true);
      }

      public boolean canUse() {
         return ShulkerEntity.this.level.getDifficulty() == Difficulty.PEACEFUL ? false : super.canUse();
      }

      protected AxisAlignedBB getTargetSearchArea(double p_188511_1_) {
         Direction direction = ((ShulkerEntity)this.mob).getAttachFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(p_188511_1_, p_188511_1_, 4.0D) : this.mob.getBoundingBox().inflate(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   class BodyHelperController extends BodyController {
      public BodyHelperController(MobEntity p_i50612_2_) {
         super(p_i50612_2_);
      }

      public void clientTick() {
      }
   }

   static class DefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public DefenseAttackGoal(ShulkerEntity p_i47061_1_) {
         super(p_i47061_1_, LivingEntity.class, 10, true, false, (p_200826_0_) -> {
            return p_200826_0_ instanceof IMob;
         });
      }

      public boolean canUse() {
         return this.mob.getTeam() == null ? false : super.canUse();
      }

      protected AxisAlignedBB getTargetSearchArea(double p_188511_1_) {
         Direction direction = ((ShulkerEntity)this.mob).getAttachFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(p_188511_1_, p_188511_1_, 4.0D) : this.mob.getBoundingBox().inflate(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   class PeekGoal extends Goal {
      private int peekTime;

      private PeekGoal() {
      }

      public boolean canUse() {
         return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(40) == 0;
      }

      public boolean canContinueToUse() {
         return ShulkerEntity.this.getTarget() == null && this.peekTime > 0;
      }

      public void start() {
         this.peekTime = 20 * (1 + ShulkerEntity.this.random.nextInt(3));
         ShulkerEntity.this.setRawPeekAmount(30);
      }

      public void stop() {
         if (ShulkerEntity.this.getTarget() == null) {
            ShulkerEntity.this.setRawPeekAmount(0);
         }

      }

      public void tick() {
         --this.peekTime;
      }
   }
}
