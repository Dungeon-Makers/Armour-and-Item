package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public class NetherSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
   private static final BlockState field_205555_d = Blocks.NETHERRACK.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   protected long seed;
   protected OctavesNoiseGenerator decorationNoise;

   public NetherSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51308_1_) {
      super(p_i51308_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_11_ + 1;
      int j = p_205610_4_ & 15;
      int k = p_205610_5_ & 15;
      double d0 = 0.03125D;
      boolean flag = this.decorationNoise.getValue((double)p_205610_4_ * 0.03125D, (double)p_205610_5_ * 0.03125D, 0.0D) * 75.0D + p_205610_1_.nextDouble() > 0.0D;
      boolean flag1 = this.decorationNoise.getValue((double)p_205610_4_ * 0.03125D, 109.0D, (double)p_205610_5_ * 0.03125D) * 75.0D + p_205610_1_.nextDouble() > 0.0D;
      int l = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      int i1 = -1;
      BlockState blockstate = field_205555_d;
      BlockState blockstate1 = field_205555_d;

      for(int j1 = 127; j1 >= 0; --j1) {
         blockpos$mutable.set(j, j1, k);
         BlockState blockstate2 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate2.getBlock() != null && !blockstate2.isAir()) {
            if (blockstate2.getBlock() == p_205610_9_.getBlock()) {
               if (i1 == -1) {
                  if (l <= 0) {
                     blockstate = AIR;
                     blockstate1 = field_205555_d;
                  } else if (j1 >= i - 4 && j1 <= i + 1) {
                     blockstate = field_205555_d;
                     blockstate1 = field_205555_d;
                     if (flag1) {
                        blockstate = GRAVEL;
                        blockstate1 = field_205555_d;
                     }

                     if (flag) {
                        blockstate = SOUL_SAND;
                        blockstate1 = SOUL_SAND;
                     }
                  }

                  if (j1 < i && (blockstate == null || blockstate.isAir())) {
                     blockstate = p_205610_10_;
                  }

                  i1 = l;
                  if (j1 >= i - 1) {
                     p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
                  }
               } else if (i1 > 0) {
                  --i1;
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
               }
            }
         } else {
            i1 = -1;
         }
      }

   }

   public void initNoise(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.decorationNoise == null) {
         this.decorationNoise = new OctavesNoiseGenerator(new SharedSeedRandom(p_205548_1_), 3, 0);
      }

      this.seed = p_205548_1_;
   }
}