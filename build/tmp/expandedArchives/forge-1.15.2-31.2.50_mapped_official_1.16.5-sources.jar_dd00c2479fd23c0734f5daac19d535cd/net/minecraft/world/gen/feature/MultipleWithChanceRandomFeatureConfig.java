package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class MultipleWithChanceRandomFeatureConfig implements IFeatureConfig {
   public final List<ConfiguredFeature<?, ?>> field_202454_a;
   public final int field_202456_c;

   public MultipleWithChanceRandomFeatureConfig(List<ConfiguredFeature<?, ?>> p_i51451_1_, int p_i51451_2_) {
      this.field_202454_a = p_i51451_1_;
      this.field_202456_c = p_i51451_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("features"), p_214634_1_.createList(this.field_202454_a.stream().map((p_227324_1_) -> {
         return p_227324_1_.func_222735_a(p_214634_1_).getValue();
      })), p_214634_1_.createString("count"), p_214634_1_.createInt(this.field_202456_c))));
   }

   public static <T> MultipleWithChanceRandomFeatureConfig func_214653_a(Dynamic<T> p_214653_0_) {
      List<ConfiguredFeature<?, ?>> list = p_214653_0_.get("features").asList(ConfiguredFeature::func_222736_a);
      int i = p_214653_0_.get("count").asInt(0);
      return new MultipleWithChanceRandomFeatureConfig(list, i);
   }
}