package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;

public class OctavesNoiseGenerator implements INoiseGenerator {
   private final ImprovedNoiseGenerator[] noiseLevels;
   private final double lowestFreqValueFactor;
   private final double lowestFreqInputFactor;

   public OctavesNoiseGenerator(SharedSeedRandom p_i225878_1_, int p_i225878_2_, int p_i225878_3_) {
      this(p_i225878_1_, new IntRBTreeSet(IntStream.rangeClosed(-p_i225878_2_, p_i225878_3_).toArray()));
   }

   public OctavesNoiseGenerator(SharedSeedRandom p_i225879_1_, IntSortedSet p_i225879_2_) {
      if (p_i225879_2_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_i225879_2_.firstInt();
         int j = p_i225879_2_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            ImprovedNoiseGenerator improvednoisegenerator = new ImprovedNoiseGenerator(p_i225879_1_);
            int l = j;
            this.noiseLevels = new ImprovedNoiseGenerator[k];
            if (j >= 0 && j < k && p_i225879_2_.contains(0)) {
               this.noiseLevels[j] = improvednoisegenerator;
            }

            for(int i1 = j + 1; i1 < k; ++i1) {
               if (i1 >= 0 && p_i225879_2_.contains(l - i1)) {
                  this.noiseLevels[i1] = new ImprovedNoiseGenerator(p_i225879_1_);
               } else {
                  p_i225879_1_.consumeCount(262);
               }
            }

            if (j > 0) {
               long k1 = (long)(improvednoisegenerator.noise(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * (double)9.223372E18F);
               SharedSeedRandom sharedseedrandom = new SharedSeedRandom(k1);

               for(int j1 = l - 1; j1 >= 0; --j1) {
                  if (j1 < k && p_i225879_2_.contains(l - j1)) {
                     this.noiseLevels[j1] = new ImprovedNoiseGenerator(sharedseedrandom);
                  } else {
                     sharedseedrandom.consumeCount(262);
                  }
               }
            }

            this.lowestFreqInputFactor = Math.pow(2.0D, (double)j);
            this.lowestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double)k) - 1.0D);
         }
      }
   }

   public double getValue(double p_205563_1_, double p_205563_3_, double p_205563_5_) {
      return this.getValue(p_205563_1_, p_205563_3_, p_205563_5_, 0.0D, 0.0D, false);
   }

   public double getValue(double p_215462_1_, double p_215462_3_, double p_215462_5_, double p_215462_7_, double p_215462_9_, boolean p_215462_11_) {
      double d0 = 0.0D;
      double d1 = this.lowestFreqInputFactor;
      double d2 = this.lowestFreqValueFactor;

      for(ImprovedNoiseGenerator improvednoisegenerator : this.noiseLevels) {
         if (improvednoisegenerator != null) {
            d0 += improvednoisegenerator.noise(wrap(p_215462_1_ * d1), p_215462_11_ ? -improvednoisegenerator.yo : wrap(p_215462_3_ * d1), wrap(p_215462_5_ * d1), p_215462_7_ * d1, p_215462_9_ * d1) * d2;
         }

         d1 /= 2.0D;
         d2 *= 2.0D;
      }

      return d0;
   }

   @Nullable
   public ImprovedNoiseGenerator getOctaveNoise(int p_215463_1_) {
      return this.noiseLevels[p_215463_1_];
   }

   public static double wrap(double p_215461_0_) {
      return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double p_215460_1_, double p_215460_3_, double p_215460_5_, double p_215460_7_) {
      return this.getValue(p_215460_1_, p_215460_3_, 0.0D, p_215460_5_, p_215460_7_, false);
   }
}