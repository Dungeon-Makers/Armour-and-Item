package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();

   public WoodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51303_1_) {
      super(p_i51303_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_4_ & 15;
      int j = p_205610_5_ & 15;
      BlockState blockstate = WHITE_TERRACOTTA;
      BlockState blockstate1 = p_205610_3_.func_203944_q().getUnderMaterial();
      int k = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean flag = Math.cos(p_205610_7_ / 3.0D * Math.PI) > 0.0D;
      int l = -1;
      boolean flag1 = false;
      int i1 = 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j1 = p_205610_6_; j1 >= 0; --j1) {
         if (i1 < 15) {
            blockpos$mutable.set(i, j1, j);
            BlockState blockstate2 = p_205610_2_.getBlockState(blockpos$mutable);
            if (blockstate2.isAir()) {
               l = -1;
            } else if (blockstate2.getBlock() == p_205610_9_.getBlock()) {
               if (l == -1) {
                  flag1 = false;
                  if (k <= 0) {
                     blockstate = Blocks.AIR.defaultBlockState();
                     blockstate1 = p_205610_9_;
                  } else if (j1 >= p_205610_11_ - 4 && j1 <= p_205610_11_ + 1) {
                     blockstate = WHITE_TERRACOTTA;
                     blockstate1 = p_205610_3_.func_203944_q().getUnderMaterial();
                  }

                  if (j1 < p_205610_11_ && (blockstate == null || blockstate.isAir())) {
                     blockstate = p_205610_10_;
                  }

                  l = k + Math.max(0, j1 - p_205610_11_);
                  if (j1 >= p_205610_11_ - 1) {
                     if (j1 > 86 + k * 2) {
                        if (flag) {
                           p_205610_2_.setBlockState(blockpos$mutable, Blocks.COARSE_DIRT.defaultBlockState(), false);
                        } else {
                           p_205610_2_.setBlockState(blockpos$mutable, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                        }
                     } else if (j1 > p_205610_11_ + 3 + k) {
                        BlockState blockstate3;
                        if (j1 >= 64 && j1 <= 127) {
                           if (flag) {
                              blockstate3 = TERRACOTTA;
                           } else {
                              blockstate3 = this.getBand(p_205610_4_, j1, p_205610_5_);
                           }
                        } else {
                           blockstate3 = ORANGE_TERRACOTTA;
                        }

                        p_205610_2_.setBlockState(blockpos$mutable, blockstate3, false);
                     } else {
                        p_205610_2_.setBlockState(blockpos$mutable, p_205610_3_.func_203944_q().getTopMaterial(), false);
                        flag1 = true;
                     }
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
                     if (blockstate1 == WHITE_TERRACOTTA) {
                        p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (l > 0) {
                  --l;
                  if (flag1) {
                     p_205610_2_.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutable, this.getBand(p_205610_4_, j1, p_205610_5_), false);
                  }
               }

               ++i1;
            }
         }
      }

   }
}