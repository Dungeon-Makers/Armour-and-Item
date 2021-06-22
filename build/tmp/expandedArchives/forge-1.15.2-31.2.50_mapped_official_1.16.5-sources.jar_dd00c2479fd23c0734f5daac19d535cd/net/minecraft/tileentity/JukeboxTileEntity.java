package net.minecraft.tileentity;

import net.minecraft.inventory.IClearable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class JukeboxTileEntity extends TileEntity implements IClearable {
   private ItemStack record = ItemStack.EMPTY;

   public JukeboxTileEntity() {
      super(TileEntityType.JUKEBOX);
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      if (p_145839_1_.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.of(p_145839_1_.getCompound("RecordItem")));
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (!this.getRecord().isEmpty()) {
         p_189515_1_.put("RecordItem", this.getRecord().save(new CompoundNBT()));
      }

      return p_189515_1_;
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack p_195535_1_) {
      this.record = p_195535_1_;
      this.setChanged();
   }

   public void clearContent() {
      this.setRecord(ItemStack.EMPTY);
   }
}