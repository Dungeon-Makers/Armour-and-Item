package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class SimpleBlockStateProvider extends BlockStateProvider {
   private final BlockState state;

   public SimpleBlockStateProvider(BlockState p_i225860_1_) {
      super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
      this.state = p_i225860_1_;
   }

   public <T> SimpleBlockStateProvider(Dynamic<T> p_i225861_1_) {
      this(BlockState.func_215698_a(p_i225861_1_.get("state").orElseEmptyMap()));
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      return this.state;
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.field_227393_a_).toString())).put(p_218175_1_.createString("state"), BlockState.func_215689_a(p_218175_1_, this.state).getValue());
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}