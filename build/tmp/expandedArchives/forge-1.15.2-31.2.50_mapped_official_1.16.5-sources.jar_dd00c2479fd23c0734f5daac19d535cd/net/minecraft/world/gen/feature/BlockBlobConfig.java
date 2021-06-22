package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockBlobConfig implements IFeatureConfig {
   public final BlockState field_214683_a;
   public final int field_202464_b;

   public BlockBlobConfig(BlockState p_i49916_1_, int p_i49916_2_) {
      this.field_214683_a = p_i49916_1_;
      this.field_202464_b = p_i49916_2_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.func_215689_a(p_214634_1_, this.field_214683_a).getValue(), p_214634_1_.createString("start_radius"), p_214634_1_.createInt(this.field_202464_b))));
   }

   public static <T> BlockBlobConfig func_214682_a(Dynamic<T> p_214682_0_) {
      BlockState blockstate = p_214682_0_.get("state").map(BlockState::func_215698_a).orElse(Blocks.AIR.defaultBlockState());
      int i = p_214682_0_.get("start_radius").asInt(0);
      return new BlockBlobConfig(blockstate, i);
   }
}