package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoFeatureConfig implements IFeatureConfig {
   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.emptyMap());
   }

   public static <T> NoFeatureConfig func_214639_a(Dynamic<T> p_214639_0_) {
      return NONE;
   }
}