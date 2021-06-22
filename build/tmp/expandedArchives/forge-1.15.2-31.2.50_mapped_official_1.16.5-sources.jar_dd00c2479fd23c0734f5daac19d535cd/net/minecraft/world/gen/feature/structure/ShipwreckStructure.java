package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ShipwreckStructure extends ScatteredStructure<ShipwreckConfig> {
   public ShipwreckStructure(Function<Dynamic<?>, ? extends ShipwreckConfig> p_i51440_1_) {
      super(p_i51440_1_);
   }

   public String getFeatureName() {
      return "Shipwreck";
   }

   public int func_202367_b() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return ShipwreckStructure.Start::new;
   }

   protected int func_202382_c() {
      return 165745295;
   }

   protected int func_204030_a(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.func_201496_a_().func_204748_h();
   }

   protected int func_211745_b(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.func_201496_a_().func_211730_k();
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225817_1_, int p_i225817_2_, int p_i225817_3_, MutableBoundingBox p_i225817_4_, int p_i225817_5_, long p_i225817_6_) {
         super(p_i225817_1_, p_i225817_2_, p_i225817_3_, p_i225817_4_, p_i225817_5_, p_i225817_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         ShipwreckConfig shipwreckconfig = (ShipwreckConfig)p_214625_1_.func_202087_b(p_214625_5_, Feature.field_204751_l);
         Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         BlockPos blockpos = new BlockPos(p_214625_3_ * 16, 90, p_214625_4_ * 16);
         ShipwreckPieces.addPieces(p_214625_2_, blockpos, rotation, this.pieces, this.random, shipwreckconfig);
         this.calculateBoundingBox();
      }
   }
}