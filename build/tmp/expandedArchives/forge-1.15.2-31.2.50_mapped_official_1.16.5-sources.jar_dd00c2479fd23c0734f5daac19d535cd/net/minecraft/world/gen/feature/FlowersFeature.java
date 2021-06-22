package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class FlowersFeature<U extends IFeatureConfig> extends Feature<U> {
   public FlowersFeature(Function<Dynamic<?>, ? extends U> p_i49876_1_) {
      super(p_i49876_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, U p_212245_5_) {
      BlockState blockstate = this.getRandomFlower(p_212245_3_, p_212245_4_, p_212245_5_);
      int i = 0;

      for(int j = 0; j < this.getCount(p_212245_5_); ++j) {
         BlockPos blockpos = this.getPos(p_212245_3_, p_212245_4_, p_212245_5_);
         if (p_212245_1_.isEmptyBlock(blockpos) && blockpos.getY() < p_212245_1_.getMaxHeight() - 1 && blockstate.canSurvive(p_212245_1_, blockpos) && this.isValid(p_212245_1_, blockpos, p_212245_5_)) {
            p_212245_1_.setBlock(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }

   public abstract boolean isValid(IWorld p_225559_1_, BlockPos p_225559_2_, U p_225559_3_);

   public abstract int getCount(U p_225560_1_);

   public abstract BlockPos getPos(Random p_225561_1_, BlockPos p_225561_2_, U p_225561_3_);

   public abstract BlockState getRandomFlower(Random p_225562_1_, BlockPos p_225562_2_, U p_225562_3_);
}
