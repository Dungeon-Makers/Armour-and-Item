package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends ScatteredStructure<OceanRuinConfig> {
   public OceanRuinStructure(Function<Dynamic<?>, ? extends OceanRuinConfig> p_i51348_1_) {
      super(p_i51348_1_);
   }

   public String getFeatureName() {
      return "Ocean_Ruin";
   }

   public int func_202367_b() {
      return 3;
   }

   protected int func_204030_a(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.func_201496_a_().func_204026_h();
   }

   protected int func_211745_b(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.func_201496_a_().func_211727_m();
   }

   public Structure.IStartFactory getStartFactory() {
      return OceanRuinStructure.Start::new;
   }

   protected int func_202382_c() {
      return 14357621;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225875_1_, int p_i225875_2_, int p_i225875_3_, MutableBoundingBox p_i225875_4_, int p_i225875_5_, long p_i225875_6_) {
         super(p_i225875_1_, p_i225875_2_, p_i225875_3_, p_i225875_4_, p_i225875_5_, p_i225875_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         OceanRuinConfig oceanruinconfig = (OceanRuinConfig)p_214625_1_.func_202087_b(p_214625_5_, Feature.field_204029_o);
         int i = p_214625_3_ * 16;
         int j = p_214625_4_ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         OceanRuinPieces.addPieces(p_214625_2_, blockpos, rotation, this.pieces, this.random, oceanruinconfig);
         this.calculateBoundingBox();
      }
   }

   public static enum Type {
      WARM("warm"),
      COLD("cold");

      private static final Map<String, OceanRuinStructure.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(OceanRuinStructure.Type::getName, (p_215134_0_) -> {
         return p_215134_0_;
      }));
      private final String name;

      private Type(String p_i50621_3_) {
         this.name = p_i50621_3_;
      }

      public String getName() {
         return this.name;
      }

      public static OceanRuinStructure.Type byName(String p_215136_0_) {
         return BY_NAME.get(p_215136_0_);
      }
   }
}