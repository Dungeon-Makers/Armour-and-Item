package net.minecraft.world.gen.feature.template;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IRuleTestType extends IDynamicDeserializer<RuleTest> {
   IRuleTestType ALWAYS_TRUE_TEST = func_214910_a("always_true", (p_214909_0_) -> {
      return AlwaysTrueRuleTest.INSTANCE;
   });
   IRuleTestType BLOCK_TEST = func_214910_a("block_match", BlockMatchRuleTest::new);
   IRuleTestType BLOCKSTATE_TEST = func_214910_a("blockstate_match", BlockStateMatchRuleTest::new);
   IRuleTestType TAG_TEST = func_214910_a("tag_match", TagMatchRuleTest::new);
   IRuleTestType RANDOM_BLOCK_TEST = func_214910_a("random_block_match", RandomBlockMatchRuleTest::new);
   IRuleTestType RANDOM_BLOCKSTATE_TEST = func_214910_a("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

   static IRuleTestType func_214910_a(String p_214910_0_, IRuleTestType p_214910_1_) {
      return Registry.register(Registry.RULE_TEST, p_214910_0_, p_214910_1_);
   }
}