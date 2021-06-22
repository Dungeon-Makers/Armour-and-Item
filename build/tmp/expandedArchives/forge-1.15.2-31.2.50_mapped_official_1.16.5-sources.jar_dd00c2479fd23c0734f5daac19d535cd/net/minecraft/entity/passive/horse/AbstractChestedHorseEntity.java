package net.minecraft.entity.passive.horse;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public abstract class AbstractChestedHorseEntity extends AbstractHorseEntity {
   private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.defineId(AbstractChestedHorseEntity.class, DataSerializers.BOOLEAN);

   protected AbstractChestedHorseEntity(EntityType<? extends AbstractChestedHorseEntity> p_i48564_1_, World p_i48564_2_) {
      super(p_i48564_1_, p_i48564_2_);
      this.canGallop = false;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_CHEST, false);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.175F);
      this.getAttribute(field_110271_bv).setBaseValue(0.5D);
   }

   public boolean hasChest() {
      return this.entityData.get(DATA_ID_CHEST);
   }

   public void setChest(boolean p_110207_1_) {
      this.entityData.set(DATA_ID_CHEST, p_110207_1_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   public double getPassengersRidingOffset() {
      return super.getPassengersRidingOffset() - 0.25D;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.DONKEY_ANGRY;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasChest()) {
         if (!this.level.isClientSide) {
            this.spawnAtLocation(Blocks.CHEST);
         }

         this.setChest(false);
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListNBT listnbt = new ListNBT();

         for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
               CompoundNBT compoundnbt = new CompoundNBT();
               compoundnbt.putByte("Slot", (byte)i);
               itemstack.save(compoundnbt);
               listnbt.add(compoundnbt);
            }
         }

         p_213281_1_.put("Items", listnbt);
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setChest(p_70037_1_.getBoolean("ChestedHorse"));
      if (this.hasChest()) {
         ListNBT listnbt = p_70037_1_.getList("Items", 10);
         this.createInventory();

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 2 && j < this.inventory.getContainerSize()) {
               this.inventory.setItem(j, ItemStack.of(compoundnbt));
            }
         }
      }

      this.func_110232_cE();
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ == 499) {
         if (this.hasChest() && p_174820_2_.isEmpty()) {
            this.setChest(false);
            this.createInventory();
            return true;
         }

         if (!this.hasChest() && p_174820_2_.getItem() == Blocks.CHEST.asItem()) {
            this.setChest(true);
            this.createInventory();
            return true;
         }
      }

      return super.setSlot(p_174820_1_, p_174820_2_);
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (itemstack.getItem() instanceof SpawnEggItem) {
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

         if (!itemstack.isEmpty()) {
            boolean flag = this.handleEating(p_184645_1_, itemstack);
            if (!flag) {
               if (!this.isTamed() || itemstack.getItem() == Items.NAME_TAG) {
                  if (itemstack.func_111282_a(p_184645_1_, this, p_184645_2_)) {
                     return true;
                  } else {
                     this.makeMad();
                     return true;
                  }
               }

               if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()) {
                  this.setChest(true);
                  this.playChestEquipsSound();
                  flag = true;
                  this.createInventory();
               }

               if (!this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE) {
                  this.openInventory(p_184645_1_);
                  return true;
               }
            }

            if (flag) {
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

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

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }
}