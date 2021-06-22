package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoPlacementConfig implements IPlacementConfig {
   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.emptyMap());
   }

   public static NoPlacementConfig func_214735_a(Dynamic<?> p_214735_0_) {
      return new NoPlacementConfig();
   }
}