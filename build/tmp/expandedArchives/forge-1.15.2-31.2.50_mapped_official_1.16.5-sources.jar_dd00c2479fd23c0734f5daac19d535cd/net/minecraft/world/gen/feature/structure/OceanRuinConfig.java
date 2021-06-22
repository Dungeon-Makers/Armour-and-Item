package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class OceanRuinConfig implements IFeatureConfig {
   public final OceanRuinStructure.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfig(OceanRuinStructure.Type p_i48866_1_, float p_i48866_2_, float p_i48866_3_) {
      this.biomeTemp = p_i48866_1_;
      this.largeProbability = p_i48866_2_;
      this.clusterProbability = p_i48866_3_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("biome_temp"), p_214634_1_.createString(this.biomeTemp.getName()), p_214634_1_.createString("large_probability"), p_214634_1_.createFloat(this.largeProbability), p_214634_1_.createString("cluster_probability"), p_214634_1_.createFloat(this.clusterProbability))));
   }

   public static <T> OceanRuinConfig func_214640_a(Dynamic<T> p_214640_0_) {
      OceanRuinStructure.Type oceanruinstructure$type = OceanRuinStructure.Type.byName(p_214640_0_.get("biome_temp").asString(""));
      float f = p_214640_0_.get("large_probability").asFloat(0.0F);
      float f1 = p_214640_0_.get("cluster_probability").asFloat(0.0F);
      return new OceanRuinConfig(oceanruinstructure$type, f, f1);
   }
}