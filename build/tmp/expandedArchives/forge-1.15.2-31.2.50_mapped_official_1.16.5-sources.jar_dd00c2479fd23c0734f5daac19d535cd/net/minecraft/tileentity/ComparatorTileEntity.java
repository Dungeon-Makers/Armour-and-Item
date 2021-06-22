package net.minecraft.tileentity;

import net.minecraft.nbt.CompoundNBT;

public class ComparatorTileEntity extends TileEntity {
   private int output;

   public ComparatorTileEntity() {
      super(TileEntityType.COMPARATOR);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putInt("OutputSignal", this.output);
      return p_189515_1_;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      this.output = p_145839_1_.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int p_145995_1_) {
      this.output = p_145995_1_;
   }
}