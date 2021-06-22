package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class VillageConfig implements IFeatureConfig {
   public final ResourceLocation startPool;
   public final int maxDepth;

   public VillageConfig(String p_i51420_1_, int p_i51420_2_) {
      this.startPool = new ResourceLocation(p_i51420_1_);
      this.maxDepth = p_i51420_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("start_pool"), p_214634_1_.createString(this.startPool.toString()), p_214634_1_.createString("size"), p_214634_1_.createInt(this.maxDepth))));
   }

   public static <T> VillageConfig func_214679_a(Dynamic<T> p_214679_0_) {
      String s = p_214679_0_.get("start_pool").asString("");
      int i = p_214679_0_.get("size").asInt(6);
      return new VillageConfig(s, i);
   }
}