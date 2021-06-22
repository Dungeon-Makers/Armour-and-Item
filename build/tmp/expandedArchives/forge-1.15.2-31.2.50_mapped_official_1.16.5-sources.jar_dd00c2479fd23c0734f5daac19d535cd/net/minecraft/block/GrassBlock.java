package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;

public class GrassBlock extends SpreadableSnowyDirtBlock implements IGrowable {
   public GrassBlock(Block.Properties p_i48388_1_) {
      super(p_i48388_1_);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.above()).isAir();
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      BlockPos blockpos = p_225535_3_.above();
      BlockState blockstate = Blocks.GRASS.defaultBlockState();

      for(int i = 0; i < 128; ++i) {
         BlockPos blockpos1 = blockpos;
         int j = 0;

         while(true) {
            if (j >= i / 16) {
               BlockState blockstate2 = p_225535_1_.getBlockState(blockpos1);
               if (blockstate2.getBlock() == blockstate.getBlock() && p_225535_2_.nextInt(10) == 0) {
                  ((IGrowable)blockstate.getBlock()).performBonemeal(p_225535_1_, p_225535_2_, blockpos1, blockstate2);
               }

               if (!blockstate2.isAir()) {
                  break;
               }

               BlockState blockstate1;
               if (p_225535_2_.nextInt(8) == 0) {
                  List<ConfiguredFeature<?, ?>> list = p_225535_1_.getBiome(blockpos1).func_201853_g();
                  if (list.isEmpty()) {
                     break;
                  }

                  ConfiguredFeature<?, ?> configuredfeature = ((DecoratedFeatureConfig)(list.get(0)).config).feature;
                  blockstate1 = ((FlowersFeature)configuredfeature.feature).getRandomFlower(p_225535_2_, blockpos1, configuredfeature.config);
               } else {
                  blockstate1 = blockstate;
               }

               if (blockstate1.canSurvive(p_225535_1_, blockpos1)) {
                  p_225535_1_.setBlock(blockpos1, blockstate1, 3);
               }
               break;
            }

            blockpos1 = blockpos1.offset(p_225535_2_.nextInt(3) - 1, (p_225535_2_.nextInt(3) - 1) * p_225535_2_.nextInt(3) / 2, p_225535_2_.nextInt(3) - 1);
            if (p_225535_1_.getBlockState(blockpos1.below()).getBlock() != this || p_225535_1_.getBlockState(blockpos1).func_224756_o(p_225535_1_, blockpos1)) {
               break;
            }

            ++j;
         }
      }

   }
}