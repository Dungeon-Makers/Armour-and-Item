package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class AirBlock extends Block {
   protected AirBlock(Block.Properties p_i48451_1_) {
      super(p_i48451_1_);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public boolean func_196261_e(BlockState p_196261_1_) {
      return true;
   }
}