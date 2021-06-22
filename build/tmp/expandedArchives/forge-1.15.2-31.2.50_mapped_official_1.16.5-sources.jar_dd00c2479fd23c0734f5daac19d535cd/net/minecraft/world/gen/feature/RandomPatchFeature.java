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
import net.minecraft.world.gen.Heightmap;

public class RandomPatchFeature extends Feature<BlockClusterFeatureConfig> {
   public RandomPatchFeature(Function<Dynamic<?>, ? extends BlockClusterFeatureConfig> p_i225816_1_) {
      super(p_i225816_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockClusterFeatureConfig p_212245_5_) {
      BlockState blockstate = p_212245_5_.stateProvider.getState(p_212245_3_, p_212245_4_);
      BlockPos blockpos;
      if (p_212245_5_.project) {
         blockpos = p_212245_1_.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, p_212245_4_);
      } else {
         blockpos = p_212245_4_;
      }

      int i = 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j = 0; j < p_212245_5_.tries; ++j) {
         blockpos$mutable.set(blockpos).move(p_212245_3_.nextInt(p_212245_5_.xspread + 1) - p_212245_3_.nextInt(p_212245_5_.xspread + 1), p_212245_3_.nextInt(p_212245_5_.yspread + 1) - p_212245_3_.nextInt(p_212245_5_.yspread + 1), p_212245_3_.nextInt(p_212245_5_.zspread + 1) - p_212245_3_.nextInt(p_212245_5_.zspread + 1));
         BlockPos blockpos1 = blockpos$mutable.below();
         BlockState blockstate1 = p_212245_1_.getBlockState(blockpos1);
         if ((p_212245_1_.isEmptyBlock(blockpos$mutable) || p_212245_5_.canReplace && p_212245_1_.getBlockState(blockpos$mutable).getMaterial().isReplaceable()) && blockstate.canSurvive(p_212245_1_, blockpos$mutable) && (p_212245_5_.whitelist.isEmpty() || p_212245_5_.whitelist.contains(blockstate1.getBlock())) && !p_212245_5_.blacklist.contains(blockstate1) && (!p_212245_5_.needWater || p_212245_1_.getFluidState(blockpos1.west()).is(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.east()).is(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.north()).is(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.south()).is(FluidTags.WATER))) {
            p_212245_5_.blockPlacer.place(p_212245_1_, blockpos$mutable, blockstate, p_212245_3_);
            ++i;
         }
      }

      return i > 0;
   }
}