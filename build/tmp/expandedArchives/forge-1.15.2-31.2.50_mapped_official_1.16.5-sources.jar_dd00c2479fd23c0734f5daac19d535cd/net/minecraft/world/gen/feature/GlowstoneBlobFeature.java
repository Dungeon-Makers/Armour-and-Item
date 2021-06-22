package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class GlowstoneBlobFeature extends Feature<NoFeatureConfig> {
   public GlowstoneBlobFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49871_1_) {
      super(p_i49871_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (!p_212245_1_.isEmptyBlock(p_212245_4_)) {
         return false;
      } else if (p_212245_1_.getBlockState(p_212245_4_.above()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         p_212245_1_.setBlock(p_212245_4_, Blocks.GLOWSTONE.defaultBlockState(), 2);

         for(int i = 0; i < 1500; ++i) {
            BlockPos blockpos = p_212245_4_.offset(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), -p_212245_3_.nextInt(12), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
            if (p_212245_1_.getBlockState(blockpos).isAir(p_212245_1_, blockpos)) {
               int j = 0;

               for(Direction direction : Direction.values()) {
                  if (p_212245_1_.getBlockState(blockpos.relative(direction)).getBlock() == Blocks.GLOWSTONE) {
                     ++j;
                  }

                  if (j > 1) {
                     break;
                  }
               }

               if (j == 1) {
                  p_212245_1_.setBlock(blockpos, Blocks.GLOWSTONE.defaultBlockState(), 2);
               }
            }
         }

         return true;
      }
   }
}
