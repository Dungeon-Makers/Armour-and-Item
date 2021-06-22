package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.CountConfig;

public class SeaPickleFeature extends Feature<CountConfig> {
   public SeaPickleFeature(Function<Dynamic<?>, ? extends CountConfig> p_i51442_1_) {
      super(p_i51442_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<?> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, CountConfig p_212245_5_) {
      int i = 0;

      for(int j = 0; j < p_212245_5_.field_204915_a; ++j) {
         int k = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int l = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int i1 = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + k, p_212245_4_.getZ() + l);
         BlockPos blockpos = new BlockPos(p_212245_4_.getX() + k, i1, p_212245_4_.getZ() + l);
         BlockState blockstate = Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, Integer.valueOf(p_212245_3_.nextInt(4) + 1));
         if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER && blockstate.canSurvive(p_212245_1_, blockpos)) {
            p_212245_1_.setBlock(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}