package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public IceAndSnowFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51435_1_) {
      super(p_i51435_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = p_212245_4_.getX() + i;
            int l = p_212245_4_.getZ() + j;
            int i1 = p_212245_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, k, l);
            blockpos$mutable.set(k, i1, l);
            blockpos$mutable1.set(blockpos$mutable).move(Direction.DOWN, 1);
            Biome biome = p_212245_1_.getBiome(blockpos$mutable);
            if (biome.shouldFreeze(p_212245_1_, blockpos$mutable1, false)) {
               p_212245_1_.setBlock(blockpos$mutable1, Blocks.ICE.defaultBlockState(), 2);
            }

            if (biome.shouldSnow(p_212245_1_, blockpos$mutable)) {
               p_212245_1_.setBlock(blockpos$mutable, Blocks.SNOW.defaultBlockState(), 2);
               BlockState blockstate = p_212245_1_.getBlockState(blockpos$mutable1);
               if (blockstate.func_196959_b(SnowyDirtBlock.SNOWY)) {
                  p_212245_1_.setBlock(blockpos$mutable1, blockstate.setValue(SnowyDirtBlock.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}