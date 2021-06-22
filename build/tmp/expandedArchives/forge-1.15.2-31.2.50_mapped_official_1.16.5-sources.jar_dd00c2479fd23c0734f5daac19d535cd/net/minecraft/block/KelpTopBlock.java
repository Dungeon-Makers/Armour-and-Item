package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class KelpTopBlock extends Block implements ILiquidContainer {
   public static final IntegerProperty field_203163_a = BlockStateProperties.AGE_25;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

   protected KelpTopBlock(Block.Properties p_i48781_1_) {
      super(p_i48781_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(field_203163_a, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState ifluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      return ifluidstate.is(FluidTags.WATER) && ifluidstate.getAmount() == 8 ? this.func_209906_a(p_196258_1_.getLevel()) : null;
   }

   public BlockState func_209906_a(IWorld p_209906_1_) {
      return this.defaultBlockState().setValue(field_203163_a, Integer.valueOf(p_209906_1_.getRandom().nextInt(25)));
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getSource(false);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      } else {
         BlockPos blockpos = p_225534_3_.above();
         BlockState blockstate = p_225534_2_.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.WATER && p_225534_1_.getValue(field_203163_a) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225534_2_, blockpos, p_225534_1_, p_225534_4_.nextDouble() < 0.14D)) {
            p_225534_2_.setBlockAndUpdate(blockpos, p_225534_1_.func_177231_a(field_203163_a));
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225534_2_, blockpos, p_225534_1_);
         }

      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block == Blocks.MAGMA_BLOCK) {
         return false;
      } else {
         return block == this || block == Blocks.KELP_PLANT || blockstate.isFaceSturdy(p_196260_2_, blockpos, Direction.UP);
      }
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         if (p_196271_2_ == Direction.DOWN) {
            return Blocks.AIR.defaultBlockState();
         }

         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() == this) {
         return Blocks.KELP_PLANT.defaultBlockState();
      } else {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_203163_a);
   }

   public boolean canPlaceLiquid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean placeLiquid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      return false;
   }
}
