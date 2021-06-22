package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FeatureRadiusConfig implements IFeatureConfig {
   public final int field_202436_a;

   public FeatureRadiusConfig(int p_i48682_1_) {
      this.field_202436_a = p_i48682_1_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("radius"), p_214634_1_.createInt(this.field_202436_a))));
   }

   public static <T> FeatureRadiusConfig func_214706_a(Dynamic<T> p_214706_0_) {
      int i = p_214706_0_.get("radius").asInt(0);
      return new FeatureRadiusConfig(i);
   }
}