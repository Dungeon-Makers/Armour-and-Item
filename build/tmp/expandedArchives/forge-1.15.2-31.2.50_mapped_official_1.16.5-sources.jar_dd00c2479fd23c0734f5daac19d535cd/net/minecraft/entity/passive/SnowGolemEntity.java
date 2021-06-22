package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SnowGolemEntity extends GolemEntity implements IRangedAttackMob, net.minecraftforge.common.IShearable {
   private static final DataParameter<Byte> DATA_PUMPKIN_ID = EntityDataManager.defineId(SnowGolemEntity.class, DataSerializers.BYTE);

   public SnowGolemEntity(EntityType<? extends SnowGolemEntity> p_i50244_1_, World p_i50244_2_) {
      super(p_i50244_1_, p_i50244_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, MobEntity.class, 10, true, false, (p_213621_0_) -> {
         return p_213621_0_ instanceof IMob;
      }));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.2F);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("Pumpkin", this.hasPumpkin());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("Pumpkin")) {
         this.setPumpkin(p_70037_1_.getBoolean("Pumpkin"));
      }

   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         int i = MathHelper.floor(this.getX());
         int j = MathHelper.floor(this.getY());
         int k = MathHelper.floor(this.getZ());
         if (this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0F);
         }

         if (this.level.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F) {
            this.hurt(DamageSource.ON_FIRE, 1.0F);
         }

         if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
            return;
         }

         BlockState blockstate = Blocks.SNOW.defaultBlockState();

         for(int l = 0; l < 4; ++l) {
            i = MathHelper.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            j = MathHelper.floor(this.getY());
            k = MathHelper.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos = new BlockPos(i, j, k);
            if (this.level.isEmptyBlock(blockpos) && this.level.getBiome(blockpos).getTemperature(blockpos) < 0.8F && blockstate.canSurvive(this.level, blockpos)) {
               this.level.setBlockAndUpdate(blockpos, blockstate);
            }
         }
      }

   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      SnowballEntity snowballentity = new SnowballEntity(this.level, this);
      double d0 = p_82196_1_.getEyeY() - (double)1.1F;
      double d1 = p_82196_1_.getX() - this.getX();
      double d2 = d0 - snowballentity.getY();
      double d3 = p_82196_1_.getZ() - this.getZ();
      float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      snowballentity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
      this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(snowballentity);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 1.7F;
   }

   protected boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (false && itemstack.getItem() == Items.SHEARS && this.hasPumpkin()) { //Forge: Moved to onSheared
         if (!this.level.isClientSide) {
            this.setPumpkin(false);
            itemstack.hurtAndBreak(1, p_184645_1_, (p_213622_1_) -> {
               p_213622_1_.broadcastBreakEvent(p_184645_2_);
            });
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean hasPumpkin() {
      return (this.entityData.get(DATA_PUMPKIN_ID) & 16) != 0;
   }

   public void setPumpkin(boolean p_184747_1_) {
      byte b0 = this.entityData.get(DATA_PUMPKIN_ID);
      if (p_184747_1_) {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 | 16));
      } else {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 & -17));
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SNOW_GOLEM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.SNOW_GOLEM_DEATH;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, BlockPos pos) {
      return this.hasPumpkin();
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
      this.setPumpkin(false);
      return new java.util.ArrayList<>();
   }
}
