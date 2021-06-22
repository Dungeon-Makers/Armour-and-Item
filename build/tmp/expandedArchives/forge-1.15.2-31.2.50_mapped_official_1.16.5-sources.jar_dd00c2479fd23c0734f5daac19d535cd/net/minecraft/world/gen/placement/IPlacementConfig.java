package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface IPlacementConfig {
   NoPlacementConfig NONE = new NoPlacementConfig();

   <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_);
}