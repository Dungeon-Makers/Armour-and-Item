package net.minecraft.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ZombiePigmanEntity extends ZombieEntity {
   private static final UUID field_110189_bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier field_110190_br = (new AttributeModifier(field_110189_bq, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION)).func_111168_a(false);
   private int field_70837_d;
   private int field_70838_e;
   private UUID field_175459_bn;

   public ZombiePigmanEntity(EntityType<? extends ZombiePigmanEntity> p_i50199_1_, World p_i50199_2_) {
      super(p_i50199_1_, p_i50199_2_);
      this.setPathfindingMalus(PathNodeType.LAVA, 8.0F);
   }

   public void setLastHurtByMob(@Nullable LivingEntity p_70604_1_) {
      super.setLastHurtByMob(p_70604_1_);
      if (p_70604_1_ != null) {
         this.field_175459_bn = p_70604_1_.getUUID();
      }

   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, new ZombiePigmanEntity.HurtByAggressorGoal(this));
      this.targetSelector.addGoal(2, new ZombiePigmanEntity.TargetAggressorGoal(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(field_110186_bp).setBaseValue(0.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.field_111264_e).setBaseValue(5.0D);
   }

   protected boolean convertsInWater() {
      return false;
   }

   protected void customServerAiStep() {
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.field_111263_d);
      LivingEntity livingentity = this.getLastHurtByMob();
      if (this.func_175457_ck()) {
         if (!this.isBaby() && !iattributeinstance.hasModifier(field_110190_br)) {
            iattributeinstance.addModifier(field_110190_br);
         }

         --this.field_70837_d;
         LivingEntity livingentity1 = livingentity != null ? livingentity : this.getTarget();
         if (!this.func_175457_ck() && livingentity1 != null) {
            if (!this.canSee(livingentity1)) {
               this.setLastHurtByMob((LivingEntity)null);
               this.setTarget((LivingEntity)null);
            } else {
               this.field_70837_d = this.func_223336_ef();
            }
         }
      } else if (iattributeinstance.hasModifier(field_110190_br)) {
         iattributeinstance.removeModifier(field_110190_br);
      }

      if (this.field_70838_e > 0 && --this.field_70838_e == 0) {
         this.playSound(SoundEvents.field_187936_hj, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.func_175457_ck() && this.field_175459_bn != null && livingentity == null) {
         PlayerEntity playerentity = this.level.getPlayerByUUID(this.field_175459_bn);
         this.setLastHurtByMob(playerentity);
         this.lastHurtByPlayer = playerentity;
         this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
      }

      super.customServerAiStep();
   }

   public static boolean func_223337_b(EntityType<ZombiePigmanEntity> p_223337_0_, IWorld p_223337_1_, SpawnReason p_223337_2_, BlockPos p_223337_3_, Random p_223337_4_) {
      return p_223337_1_.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putShort("Anger", (short)this.field_70837_d);
      if (this.field_175459_bn != null) {
         p_213281_1_.putString("HurtBy", this.field_175459_bn.toString());
      } else {
         p_213281_1_.putString("HurtBy", "");
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.field_70837_d = p_70037_1_.getShort("Anger");
      String s = p_70037_1_.getString("HurtBy");
      if (!s.isEmpty()) {
         this.field_175459_bn = UUID.fromString(s);
         PlayerEntity playerentity = this.level.getPlayerByUUID(this.field_175459_bn);
         this.setLastHurtByMob(playerentity);
         if (playerentity != null) {
            this.lastHurtByPlayer = playerentity;
            this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
         }
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         Entity entity = p_70097_1_.getEntity();
         if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canSee(entity)) {
            this.func_226547_i_((LivingEntity)entity);
         }

         return super.hurt(p_70097_1_, p_70097_2_);
      }
   }

   private boolean func_226547_i_(LivingEntity p_226547_1_) {
      this.field_70837_d = this.func_223336_ef();
      this.field_70838_e = this.random.nextInt(40);
      this.setLastHurtByMob(p_226547_1_);
      return true;
   }

   private int func_223336_ef() {
      return 400 + this.random.nextInt(400);
   }

   private boolean func_175457_ck() {
      return this.field_70837_d > 0;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.field_187935_hi;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.field_187938_hl;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.field_187937_hk;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public boolean func_191990_c(PlayerEntity p_191990_1_) {
      return this.func_175457_ck();
   }

   static class HurtByAggressorGoal extends HurtByTargetGoal {
      public HurtByAggressorGoal(ZombiePigmanEntity p_i45828_1_) {
         super(p_i45828_1_);
         this.setAlertOthers(new Class[]{ZombieEntity.class});
      }

      protected void alertOther(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
         if (p_220793_1_ instanceof ZombiePigmanEntity && this.mob.canSee(p_220793_2_) && ((ZombiePigmanEntity)p_220793_1_).func_226547_i_(p_220793_2_)) {
            p_220793_1_.setTarget(p_220793_2_);
         }

      }
   }

   static class TargetAggressorGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public TargetAggressorGoal(ZombiePigmanEntity p_i45829_1_) {
         super(p_i45829_1_, PlayerEntity.class, true);
      }

      public boolean canUse() {
         return ((ZombiePigmanEntity)this.mob).func_175457_ck() && super.canUse();
      }
   }
}