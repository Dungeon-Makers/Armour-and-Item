package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeManager {
   private final BiomeManager.IBiomeReader noiseBiomeSource;
   private final long biomeZoomSeed;
   private final IBiomeMagnifier zoomer;

   public BiomeManager(BiomeManager.IBiomeReader p_i225744_1_, long p_i225744_2_, IBiomeMagnifier p_i225744_4_) {
      this.noiseBiomeSource = p_i225744_1_;
      this.biomeZoomSeed = p_i225744_2_;
      this.zoomer = p_i225744_4_;
   }

   public BiomeManager withDifferentSource(BiomeProvider p_226835_1_) {
      return new BiomeManager(p_226835_1_, this.biomeZoomSeed, this.zoomer);
   }

   public Biome getBiome(BlockPos p_226836_1_) {
      return this.zoomer.getBiome(this.biomeZoomSeed, p_226836_1_.getX(), p_226836_1_.getY(), p_226836_1_.getZ(), this.noiseBiomeSource);
   }

   public interface IBiomeReader {
      Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_);
   }
}