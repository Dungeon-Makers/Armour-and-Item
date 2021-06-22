package net.minecraft.block;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class WallBlock extends FourWayBlock {
   public static final BooleanProperty UP = BlockStateProperties.UP;
   private final VoxelShape[] field_196422_D;
   private final VoxelShape[] field_196423_E;

   public WallBlock(Block.Properties p_i48301_1_) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, p_i48301_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(true)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.field_196422_D = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.field_196423_E = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return p_220053_1_.getValue(UP) ? this.field_196422_D[this.getAABBIndex(p_220053_1_)] : super.getShape(p_220053_1_, p_220053_2_, p_220053_3_, p_220053_4_);
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return p_220071_1_.getValue(UP) ? this.field_196423_E[this.getAABBIndex(p_220071_1_)] : super.getCollisionShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   private boolean connectsTo(BlockState p_220113_1_, boolean p_220113_2_, Direction p_220113_3_) {
      Block block = p_220113_1_.getBlock();
      boolean flag = block.is(BlockTags.WALLS) || block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(p_220113_1_, p_220113_3_);
      return !isExceptionForConnection(block) && p_220113_2_ || flag;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IWorldReader iworldreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      IFluidState ifluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      BlockState blockstate = iworldreader.getBlockState(blockpos1);
      BlockState blockstate1 = iworldreader.getBlockState(blockpos2);
      BlockState blockstate2 = iworldreader.getBlockState(blockpos3);
      BlockState blockstate3 = iworldreader.getBlockState(blockpos4);
      boolean flag = this.connectsTo(blockstate, blockstate.isFaceSturdy(iworldreader, blockpos1, Direction.SOUTH), Direction.SOUTH);
      boolean flag1 = this.connectsTo(blockstate1, blockstate1.isFaceSturdy(iworldreader, blockpos2, Direction.WEST), Direction.WEST);
      boolean flag2 = this.connectsTo(blockstate2, blockstate2.isFaceSturdy(iworldreader, blockpos3, Direction.NORTH), Direction.NORTH);
      boolean flag3 = this.connectsTo(blockstate3, blockstate3.isFaceSturdy(iworldreader, blockpos4, Direction.EAST), Direction.EAST);
      boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
      return this.defaultBlockState().setValue(UP, Boolean.valueOf(flag4 || !iworldreader.isEmptyBlock(blockpos.above()))).setValue(NORTH, Boolean.valueOf(flag)).setValue(EAST, Boolean.valueOf(flag1)).setValue(SOUTH, Boolean.valueOf(flag2)).setValue(WEST, Boolean.valueOf(flag3)).setValue(WATERLOGGED, Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(WATERLOGGED)) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      if (p_196271_2_ == Direction.DOWN) {
         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Direction direction = p_196271_2_.getOpposite();
         boolean flag = p_196271_2_ == Direction.NORTH ? this.connectsTo(p_196271_3_, p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, direction), direction) : p_196271_1_.getValue(NORTH);
         boolean flag1 = p_196271_2_ == Direction.EAST ? this.connectsTo(p_196271_3_, p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, direction), direction) : p_196271_1_.getValue(EAST);
         boolean flag2 = p_196271_2_ == Direction.SOUTH ? this.connectsTo(p_196271_3_, p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, direction), direction) : p_196271_1_.getValue(SOUTH);
         boolean flag3 = p_196271_2_ == Direction.WEST ? this.connectsTo(p_196271_3_, p_196271_3_.isFaceSturdy(p_196271_4_, p_196271_6_, direction), direction) : p_196271_1_.getValue(WEST);
         boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
         return p_196271_1_.setValue(UP, Boolean.valueOf(flag4 || !p_196271_4_.isEmptyBlock(p_196271_5_.above()))).setValue(NORTH, Boolean.valueOf(flag)).setValue(EAST, Boolean.valueOf(flag1)).setValue(SOUTH, Boolean.valueOf(flag2)).setValue(WEST, Boolean.valueOf(flag3));
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}