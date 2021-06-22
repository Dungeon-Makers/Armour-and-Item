package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeaGrassBlock;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class SeaGrassFeature extends Feature<SeaGrassConfig> {
   public SeaGrassFeature(Function<Dynamic<?>, ? extends SeaGrassConfig> p_i51441_1_) {
      super(p_i51441_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SeaGrassConfig p_212245_5_) {
      int i = 0;

      for(int j = 0; j < p_212245_5_.field_203237_a; ++j) {
         int k = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int l = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int i1 = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + k, p_212245_4_.getZ() + l);
         BlockPos blockpos = new BlockPos(p_212245_4_.getX() + k, i1, p_212245_4_.getZ() + l);
         if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
            boolean flag = p_212245_3_.nextDouble() < p_212245_5_.field_203238_b;
            BlockState blockstate = flag ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
            if (blockstate.canSurvive(p_212245_1_, blockpos)) {
               if (flag) {
                  BlockState blockstate1 = blockstate.setValue(TallSeaGrassBlock.HALF, DoubleBlockHalf.UPPER);
                  BlockPos blockpos1 = blockpos.above();
                  if (p_212245_1_.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
                     p_212245_1_.setBlock(blockpos, blockstate, 2);
                     p_212245_1_.setBlock(blockpos1, blockstate1, 2);
                  }
               } else {
                  p_212245_1_.setBlock(blockpos, blockstate, 2);
               }

               ++i;
            }
         }
      }

      return i > 0;
   }
}