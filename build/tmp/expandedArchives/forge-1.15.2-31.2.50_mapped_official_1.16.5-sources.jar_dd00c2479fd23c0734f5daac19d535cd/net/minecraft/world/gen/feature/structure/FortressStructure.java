package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FortressStructure extends Structure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> FORTRESS_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.field_200785_Y, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4));

   public FortressStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51476_1_) {
      super(p_i51476_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      int i = p_225558_4_ >> 4;
      int j = p_225558_5_ >> 4;
      p_225558_3_.setSeed((long)(i ^ j << 4) ^ p_225558_2_.func_202089_c());
      p_225558_3_.nextInt();
      if (p_225558_3_.nextInt(3) != 0) {
         return false;
      } else if (p_225558_4_ != (i << 4) + 4 + p_225558_3_.nextInt(8)) {
         return false;
      } else {
         return p_225558_5_ != (j << 4) + 4 + p_225558_3_.nextInt(8) ? false : p_225558_2_.func_202094_a(p_225558_6_, this);
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return FortressStructure.Start::new;
   }

   public String getFeatureName() {
      return "Fortress";
   }

   public int func_202367_b() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpecialEnemies() {
      return FORTRESS_ENEMIES;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225812_1_, int p_i225812_2_, int p_i225812_3_, MutableBoundingBox p_i225812_4_, int p_i225812_5_, long p_i225812_6_) {
         super(p_i225812_1_, p_i225812_2_, p_i225812_3_, p_i225812_4_, p_i225812_5_, p_i225812_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         FortressPieces.Start fortresspieces$start = new FortressPieces.Start(this.random, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2);
         this.pieces.add(fortresspieces$start);
         fortresspieces$start.addChildren(fortresspieces$start, this.pieces, this.random);
         List<StructurePiece> list = fortresspieces$start.pendingChildren;

         while(!list.isEmpty()) {
            int i = this.random.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.addChildren(fortresspieces$start, this.pieces, this.random);
         }

         this.calculateBoundingBox();
         this.moveInsideHeights(this.random, 48, 70);
      }
   }
}