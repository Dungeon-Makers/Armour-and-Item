package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.DefendVillageTargetGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.MoveTowardsVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ShowVillagerFlowerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IronGolemEntity extends GolemEntity {
   protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(IronGolemEntity.class, DataSerializers.BYTE);
   private int attackAnimationTick;
   private int offerFlowerTick;

   public IronGolemEntity(EntityType<? extends IronGolemEntity> p_i50267_1_, World p_i50267_2_) {
      super(p_i50267_1_, p_i50267_2_);
      this.maxUpStep = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
      this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> {
         return false;
      }));
      this.goalSelector.addGoal(5, new ShowVillagerFlowerGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (p_213619_0_) -> {
         return p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity);
      }));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.field_111266_c).setBaseValue(1.0D);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).setBaseValue(15.0D);
   }

   protected int decreaseAirSupply(int p_70682_1_) {
      return p_70682_1_;
   }

   protected void doPush(Entity p_82167_1_) {
      if (p_82167_1_ instanceof IMob && !(p_82167_1_ instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
         this.setTarget((LivingEntity)p_82167_1_);
      }

      super.doPush(p_82167_1_);
   }

   public void aiStep() {
      super.aiStep();
      if (this.attackAnimationTick > 0) {
         --this.attackAnimationTick;
      }

      if (this.offerFlowerTick > 0) {
         --this.offerFlowerTick;
      }

      if (getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)2.5000003E-7F && this.random.nextInt(5) == 0) {
         int i = MathHelper.floor(this.getX());
         int j = MathHelper.floor(this.getY() - (double)0.2F);
         int k = MathHelper.floor(this.getZ());
         BlockPos pos = new BlockPos(i, j, k);
         BlockState blockstate = this.level.getBlockState(pos);
         if (!blockstate.isAir(this.level, pos)) {
            this.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean canAttackType(EntityType<?> p_213358_1_) {
      if (this.isPlayerCreated() && p_213358_1_ == EntityType.PLAYER) {
         return false;
      } else {
         return p_213358_1_ == EntityType.CREEPER ? false : super.canAttackType(p_213358_1_);
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setPlayerCreated(p_70037_1_.getBoolean("PlayerCreated"));
   }

   private float getAttackDamage() {
      return (float)this.getAttribute(SharedMonsterAttributes.field_111264_e).getValue();
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      this.attackAnimationTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      float f = this.getAttackDamage();
      float f1 = f > 0.0F ? f / 2.0F + (float)this.random.nextInt((int)f) : 0.0F;
      boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), f1);
      if (flag) {
         p_70652_1_.setDeltaMovement(p_70652_1_.getDeltaMovement().add(0.0D, (double)0.4F, 0.0D));
         this.doEnchantDamageEffects(this, p_70652_1_);
      }

      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return flag;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      IronGolemEntity.Cracks irongolementity$cracks = this.getCrackiness();
      boolean flag = super.hurt(p_70097_1_, p_70097_2_);
      if (flag && this.getCrackiness() != irongolementity$cracks) {
         this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
      }

      return flag;
   }

   public IronGolemEntity.Cracks getCrackiness() {
      return IronGolemEntity.Cracks.byFraction(this.getHealth() / this.getMaxHealth());
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackAnimationTick = 10;
         this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (p_70103_1_ == 11) {
         this.offerFlowerTick = 400;
      } else if (p_70103_1_ == 34) {
         this.offerFlowerTick = 0;
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackAnimationTick() {
      return this.attackAnimationTick;
   }

   public void offerFlower(boolean p_70851_1_) {
      if (p_70851_1_) {
         this.offerFlowerTick = 400;
         this.level.broadcastEntityEvent(this, (byte)11);
      } else {
         this.offerFlowerTick = 0;
         this.level.broadcastEntityEvent(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.IRON_GOLEM_DEATH;
   }

   protected boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      Item item = itemstack.getItem();
      if (item != Items.IRON_INGOT) {
         return false;
      } else {
         float f = this.getHealth();
         this.heal(25.0F);
         if (this.getHealth() == f) {
            return false;
         } else {
            float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
            if (!p_184645_1_.abilities.instabuild) {
               itemstack.shrink(1);
            }

            return true;
         }
      }
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public int getOfferFlowerTick() {
      return this.offerFlowerTick;
   }

   public boolean isPlayerCreated() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setPlayerCreated(boolean p_70849_1_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_70849_1_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
      }

   }

   public void die(DamageSource p_70645_1_) {
      super.die(p_70645_1_);
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.below();
      BlockState blockstate = p_205019_1_.getBlockState(blockpos1);
      if (!blockstate.entityCanStandOnFace(p_205019_1_, blockpos1, this)) {
         return false;
      } else {
         for(int i = 1; i < 3; ++i) {
            BlockPos blockpos2 = blockpos.above(i);
            BlockState blockstate1 = p_205019_1_.getBlockState(blockpos2);
            if (!WorldEntitySpawner.func_222266_a(p_205019_1_, blockpos2, blockstate1, blockstate1.getFluidState())) {
               return false;
            }
         }

         return WorldEntitySpawner.func_222266_a(p_205019_1_, blockpos, p_205019_1_.getBlockState(blockpos), Fluids.EMPTY.defaultFluidState()) && p_205019_1_.isUnobstructed(this);
      }
   }

   public static enum Cracks {
      NONE(1.0F),
      LOW(0.75F),
      MEDIUM(0.5F),
      HIGH(0.25F);

      private static final List<IronGolemEntity.Cracks> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_226516_0_) -> {
         return (double)p_226516_0_.fraction;
      })).collect(ImmutableList.toImmutableList());
      private final float fraction;

      private Cracks(float p_i225732_3_) {
         this.fraction = p_i225732_3_;
      }

      public static IronGolemEntity.Cracks byFraction(float p_226515_0_) {
         for(IronGolemEntity.Cracks irongolementity$cracks : BY_DAMAGE) {
            if (p_226515_0_ < irongolementity$cracks.fraction) {
               return irongolementity$cracks;
            }
         }

         return NONE;
      }
   }
}
