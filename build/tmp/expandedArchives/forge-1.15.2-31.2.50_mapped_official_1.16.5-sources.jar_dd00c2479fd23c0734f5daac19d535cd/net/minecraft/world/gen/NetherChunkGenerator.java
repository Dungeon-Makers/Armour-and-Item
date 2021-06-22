package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.Feature;

public class NetherChunkGenerator extends NoiseChunkGenerator<NetherGenSettings> {
   private final double[] field_222573_h = this.func_222572_j();

   public NetherChunkGenerator(World p_i48694_1_, BiomeProvider p_i48694_2_, NetherGenSettings p_i48694_3_) {
      super(p_i48694_1_, p_i48694_2_, 4, 8, 128, p_i48694_3_, false);
   }

   protected void fillNoiseColumn(double[] p_222548_1_, int p_222548_2_, int p_222548_3_) {
      double d0 = 684.412D;
      double d1 = 2053.236D;
      double d2 = 8.555150000000001D;
      double d3 = 34.2206D;
      int i = -10;
      int j = 3;
      this.func_222546_a(p_222548_1_, p_222548_2_, p_222548_3_, 684.412D, 2053.236D, 8.555150000000001D, 34.2206D, 3, -10);
   }

   protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
      return new double[]{0.0D, 0.0D};
   }

   protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
      return this.field_222573_h[p_222545_5_];
   }

   private double[] func_222572_j() {
      double[] adouble = new double[this.func_222550_i()];

      for(int i = 0; i < this.func_222550_i(); ++i) {
         adouble[i] = Math.cos((double)i * Math.PI * 6.0D / (double)this.func_222550_i()) * 2.0D;
         double d0 = (double)i;
         if (i > this.func_222550_i() / 2) {
            d0 = (double)(this.func_222550_i() - 1 - i);
         }

         if (d0 < 4.0D) {
            d0 = 4.0D - d0;
            adouble[i] -= d0 * d0 * d0 * 10.0D;
         }
      }

      return adouble;
   }

   public List<Biome.SpawnListEntry> func_177458_a(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      if (p_177458_1_ == EntityClassification.MONSTER) {
         if (Feature.field_202337_o.func_202366_b(this.field_222540_a, p_177458_2_)) {
            return Feature.field_202337_o.getSpecialEnemies();
         }

         if (Feature.field_202337_o.func_175796_a(this.field_222540_a, p_177458_2_) && this.field_222540_a.getBlockState(p_177458_2_.below()).getBlock() == Blocks.NETHER_BRICKS) {
            return Feature.field_202337_o.getSpecialEnemies();
         }
      }

      return super.func_177458_a(p_177458_1_, p_177458_2_);
   }

   public int getSpawnHeight() {
      return this.field_222540_a.getSeaLevel() + 1;
   }

   public int func_207511_e() {
      return 128;
   }

   public int func_222530_f() {
      return 32;
   }
}