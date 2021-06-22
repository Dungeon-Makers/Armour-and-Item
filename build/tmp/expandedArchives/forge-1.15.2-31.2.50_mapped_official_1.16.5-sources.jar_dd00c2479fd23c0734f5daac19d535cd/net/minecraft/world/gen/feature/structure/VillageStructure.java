package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VillageStructure extends Structure<VillageConfig> {
   public VillageStructure(Function<Dynamic<?>, ? extends VillageConfig> p_i51419_1_) {
      super(p_i51419_1_);
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.func_201496_a_().func_202173_a();
      int j = p_211744_1_.func_201496_a_().func_211729_b();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureWithSalt(p_211744_1_.func_202089_c(), k1, l1, 10387312);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + p_211744_2_.nextInt(i - j);
      l1 = l1 + p_211744_2_.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos chunkpos = this.func_211744_a(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      return p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z ? p_225558_2_.func_202094_a(p_225558_6_, this) : false;
   }

   public Structure.IStartFactory getStartFactory() {
      return VillageStructure.Start::new;
   }

   public String getFeatureName() {
      return "Village";
   }

   public int func_202367_b() {
      return 8;
   }

   public static class Start extends MarginedStructureStart {
      public Start(Structure<?> p_i225821_1_, int p_i225821_2_, int p_i225821_3_, MutableBoundingBox p_i225821_4_, int p_i225821_5_, long p_i225821_6_) {
         super(p_i225821_1_, p_i225821_2_, p_i225821_3_, p_i225821_4_, p_i225821_5_, p_i225821_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         VillageConfig villageconfig = (VillageConfig)p_214625_1_.func_202087_b(p_214625_5_, Feature.field_214550_p);
         BlockPos blockpos = new BlockPos(p_214625_3_ * 16, 0, p_214625_4_ * 16);
         VillagePieces.func_214838_a(p_214625_1_, p_214625_2_, blockpos, this.pieces, this.random, villageconfig);
         this.calculateBoundingBox();
      }
   }
}