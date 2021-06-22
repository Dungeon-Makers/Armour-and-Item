package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlueIceFeature extends Feature<NoFeatureConfig> {
   public BlueIceFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49912_1_) {
      super(p_i49912_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (p_212245_4_.getY() > p_212245_1_.getSeaLevel() - 1) {
         return false;
      } else if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.WATER && p_212245_1_.getBlockState(p_212245_4_.below()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && p_212245_1_.getBlockState(p_212245_4_.relative(direction)).getBlock() == Blocks.PACKED_ICE) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         } else {
            p_212245_1_.setBlock(p_212245_4_, Blocks.BLUE_ICE.defaultBlockState(), 2);

            for(int i = 0; i < 200; ++i) {
               int j = p_212245_3_.nextInt(5) - p_212245_3_.nextInt(6);
               int k = 3;
               if (j < 2) {
                  k += j / 2;
               }

               if (k >= 1) {
                  BlockPos blockpos = p_212245_4_.offset(p_212245_3_.nextInt(k) - p_212245_3_.nextInt(k), j, p_212245_3_.nextInt(k) - p_212245_3_.nextInt(k));
                  BlockState blockstate = p_212245_1_.getBlockState(blockpos);
                  Block block = blockstate.getBlock();
                  if (blockstate.getMaterial() == Material.AIR || block == Blocks.WATER || block == Blocks.PACKED_ICE || block == Blocks.ICE) {
                     for(Direction direction1 : Direction.values()) {
                        Block block1 = p_212245_1_.getBlockState(blockpos.relative(direction1)).getBlock();
                        if (block1 == Blocks.BLUE_ICE) {
                           p_212245_1_.setBlock(blockpos, Blocks.BLUE_ICE.defaultBlockState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}