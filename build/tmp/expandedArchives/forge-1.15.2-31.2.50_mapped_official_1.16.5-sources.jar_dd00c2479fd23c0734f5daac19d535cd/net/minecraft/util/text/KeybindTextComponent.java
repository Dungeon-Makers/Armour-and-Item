package net.minecraft.util.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindTextComponent extends TextComponent {
   public static Function<String, Supplier<String>> keyResolver = (p_193635_0_) -> {
      return () -> {
         return p_193635_0_;
      };
   };
   private final String name;
   private Supplier<String> nameResolver;

   public KeybindTextComponent(String p_i47521_1_) {
      this.name = p_i47521_1_;
   }

   public String getContents() {
      if (this.nameResolver == null) {
         this.nameResolver = keyResolver.apply(this.name);
      }

      return this.nameResolver.get();
   }

   public KeybindTextComponent func_150259_f() {
      return new KeybindTextComponent(this.name);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof KeybindTextComponent)) {
         return false;
      } else {
         KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_equals_1_;
         return this.name.equals(keybindtextcomponent.name) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "KeybindComponent{keybind='" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getName() {
      return this.name;
   }
}