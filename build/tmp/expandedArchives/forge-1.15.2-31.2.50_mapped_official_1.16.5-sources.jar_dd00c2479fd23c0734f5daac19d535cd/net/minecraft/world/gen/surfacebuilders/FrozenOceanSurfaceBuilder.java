package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState ICE = Blocks.ICE.defaultBlockState();
   private PerlinNoiseGenerator icebergNoise;
   private PerlinNoiseGenerator icebergRoofNoise;
   private long seed;

   public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51313_1_) {
      super(p_i51313_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      float f = p_205610_3_.getTemperature(blockpos$mutable.set(p_205610_4_, 63, p_205610_5_));
      double d2 = Math.min(Math.abs(p_205610_7_), this.icebergNoise.getValue((double)p_205610_4_ * 0.1D, (double)p_205610_5_ * 0.1D, false) * 15.0D);
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.icebergRoofNoise.getValue((double)p_205610_4_ * 0.09765625D, (double)p_205610_5_ * 0.09765625D, false));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)p_205610_11_ - d0 - 7.0D;
            d0 = d0 + (double)p_205610_11_;
         } else {
            d0 = 0.0D;
         }
      }

      int k1 = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      BlockState blockstate2 = p_205610_3_.func_203944_q().getUnderMaterial();
      BlockState blockstate = p_205610_3_.func_203944_q().getTopMaterial();
      int l1 = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      int j = -1;
      int k = 0;
      int l = 2 + p_205610_1_.nextInt(4);
      int i1 = p_205610_11_ + 18 + p_205610_1_.nextInt(10);

      for(int j1 = Math.max(p_205610_6_, (int)d0 + 1); j1 >= 0; --j1) {
         blockpos$mutable.set(k1, j1, i);
         if (p_205610_2_.getBlockState(blockpos$mutable).isAir() && j1 < (int)d0 && p_205610_1_.nextDouble() > 0.01D) {
            p_205610_2_.setBlockState(blockpos$mutable, PACKED_ICE, false);
         } else if (p_205610_2_.getBlockState(blockpos$mutable).getMaterial() == Material.WATER && j1 > (int)d1 && j1 < p_205610_11_ && d1 != 0.0D && p_205610_1_.nextDouble() > 0.15D) {
            p_205610_2_.setBlockState(blockpos$mutable, PACKED_ICE, false);
         }

         BlockState blockstate1 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate1.isAir()) {
            j = -1;
         } else if (blockstate1.getBlock() != p_205610_9_.getBlock()) {
            if (blockstate1.getBlock() == Blocks.PACKED_ICE && k <= l && j1 > i1) {
               p_205610_2_.setBlockState(blockpos$mutable, SNOW_BLOCK, false);
               ++k;
            }
         } else if (j == -1) {
            if (l1 <= 0) {
               blockstate = AIR;
               blockstate2 = p_205610_9_;
            } else if (j1 >= p_205610_11_ - 4 && j1 <= p_205610_11_ + 1) {
               blockstate = p_205610_3_.func_203944_q().getTopMaterial();
               blockstate2 = p_205610_3_.func_203944_q().getUnderMaterial();
            }

            if (j1 < p_205610_11_ && (blockstate == null || blockstate.isAir())) {
               if (p_205610_3_.getTemperature(blockpos$mutable.set(p_205610_4_, j1, p_205610_5_)) < 0.15F) {
                  blockstate = ICE;
               } else {
                  blockstate = p_205610_10_;
               }
            }

            j = l1;
            if (j1 >= p_205610_11_ - 1) {
               p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
            } else if (j1 < p_205610_11_ - 7 - l1) {
               blockstate = AIR;
               blockstate2 = p_205610_9_;
               p_205610_2_.setBlockState(blockpos$mutable, GRAVEL, false);
            } else {
               p_205610_2_.setBlockState(blockpos$mutable, blockstate2, false);
            }
         } else if (j > 0) {
            --j;
            p_205610_2_.setBlockState(blockpos$mutable, blockstate2, false);
            if (j == 0 && blockstate2.getBlock() == Blocks.SAND && l1 > 1) {
               j = p_205610_1_.nextInt(4) + Math.max(0, j1 - 63);
               blockstate2 = blockstate2.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   public void initNoise(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.icebergNoise == null || this.icebergRoofNoise == null) {
         SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_205548_1_);
         this.icebergNoise = new PerlinNoiseGenerator(sharedseedrandom, 3, 0);
         this.icebergRoofNoise = new PerlinNoiseGenerator(sharedseedrandom, 0, 0);
      }

      this.seed = p_205548_1_;
   }
}