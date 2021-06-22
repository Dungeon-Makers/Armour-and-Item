package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class WoodlandMansionStructure extends Structure<NoFeatureConfig> {
   public WoodlandMansionStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51413_1_) {
      super(p_i51413_1_);
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.func_201496_a_().func_202179_i();
      int j = p_211744_1_.func_201496_a_().func_211726_q();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureWithSalt(p_211744_1_.func_202089_c(), k1, l1, 10387319);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      l1 = l1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos chunkpos = this.func_211744_a(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      if (p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z) {
         for(Biome biome : p_225558_2_.getBiomeSource().getBiomesWithin(p_225558_4_ * 16 + 9, p_225558_2_.func_222530_f(), p_225558_5_ * 16 + 9, 32)) {
            if (!p_225558_2_.func_202094_a(biome, this)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return WoodlandMansionStructure.Start::new;
   }

   public String getFeatureName() {
      return "Mansion";
   }

   public int func_202367_b() {
      return 8;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225823_1_, int p_i225823_2_, int p_i225823_3_, MutableBoundingBox p_i225823_4_, int p_i225823_5_, long p_i225823_6_) {
         super(p_i225823_1_, p_i225823_2_, p_i225823_3_, p_i225823_4_, p_i225823_5_, p_i225823_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
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

         int k = (p_214625_3_ << 4) + 7;
         int l = (p_214625_4_ << 4) + 7;
         int i1 = p_214625_1_.getFirstOccupiedHeight(k, l, Heightmap.Type.WORLD_SURFACE_WG);
         int j1 = p_214625_1_.getFirstOccupiedHeight(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
         int k1 = p_214625_1_.getFirstOccupiedHeight(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
         int l1 = p_214625_1_.getFirstOccupiedHeight(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
         int i2 = Math.min(Math.min(i1, j1), Math.min(k1, l1));
         if (i2 >= 60) {
            BlockPos blockpos = new BlockPos(p_214625_3_ * 16 + 8, i2 + 1, p_214625_4_ * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(p_214625_2_, blockpos, rotation, list, this.random);
            this.pieces.addAll(list);
            this.calculateBoundingBox();
         }
      }

      public void func_225565_a_(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
         super.func_225565_a_(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_);
         int i = this.boundingBox.y0;

         for(int j = p_225565_4_.x0; j <= p_225565_4_.x1; ++j) {
            for(int k = p_225565_4_.z0; k <= p_225565_4_.z1; ++k) {
               BlockPos blockpos = new BlockPos(j, i, k);
               if (!p_225565_1_.isEmptyBlock(blockpos) && this.boundingBox.isInside(blockpos)) {
                  boolean flag = false;

                  for(StructurePiece structurepiece : this.pieces) {
                     if (structurepiece.getBoundingBox().isInside(blockpos)) {
                        flag = true;
                        break;
                     }
                  }

                  if (flag) {
                     for(int l = i - 1; l > 1; --l) {
                        BlockPos blockpos1 = new BlockPos(j, l, k);
                        if (!p_225565_1_.isEmptyBlock(blockpos1) && !p_225565_1_.getBlockState(blockpos1).getMaterial().isLiquid()) {
                           break;
                        }

                        p_225565_1_.setBlock(blockpos1, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}