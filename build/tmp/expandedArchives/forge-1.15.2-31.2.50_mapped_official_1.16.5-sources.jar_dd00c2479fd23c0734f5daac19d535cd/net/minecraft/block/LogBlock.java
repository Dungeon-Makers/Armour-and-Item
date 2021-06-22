package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LogBlock extends RotatedPillarBlock {
   private final MaterialColor field_196504_b;

   public LogBlock(MaterialColor p_i48367_1_, Block.Properties p_i48367_2_) {
      super(p_i48367_2_);
      this.field_196504_b = p_i48367_1_;
   }

   public MaterialColor func_180659_g(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return p_180659_1_.getValue(AXIS) == Direction.Axis.Y ? this.field_196504_b : this.field_181083_K;
   }
}