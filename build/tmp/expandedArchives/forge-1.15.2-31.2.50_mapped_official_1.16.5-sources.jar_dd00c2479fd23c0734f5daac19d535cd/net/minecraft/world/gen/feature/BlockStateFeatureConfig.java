package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockStateFeatureConfig implements IFeatureConfig {
   public final BlockState state;

   public BlockStateFeatureConfig(BlockState p_i225831_1_) {
      this.state = p_i225831_1_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.func_215689_a(p_214634_1_, this.state).getValue())));
   }

   public static <T> BlockStateFeatureConfig func_227271_a_(Dynamic<T> p_227271_0_) {
      BlockState blockstate = p_227271_0_.get("state").map(BlockState::func_215698_a).orElse(Blocks.AIR.defaultBlockState());
      return new BlockStateFeatureConfig(blockstate);
   }
}