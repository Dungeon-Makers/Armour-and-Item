package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpTopBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public KelpFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51487_1_) {
      super(p_i51487_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = 0;
      int j = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX(), p_212245_4_.getZ());
      BlockPos blockpos = new BlockPos(p_212245_4_.getX(), j, p_212245_4_.getZ());
      if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         BlockState blockstate = Blocks.KELP.defaultBlockState();
         BlockState blockstate1 = Blocks.KELP_PLANT.defaultBlockState();
         int k = 1 + p_212245_3_.nextInt(10);

         for(int l = 0; l <= k; ++l) {
            if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER && p_212245_1_.getBlockState(blockpos.above()).getBlock() == Blocks.WATER && blockstate1.canSurvive(p_212245_1_, blockpos)) {
               if (l == k) {
                  p_212245_1_.setBlock(blockpos, blockstate.setValue(KelpTopBlock.field_203163_a, Integer.valueOf(p_212245_3_.nextInt(4) + 20)), 2);
                  ++i;
               } else {
                  p_212245_1_.setBlock(blockpos, blockstate1, 2);
               }
            } else if (l > 0) {
               BlockPos blockpos1 = blockpos.below();
               if (blockstate.canSurvive(p_212245_1_, blockpos1) && p_212245_1_.getBlockState(blockpos1.below()).getBlock() != Blocks.KELP) {
                  p_212245_1_.setBlock(blockpos1, blockstate.setValue(KelpTopBlock.field_203163_a, Integer.valueOf(p_212245_3_.nextInt(4) + 20)), 2);
                  ++i;
               }
               break;
            }

            blockpos = blockpos.above();
         }
      }

      return i > 0;
   }
}