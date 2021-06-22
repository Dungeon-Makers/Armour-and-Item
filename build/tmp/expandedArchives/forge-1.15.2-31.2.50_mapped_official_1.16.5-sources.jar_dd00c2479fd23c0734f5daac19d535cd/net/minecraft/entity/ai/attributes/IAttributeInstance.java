package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAttributeInstance {
   IAttribute getAttribute();

   double getBaseValue();

   void setBaseValue(double p_111128_1_);

   Set<AttributeModifier> getModifiers(AttributeModifier.Operation p_225504_1_);

   Set<AttributeModifier> getModifiers();

   boolean hasModifier(AttributeModifier p_180374_1_);

   @Nullable
   AttributeModifier getModifier(UUID p_111127_1_);

   void addModifier(AttributeModifier p_111121_1_);

   void removeModifier(AttributeModifier p_111124_1_);

   void removeModifier(UUID p_188479_1_);

   @OnlyIn(Dist.CLIENT)
   void removeModifiers();

   double getValue();

   @OnlyIn(Dist.CLIENT)
   default void func_226302_a_(IAttributeInstance p_226302_1_) {
      this.setBaseValue(p_226302_1_.getBaseValue());
      Set<AttributeModifier> set = p_226302_1_.getModifiers();
      Set<AttributeModifier> set1 = this.getModifiers();
      ImmutableSet<AttributeModifier> immutableset = ImmutableSet.copyOf(Sets.difference(set, set1));
      ImmutableSet<AttributeModifier> immutableset1 = ImmutableSet.copyOf(Sets.difference(set1, set));
      immutableset.forEach(this::addModifier);
      immutableset1.forEach(this::removeModifier);
   }
}