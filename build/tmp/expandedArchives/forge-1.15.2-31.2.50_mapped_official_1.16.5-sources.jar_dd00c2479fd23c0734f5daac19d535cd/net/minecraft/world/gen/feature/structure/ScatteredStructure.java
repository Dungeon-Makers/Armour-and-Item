package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class ScatteredStructure<C extends IFeatureConfig> extends Structure<C> {
   public ScatteredStructure(Function<Dynamic<?>, ? extends C> p_i51449_1_) {
      super(p_i51449_1_);
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = this.func_204030_a(p_211744_1_);
      int j = this.func_211745_b(p_211744_1_);
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureWithSalt(p_211744_1_.func_202089_c(), k1, l1, this.func_202382_c());
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + p_211744_2_.nextInt(i - j);
      l1 = l1 + p_211744_2_.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos chunkpos = this.func_211744_a(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      return p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z && p_225558_2_.func_202094_a(p_225558_6_, this);
   }

   protected int func_204030_a(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.func_201496_a_().func_202177_g();
   }

   protected int func_211745_b(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.func_201496_a_().func_211731_i();
   }

   protected abstract int func_202382_c();
}