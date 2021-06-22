package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SpringFeature extends Feature<LiquidsConfig> {
   public SpringFeature(Function<Dynamic<?>, ? extends LiquidsConfig> p_i51430_1_) {
      super(p_i51430_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, LiquidsConfig p_212245_5_) {
      if (!p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.above()).getBlock())) {
         return false;
      } else if (p_212245_5_.requiresBlockBelow && !p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.below()).getBlock())) {
         return false;
      } else {
         BlockState blockstate = p_212245_1_.getBlockState(p_212245_4_);
         if (!blockstate.isAir(p_212245_1_, p_212245_4_) && !p_212245_5_.validBlocks.contains(blockstate.getBlock())) {
            return false;
         } else {
            int i = 0;
            int j = 0;
            if (p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.west()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.east()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.north()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.south()).getBlock())) {
               ++j;
            }

            if (p_212245_5_.validBlocks.contains(p_212245_1_.getBlockState(p_212245_4_.below()).getBlock())) {
               ++j;
            }

            int k = 0;
            if (p_212245_1_.isEmptyBlock(p_212245_4_.west())) {
               ++k;
            }

            if (p_212245_1_.isEmptyBlock(p_212245_4_.east())) {
               ++k;
            }

            if (p_212245_1_.isEmptyBlock(p_212245_4_.north())) {
               ++k;
            }

            if (p_212245_1_.isEmptyBlock(p_212245_4_.south())) {
               ++k;
            }

            if (p_212245_1_.isEmptyBlock(p_212245_4_.below())) {
               ++k;
            }

            if (j == p_212245_5_.rockCount && k == p_212245_5_.holeCount) {
               p_212245_1_.setBlock(p_212245_4_, p_212245_5_.state.createLegacyBlock(), 2);
               p_212245_1_.getLiquidTicks().scheduleTick(p_212245_4_, p_212245_5_.state.getType(), 0);
               ++i;
            }

            return i > 0;
         }
      }
   }
}
