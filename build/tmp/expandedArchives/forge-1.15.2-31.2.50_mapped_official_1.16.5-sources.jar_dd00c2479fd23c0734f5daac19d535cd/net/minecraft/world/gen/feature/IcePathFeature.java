package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IcePathFeature extends Feature<FeatureRadiusConfig> {
   private final Block field_150555_a = Blocks.PACKED_ICE;

   public IcePathFeature(Function<Dynamic<?>, ? extends FeatureRadiusConfig> p_i49861_1_) {
      super(p_i49861_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, FeatureRadiusConfig p_212245_5_) {
      while(p_212245_1_.isEmptyBlock(p_212245_4_) && p_212245_4_.getY() > 2) {
         p_212245_4_ = p_212245_4_.below();
      }

      if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         int i = p_212245_3_.nextInt(p_212245_5_.field_202436_a) + 2;
         int j = 1;

         for(int k = p_212245_4_.getX() - i; k <= p_212245_4_.getX() + i; ++k) {
            for(int l = p_212245_4_.getZ() - i; l <= p_212245_4_.getZ() + i; ++l) {
               int i1 = k - p_212245_4_.getX();
               int j1 = l - p_212245_4_.getZ();
               if (i1 * i1 + j1 * j1 <= i * i) {
                  for(int k1 = p_212245_4_.getY() - 1; k1 <= p_212245_4_.getY() + 1; ++k1) {
                     BlockPos blockpos = new BlockPos(k, k1, l);
                     Block block = p_212245_1_.getBlockState(blockpos).getBlock();
                     if (isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        p_212245_1_.setBlock(blockpos, this.field_150555_a.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}