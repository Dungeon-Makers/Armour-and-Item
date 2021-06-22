package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class LanternBlock extends Block {
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   protected static final VoxelShape AABB = VoxelShapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
   protected static final VoxelShape HANGING_AABB = VoxelShapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

   public LanternBlock(Block.Properties p_i49980_1_) {
      super(p_i49980_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, Boolean.valueOf(false)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      for(Direction direction : p_196258_1_.getNearestLookingDirections()) {
         if (direction.getAxis() == Direction.Axis.Y) {
            BlockState blockstate = this.defaultBlockState().setValue(HANGING, Boolean.valueOf(direction == Direction.UP));
            if (blockstate.canSurvive(p_196258_1_.getLevel(), p_196258_1_.getClickedPos())) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return p_220053_1_.getValue(HANGING) ? HANGING_AABB : AABB;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HANGING);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction direction = getConnectedDirection(p_196260_1_).getOpposite();
      return Block.canSupportCenter(p_196260_2_, p_196260_3_.relative(direction), direction.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState p_220277_0_) {
      return p_220277_0_.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return getConnectedDirection(p_196271_1_).getOpposite() == p_196271_2_ && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}