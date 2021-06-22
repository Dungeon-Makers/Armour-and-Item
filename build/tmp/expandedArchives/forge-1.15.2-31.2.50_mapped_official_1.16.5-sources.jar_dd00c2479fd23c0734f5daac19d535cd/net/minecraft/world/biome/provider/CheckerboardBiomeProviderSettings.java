package net.minecraft.world.biome.provider;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.WorldInfo;

public class CheckerboardBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome[] field_205434_a = new Biome[]{Biomes.PLAINS};
   private int field_205435_b = 1;

   public CheckerboardBiomeProviderSettings(WorldInfo p_i225747_1_) {
   }

   public CheckerboardBiomeProviderSettings func_206860_a(Biome[] p_206860_1_) {
      this.field_205434_a = p_206860_1_;
      return this;
   }

   public CheckerboardBiomeProviderSettings func_206861_a(int p_206861_1_) {
      this.field_205435_b = p_206861_1_;
      return this;
   }

   public Biome[] func_205432_a() {
      return this.field_205434_a;
   }

   public int func_205433_b() {
      return this.field_205435_b;
   }
}