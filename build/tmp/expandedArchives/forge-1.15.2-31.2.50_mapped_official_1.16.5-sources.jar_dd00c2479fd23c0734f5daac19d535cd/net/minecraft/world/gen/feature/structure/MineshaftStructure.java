package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class MineshaftStructure extends Structure<MineshaftConfig> {
   public MineshaftStructure(Function<Dynamic<?>, ? extends MineshaftConfig> p_i51478_1_) {
      super(p_i51478_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ((SharedSeedRandom)p_225558_3_).setLargeFeatureSeed(p_225558_2_.func_202089_c(), p_225558_4_, p_225558_5_);
      if (p_225558_2_.func_202094_a(p_225558_6_, this)) {
         MineshaftConfig mineshaftconfig = (MineshaftConfig)p_225558_2_.func_202087_b(p_225558_6_, this);
         double d0 = mineshaftconfig.probability;
         return p_225558_3_.nextDouble() < d0;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return MineshaftStructure.Start::new;
   }

   public String getFeatureName() {
      return "Mineshaft";
   }

   public int func_202367_b() {
      return 8;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225811_1_, int p_i225811_2_, int p_i225811_3_, MutableBoundingBox p_i225811_4_, int p_i225811_5_, long p_i225811_6_) {
         super(p_i225811_1_, p_i225811_2_, p_i225811_3_, p_i225811_4_, p_i225811_5_, p_i225811_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         MineshaftConfig mineshaftconfig = (MineshaftConfig)p_214625_1_.func_202087_b(p_214625_5_, Feature.field_202329_g);
         MineshaftPieces.Room mineshaftpieces$room = new MineshaftPieces.Room(0, this.random, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2, mineshaftconfig.type);
         this.pieces.add(mineshaftpieces$room);
         mineshaftpieces$room.addChildren(mineshaftpieces$room, this.pieces, this.random);
         this.calculateBoundingBox();
         if (mineshaftconfig.type == MineshaftStructure.Type.MESA) {
            int i = -5;
            int j = p_214625_1_.func_222530_f() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 - -5;
            this.boundingBox.move(0, j, 0);

            for(StructurePiece structurepiece : this.pieces) {
               structurepiece.move(0, j, 0);
            }
         } else {
            this.moveBelowSeaLevel(p_214625_1_.func_222530_f(), this.random, 10);
         }

      }
   }

   public static enum Type {
      NORMAL("normal"),
      MESA("mesa");

      private static final Map<String, MineshaftStructure.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MineshaftStructure.Type::getName, (p_214716_0_) -> {
         return p_214716_0_;
      }));
      private final String name;

      private Type(String p_i50444_3_) {
         this.name = p_i50444_3_;
      }

      public String getName() {
         return this.name;
      }

      public static MineshaftStructure.Type byName(String p_214715_0_) {
         return BY_NAME.get(p_214715_0_);
      }

      public static MineshaftStructure.Type byId(int p_189910_0_) {
         return p_189910_0_ >= 0 && p_189910_0_ < values().length ? values()[p_189910_0_] : NORMAL;
      }
   }
}