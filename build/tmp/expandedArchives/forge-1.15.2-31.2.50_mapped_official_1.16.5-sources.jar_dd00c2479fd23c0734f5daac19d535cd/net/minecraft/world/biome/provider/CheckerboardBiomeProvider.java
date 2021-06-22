package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeProvider extends BiomeProvider {
   private final Biome[] allowedBiomes;
   private final int bitShift;

   public CheckerboardBiomeProvider(CheckerboardBiomeProviderSettings p_i48973_1_) {
      super(ImmutableSet.copyOf(p_i48973_1_.func_205432_a()));
      this.allowedBiomes = p_i48973_1_.func_205432_a();
      this.bitShift = p_i48973_1_.func_205433_b() + 2;
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      return this.allowedBiomes[Math.abs(((p_225526_1_ >> this.bitShift) + (p_225526_3_ >> this.bitShift)) % this.allowedBiomes.length)];
   }
}