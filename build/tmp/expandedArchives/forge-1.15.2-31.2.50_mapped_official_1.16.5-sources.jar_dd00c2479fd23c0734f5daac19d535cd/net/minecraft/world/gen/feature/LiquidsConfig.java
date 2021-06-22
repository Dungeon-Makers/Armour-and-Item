package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LiquidsConfig implements IFeatureConfig {
   public final IFluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final Set<Block> validBlocks;

   public LiquidsConfig(IFluidState p_i225841_1_, boolean p_i225841_2_, int p_i225841_3_, int p_i225841_4_, Set<Block> p_i225841_5_) {
      this.state = p_i225841_1_;
      this.requiresBlockBelow = p_i225841_2_;
      this.rockCount = p_i225841_3_;
      this.holeCount = p_i225841_4_;
      this.validBlocks = p_i225841_5_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), IFluidState.func_215680_a(p_214634_1_, this.state).getValue(), p_214634_1_.createString("requires_block_below"), p_214634_1_.createBoolean(this.requiresBlockBelow), p_214634_1_.createString("rock_count"), p_214634_1_.createInt(this.rockCount), p_214634_1_.createString("hole_count"), p_214634_1_.createInt(this.holeCount), p_214634_1_.createString("valid_blocks"), p_214634_1_.createList(this.validBlocks.stream().map(Registry.BLOCK::getKey).map(ResourceLocation::toString).map(p_214634_1_::createString)))));
   }

   public static <T> LiquidsConfig func_214677_a(Dynamic<T> p_214677_0_) {
      return new LiquidsConfig(p_214677_0_.get("state").map(IFluidState::func_215681_a).orElse(Fluids.EMPTY.defaultFluidState()), p_214677_0_.get("requires_block_below").asBoolean(true), p_214677_0_.get("rock_count").asInt(4), p_214677_0_.get("hole_count").asInt(1), ImmutableSet.copyOf(p_214677_0_.get("valid_blocks").asList((p_227367_0_) -> {
         return Registry.BLOCK.get(new ResourceLocation(p_227367_0_.asString("minecraft:air")));
      })));
   }
}