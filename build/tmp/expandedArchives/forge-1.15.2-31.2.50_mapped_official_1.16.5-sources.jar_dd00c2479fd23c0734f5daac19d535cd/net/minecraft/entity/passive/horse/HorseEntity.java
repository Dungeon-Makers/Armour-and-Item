package net.minecraft.entity.passive.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HorseEntity extends AbstractHorseEntity {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> DATA_ID_TYPE_VARIANT = EntityDataManager.defineId(HorseEntity.class, DataSerializers.INT);
   private static final String[] field_110268_bz = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] field_110269_bA = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] field_110291_bB = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] field_110292_bC = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   @Nullable
   private String field_110286_bQ;
   private final String[] field_110280_bR = new String[2];

   public HorseEntity(EntityType<? extends HorseEntity> p_i50238_1_, World p_i50238_2_) {
      super(p_i50238_1_, p_i50238_2_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Variant", this.func_110202_bQ());
      if (!this.inventory.getItem(1).isEmpty()) {
         p_213281_1_.put("ArmorItem", this.inventory.getItem(1).save(new CompoundNBT()));
      }

   }

   public ItemStack getArmor() {
      return this.getItemBySlot(EquipmentSlotType.CHEST);
   }

   private void setArmor(ItemStack p_213805_1_) {
      this.setItemSlot(EquipmentSlotType.CHEST, p_213805_1_);
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.func_110235_q(p_70037_1_.getInt("Variant"));
      if (p_70037_1_.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
            this.inventory.setItem(1, itemstack);
         }
      }

      this.func_110232_cE();
   }

   public void func_110235_q(int p_110235_1_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_110235_1_);
      this.func_110230_cF();
   }

   public int func_110202_bQ() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void func_110230_cF() {
      this.field_110286_bQ = null;
   }

   @OnlyIn(Dist.CLIENT)
   private void func_110247_cG() {
      int i = this.func_110202_bQ();
      int j = (i & 255) % 7;
      int k = ((i & '\uff00') >> 8) % 5;
      this.field_110280_bR[0] = field_110268_bz[j];
      this.field_110280_bR[1] = field_110291_bB[k];
      this.field_110286_bQ = "horse/" + field_110269_bA[j] + field_110292_bC[k];
   }

   @OnlyIn(Dist.CLIENT)
   public String func_110264_co() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110286_bQ;
   }

   @OnlyIn(Dist.CLIENT)
   public String[] func_110212_cp() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110280_bR;
   }

   protected void func_110232_cE() {
      super.func_110232_cE();
      this.setArmorEquipment(this.inventory.getItem(1));
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

   private void setArmorEquipment(ItemStack p_213804_1_) {
      this.setArmor(p_213804_1_);
      if (!this.level.isClientSide) {
         this.getAttribute(SharedMonsterAttributes.field_188791_g).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(p_213804_1_)) {
            int i = ((HorseArmorItem)p_213804_1_.getItem()).getProtection();
            if (i != 0) {
               this.getAttribute(SharedMonsterAttributes.field_188791_g).addModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)).func_111168_a(false));
            }
         }
      }

   }

   public void containerChanged(IInventory p_76316_1_) {
      ItemStack itemstack = this.getArmor();
      super.containerChanged(p_76316_1_);
      ItemStack itemstack1 = this.getArmor();
      if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
         this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      super.playGallopSound(p_190680_1_);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue(this.generateRandomSpeed());
      this.getAttribute(field_110271_bv).setBaseValue(this.generateRandomJumpStrength());
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && this.entityData.isDirty()) {
         this.entityData.clearDirty();
         this.func_110230_cF();
      }

      ItemStack stack = this.inventory.getItem(1);
      if (isArmor(stack)) stack.onHorseArmorTick(level, this);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      super.getHurtSound(p_184601_1_);
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.HORSE_ANGRY;
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      boolean flag = !itemstack.isEmpty();
      if (flag && itemstack.getItem() instanceof SpawnEggItem) {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      } else {
         if (!this.isBaby()) {
            if (this.isTamed() && p_184645_1_.isSecondaryUseActive()) {
               this.openInventory(p_184645_1_);
               return true;
            }

            if (this.isVehicle()) {
               return super.func_184645_a(p_184645_1_, p_184645_2_);
            }
         }

         if (flag) {
            if (this.handleEating(p_184645_1_, itemstack)) {
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (itemstack.func_111282_a(p_184645_1_, this, p_184645_2_)) {
               return true;
            }

            if (!this.isTamed()) {
               this.makeMad();
               return true;
            }

            boolean flag1 = !this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
            if (this.isArmor(itemstack) || flag1) {
               this.openInventory(p_184645_1_);
               return true;
            }
         }

         if (this.isBaby()) {
            return super.func_184645_a(p_184645_1_, p_184645_2_);
         } else {
            this.doPlayerRide(p_184645_1_);
            return true;
         }
      }
   }

   public boolean canMate(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (!(p_70878_1_ instanceof DonkeyEntity) && !(p_70878_1_ instanceof HorseEntity)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorseEntity)p_70878_1_).canParent();
      }
   }

   public AgeableEntity func_90011_a(AgeableEntity p_90011_1_) {
      AbstractHorseEntity abstracthorseentity;
      if (p_90011_1_ instanceof DonkeyEntity) {
         abstracthorseentity = EntityType.MULE.create(this.level);
      } else {
         HorseEntity horseentity = (HorseEntity)p_90011_1_;
         abstracthorseentity = EntityType.HORSE.create(this.level);
         int j = this.random.nextInt(9);
         int i;
         if (j < 4) {
            i = this.func_110202_bQ() & 255;
         } else if (j < 8) {
            i = horseentity.func_110202_bQ() & 255;
         } else {
            i = this.random.nextInt(7);
         }

         int k = this.random.nextInt(5);
         if (k < 2) {
            i = i | this.func_110202_bQ() & '\uff00';
         } else if (k < 4) {
            i = i | horseentity.func_110202_bQ() & '\uff00';
         } else {
            i = i | this.random.nextInt(5) << 8 & '\uff00';
         }

         ((HorseEntity)abstracthorseentity).func_110235_q(i);
      }

      this.setOffspringAttributes(p_90011_1_, abstracthorseentity);
      return abstracthorseentity;
   }

   public boolean func_190677_dK() {
      return true;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      return p_190682_1_.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      int i;
      if (p_213386_4_ instanceof HorseEntity.HorseData) {
         i = ((HorseEntity.HorseData)p_213386_4_).variant;
      } else {
         i = this.random.nextInt(7);
         p_213386_4_ = new HorseEntity.HorseData(i);
      }

      this.func_110235_q(i | this.random.nextInt(5) << 8);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public static class HorseData extends AgeableEntity.AgeableData {
      public final int variant;

      public HorseData(int p_i47337_1_) {
         this.variant = p_i47337_1_;
      }
   }
}
