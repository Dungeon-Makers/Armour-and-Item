package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public class RuleEntry {
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundNBT outputTag;

   public RuleEntry(RuleTest p_i51326_1_, RuleTest p_i51326_2_, BlockState p_i51326_3_) {
      this(p_i51326_1_, p_i51326_2_, p_i51326_3_, (CompoundNBT)null);
   }

   public RuleEntry(RuleTest p_i51327_1_, RuleTest p_i51327_2_, BlockState p_i51327_3_, @Nullable CompoundNBT p_i51327_4_) {
      this.inputPredicate = p_i51327_1_;
      this.locPredicate = p_i51327_2_;
      this.outputState = p_i51327_3_;
      this.outputTag = p_i51327_4_;
   }

   public boolean func_215211_a(BlockState p_215211_1_, BlockState p_215211_2_, Random p_215211_3_) {
      return this.inputPredicate.test(p_215211_1_, p_215211_3_) && this.locPredicate.test(p_215211_2_, p_215211_3_);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundNBT getOutputTag() {
      return this.outputTag;
   }

   public <T> Dynamic<T> func_215212_a(DynamicOps<T> p_215212_1_) {
      T t = p_215212_1_.createMap(ImmutableMap.of(p_215212_1_.createString("input_predicate"), this.inputPredicate.func_215179_b(p_215212_1_).getValue(), p_215212_1_.createString("location_predicate"), this.locPredicate.func_215179_b(p_215212_1_).getValue(), p_215212_1_.createString("output_state"), BlockState.func_215689_a(p_215212_1_, this.outputState).getValue()));
      return this.outputTag == null ? new Dynamic<>(p_215212_1_, t) : new Dynamic<>(p_215212_1_, p_215212_1_.mergeInto(t, p_215212_1_.createString("output_nbt"), (new Dynamic<>(NBTDynamicOps.INSTANCE, this.outputTag)).convert(p_215212_1_).getValue()));
   }

   public static <T> RuleEntry func_215213_a(Dynamic<T> p_215213_0_) {
      Dynamic<T> dynamic = p_215213_0_.get("input_predicate").orElseEmptyMap();
      Dynamic<T> dynamic1 = p_215213_0_.get("location_predicate").orElseEmptyMap();
      RuleTest ruletest = IDynamicDeserializer.func_214907_a(dynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      RuleTest ruletest1 = IDynamicDeserializer.func_214907_a(dynamic1, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      BlockState blockstate = BlockState.func_215698_a(p_215213_0_.get("output_state").orElseEmptyMap());
      CompoundNBT compoundnbt = (CompoundNBT)p_215213_0_.get("output_nbt").map((p_215210_0_) -> {
         return p_215210_0_.convert(NBTDynamicOps.INSTANCE).getValue();
      }).orElse((INBT)null);
      return new RuleEntry(ruletest, ruletest1, blockstate, compoundnbt);
   }
}