package net.minecraft.world.biome.provider;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.WorldInfo;

public class SingleBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome field_205438_a = Biomes.PLAINS;

   public SingleBiomeProviderSettings(WorldInfo p_i225748_1_) {
   }

   public SingleBiomeProviderSettings func_205436_a(Biome p_205436_1_) {
      this.field_205438_a = p_205436_1_;
      return this;
   }

   public Biome func_205437_a() {
      return this.field_205438_a;
   }
}