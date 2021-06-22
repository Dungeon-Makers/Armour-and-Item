package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockBlobFeature extends Feature<BlockBlobConfig> {
   public BlockBlobFeature(Function<Dynamic<?>, ? extends BlockBlobConfig> p_i49915_1_) {
      super(p_i49915_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockBlobConfig p_212245_5_) {
      while(true) {
         label48: {
            if (p_212245_4_.getY() > 3) {
               if (p_212245_1_.isEmptyBlock(p_212245_4_.below())) {
                  break label48;
               }

               Block block = p_212245_1_.getBlockState(p_212245_4_.below()).getBlock();
               if (!isDirt(block) && !isStone(block)) {
                  break label48;
               }
            }

            if (p_212245_4_.getY() <= 3) {
               return false;
            }

            int i1 = p_212245_5_.field_202464_b;

            for(int i = 0; i1 >= 0 && i < 3; ++i) {
               int j = i1 + p_212245_3_.nextInt(2);
               int k = i1 + p_212245_3_.nextInt(2);
               int l = i1 + p_212245_3_.nextInt(2);
               float f = (float)(j + k + l) * 0.333F + 0.5F;

               for(BlockPos blockpos : BlockPos.betweenClosed(p_212245_4_.offset(-j, -k, -l), p_212245_4_.offset(j, k, l))) {
                  if (blockpos.distSqr(p_212245_4_) <= (double)(f * f)) {
                     p_212245_1_.setBlock(blockpos, p_212245_5_.field_214683_a, 4);
                  }
               }

               p_212245_4_ = p_212245_4_.offset(-(i1 + 1) + p_212245_3_.nextInt(2 + i1 * 2), 0 - p_212245_3_.nextInt(2), -(i1 + 1) + p_212245_3_.nextInt(2 + i1 * 2));
            }

            return true;
         }

         p_212245_4_ = p_212245_4_.below();
      }
   }
}