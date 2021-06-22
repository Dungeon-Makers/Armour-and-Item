package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   public static List<EffectInstance> getMobEffects(ItemStack p_185189_0_) {
      return getAllEffects(p_185189_0_.getTag());
   }

   public static List<EffectInstance> getAllEffects(Potion p_185186_0_, Collection<EffectInstance> p_185186_1_) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(p_185186_0_.getEffects());
      list.addAll(p_185186_1_);
      return list;
   }

   public static List<EffectInstance> getAllEffects(@Nullable CompoundNBT p_185185_0_) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(getPotion(p_185185_0_).getEffects());
      getCustomEffects(p_185185_0_, list);
      return list;
   }

   public static List<EffectInstance> getCustomEffects(ItemStack p_185190_0_) {
      return getCustomEffects(p_185190_0_.getTag());
   }

   public static List<EffectInstance> getCustomEffects(@Nullable CompoundNBT p_185192_0_) {
      List<EffectInstance> list = Lists.newArrayList();
      getCustomEffects(p_185192_0_, list);
      return list;
   }

   public static void getCustomEffects(@Nullable CompoundNBT p_185193_0_, List<EffectInstance> p_185193_1_) {
      if (p_185193_0_ != null && p_185193_0_.contains("CustomPotionEffects", 9)) {
         ListNBT listnbt = p_185193_0_.getList("CustomPotionEffects", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            EffectInstance effectinstance = EffectInstance.load(compoundnbt);
            if (effectinstance != null) {
               p_185193_1_.add(effectinstance);
            }
         }
      }

   }

   public static int getColor(ItemStack p_190932_0_) {
      CompoundNBT compoundnbt = p_190932_0_.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         return compoundnbt.getInt("CustomPotionColor");
      } else {
         return getPotion(p_190932_0_) == Potions.EMPTY ? 16253176 : getColor(getMobEffects(p_190932_0_));
      }
   }

   public static int getColor(Potion p_185183_0_) {
      return p_185183_0_ == Potions.EMPTY ? 16253176 : getColor(p_185183_0_.getEffects());
   }

   public static int getColor(Collection<EffectInstance> p_185181_0_) {
      int i = 3694022;
      if (p_185181_0_.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(EffectInstance effectinstance : p_185181_0_) {
            if (effectinstance.isVisible()) {
               int k = effectinstance.getEffect().getColor();
               int l = effectinstance.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static Potion getPotion(ItemStack p_185191_0_) {
      return getPotion(p_185191_0_.getTag());
   }

   public static Potion getPotion(@Nullable CompoundNBT p_185187_0_) {
      return p_185187_0_ == null ? Potions.EMPTY : Potion.byName(p_185187_0_.getString("Potion"));
   }

   public static ItemStack setPotion(ItemStack p_185188_0_, Potion p_185188_1_) {
      ResourceLocation resourcelocation = Registry.POTION.getKey(p_185188_1_);
      if (p_185188_1_ == Potions.EMPTY) {
         p_185188_0_.removeTagKey("Potion");
      } else {
         p_185188_0_.getOrCreateTag().putString("Potion", resourcelocation.toString());
      }

      return p_185188_0_;
   }

   public static ItemStack setCustomEffects(ItemStack p_185184_0_, Collection<EffectInstance> p_185184_1_) {
      if (p_185184_1_.isEmpty()) {
         return p_185184_0_;
      } else {
         CompoundNBT compoundnbt = p_185184_0_.getOrCreateTag();
         ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);

         for(EffectInstance effectinstance : p_185184_1_) {
            listnbt.add(effectinstance.save(new CompoundNBT()));
         }

         compoundnbt.put("CustomPotionEffects", listnbt);
         return p_185184_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack p_185182_0_, List<ITextComponent> p_185182_1_, float p_185182_2_) {
      List<EffectInstance> list = getMobEffects(p_185182_0_);
      List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
      if (list.isEmpty()) {
         p_185182_1_.add((new TranslationTextComponent("effect.none")).func_211708_a(TextFormatting.GRAY));
      } else {
         for(EffectInstance effectinstance : list) {
            ITextComponent itextcomponent = new TranslationTextComponent(effectinstance.getDescriptionId());
            Effect effect = effectinstance.getEffect();
            Map<IAttribute, AttributeModifier> map = effect.getAttributeModifiers();
            if (!map.isEmpty()) {
               for(Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                  list1.add(new Tuple<>(entry.getKey().func_111108_a(), attributemodifier1));
               }
            }

            if (effectinstance.getAmplifier() > 0) {
               itextcomponent.func_150258_a(" ").func_150257_a(new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
            }

            if (effectinstance.getDuration() > 20) {
               itextcomponent.func_150258_a(" (").func_150258_a(EffectUtils.formatDuration(effectinstance, p_185182_2_)).func_150258_a(")");
            }

            p_185182_1_.add(itextcomponent.func_211708_a(effect.getCategory().getTooltipFormatting()));
         }
      }

      if (!list1.isEmpty()) {
         p_185182_1_.add(new StringTextComponent(""));
         p_185182_1_.add((new TranslationTextComponent("potion.whenDrank")).func_211708_a(TextFormatting.DARK_PURPLE));

         for(Tuple<String, AttributeModifier> tuple : list1) {
            AttributeModifier attributemodifier2 = tuple.getB();
            double d0 = attributemodifier2.getAmount();
            double d1;
            if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               d1 = attributemodifier2.getAmount();
            } else {
               d1 = attributemodifier2.getAmount() * 100.0D;
            }

            if (d0 > 0.0D) {
               p_185182_1_.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)tuple.getA()))).func_211708_a(TextFormatting.BLUE));
            } else if (d0 < 0.0D) {
               d1 = d1 * -1.0D;
               p_185182_1_.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)tuple.getA()))).func_211708_a(TextFormatting.RED));
            }
         }
      }

   }
}