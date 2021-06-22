package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placement.ConfiguredPlacement;

public class DecoratedFeatureConfig implements IFeatureConfig {
   public final ConfiguredFeature<?, ?> feature;
   public final ConfiguredPlacement<?> decorator;

   public DecoratedFeatureConfig(ConfiguredFeature<?, ?> p_i49891_1_, ConfiguredPlacement<?> p_i49891_2_) {
      this.feature = p_i49891_1_;
      this.decorator = p_i49891_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("feature"), this.feature.func_222735_a(p_214634_1_).getValue(), p_214634_1_.createString("decorator"), this.decorator.func_215094_a(p_214634_1_).getValue())));
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.feature), Registry.DECORATOR.getKey(this.decorator.decorator));
   }

   public static <T> DecoratedFeatureConfig func_214688_a(Dynamic<T> p_214688_0_) {
      ConfiguredFeature<?, ?> configuredfeature = ConfiguredFeature.func_222736_a(p_214688_0_.get("feature").orElseEmptyMap());
      ConfiguredPlacement<?> configuredplacement = ConfiguredPlacement.func_215095_a(p_214688_0_.get("decorator").orElseEmptyMap());
      return new DecoratedFeatureConfig(configuredfeature, configuredplacement);
   }
}