package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger field_151476_f = LogManager.getLogger();
   public static final IAttribute field_111267_a = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, Float.MIN_VALUE, 1024.0D)).func_111117_a("Max Health").func_111112_a(true);  // Forge: set smallest max-health value to fix MC-119183. This gets rounded to float so we use the smallest positive float value.
   public static final IAttribute field_111265_b = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).func_111117_a("Follow Range");
   public static final IAttribute field_111266_c = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).func_111117_a("Knockback Resistance");
   public static final IAttribute field_111263_d = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", (double)0.7F, 0.0D, 1024.0D)).func_111117_a("Movement Speed").func_111112_a(true);
   public static final IAttribute field_193334_e = (new RangedAttribute((IAttribute)null, "generic.flyingSpeed", (double)0.4F, 0.0D, 1024.0D)).func_111117_a("Flying Speed").func_111112_a(true);
   public static final IAttribute field_111264_e = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final IAttribute field_221120_g = new RangedAttribute((IAttribute)null, "generic.attackKnockback", 0.0D, 0.0D, 5.0D);
   public static final IAttribute field_188790_f = (new RangedAttribute((IAttribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).func_111112_a(true);
   public static final IAttribute field_188791_g = (new RangedAttribute((IAttribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).func_111112_a(true);
   public static final IAttribute field_189429_h = (new RangedAttribute((IAttribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).func_111112_a(true);
   public static final IAttribute field_188792_h = (new RangedAttribute((IAttribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).func_111112_a(true);

   public static ListNBT func_111257_a(AbstractAttributeMap p_111257_0_) {
      ListNBT listnbt = new ListNBT();

      for(IAttributeInstance iattributeinstance : p_111257_0_.func_111146_a()) {
         listnbt.add(func_111261_a(iattributeinstance));
      }

      return listnbt;
   }

   private static CompoundNBT func_111261_a(IAttributeInstance p_111261_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      IAttribute iattribute = p_111261_0_.getAttribute();
      compoundnbt.putString("Name", iattribute.func_111108_a());
      compoundnbt.putDouble("Base", p_111261_0_.getBaseValue());
      Collection<AttributeModifier> collection = p_111261_0_.getModifiers();
      if (collection != null && !collection.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for(AttributeModifier attributemodifier : collection) {
            if (attributemodifier.func_111165_e()) {
               listnbt.add(func_111262_a(attributemodifier));
            }
         }

         compoundnbt.put("Modifiers", listnbt);
      }

      return compoundnbt;
   }

   public static CompoundNBT func_111262_a(AttributeModifier p_111262_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", p_111262_0_.getName());
      compoundnbt.putDouble("Amount", p_111262_0_.getAmount());
      compoundnbt.putInt("Operation", p_111262_0_.getOperation().toValue());
      compoundnbt.putUUID("UUID", p_111262_0_.getId());
      return compoundnbt;
   }

   public static void func_151475_a(AbstractAttributeMap p_151475_0_, ListNBT p_151475_1_) {
      for(int i = 0; i < p_151475_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_151475_1_.getCompound(i);
         IAttributeInstance iattributeinstance = p_151475_0_.func_111152_a(compoundnbt.getString("Name"));
         if (iattributeinstance == null) {
            field_151476_f.warn("Ignoring unknown attribute '{}'", (Object)compoundnbt.getString("Name"));
         } else {
            func_111258_a(iattributeinstance, compoundnbt);
         }
      }

   }

   private static void func_111258_a(IAttributeInstance p_111258_0_, CompoundNBT p_111258_1_) {
      p_111258_0_.setBaseValue(p_111258_1_.getDouble("Base"));
      if (p_111258_1_.contains("Modifiers", 9)) {
         ListNBT listnbt = p_111258_1_.getList("Modifiers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            AttributeModifier attributemodifier = func_111259_a(listnbt.getCompound(i));
            if (attributemodifier != null) {
               AttributeModifier attributemodifier1 = p_111258_0_.getModifier(attributemodifier.getId());
               if (attributemodifier1 != null) {
                  p_111258_0_.removeModifier(attributemodifier1);
               }

               p_111258_0_.addModifier(attributemodifier);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier func_111259_a(CompoundNBT p_111259_0_) {
      UUID uuid = p_111259_0_.getUUID("UUID");

      try {
         AttributeModifier.Operation attributemodifier$operation = AttributeModifier.Operation.fromValue(p_111259_0_.getInt("Operation"));
         return new AttributeModifier(uuid, p_111259_0_.getString("Name"), p_111259_0_.getDouble("Amount"), attributemodifier$operation);
      } catch (Exception exception) {
         field_151476_f.warn("Unable to create attribute: {}", (Object)exception.getMessage());
         return null;
      }
   }
}
