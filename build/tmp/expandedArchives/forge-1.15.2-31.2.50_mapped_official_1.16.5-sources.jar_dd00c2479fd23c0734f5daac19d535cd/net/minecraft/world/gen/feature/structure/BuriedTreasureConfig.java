package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class BuriedTreasureConfig implements IFeatureConfig {
   public final float field_204293_a;

   public BuriedTreasureConfig(float p_i48877_1_) {
      this.field_204293_a = p_i48877_1_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("probability"), p_214634_1_.createFloat(this.field_204293_a))));
   }

   public static <T> BuriedTreasureConfig func_214684_a(Dynamic<T> p_214684_0_) {
      float f = p_214684_0_.get("probability").asFloat(0.0F);
      return new BuriedTreasureConfig(f);
   }
}