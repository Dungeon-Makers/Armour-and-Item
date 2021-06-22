package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountRangeConfig implements IPlacementConfig {
   public final int field_202469_a;
   public final int field_202470_b;
   public final int field_202471_c;
   public final int field_202472_d;

   public CountRangeConfig(int p_i48686_1_, int p_i48686_2_, int p_i48686_3_, int p_i48686_4_) {
      this.field_202469_a = p_i48686_1_;
      this.field_202470_b = p_i48686_2_;
      this.field_202471_c = p_i48686_3_;
      this.field_202472_d = p_i48686_4_;
   }

   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.field_202469_a), p_214719_1_.createString("bottom_offset"), p_214719_1_.createInt(this.field_202470_b), p_214719_1_.createString("top_offset"), p_214719_1_.createInt(this.field_202471_c), p_214719_1_.createString("maximum"), p_214719_1_.createInt(this.field_202472_d))));
   }

   public static CountRangeConfig func_214733_a(Dynamic<?> p_214733_0_) {
      int i = p_214733_0_.get("count").asInt(0);
      int j = p_214733_0_.get("bottom_offset").asInt(0);
      int k = p_214733_0_.get("top_offset").asInt(0);
      int l = p_214733_0_.get("maximum").asInt(0);
      return new CountRangeConfig(i, j, k, l);
   }
}