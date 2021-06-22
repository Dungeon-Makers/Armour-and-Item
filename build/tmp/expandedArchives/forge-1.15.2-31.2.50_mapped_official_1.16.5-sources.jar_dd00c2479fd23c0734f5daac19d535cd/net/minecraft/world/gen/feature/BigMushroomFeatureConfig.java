package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BigMushroomFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider capProvider;
   public final BlockStateProvider stemProvider;
   public final int foliageRadius;

   public BigMushroomFeatureConfig(BlockStateProvider p_i225832_1_, BlockStateProvider p_i225832_2_, int p_i225832_3_) {
      this.capProvider = p_i225832_1_;
      this.stemProvider = p_i225832_2_;
      this.foliageRadius = p_i225832_3_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("cap_provider"), this.capProvider.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("stem_provider"), this.stemProvider.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("foliage_radius"), p_214634_1_.createInt(this.foliageRadius));
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(builder.build()));
   }

   public static <T> BigMushroomFeatureConfig func_222853_a(Dynamic<T> p_222853_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_222853_0_.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType<?> blockstateprovidertype1 = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_222853_0_.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BigMushroomFeatureConfig(blockstateprovidertype.func_227399_a_(p_222853_0_.get("cap_provider").orElseEmptyMap()), blockstateprovidertype1.func_227399_a_(p_222853_0_.get("stem_provider").orElseEmptyMap()), p_222853_0_.get("foliage_radius").asInt(2));
   }
}