package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends RuleTest {
   private final BlockState blockState;

   public BlockStateMatchRuleTest(BlockState p_i51330_1_) {
      this.blockState = p_i51330_1_;
   }

   public <T> BlockStateMatchRuleTest(Dynamic<T> p_i51331_1_) {
      this(BlockState.func_215698_a(p_i51331_1_.get("blockstate").orElseEmptyMap()));
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.blockState;
   }

   protected IRuleTestType getType() {
      return IRuleTestType.BLOCKSTATE_TEST;
   }

   protected <T> Dynamic<T> func_215182_a(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.createMap(ImmutableMap.of(p_215182_1_.createString("blockstate"), BlockState.func_215689_a(p_215182_1_, this.blockState).getValue())));
   }
}