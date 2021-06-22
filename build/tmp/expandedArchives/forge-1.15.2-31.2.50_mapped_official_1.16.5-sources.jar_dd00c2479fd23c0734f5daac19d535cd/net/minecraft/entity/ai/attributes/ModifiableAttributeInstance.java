package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final AbstractAttributeMap field_111138_a;
   private final IAttribute attribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<String, Set<AttributeModifier>> field_111134_d = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> permanentModifiers = Maps.newHashMap();
   private double field_111132_f;
   private boolean field_111133_g = true;
   private double field_111139_h;

   public ModifiableAttributeInstance(AbstractAttributeMap p_i1608_1_, IAttribute p_i1608_2_) {
      this.field_111138_a = p_i1608_1_;
      this.attribute = p_i1608_2_;
      this.field_111132_f = p_i1608_2_.getDefaultValue();

      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         this.modifiersByOperation.put(attributemodifier$operation, Sets.newHashSet());
      }

   }

   public IAttribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.field_111132_f;
   }

   public void setBaseValue(double p_111128_1_) {
      if (p_111128_1_ != this.getBaseValue()) {
         this.field_111132_f = p_111128_1_;
         this.func_111131_f();
      }
   }

   public Set<AttributeModifier> getModifiers(AttributeModifier.Operation p_225504_1_) {
      return this.modifiersByOperation.get(p_225504_1_);
   }

   public Set<AttributeModifier> getModifiers() {
      Set<AttributeModifier> set = Sets.newHashSet();

      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         set.addAll(this.getModifiers(attributemodifier$operation));
      }

      return set;
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_111127_1_) {
      return this.permanentModifiers.get(p_111127_1_);
   }

   public boolean hasModifier(AttributeModifier p_180374_1_) {
      return this.permanentModifiers.get(p_180374_1_.getId()) != null;
   }

   public void addModifier(AttributeModifier p_111121_1_) {
      if (this.getModifier(p_111121_1_.getId()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> set = this.field_111134_d.computeIfAbsent(p_111121_1_.getName(), (p_220369_0_) -> {
            return Sets.newHashSet();
         });
         this.modifiersByOperation.get(p_111121_1_.getOperation()).add(p_111121_1_);
         set.add(p_111121_1_);
         this.permanentModifiers.put(p_111121_1_.getId(), p_111121_1_);
         this.func_111131_f();
      }
   }

   protected void func_111131_f() {
      this.field_111133_g = true;
      this.field_111138_a.func_180794_a(this);
   }

   public void removeModifier(AttributeModifier p_111124_1_) {
      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         this.modifiersByOperation.get(attributemodifier$operation).remove(p_111124_1_);
      }

      Set<AttributeModifier> set = this.field_111134_d.get(p_111124_1_.getName());
      if (set != null) {
         set.remove(p_111124_1_);
         if (set.isEmpty()) {
            this.field_111134_d.remove(p_111124_1_.getName());
         }
      }

      this.permanentModifiers.remove(p_111124_1_.getId());
      this.func_111131_f();
   }

   public void removeModifier(UUID p_188479_1_) {
      AttributeModifier attributemodifier = this.getModifier(p_188479_1_);
      if (attributemodifier != null) {
         this.removeModifier(attributemodifier);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeModifiers() {
      Collection<AttributeModifier> collection = this.getModifiers();
      if (collection != null) {
         for(AttributeModifier attributemodifier : Lists.newArrayList(collection)) {
            this.removeModifier(attributemodifier);
         }

      }
   }

   public double getValue() {
      if (this.field_111133_g) {
         this.field_111139_h = this.calculateValue();
         this.field_111133_g = false;
      }

      return this.field_111139_h;
   }

   private double calculateValue() {
      double d0 = this.getBaseValue();

      for(AttributeModifier attributemodifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
         d0 += attributemodifier.getAmount();
      }

      double d1 = d0;

      for(AttributeModifier attributemodifier1 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
         d1 += d0 * attributemodifier1.getAmount();
      }

      for(AttributeModifier attributemodifier2 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         d1 *= 1.0D + attributemodifier2.getAmount();
      }

      return this.attribute.sanitizeValue(d1);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation p_220370_1_) {
      Set<AttributeModifier> set = Sets.newHashSet(this.getModifiers(p_220370_1_));

      for(IAttribute iattribute = this.attribute.func_180372_d(); iattribute != null; iattribute = iattribute.func_180372_d()) {
         IAttributeInstance iattributeinstance = this.field_111138_a.func_111151_a(iattribute);
         if (iattributeinstance != null) {
            set.addAll(iattributeinstance.getModifiers(p_220370_1_));
         }
      }

      return set;
   }
}