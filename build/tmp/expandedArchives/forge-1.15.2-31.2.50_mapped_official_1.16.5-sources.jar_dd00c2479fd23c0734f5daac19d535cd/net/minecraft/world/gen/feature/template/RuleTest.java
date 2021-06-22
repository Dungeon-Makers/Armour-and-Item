package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public abstract class RuleTest {
   public abstract boolean test(BlockState p_215181_1_, Random p_215181_2_);

   public <T> Dynamic<T> func_215179_b(DynamicOps<T> p_215179_1_) {
      return new Dynamic<>(p_215179_1_, p_215179_1_.mergeInto(this.func_215182_a(p_215179_1_).getValue(), p_215179_1_.createString("predicate_type"), p_215179_1_.createString(Registry.RULE_TEST.getKey(this.getType()).toString())));
   }

   protected abstract IRuleTestType getType();

   protected abstract <T> Dynamic<T> func_215182_a(DynamicOps<T> p_215182_1_);
}