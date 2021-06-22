package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class WeightedBlockStateProvider extends BlockStateProvider {
   private final WeightedList<BlockState> weightedList;

   private WeightedBlockStateProvider(WeightedList<BlockState> p_i225862_1_) {
      super(BlockStateProviderType.WEIGHTED_STATE_PROVIDER);
      this.weightedList = p_i225862_1_;
   }

   public WeightedBlockStateProvider() {
      this(new WeightedList<>());
   }

   public <T> WeightedBlockStateProvider(Dynamic<T> p_i225863_1_) {
      this(new WeightedList<>(p_i225863_1_.get("entries").orElseEmptyList(), BlockState::func_215698_a));
   }

   public WeightedBlockStateProvider add(BlockState p_227407_1_, int p_227407_2_) {
      this.weightedList.add(p_227407_1_, p_227407_2_);
      return this;
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      return this.weightedList.getOne(p_225574_1_);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.field_227393_a_).toString())).put(p_218175_1_.createString("entries"), this.weightedList.func_226310_a_(p_218175_1_, (p_227408_1_) -> {
         return BlockState.func_215689_a(p_218175_1_, p_227408_1_);
      }));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}