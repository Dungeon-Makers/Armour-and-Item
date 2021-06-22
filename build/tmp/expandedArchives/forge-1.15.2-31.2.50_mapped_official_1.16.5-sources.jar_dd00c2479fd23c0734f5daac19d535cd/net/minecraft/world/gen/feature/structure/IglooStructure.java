package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class IglooStructure extends ScatteredStructure<NoFeatureConfig> {
   public IglooStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51491_1_) {
      super(p_i51491_1_);
   }

   public String getFeatureName() {
      return "Igloo";
   }

   public int func_202367_b() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return IglooStructure.Start::new;
   }

   protected int func_202382_c() {
      return 14357618;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225806_1_, int p_i225806_2_, int p_i225806_3_, MutableBoundingBox p_i225806_4_, int p_i225806_5_, long p_i225806_6_) {
         super(p_i225806_1_, p_i225806_2_, p_i225806_3_, p_i225806_4_, p_i225806_5_, p_i225806_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         NoFeatureConfig nofeatureconfig = (NoFeatureConfig)p_214625_1_.func_202087_b(p_214625_5_, Feature.field_202333_k);
         int i = p_214625_3_ * 16;
         int j = p_214625_4_ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         IglooPieces.func_207617_a(p_214625_2_, blockpos, rotation, this.pieces, this.random, nofeatureconfig);
         this.calculateBoundingBox();
      }
   }
}