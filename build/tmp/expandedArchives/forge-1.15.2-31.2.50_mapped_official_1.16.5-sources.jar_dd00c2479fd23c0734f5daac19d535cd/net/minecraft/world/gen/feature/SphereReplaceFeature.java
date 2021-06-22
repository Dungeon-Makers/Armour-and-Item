package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SphereReplaceFeature extends Feature<SphereReplaceConfig> {
   public SphereReplaceFeature(Function<Dynamic<?>, ? extends SphereReplaceConfig> p_i49885_1_) {
      super(p_i49885_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SphereReplaceConfig p_212245_5_) {
      if (!p_212245_1_.getFluidState(p_212245_4_).is(FluidTags.WATER)) {
         return false;
      } else {
         int i = 0;
         int j = p_212245_3_.nextInt(p_212245_5_.radius - 2) + 2;

         for(int k = p_212245_4_.getX() - j; k <= p_212245_4_.getX() + j; ++k) {
            for(int l = p_212245_4_.getZ() - j; l <= p_212245_4_.getZ() + j; ++l) {
               int i1 = k - p_212245_4_.getX();
               int j1 = l - p_212245_4_.getZ();
               if (i1 * i1 + j1 * j1 <= j * j) {
                  for(int k1 = p_212245_4_.getY() - p_212245_5_.field_202433_c; k1 <= p_212245_4_.getY() + p_212245_5_.field_202433_c; ++k1) {
                     BlockPos blockpos = new BlockPos(k, k1, l);
                     BlockState blockstate = p_212245_1_.getBlockState(blockpos);

                     for(BlockState blockstate1 : p_212245_5_.targets) {
                        if (blockstate1.getBlock() == blockstate.getBlock()) {
                           p_212245_1_.setBlock(blockpos, p_212245_5_.state, 2);
                           ++i;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return i > 0;
      }
   }
}