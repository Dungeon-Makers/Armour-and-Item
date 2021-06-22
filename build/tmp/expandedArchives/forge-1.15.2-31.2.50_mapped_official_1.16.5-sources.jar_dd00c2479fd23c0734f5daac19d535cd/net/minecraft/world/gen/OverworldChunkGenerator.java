package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class OverworldChunkGenerator extends NoiseChunkGenerator<OverworldGenSettings> {
   private static final float[] field_222576_h = Util.make(new float[25], (p_222575_0_) -> {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
            p_222575_0_[i + 2 + (j + 2) * 5] = f;
         }
      }

   });
   private final OctavesNoiseGenerator field_185984_c;
   private final boolean field_222577_j;
   private final PhantomSpawner field_203230_r = new PhantomSpawner();
   private final PatrolSpawner field_222578_l = new PatrolSpawner();
   private final CatSpawner field_222579_m = new CatSpawner();
   private final VillageSiege field_225495_n = new VillageSiege();

   public OverworldChunkGenerator(IWorld p_i48957_1_, BiomeProvider p_i48957_2_, OverworldGenSettings p_i48957_3_) {
      super(p_i48957_1_, p_i48957_2_, 4, 8, 256, p_i48957_3_, true);
      this.random.consumeCount(2620);
      this.field_185984_c = new OctavesNoiseGenerator(this.random, 15, 0);
      this.field_222577_j = p_i48957_1_.getLevelData().func_76067_t() == WorldType.field_151360_e;
   }

   public void func_202093_c(WorldGenRegion p_202093_1_) {
      int i = p_202093_1_.getCenterX();
      int j = p_202093_1_.getCenterZ();
      Biome biome = p_202093_1_.getBiome((new ChunkPos(i, j)).getWorldPosition());
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setDecorationSeed(p_202093_1_.getSeed(), i << 4, j << 4);
      WorldEntitySpawner.spawnMobsForChunkGeneration(p_202093_1_, biome, i, j, sharedseedrandom);
   }

   protected void fillNoiseColumn(double[] p_222548_1_, int p_222548_2_, int p_222548_3_) {
      double d0 = (double)684.412F;
      double d1 = (double)684.412F;
      double d2 = 8.555149841308594D;
      double d3 = 4.277574920654297D;
      int i = -10;
      int j = 3;
      this.func_222546_a(p_222548_1_, p_222548_2_, p_222548_3_, (double)684.412F, (double)684.412F, 8.555149841308594D, 4.277574920654297D, 3, -10);
   }

   protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
      double d0 = 8.5D;
      double d1 = ((double)p_222545_5_ - (8.5D + p_222545_1_ * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / p_222545_3_;
      if (d1 < 0.0D) {
         d1 *= 4.0D;
      }

      return d1;
   }

   protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
      double[] adouble = new double[2];
      float f = 0.0F;
      float f1 = 0.0F;
      float f2 = 0.0F;
      int i = 2;
      int j = this.func_222530_f();
      float f3 = this.biomeSource.getNoiseBiome(p_222549_1_, j, p_222549_2_).getDepth();

      for(int k = -2; k <= 2; ++k) {
         for(int l = -2; l <= 2; ++l) {
            Biome biome = this.biomeSource.getNoiseBiome(p_222549_1_ + k, j, p_222549_2_ + l);
            float f4 = biome.getDepth();
            float f5 = biome.getScale();
            if (this.field_222577_j && f4 > 0.0F) {
               f4 = 1.0F + f4 * 2.0F;
               f5 = 1.0F + f5 * 4.0F;
            }

            float f6 = field_222576_h[k + 2 + (l + 2) * 5] / (f4 + 2.0F);
            if (biome.getDepth() > f3) {
               f6 /= 2.0F;
            }

            f += f5 * f6;
            f1 += f4 * f6;
            f2 += f6;
         }
      }

      f = f / f2;
      f1 = f1 / f2;
      f = f * 0.9F + 0.1F;
      f1 = (f1 * 4.0F - 1.0F) / 8.0F;
      adouble[0] = (double)f1 + this.func_222574_c(p_222549_1_, p_222549_2_);
      adouble[1] = (double)f;
      return adouble;
   }

   private double func_222574_c(int p_222574_1_, int p_222574_2_) {
      double d0 = this.field_185984_c.getValue((double)(p_222574_1_ * 200), 10.0D, (double)(p_222574_2_ * 200), 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
      if (d0 < 0.0D) {
         d0 = -d0 * 0.3D;
      }

      d0 = d0 * 3.0D - 2.0D;
      if (d0 < 0.0D) {
         d0 = d0 / 28.0D;
      } else {
         if (d0 > 1.0D) {
            d0 = 1.0D;
         }

         d0 = d0 / 40.0D;
      }

      return d0;
   }

   public List<Biome.SpawnListEntry> func_177458_a(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      if (Feature.field_202334_l.func_202383_b(this.field_222540_a, p_177458_2_)) {
         if (p_177458_1_ == EntityClassification.MONSTER) {
            return Feature.field_202334_l.getSpecialEnemies();
         }

         if (p_177458_1_ == EntityClassification.CREATURE) {
            return Feature.field_202334_l.getSpecialAnimals();
         }
      } else if (p_177458_1_ == EntityClassification.MONSTER) {
         if (Feature.field_214536_b.func_175796_a(this.field_222540_a, p_177458_2_)) {
            return Feature.field_214536_b.getSpecialEnemies();
         }

         if (Feature.field_202336_n.func_175796_a(this.field_222540_a, p_177458_2_)) {
            return Feature.field_202336_n.getSpecialEnemies();
         }
      }

      return super.func_177458_a(p_177458_1_, p_177458_2_);
   }

   public void func_203222_a(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      this.field_203230_r.func_203232_a(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_222578_l.func_222696_a(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_222579_m.func_221124_a(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_225495_n.func_225477_a(p_203222_1_, p_203222_2_, p_203222_3_);
   }

   public int getSpawnHeight() {
      return this.field_222540_a.getSeaLevel() + 1;
   }

   public int func_222530_f() {
      return 63;
   }
}