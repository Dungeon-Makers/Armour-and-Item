package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EndCityStructure extends Structure<NoFeatureConfig> {
   public EndCityStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49883_1_) {
      super(p_i49883_1_);
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.func_201496_a_().func_202178_h();
      int j = p_211744_1_.func_201496_a_().func_211728_o();
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
         if (!p_225558_2_.func_202094_a(p_225558_6_, this)) {
            return false;
         } else {
            int i = getYPositionForFeature(p_225558_4_, p_225558_5_, p_225558_2_);
            return i >= 60;
         }
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return EndCityStructure.Start::new;
   }

   public String getFeatureName() {
      return "EndCity";
   }

   public int func_202367_b() {
      return 8;
   }

   private static int getYPositionForFeature(int p_191070_0_, int p_191070_1_, ChunkGenerator<?> p_191070_2_) {
      Random random = new Random((long)(p_191070_0_ + p_191070_1_ * 10387313));
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
         i = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      int k = (p_191070_0_ << 4) + 7;
      int l = (p_191070_1_ << 4) + 7;
      int i1 = p_191070_2_.getFirstOccupiedHeight(k, l, Heightmap.Type.WORLD_SURFACE_WG);
      int j1 = p_191070_2_.getFirstOccupiedHeight(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
      int k1 = p_191070_2_.getFirstOccupiedHeight(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
      int l1 = p_191070_2_.getFirstOccupiedHeight(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
      return Math.min(Math.min(i1, j1), Math.min(k1, l1));
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225802_1_, int p_i225802_2_, int p_i225802_3_, MutableBoundingBox p_i225802_4_, int p_i225802_5_, long p_i225802_6_) {
         super(p_i225802_1_, p_i225802_2_, p_i225802_3_, p_i225802_4_, p_i225802_5_, p_i225802_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         int i = EndCityStructure.getYPositionForFeature(p_214625_3_, p_214625_4_, p_214625_1_);
         if (i >= 60) {
            BlockPos blockpos = new BlockPos(p_214625_3_ * 16 + 8, i, p_214625_4_ * 16 + 8);
            EndCityPieces.startHouseTower(p_214625_2_, blockpos, rotation, this.pieces, this.random);
            this.calculateBoundingBox();
         }
      }
   }
}