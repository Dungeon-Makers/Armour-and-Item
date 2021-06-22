package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   public BuriedTreasureStructure(Function<Dynamic<?>, ? extends BuriedTreasureConfig> p_i49910_1_) {
      super(p_i49910_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      if (p_225558_2_.func_202094_a(p_225558_6_, this)) {
         ((SharedSeedRandom)p_225558_3_).setLargeFeatureWithSalt(p_225558_2_.func_202089_c(), p_225558_4_, p_225558_5_, 10387320);
         BuriedTreasureConfig buriedtreasureconfig = (BuriedTreasureConfig)p_225558_2_.func_202087_b(p_225558_6_, this);
         return p_225558_3_.nextFloat() < buriedtreasureconfig.field_204293_a;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return BuriedTreasureStructure.Start::new;
   }

   public String getFeatureName() {
      return "Buried_Treasure";
   }

   public int func_202367_b() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225799_1_, int p_i225799_2_, int p_i225799_3_, MutableBoundingBox p_i225799_4_, int p_i225799_5_, long p_i225799_6_) {
         super(p_i225799_1_, p_i225799_2_, p_i225799_3_, p_i225799_4_, p_i225799_5_, p_i225799_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         int i = p_214625_3_ * 16;
         int j = p_214625_4_ * 16;
         BlockPos blockpos = new BlockPos(i + 9, 90, j + 9);
         this.pieces.add(new BuriedTreasure.Piece(blockpos));
         this.calculateBoundingBox();
      }

      public BlockPos getLocatePos() {
         return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
      }
   }
}