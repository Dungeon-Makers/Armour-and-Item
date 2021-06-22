package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class AxisRotatingBlockStateProvider extends BlockStateProvider {
   private final Block block;

   public AxisRotatingBlockStateProvider(Block p_i225858_1_) {
      super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
      this.block = p_i225858_1_;
   }

   public <T> AxisRotatingBlockStateProvider(Dynamic<T> p_i225859_1_) {
      this(BlockState.func_215698_a(p_i225859_1_.get("state").orElseEmptyMap()).getBlock());
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      Direction.Axis direction$axis = Direction.Axis.func_218393_a(p_225574_1_);
      return this.block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction$axis);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.field_227393_a_).toString())).put(p_218175_1_.createString("state"), BlockState.func_215689_a(p_218175_1_, this.block.defaultBlockState()).getValue());
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}