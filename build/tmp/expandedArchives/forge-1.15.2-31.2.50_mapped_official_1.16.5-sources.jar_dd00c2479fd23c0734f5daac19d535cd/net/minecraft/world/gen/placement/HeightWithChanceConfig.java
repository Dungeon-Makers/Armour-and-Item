package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HeightWithChanceConfig implements IPlacementConfig {
   public final int field_202481_a;
   public final float field_202482_b;

   public HeightWithChanceConfig(int p_i48663_1_, float p_i48663_2_) {
      this.field_202481_a = p_i48663_1_;
      this.field_202482_b = p_i48663_2_;
   }

   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.field_202481_a), p_214719_1_.createString("chance"), p_214719_1_.createFloat(this.field_202482_b))));
   }

   public static HeightWithChanceConfig func_214724_a(Dynamic<?> p_214724_0_) {
      int i = p_214724_0_.get("count").asInt(0);
      float f = p_214724_0_.get("chance").asFloat(0.0F);
      return new HeightWithChanceConfig(i, f);
   }
}