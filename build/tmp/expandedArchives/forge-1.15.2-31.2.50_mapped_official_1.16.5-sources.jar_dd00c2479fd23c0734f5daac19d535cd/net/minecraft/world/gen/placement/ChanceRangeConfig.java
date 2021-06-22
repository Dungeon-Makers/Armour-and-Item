package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float field_202488_a;
   public final int field_202490_c;
   public final int field_202489_b;
   public final int field_202491_d;

   public ChanceRangeConfig(float p_i48687_1_, int p_i48687_2_, int p_i48687_3_, int p_i48687_4_) {
      this.field_202488_a = p_i48687_1_;
      this.field_202490_c = p_i48687_2_;
      this.field_202489_b = p_i48687_3_;
      this.field_202491_d = p_i48687_4_;
   }

   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("chance"), p_214719_1_.createFloat(this.field_202488_a), p_214719_1_.createString("bottom_offset"), p_214719_1_.createInt(this.field_202490_c), p_214719_1_.createString("top_offset"), p_214719_1_.createInt(this.field_202489_b), p_214719_1_.createString("top"), p_214719_1_.createInt(this.field_202491_d))));
   }

   public static ChanceRangeConfig func_214732_a(Dynamic<?> p_214732_0_) {
      float f = p_214732_0_.get("chance").asFloat(0.0F);
      int i = p_214732_0_.get("bottom_offset").asInt(0);
      int j = p_214732_0_.get("top_offset").asInt(0);
      int k = p_214732_0_.get("top").asInt(0);
      return new ChanceRangeConfig(f, i, j, k);
   }
}