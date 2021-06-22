package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeaGrassConfig implements IFeatureConfig {
   public final int field_203237_a;
   public final double field_203238_b;

   public SeaGrassConfig(int p_i48776_1_, double p_i48776_2_) {
      this.field_203237_a = p_i48776_1_;
      this.field_203238_b = p_i48776_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("count"), p_214634_1_.createInt(this.field_203237_a), p_214634_1_.createString("tall_seagrass_probability"), p_214634_1_.createDouble(this.field_203238_b))));
   }

   public static <T> SeaGrassConfig func_214659_a(Dynamic<T> p_214659_0_) {
      int i = p_214659_0_.get("count").asInt(0);
      double d0 = p_214659_0_.get("tall_seagrass_probability").asDouble(0.0D);
      return new SeaGrassConfig(i, d0);
   }
}