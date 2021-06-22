package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentStructure extends Structure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.GUARDIAN, 1, 2, 4));

   public OceanMonumentStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51474_1_) {
      super(p_i51474_1_);
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.func_201496_a_().func_202174_b();
      int j = p_211744_1_.func_201496_a_().func_202171_c();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureWithSalt(p_211744_1_.func_202089_c(), k1, l1, 10387313);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      l1 = l1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos chunkpos = this.func_211744_a(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      if (p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z) {
         for(Biome biome : p_225558_2_.getBiomeSource().getBiomesWithin(p_225558_4_ * 16 + 9, p_225558_2_.func_222530_f(), p_225558_5_ * 16 + 9, 16)) {
            if (!p_225558_2_.func_202094_a(biome, this)) {
               return false;
            }
         }

         for(Biome biome1 : p_225558_2_.getBiomeSource().getBiomesWithin(p_225558_4_ * 16 + 9, p_225558_2_.func_222530_f(), p_225558_5_ * 16 + 9, 29)) {
            if (biome1.getBiomeCategory() != Biome.Category.OCEAN && biome1.getBiomeCategory() != Biome.Category.RIVER) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return OceanMonumentStructure.Start::new;
   }

   public String getFeatureName() {
      return "Monument";
   }

   public int func_202367_b() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpecialEnemies() {
      return MONUMENT_ENEMIES;
   }

   public static class Start extends StructureStart {
      private boolean isCreated;

      public Start(Structure<?> p_i225814_1_, int p_i225814_2_, int p_i225814_3_, MutableBoundingBox p_i225814_4_, int p_i225814_5_, long p_i225814_6_) {
         super(p_i225814_1_, p_i225814_2_, p_i225814_3_, p_i225814_4_, p_i225814_5_, p_i225814_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         this.generatePieces(p_214625_3_, p_214625_4_);
      }

      private void generatePieces(int p_214633_1_, int p_214633_2_) {
         int i = p_214633_1_ * 16 - 29;
         int j = p_214633_2_ * 16 - 29;
         Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
         this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, i, j, direction));
         this.calculateBoundingBox();
         this.isCreated = true;
      }

      public void func_225565_a_(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
         if (!this.isCreated) {
            this.pieces.clear();
            this.generatePieces(this.getChunkX(), this.getChunkZ());
         }

         super.func_225565_a_(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_);
      }
   }
}