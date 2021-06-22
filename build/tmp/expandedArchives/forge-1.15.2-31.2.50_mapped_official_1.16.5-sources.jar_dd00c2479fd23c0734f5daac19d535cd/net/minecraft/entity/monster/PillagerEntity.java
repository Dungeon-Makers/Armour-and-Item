package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PillagerEntity extends AbstractIllagerEntity implements ICrossbowUser, IRangedAttackMob {
   private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(PillagerEntity.class, DataSerializers.BOOLEAN);
   private final Inventory inventory = new Inventory(5);

   public PillagerEntity(EntityType<? extends PillagerEntity> p_i50198_1_, World p_i50198_2_) {
      super(p_i50198_1_, p_i50198_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.35F);
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.field_111264_e).setBaseValue(5.0D);
      this.getAttribute(SharedMonsterAttributes.field_111265_b).setBaseValue(32.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CHARGING_CROSSBOW, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isChargingCrossbow() {
      return this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   public void setChargingCrossbow(boolean p_213671_1_) {
      this.entityData.set(IS_CHARGING_CROSSBOW, p_213671_1_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
         ItemStack itemstack = this.inventory.getItem(i);
         if (!itemstack.isEmpty()) {
            listnbt.add(itemstack.save(new CompoundNBT()));
         }
      }

      p_213281_1_.put("Inventory", listnbt);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isChargingCrossbow()) {
         return AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE;
      } else if (this.func_213382_a(Items.CROSSBOW)) {
         return AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? AbstractIllagerEntity.ArmPose.ATTACKING : AbstractIllagerEntity.ArmPose.NEUTRAL;
      }
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      ListNBT listnbt = p_70037_1_.getList("Inventory", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.inventory.addItem(itemstack);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      Block block = p_205022_2_.getBlockState(p_205022_1_.below()).getBlock();
      return block != Blocks.GRASS_BLOCK && block != Blocks.SAND ? 0.5F - p_205022_2_.getBrightness(p_205022_1_) : 10.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.populateDefaultEquipmentSlots(p_213386_2_);
      this.populateDefaultEquipmentEnchantments(p_213386_2_);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      ItemStack itemstack = new ItemStack(Items.CROSSBOW);
      if (this.random.nextInt(300) == 0) {
         Map<Enchantment, Integer> map = Maps.newHashMap();
         map.put(Enchantments.PIERCING, 1);
         EnchantmentHelper.setEnchantments(map, itemstack);
      }

      this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      if (super.isAlliedTo(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.PILLAGER_HURT;
   }

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      Hand hand = ProjectileHelper.getWeaponHoldingHand(this, Items.CROSSBOW);
      ItemStack itemstack = this.getItemInHand(hand);
      if (this.func_213382_a(Items.CROSSBOW)) {
         CrossbowItem.performShooting(this.level, this, hand, itemstack, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      }

      this.noActionTime = 0;
   }

   public void func_213670_a(LivingEntity p_213670_1_, ItemStack p_213670_2_, IProjectile p_213670_3_, float p_213670_4_) {
      Entity entity = (Entity)p_213670_3_;
      double d0 = p_213670_1_.getX() - this.getX();
      double d1 = p_213670_1_.getZ() - this.getZ();
      double d2 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
      double d3 = p_213670_1_.getY(0.3333333333333333D) - entity.getY() + d2 * (double)0.2F;
      Vector3f vector3f = this.func_213673_a(new Vec3d(d0, d3, d1), p_213670_4_);
      p_213670_3_.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   private Vector3f func_213673_a(Vec3d p_213673_1_, float p_213673_2_) {
      Vec3d vec3d = p_213673_1_.normalize();
      Vec3d vec3d1 = vec3d.cross(new Vec3d(0.0D, 1.0D, 0.0D));
      if (vec3d1.lengthSqr() <= 1.0E-7D) {
         vec3d1 = vec3d.cross(this.getUpVector(1.0F));
      }

      Quaternion quaternion = new Quaternion(new Vector3f(vec3d1), 90.0F, true);
      Vector3f vector3f = new Vector3f(vec3d);
      vector3f.transform(quaternion);
      Quaternion quaternion1 = new Quaternion(vector3f, p_213673_2_, true);
      Vector3f vector3f1 = new Vector3f(vec3d);
      vector3f1.transform(quaternion1);
      return vector3f1;
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (itemstack.getItem() instanceof BannerItem) {
         super.pickUpItem(p_175445_1_);
      } else {
         Item item = itemstack.getItem();
         if (this.wantsItem(item)) {
            ItemStack itemstack1 = this.inventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
               p_175445_1_.remove();
            } else {
               itemstack.setCount(itemstack1.getCount());
            }
         }
      }

   }

   private boolean wantsItem(Item p_213672_1_) {
      return this.hasActiveRaid() && p_213672_1_ == Items.WHITE_BANNER;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (super.setSlot(p_174820_1_, p_174820_2_)) {
         return true;
      } else {
         int i = p_174820_1_ - 300;
         if (i >= 0 && i < this.inventory.getContainerSize()) {
            this.inventory.setItem(i, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
      Raid raid = this.getCurrentRaid();
      boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
      if (flag) {
         ItemStack itemstack = new ItemStack(Items.CROSSBOW);
         Map<Enchantment, Integer> map = Maps.newHashMap();
         if (p_213660_1_ > raid.getNumGroups(Difficulty.NORMAL)) {
            map.put(Enchantments.QUICK_CHARGE, 2);
         } else if (p_213660_1_ > raid.getNumGroups(Difficulty.EASY)) {
            map.put(Enchantments.QUICK_CHARGE, 1);
         }

         map.put(Enchantments.MULTISHOT, 1);
         EnchantmentHelper.setEnchantments(map, itemstack);
         this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }
}