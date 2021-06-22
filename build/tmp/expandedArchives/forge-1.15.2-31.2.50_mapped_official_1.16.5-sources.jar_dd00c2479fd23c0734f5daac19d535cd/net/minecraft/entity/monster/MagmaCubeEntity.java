package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;

public class MagmaCubeEntity extends SlimeEntity {
   public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> p_i50202_1_, World p_i50202_2_) {
      super(p_i50202_1_, p_i50202_2_);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.2F);
   }

   public static boolean checkMagmaCubeSpawnRules(EntityType<MagmaCubeEntity> p_223367_0_, IWorld p_223367_1_, SpawnReason p_223367_2_, BlockPos p_223367_3_, Random p_223367_4_) {
      return p_223367_1_.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   protected void setSize(int p_70799_1_, boolean p_70799_2_) {
      super.setSize(p_70799_1_, p_70799_2_);
      this.getAttribute(SharedMonsterAttributes.field_188791_g).setBaseValue((double)(p_70799_1_ * 3));
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected IParticleData getParticleType() {
      return ParticleTypes.FLAME;
   }

   protected ResourceLocation getDefaultLootTable() {
      return this.isTiny() ? LootTables.EMPTY : this.getType().getDefaultLootTable();
   }

   public boolean isOnFire() {
      return false;
   }

   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.9F;
   }

   protected void jumpFromGround() {
      Vec3d vec3d = this.getDeltaMovement();
      this.setDeltaMovement(vec3d.x, (double)(this.getJumpPower() + (float)this.getSize() * 0.1F), vec3d.z);
      this.hasImpulse = true;
      net.minecraftforge.common.ForgeHooks.onLivingJump(this);
   }

   protected void jumpInLiquid(Tag<Fluid> p_180466_1_) {
      if (p_180466_1_ == FluidTags.LAVA) {
         Vec3d vec3d = this.getDeltaMovement();
         this.setDeltaMovement(vec3d.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3d.z);
         this.hasImpulse = true;
      } else {
         super.jumpInLiquid(p_180466_1_);
      }

   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected boolean isDealsDamage() {
      return this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return super.getAttackDamage() + 2.0F;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_HURT_SMALL : SoundEvents.MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_DEATH_SMALL : SoundEvents.MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_SQUISH_SMALL : SoundEvents.MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.MAGMA_CUBE_JUMP;
   }
}
