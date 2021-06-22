package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class SingleBiomeProvider extends BiomeProvider {
   private final Biome biome;

   public SingleBiomeProvider(SingleBiomeProviderSettings p_i48972_1_) {
      super(ImmutableSet.of(p_i48972_1_.func_205437_a()));
      this.biome = p_i48972_1_.func_205437_a();
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      return this.biome;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int p_225531_1_, int p_225531_2_, int p_225531_3_, int p_225531_4_, List<Biome> p_225531_5_, Random p_225531_6_) {
      return p_225531_5_.contains(this.biome) ? new BlockPos(p_225531_1_ - p_225531_4_ + p_225531_6_.nextInt(p_225531_4_ * 2 + 1), p_225531_2_, p_225531_3_ - p_225531_4_ + p_225531_6_.nextInt(p_225531_4_ * 2 + 1)) : null;
   }

   public Set<Biome> getBiomesWithin(int p_225530_1_, int p_225530_2_, int p_225530_3_, int p_225530_4_) {
      return Sets.newHashSet(this.biome);
   }
}