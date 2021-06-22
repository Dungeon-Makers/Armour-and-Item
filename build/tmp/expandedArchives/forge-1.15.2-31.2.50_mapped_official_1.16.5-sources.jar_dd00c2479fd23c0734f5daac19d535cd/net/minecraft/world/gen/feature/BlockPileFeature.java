package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockPileFeature extends Feature<BlockStateProvidingFeatureConfig> {
   public BlockPileFeature(Function<Dynamic<?>, ? extends BlockStateProvidingFeatureConfig> p_i49914_1_) {
      super(p_i49914_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockStateProvidingFeatureConfig p_212245_5_) {
      if (p_212245_4_.getY() < 5) {
         return false;
      } else {
         int i = 2 + p_212245_3_.nextInt(2);
         int j = 2 + p_212245_3_.nextInt(2);

         for(BlockPos blockpos : BlockPos.betweenClosed(p_212245_4_.offset(-i, 0, -j), p_212245_4_.offset(i, 1, j))) {
            int k = p_212245_4_.getX() - blockpos.getX();
            int l = p_212245_4_.getZ() - blockpos.getZ();
            if ((float)(k * k + l * l) <= p_212245_3_.nextFloat() * 10.0F - p_212245_3_.nextFloat() * 6.0F) {
               this.tryPlaceBlock(p_212245_1_, blockpos, p_212245_3_, p_212245_5_);
            } else if ((double)p_212245_3_.nextFloat() < 0.031D) {
               this.tryPlaceBlock(p_212245_1_, blockpos, p_212245_3_, p_212245_5_);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(IWorld p_214621_1_, BlockPos p_214621_2_, Random p_214621_3_) {
      BlockPos blockpos = p_214621_2_.below();
      BlockState blockstate = p_214621_1_.getBlockState(blockpos);
      return blockstate.getBlock() == Blocks.GRASS_PATH ? p_214621_3_.nextBoolean() : blockstate.isFaceSturdy(p_214621_1_, blockpos, Direction.UP);
   }

   private void tryPlaceBlock(IWorld p_227225_1_, BlockPos p_227225_2_, Random p_227225_3_, BlockStateProvidingFeatureConfig p_227225_4_) {
      if (p_227225_1_.isEmptyBlock(p_227225_2_) && this.mayPlaceOn(p_227225_1_, p_227225_2_, p_227225_3_)) {
         p_227225_1_.setBlock(p_227225_2_, p_227225_4_.stateProvider.getState(p_227225_3_, p_227225_2_), 4);
      }

   }
}