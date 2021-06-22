package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DepthAverageConfig implements IPlacementConfig {
   public final int field_202483_a;
   public final int baseline;
   public final int spread;

   public DepthAverageConfig(int p_i48661_1_, int p_i48661_2_, int p_i48661_3_) {
      this.field_202483_a = p_i48661_1_;
      this.baseline = p_i48661_2_;
      this.spread = p_i48661_3_;
   }

   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.field_202483_a), p_214719_1_.createString("baseline"), p_214719_1_.createInt(this.baseline), p_214719_1_.createString("spread"), p_214719_1_.createInt(this.spread))));
   }

   public static DepthAverageConfig func_214729_a(Dynamic<?> p_214729_0_) {
      int i = p_214729_0_.get("count").asInt(0);
      int j = p_214729_0_.get("baseline").asInt(0);
      int k = p_214729_0_.get("spread").asInt(0);
      return new DepthAverageConfig(i, j, k);
   }
}