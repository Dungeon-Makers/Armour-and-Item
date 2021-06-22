package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndIslandFeature extends Feature<NoFeatureConfig> {
   public EndIslandFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49880_1_) {
      super(p_i49880_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      float f = (float)(p_212245_3_.nextInt(3) + 4);

      for(int i = 0; f > 0.5F; --i) {
         for(int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
            for(int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
               if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                  this.func_202278_a(p_212245_1_, p_212245_4_.offset(j, i, k), Blocks.END_STONE.defaultBlockState());
               }
            }
         }

         f = (float)((double)f - ((double)p_212245_3_.nextInt(2) + 0.5D));
      }

      return true;
   }
}