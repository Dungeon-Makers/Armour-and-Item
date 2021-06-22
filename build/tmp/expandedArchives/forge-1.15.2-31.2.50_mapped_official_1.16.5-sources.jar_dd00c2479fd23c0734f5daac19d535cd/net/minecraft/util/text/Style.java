package net.minecraft.util.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Style {
   private Style field_150249_a;
   private TextFormatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private String insertion;
   private static final Style field_150250_j = new Style() {
      @Nullable
      public TextFormatting func_150215_a() {
         return null;
      }

      public boolean isBold() {
         return false;
      }

      public boolean isItalic() {
         return false;
      }

      public boolean isStrikethrough() {
         return false;
      }

      public boolean isUnderlined() {
         return false;
      }

      public boolean isObfuscated() {
         return false;
      }

      @Nullable
      public ClickEvent getClickEvent() {
         return null;
      }

      @Nullable
      public HoverEvent getHoverEvent() {
         return null;
      }

      @Nullable
      public String getInsertion() {
         return null;
      }

      public Style func_150238_a(TextFormatting p_150238_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150227_a(Boolean p_150227_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150217_b(Boolean p_150217_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150225_c(Boolean p_150225_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150228_d(Boolean p_150228_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150237_e(Boolean p_150237_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150241_a(ClickEvent p_150241_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150209_a(HoverEvent p_150209_1_) {
         throw new UnsupportedOperationException();
      }

      public Style func_150221_a(Style p_150221_1_) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return "Style.ROOT";
      }

      public Style func_150232_l() {
         return this;
      }

      public Style func_150206_m() {
         return this;
      }

      public String func_150218_j() {
         return "";
      }
   };

   @Nullable
   public TextFormatting func_150215_a() {
      return this.color == null ? this.func_150224_n().func_150215_a() : this.color;
   }

   public boolean isBold() {
      return this.bold == null ? this.func_150224_n().isBold() : this.bold;
   }

   public boolean isItalic() {
      return this.italic == null ? this.func_150224_n().isItalic() : this.italic;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == null ? this.func_150224_n().isStrikethrough() : this.strikethrough;
   }

   public boolean isUnderlined() {
      return this.underlined == null ? this.func_150224_n().isUnderlined() : this.underlined;
   }

   public boolean isObfuscated() {
      return this.obfuscated == null ? this.func_150224_n().isObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent == null ? this.func_150224_n().getClickEvent() : this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent == null ? this.func_150224_n().getHoverEvent() : this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion == null ? this.func_150224_n().getInsertion() : this.insertion;
   }

   public Style func_150238_a(TextFormatting p_150238_1_) {
      this.color = p_150238_1_;
      return this;
   }

   public Style func_150227_a(Boolean p_150227_1_) {
      this.bold = p_150227_1_;
      return this;
   }

   public Style func_150217_b(Boolean p_150217_1_) {
      this.italic = p_150217_1_;
      return this;
   }

   public Style func_150225_c(Boolean p_150225_1_) {
      this.strikethrough = p_150225_1_;
      return this;
   }

   public Style func_150228_d(Boolean p_150228_1_) {
      this.underlined = p_150228_1_;
      return this;
   }

   public Style func_150237_e(Boolean p_150237_1_) {
      this.obfuscated = p_150237_1_;
      return this;
   }

   public Style func_150241_a(ClickEvent p_150241_1_) {
      this.clickEvent = p_150241_1_;
      return this;
   }

   public Style func_150209_a(HoverEvent p_150209_1_) {
      this.hoverEvent = p_150209_1_;
      return this;
   }

   public Style func_179989_a(String p_179989_1_) {
      this.insertion = p_179989_1_;
      return this;
   }

   public Style func_150221_a(Style p_150221_1_) {
      this.field_150249_a = p_150221_1_;
      return this;
   }

   public String func_150218_j() {
      if (this.isEmpty()) {
         return this.field_150249_a != null ? this.field_150249_a.func_150218_j() : "";
      } else {
         StringBuilder stringbuilder = new StringBuilder();
         if (this.func_150215_a() != null) {
            stringbuilder.append((Object)this.func_150215_a());
         }

         if (this.isBold()) {
            stringbuilder.append((Object)TextFormatting.BOLD);
         }

         if (this.isItalic()) {
            stringbuilder.append((Object)TextFormatting.ITALIC);
         }

         if (this.isUnderlined()) {
            stringbuilder.append((Object)TextFormatting.UNDERLINE);
         }

         if (this.isObfuscated()) {
            stringbuilder.append((Object)TextFormatting.OBFUSCATED);
         }

         if (this.isStrikethrough()) {
            stringbuilder.append((Object)TextFormatting.STRIKETHROUGH);
         }

         return stringbuilder.toString();
      }
   }

   private Style func_150224_n() {
      return this.field_150249_a == null ? field_150250_j : this.field_150249_a;
   }

   public String toString() {
      return "Style{hasParent=" + (this.field_150249_a != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Style)) {
         return false;
      } else {
         Style style = (Style)p_equals_1_;
         if (this.isBold() == style.isBold() && this.func_150215_a() == style.func_150215_a() && this.isItalic() == style.isItalic() && this.isObfuscated() == style.isObfuscated() && this.isStrikethrough() == style.isStrikethrough() && this.isUnderlined() == style.isUnderlined()) {
            if (this.getClickEvent() != null) {
               if (!this.getClickEvent().equals(style.getClickEvent())) {
                  return false;
               }
            } else if (style.getClickEvent() != null) {
               return false;
            }

            if (this.getHoverEvent() != null) {
               if (!this.getHoverEvent().equals(style.getHoverEvent())) {
                  return false;
               }
            } else if (style.getHoverEvent() != null) {
               return false;
            }

            if (this.getInsertion() != null) {
               if (this.getInsertion().equals(style.getInsertion())) {
                  return true;
               }
            } else if (style.getInsertion() == null) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
   }

   public Style func_150232_l() {
      Style style = new Style();
      style.bold = this.bold;
      style.italic = this.italic;
      style.strikethrough = this.strikethrough;
      style.underlined = this.underlined;
      style.obfuscated = this.obfuscated;
      style.color = this.color;
      style.clickEvent = this.clickEvent;
      style.hoverEvent = this.hoverEvent;
      style.field_150249_a = this.field_150249_a;
      style.insertion = this.insertion;
      return style;
   }

   public Style func_150206_m() {
      Style style = new Style();
      style.func_150227_a(this.isBold());
      style.func_150217_b(this.isItalic());
      style.func_150225_c(this.isStrikethrough());
      style.func_150228_d(this.isUnderlined());
      style.func_150237_e(this.isObfuscated());
      style.func_150238_a(this.func_150215_a());
      style.func_150241_a(this.getClickEvent());
      style.func_150209_a(this.getHoverEvent());
      style.func_179989_a(this.getInsertion());
      return style;
   }

   public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
      @Nullable
      public Style deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            Style style = new Style();
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            if (jsonobject == null) {
               return null;
            } else {
               if (jsonobject.has("bold")) {
                  style.bold = jsonobject.get("bold").getAsBoolean();
               }

               if (jsonobject.has("italic")) {
                  style.italic = jsonobject.get("italic").getAsBoolean();
               }

               if (jsonobject.has("underlined")) {
                  style.underlined = jsonobject.get("underlined").getAsBoolean();
               }

               if (jsonobject.has("strikethrough")) {
                  style.strikethrough = jsonobject.get("strikethrough").getAsBoolean();
               }

               if (jsonobject.has("obfuscated")) {
                  style.obfuscated = jsonobject.get("obfuscated").getAsBoolean();
               }

               if (jsonobject.has("color")) {
                  style.color = p_deserialize_3_.deserialize(jsonobject.get("color"), TextFormatting.class);
               }

               if (jsonobject.has("insertion")) {
                  style.insertion = jsonobject.get("insertion").getAsString();
               }

               if (jsonobject.has("clickEvent")) {
                  JsonObject jsonobject1 = JSONUtils.getAsJsonObject(jsonobject, "clickEvent");
                  String s = JSONUtils.getAsString(jsonobject1, "action", (String)null);
                  ClickEvent.Action clickevent$action = s == null ? null : ClickEvent.Action.getByName(s);
                  String s1 = JSONUtils.getAsString(jsonobject1, "value", (String)null);
                  if (clickevent$action != null && s1 != null && clickevent$action.isAllowedFromServer()) {
                     style.clickEvent = new ClickEvent(clickevent$action, s1);
                  }
               }

               if (jsonobject.has("hoverEvent")) {
                  JsonObject jsonobject2 = JSONUtils.getAsJsonObject(jsonobject, "hoverEvent");
                  String s2 = JSONUtils.getAsString(jsonobject2, "action", (String)null);
                  HoverEvent.Action hoverevent$action = s2 == null ? null : HoverEvent.Action.getByName(s2);
                  ITextComponent itextcomponent = p_deserialize_3_.deserialize(jsonobject2.get("value"), ITextComponent.class);
                  if (hoverevent$action != null && itextcomponent != null && hoverevent$action.isAllowedFromServer()) {
                     style.hoverEvent = new HoverEvent(hoverevent$action, itextcomponent);
                  }
               }

               return style;
            }
         } else {
            return null;
         }
      }

      @Nullable
      public JsonElement serialize(Style p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         if (p_serialize_1_.isEmpty()) {
            return null;
         } else {
            JsonObject jsonobject = new JsonObject();
            if (p_serialize_1_.bold != null) {
               jsonobject.addProperty("bold", p_serialize_1_.bold);
            }

            if (p_serialize_1_.italic != null) {
               jsonobject.addProperty("italic", p_serialize_1_.italic);
            }

            if (p_serialize_1_.underlined != null) {
               jsonobject.addProperty("underlined", p_serialize_1_.underlined);
            }

            if (p_serialize_1_.strikethrough != null) {
               jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
            }

            if (p_serialize_1_.obfuscated != null) {
               jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
            }

            if (p_serialize_1_.color != null) {
               jsonobject.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
            }

            if (p_serialize_1_.insertion != null) {
               jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
            }

            if (p_serialize_1_.clickEvent != null) {
               JsonObject jsonobject1 = new JsonObject();
               jsonobject1.addProperty("action", p_serialize_1_.clickEvent.getAction().getName());
               jsonobject1.addProperty("value", p_serialize_1_.clickEvent.getValue());
               jsonobject.add("clickEvent", jsonobject1);
            }

            if (p_serialize_1_.hoverEvent != null) {
               JsonObject jsonobject2 = new JsonObject();
               jsonobject2.addProperty("action", p_serialize_1_.hoverEvent.getAction().getName());
               jsonobject2.add("value", p_serialize_3_.serialize(p_serialize_1_.hoverEvent.func_150702_b()));
               jsonobject.add("hoverEvent", jsonobject2);
            }

            return jsonobject;
         }
      }
   }
}