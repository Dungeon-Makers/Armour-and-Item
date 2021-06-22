package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class FillLayerFeature extends Feature<FillLayerConfig> {
   public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerConfig> p_i49877_1_) {
      super(p_i49877_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, FillLayerConfig p_212245_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = p_212245_4_.getX() + i;
            int l = p_212245_4_.getZ() + j;
            int i1 = p_212245_5_.height;
            blockpos$mutable.set(k, i1, l);
            if (p_212245_1_.getBlockState(blockpos$mutable).isAir()) {
               p_212245_1_.setBlock(blockpos$mutable, p_212245_5_.state, 2);
            }
         }
      }

      return true;
   }
}