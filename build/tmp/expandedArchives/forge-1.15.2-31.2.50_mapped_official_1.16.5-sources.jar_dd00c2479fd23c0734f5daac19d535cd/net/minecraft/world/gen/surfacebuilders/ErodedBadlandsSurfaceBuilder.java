package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();

   public ErodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51314_1_) {
      super(p_i51314_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = Math.min(Math.abs(p_205610_7_), this.pillarNoise.getValue((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D, false) * 15.0D);
      if (d1 > 0.0D) {
         double d2 = 0.001953125D;
         double d3 = Math.abs(this.pillarRoofNoise.getValue((double)p_205610_4_ * 0.001953125D, (double)p_205610_5_ * 0.001953125D, false));
         d0 = d1 * d1 * 2.5D;
         double d4 = Math.ceil(d3 * 50.0D) + 14.0D;
         if (d0 > d4) {
            d0 = d4;
         }

         d0 = d0 + 64.0D;
      }

      int l = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      BlockState blockstate2 = WHITE_TERRACOTTA;
      BlockState blockstate = p_205610_3_.func_203944_q().getUnderMaterial();
      int i1 = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean flag = Math.cos(p_205610_7_ / 3.0D * Math.PI) > 0.0D;
      int j = -1;
      boolean flag1 = false;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k = Math.max(p_205610_6_, (int)d0 + 1); k >= 0; --k) {
         blockpos$mutable.set(l, k, i);
         if (p_205610_2_.getBlockState(blockpos$mutable).isAir() && k < (int)d0) {
            p_205610_2_.setBlockState(blockpos$mutable, p_205610_9_, false);
         }

         BlockState blockstate1 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate1.isAir()) {
            j = -1;
         } else if (blockstate1.getBlock() == p_205610_9_.getBlock()) {
            if (j == -1) {
               flag1 = false;
               if (i1 <= 0) {
                  blockstate2 = Blocks.AIR.defaultBlockState();
                  blockstate = p_205610_9_;
               } else if (k >= p_205610_11_ - 4 && k <= p_205610_11_ + 1) {
                  blockstate2 = WHITE_TERRACOTTA;
                  blockstate = p_205610_3_.func_203944_q().getUnderMaterial();
               }

               if (k < p_205610_11_ && (blockstate2 == null || blockstate2.isAir())) {
                  blockstate2 = p_205610_10_;
               }

               j = i1 + Math.max(0, k - p_205610_11_);
               if (k >= p_205610_11_ - 1) {
                  if (k <= p_205610_11_ + 3 + i1) {
                     p_205610_2_.setBlockState(blockpos$mutable, p_205610_3_.func_203944_q().getTopMaterial(), false);
                     flag1 = true;
                  } else {
                     BlockState blockstate3;
                     if (k >= 64 && k <= 127) {
                        if (flag) {
                           blockstate3 = TERRACOTTA;
                        } else {
                           blockstate3 = this.getBand(p_205610_4_, k, p_205610_5_);
                        }
                     } else {
                        blockstate3 = ORANGE_TERRACOTTA;
                     }

                     p_205610_2_.setBlockState(blockpos$mutable, blockstate3, false);
                  }
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
                  Block block = blockstate.getBlock();
                  if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                     p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (j > 0) {
               --j;
               if (flag1) {
                  p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, this.getBand(p_205610_4_, k, p_205610_5_), false);
               }
            }
         }
      }

   }
}