package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;

public class AlwaysTrueRuleTest extends RuleTest {
   public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

   private AlwaysTrueRuleTest() {
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return true;
   }

   protected IRuleTestType getType() {
      return IRuleTestType.ALWAYS_TRUE_TEST;
   }

   protected <T> Dynamic<T> func_215182_a(DynamicOps<T> p_215182_1_) {
      return new Dynamic<>(p_215182_1_, p_215182_1_.emptyMap());
   }
}