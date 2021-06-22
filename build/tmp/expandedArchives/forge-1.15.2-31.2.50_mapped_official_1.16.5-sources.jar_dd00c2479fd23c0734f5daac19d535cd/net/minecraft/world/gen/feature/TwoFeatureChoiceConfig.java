package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final ConfiguredFeature<?, ?> featureTrue;
   public final ConfiguredFeature<?, ?> featureFalse;

   public TwoFeatureChoiceConfig(ConfiguredFeature<?, ?> p_i225835_1_, ConfiguredFeature<?, ?> p_i225835_2_) {
      this.featureTrue = p_i225835_1_;
      this.featureFalse = p_i225835_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("feature_true"), this.featureTrue.func_222735_a(p_214634_1_).getValue(), p_214634_1_.createString("feature_false"), this.featureFalse.func_222735_a(p_214634_1_).getValue())));
   }

   public static <T> TwoFeatureChoiceConfig func_227287_a_(Dynamic<T> p_227287_0_) {
      ConfiguredFeature<?, ?> configuredfeature = ConfiguredFeature.func_222736_a(p_227287_0_.get("feature_true").orElseEmptyMap());
      ConfiguredFeature<?, ?> configuredfeature1 = ConfiguredFeature.func_222736_a(p_227287_0_.get("feature_false").orElseEmptyMap());
      return new TwoFeatureChoiceConfig(configuredfeature, configuredfeature1);
   }
}