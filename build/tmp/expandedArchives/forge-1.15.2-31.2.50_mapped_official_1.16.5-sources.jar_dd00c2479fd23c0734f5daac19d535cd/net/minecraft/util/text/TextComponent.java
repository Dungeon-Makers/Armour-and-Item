package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class TextComponent implements ITextComponent {
   protected final List<ITextComponent> siblings = Lists.newArrayList();
   private Style style;

   public ITextComponent func_150257_a(ITextComponent p_150257_1_) {
      p_150257_1_.getStyle().func_150221_a(this.getStyle());
      this.siblings.add(p_150257_1_);
      return this;
   }

   public List<ITextComponent> getSiblings() {
      return this.siblings;
   }

   public ITextComponent func_150255_a(Style p_150255_1_) {
      this.style = p_150255_1_;

      for(ITextComponent itextcomponent : this.siblings) {
         itextcomponent.getStyle().func_150221_a(this.getStyle());
      }

      return this;
   }

   public Style getStyle() {
      if (this.style == null) {
         this.style = new Style();

         for(ITextComponent itextcomponent : this.siblings) {
            itextcomponent.getStyle().func_150221_a(this.style);
         }
      }

      return this.style;
   }

   public Stream<ITextComponent> func_212640_c() {
      return Streams.concat(Stream.of(this), this.siblings.stream().flatMap(ITextComponent::func_212640_c));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponent)) {
         return false;
      } else {
         TextComponent textcomponent = (TextComponent)p_equals_1_;
         return this.siblings.equals(textcomponent.siblings) && this.getStyle().equals(textcomponent.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getStyle(), this.siblings);
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}