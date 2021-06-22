package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PistonHeadBlock extends DirectionalBlock {
   public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
   public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
   protected static final VoxelShape EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final VoxelShape UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
   protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
   protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   public PistonHeadBlock(Block.Properties p_i48280_1_) {
      super(p_i48280_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, PistonType.DEFAULT).setValue(SHORT, Boolean.valueOf(false)));
   }

   private VoxelShape func_196424_i(BlockState p_196424_1_) {
      switch((Direction)p_196424_1_.getValue(FACING)) {
      case DOWN:
      default:
         return DOWN_AABB;
      case UP:
         return UP_AABB;
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
         return EAST_AABB;
      }
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.or(this.func_196424_i(p_220053_1_), this.func_196425_x(p_220053_1_));
   }

   private VoxelShape func_196425_x(BlockState p_196425_1_) {
      boolean flag = p_196425_1_.getValue(SHORT);
      switch((Direction)p_196425_1_.getValue(FACING)) {
      case DOWN:
      default:
         return flag ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB;
      case UP:
         return flag ? SHORT_UP_ARM_AABB : UP_ARM_AABB;
      case NORTH:
         return flag ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB;
      case SOUTH:
         return flag ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB;
      case WEST:
         return flag ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB;
      case EAST:
         return flag ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB;
      }
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isClientSide && p_176208_4_.abilities.instabuild) {
         BlockPos blockpos = p_176208_2_.relative(p_176208_3_.getValue(FACING).getOpposite());
         Block block = p_176208_1_.getBlockState(blockpos).getBlock();
         if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
            p_176208_1_.removeBlock(blockpos, false);
         }
      }

      super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         Direction direction = p_196243_1_.getValue(FACING).getOpposite();
         p_196243_3_ = p_196243_3_.relative(direction);
         BlockState blockstate = p_196243_2_.getBlockState(p_196243_3_);
         if ((blockstate.getBlock() == Blocks.PISTON || blockstate.getBlock() == Blocks.STICKY_PISTON) && blockstate.getValue(PistonBlock.EXTENDED)) {
            dropResources(blockstate, p_196243_2_, p_196243_3_);
            p_196243_2_.removeBlock(p_196243_3_, false);
         }

      }
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.getValue(FACING) && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Block block = p_196260_2_.getBlockState(p_196260_3_.relative(p_196260_1_.getValue(FACING).getOpposite())).getBlock();
      return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON;
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_1_.canSurvive(p_220069_2_, p_220069_3_)) {
         BlockPos blockpos = p_220069_3_.relative(p_220069_1_.getValue(FACING).getOpposite());
         p_220069_2_.getBlockState(blockpos).neighborChanged(p_220069_2_, blockpos, p_220069_4_, p_220069_5_, false);
      }

   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(p_185473_3_.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE, SHORT);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}