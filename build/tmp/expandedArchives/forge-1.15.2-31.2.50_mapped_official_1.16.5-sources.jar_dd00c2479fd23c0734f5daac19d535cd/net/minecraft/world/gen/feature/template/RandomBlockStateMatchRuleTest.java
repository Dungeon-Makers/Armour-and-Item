package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class RandomBlockStateMatchRuleTest extends RuleTest {
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchRuleTest(BlockState p_i51322_1_, float p_i51322_2_) {
      this.blockState = p_i51322_1_;
      this.probability = p_i51322_2_;
   }

   public <T> RandomBlockStateMatchRuleTest(Dynamic<T> p_i51323_1_) {
      this(BlockState.func_215698_a(p_i51323_1_.get("blockstate").orElseEmptyMap()), p_i51323_1_.get("probability").asFloat(1.0F));
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.blockState && p_215181_2_.nextFloat() < this.probability;
   }

   protected IRuleTestType getType() {
      return IRuleTestType.RANDOM_BLOCKSTATE_TEST;
   }

   protected <T> Dynamic<T> func_215182_a(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.createMap(ImmutableMap.of(p_215182_1_.createString("blockstate"), BlockState.func_215689_a(p_215182_1_, this.blockState).getValue(), p_215182_1_.createString("probability"), p_215182_1_.createFloat(this.probability))));
   }
}