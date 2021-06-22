package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent func_211401_a(ITextComponent p_211401_0_, Style p_211401_1_) {
      if (p_211401_1_.isEmpty()) {
         return p_211401_0_;
      } else {
         return p_211401_0_.getStyle().isEmpty() ? p_211401_0_.func_150255_a(p_211401_1_.func_150232_l()) : (new StringTextComponent("")).func_150257_a(p_211401_0_).func_150255_a(p_211401_1_.func_150232_l());
      }
   }

   public static ITextComponent func_197680_a(@Nullable CommandSource p_197680_0_, ITextComponent p_197680_1_, @Nullable Entity p_197680_2_, int p_197680_3_) throws CommandSyntaxException {
      if (p_197680_3_ > 100) {
         return p_197680_1_;
      } else {
         ++p_197680_3_;
         ITextComponent itextcomponent = p_197680_1_ instanceof ITargetedTextComponent ? ((ITargetedTextComponent)p_197680_1_).func_197668_a(p_197680_0_, p_197680_2_, p_197680_3_) : p_197680_1_.func_150259_f();

         for(ITextComponent itextcomponent1 : p_197680_1_.getSiblings()) {
            itextcomponent.func_150257_a(func_197680_a(p_197680_0_, itextcomponent1, p_197680_2_, p_197680_3_));
         }

         return func_211401_a(itextcomponent, p_197680_1_.getStyle());
      }
   }

   public static ITextComponent getDisplayName(GameProfile p_197679_0_) {
      if (p_197679_0_.getName() != null) {
         return new StringTextComponent(p_197679_0_.getName());
      } else {
         return p_197679_0_.getId() != null ? new StringTextComponent(p_197679_0_.getId().toString()) : new StringTextComponent("(unknown)");
      }
   }

   public static ITextComponent formatList(Collection<String> p_197678_0_) {
      return formatAndSortList(p_197678_0_, (p_197681_0_) -> {
         return (new StringTextComponent(p_197681_0_)).func_211708_a(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent formatAndSortList(Collection<T> p_197675_0_, Function<T, ITextComponent> p_197675_1_) {
      if (p_197675_0_.isEmpty()) {
         return new StringTextComponent("");
      } else if (p_197675_0_.size() == 1) {
         return p_197675_1_.apply(p_197675_0_.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(p_197675_0_);
         list.sort(Comparable::compareTo);
         return func_197677_b(list, p_197675_1_);
      }
   }

   public static <T> ITextComponent func_197677_b(Collection<T> p_197677_0_, Function<T, ITextComponent> p_197677_1_) {
      if (p_197677_0_.isEmpty()) {
         return new StringTextComponent("");
      } else if (p_197677_0_.size() == 1) {
         return p_197677_1_.apply(p_197677_0_.iterator().next());
      } else {
         ITextComponent itextcomponent = new StringTextComponent("");
         boolean flag = true;

         for(T t : p_197677_0_) {
            if (!flag) {
               itextcomponent.func_150257_a((new StringTextComponent(", ")).func_211708_a(TextFormatting.GRAY));
            }

            itextcomponent.func_150257_a(p_197677_1_.apply(t));
            flag = false;
         }

         return itextcomponent;
      }
   }

   public static ITextComponent func_197676_a(ITextComponent p_197676_0_) {
      return (new StringTextComponent("[")).func_150257_a(p_197676_0_).func_150258_a("]");
   }

   public static ITextComponent fromMessage(Message p_202465_0_) {
      return (ITextComponent)(p_202465_0_ instanceof ITextComponent ? (ITextComponent)p_202465_0_ : new StringTextComponent(p_202465_0_.getString()));
   }
}