package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class FlatLayerInfo {
   private final BlockState blockState;
   private final int height;
   private int start;

   public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_) {
      this.height = p_i45467_1_;
      this.blockState = p_i45467_2_.defaultBlockState();
   }

   public int getHeight() {
      return this.height;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public int getStart() {
      return this.start;
   }

   public void setStart(int p_82660_1_) {
      this.start = p_82660_1_;
   }

   public String toString() {
      return (this.height != 1 ? this.height + "*" : "") + Registry.BLOCK.getKey(this.blockState.getBlock());
   }
}