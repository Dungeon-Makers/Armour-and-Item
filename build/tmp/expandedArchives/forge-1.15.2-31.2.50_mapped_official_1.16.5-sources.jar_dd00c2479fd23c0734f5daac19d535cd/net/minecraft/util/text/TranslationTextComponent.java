package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class TranslationTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final LanguageMap field_200526_d = new LanguageMap();
   private static final LanguageMap field_200527_e = LanguageMap.getInstance();
   private final String key;
   private final Object[] args;
   private final Object field_150274_f = new Object();
   private long field_150275_g = -1L;
   protected final List<ITextComponent> decomposedParts = Lists.newArrayList();
   public static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslationTextComponent(String p_i45160_1_, Object... p_i45160_2_) {
      this.key = p_i45160_1_;
      this.args = p_i45160_2_;

      for(int i = 0; i < p_i45160_2_.length; ++i) {
         Object object = p_i45160_2_[i];
         if (object instanceof ITextComponent) {
            ITextComponent itextcomponent = ((ITextComponent)object).func_212638_h();
            this.args[i] = itextcomponent;
            itextcomponent.getStyle().func_150221_a(this.getStyle());
         } else if (object == null) {
            this.args[i] = "null";
         }
      }

   }

   @VisibleForTesting
   synchronized void decompose() {
      synchronized(this.field_150274_f) {
         long i = field_200527_e.func_150510_c();
         if (i == this.field_150275_g) {
            return;
         }

         this.field_150275_g = i;
         this.decomposedParts.clear();
      }

      String s = field_200527_e.func_74805_b(this.key);

      try {
         this.func_150269_b(s);
      } catch (TranslationTextComponentFormatException var5) {
         this.decomposedParts.clear();
         this.decomposedParts.add(new StringTextComponent(s));
      }

   }

   protected void func_150269_b(String p_150269_1_) {
      Matcher matcher = FORMAT_PATTERN.matcher(p_150269_1_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               ITextComponent itextcomponent = new StringTextComponent(String.format(p_150269_1_.substring(j, k)));
               itextcomponent.getStyle().func_150221_a(this.getStyle());
               this.decomposedParts.add(itextcomponent);
            }

            String s2 = matcher.group(2);
            String s = p_150269_1_.substring(k, l);
            if ("%".equals(s2) && "%%".equals(s)) {
               ITextComponent itextcomponent2 = new StringTextComponent("%");
               itextcomponent2.getStyle().func_150221_a(this.getStyle());
               this.decomposedParts.add(itextcomponent2);
            } else {
               if (!"s".equals(s2)) {
                  throw new TranslationTextComponentFormatException(this, "Unsupported format: '" + s + "'");
               }

               String s1 = matcher.group(1);
               int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;
               if (i1 < this.args.length) {
                  this.decomposedParts.add(this.func_150272_a(i1));
               }
            }
         }

         if (j == 0) {
            // if we failed to match above, lets try the messageformat handler instead.
            j = net.minecraftforge.fml.TextComponentMessageFormatHandler.handle(this, this.decomposedParts, this.args, p_150269_1_);
         }
         if (j < p_150269_1_.length()) {
            ITextComponent itextcomponent1 = new StringTextComponent(String.format(p_150269_1_.substring(j)));
            itextcomponent1.getStyle().func_150221_a(this.getStyle());
            this.decomposedParts.add(itextcomponent1);
         }

      } catch (IllegalFormatException illegalformatexception) {
         throw new TranslationTextComponentFormatException(this, illegalformatexception);
      }
   }

   private ITextComponent func_150272_a(int p_150272_1_) {
      if (p_150272_1_ >= this.args.length) {
         throw new TranslationTextComponentFormatException(this, p_150272_1_);
      } else {
         Object object = this.args[p_150272_1_];
         ITextComponent itextcomponent;
         if (object instanceof ITextComponent) {
            itextcomponent = (ITextComponent)object;
         } else {
            itextcomponent = new StringTextComponent(object == null ? "null" : object.toString());
            itextcomponent.getStyle().func_150221_a(this.getStyle());
         }

         return itextcomponent;
      }
   }

   public ITextComponent func_150255_a(Style p_150255_1_) {
      super.func_150255_a(p_150255_1_);

      for(Object object : this.args) {
         if (object instanceof ITextComponent) {
            ((ITextComponent)object).getStyle().func_150221_a(this.getStyle());
         }
      }

      if (this.field_150275_g > -1L) {
         for(ITextComponent itextcomponent : this.decomposedParts) {
            itextcomponent.getStyle().func_150221_a(p_150255_1_);
         }
      }

      return this;
   }

   public Stream<ITextComponent> func_212640_c() {
      this.decompose();
      return Streams.<ITextComponent>concat(this.decomposedParts.stream(), this.siblings.stream()).flatMap(ITextComponent::func_212640_c);
   }

   public String getContents() {
      this.decompose();
      StringBuilder stringbuilder = new StringBuilder();

      for(ITextComponent itextcomponent : this.decomposedParts) {
         stringbuilder.append(itextcomponent.getContents());
      }

      return stringbuilder.toString();
   }

   public TranslationTextComponent func_150259_f() {
      Object[] aobject = new Object[this.args.length];

      for(int i = 0; i < this.args.length; ++i) {
         if (this.args[i] instanceof ITextComponent) {
            aobject[i] = ((ITextComponent)this.args[i]).func_212638_h();
         } else {
            aobject[i] = this.args[i];
         }
      }

      return new TranslationTextComponent(this.key, aobject);
   }

   public ITextComponent func_197668_a(@Nullable CommandSource p_197668_1_, @Nullable Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException {
      Object[] aobject = new Object[this.args.length];

      for(int i = 0; i < aobject.length; ++i) {
         Object object = this.args[i];
         if (object instanceof ITextComponent) {
            aobject[i] = TextComponentUtils.func_197680_a(p_197668_1_, (ITextComponent)object, p_197668_2_, p_197668_3_);
         } else {
            aobject[i] = object;
         }
      }

      return new TranslationTextComponent(this.key, aobject);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TranslationTextComponent)) {
         return false;
      } else {
         TranslationTextComponent translationtextcomponent = (TranslationTextComponent)p_equals_1_;
         return Arrays.equals(this.args, translationtextcomponent.args) && this.key.equals(translationtextcomponent.key) && super.equals(p_equals_1_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      i = 31 * i + Arrays.hashCode(this.args);
      return i;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
