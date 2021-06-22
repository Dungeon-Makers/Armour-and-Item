package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class MultipleRandomFeatureConfig implements IFeatureConfig {
   public final List<ConfiguredRandomFeatureList<?>> features;
   public final ConfiguredFeature<?, ?> defaultFeature;

   public MultipleRandomFeatureConfig(List<ConfiguredRandomFeatureList<?>> p_i51455_1_, ConfiguredFeature<?, ?> p_i51455_2_) {
      this.features = p_i51455_1_;
      this.defaultFeature = p_i51455_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      T t = p_214634_1_.createList(this.features.stream().map((p_227288_1_) -> {
         return p_227288_1_.func_214841_a(p_214634_1_).getValue();
      }));
      T t1 = this.defaultFeature.func_222735_a(p_214634_1_).getValue();
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("features"), t, p_214634_1_.createString("default"), t1)));
   }

   public static <T> MultipleRandomFeatureConfig func_214648_a(Dynamic<T> p_214648_0_) {
      List<ConfiguredRandomFeatureList<?>> list = p_214648_0_.get("features").asList(ConfiguredRandomFeatureList::func_214840_a);
      ConfiguredFeature<?, ?> configuredfeature = ConfiguredFeature.func_222736_a(p_214648_0_.get("default").orElseEmptyMap());
      return new MultipleRandomFeatureConfig(list, configuredfeature);
   }
}