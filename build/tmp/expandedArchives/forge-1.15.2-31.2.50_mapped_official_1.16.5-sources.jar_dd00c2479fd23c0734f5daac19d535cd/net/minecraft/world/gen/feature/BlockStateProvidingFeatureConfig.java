package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockStateProvidingFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider stateProvider;

   public BlockStateProvidingFeatureConfig(BlockStateProvider p_i225830_1_) {
      this.stateProvider = p_i225830_1_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("state_provider"), this.stateProvider.func_218175_a(p_214634_1_));
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(builder.build()));
   }

   public static <T> BlockStateProvidingFeatureConfig func_227269_a_(Dynamic<T> p_227269_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_227269_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockStateProvidingFeatureConfig(blockstateprovidertype.func_227399_a_(p_227269_0_.get("state_provider").orElseEmptyMap()));
   }
}