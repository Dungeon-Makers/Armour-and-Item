package net.minecraft.util.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public interface ITextComponent extends Message, Iterable<ITextComponent> {
   ITextComponent func_150255_a(Style p_150255_1_);

   Style getStyle();

   default ITextComponent func_150258_a(String p_150258_1_) {
      return this.func_150257_a(new StringTextComponent(p_150258_1_));
   }

   ITextComponent func_150257_a(ITextComponent p_150257_1_);

   String getContents();

   default String getString() {
      StringBuilder stringbuilder = new StringBuilder();
      this.func_212640_c().forEach((p_212635_1_) -> {
         stringbuilder.append(p_212635_1_.getContents());
      });
      return stringbuilder.toString();
   }

   default String getString(int p_212636_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      Iterator<ITextComponent> iterator = this.func_212640_c().iterator();

      while(iterator.hasNext()) {
         int i = p_212636_1_ - stringbuilder.length();
         if (i <= 0) {
            break;
         }

         String s = iterator.next().getContents();
         stringbuilder.append(s.length() <= i ? s : s.substring(0, i));
      }

      return stringbuilder.toString();
   }

   default String func_150254_d() {
      StringBuilder stringbuilder = new StringBuilder();
      String s = "";
      Iterator<ITextComponent> iterator = this.func_212640_c().iterator();

      while(iterator.hasNext()) {
         ITextComponent itextcomponent = iterator.next();
         String s1 = itextcomponent.getContents();
         if (!s1.isEmpty()) {
            String s2 = itextcomponent.getStyle().func_150218_j();
            if (!s2.equals(s)) {
               if (!s.isEmpty()) {
                  stringbuilder.append((Object)TextFormatting.RESET);
               }

               stringbuilder.append(s2);
               s = s2;
            }

            stringbuilder.append(s1);
         }
      }

      if (!s.isEmpty()) {
         stringbuilder.append((Object)TextFormatting.RESET);
      }

      return stringbuilder.toString();
   }

   List<ITextComponent> getSiblings();

   Stream<ITextComponent> func_212640_c();

   default Stream<ITextComponent> func_212637_f() {
      return this.func_212640_c().map(ITextComponent::func_212639_b);
   }

   default Iterator<ITextComponent> iterator() {
      return this.func_212637_f().iterator();
   }

   ITextComponent func_150259_f();

   default ITextComponent func_212638_h() {
      ITextComponent itextcomponent = this.func_150259_f();
      itextcomponent.func_150255_a(this.getStyle().func_150232_l());

      for(ITextComponent itextcomponent1 : this.getSiblings()) {
         itextcomponent.func_150257_a(itextcomponent1.func_212638_h());
      }

      return itextcomponent;
   }

   default ITextComponent func_211710_a(Consumer<Style> p_211710_1_) {
      p_211710_1_.accept(this.getStyle());
      return this;
   }

   default ITextComponent func_211709_a(TextFormatting... p_211709_1_) {
      for(TextFormatting textformatting : p_211709_1_) {
         this.func_211708_a(textformatting);
      }

      return this;
   }

   default ITextComponent func_211708_a(TextFormatting p_211708_1_) {
      Style style = this.getStyle();
      if (p_211708_1_.isColor()) {
         style.func_150238_a(p_211708_1_);
      }

      if (p_211708_1_.isFormat()) {
         switch(p_211708_1_) {
         case OBFUSCATED:
            style.func_150237_e(true);
            break;
         case BOLD:
            style.func_150227_a(true);
            break;
         case STRIKETHROUGH:
            style.func_150225_c(true);
            break;
         case UNDERLINE:
            style.func_150228_d(true);
            break;
         case ITALIC:
            style.func_150217_b(true);
         }
      }

      return this;
   }

   static ITextComponent func_212639_b(ITextComponent p_212639_0_) {
      ITextComponent itextcomponent = p_212639_0_.func_150259_f();
      itextcomponent.func_150255_a(p_212639_0_.getStyle().func_150206_m());
      return itextcomponent;
   }

   public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder gsonbuilder = new GsonBuilder();
         gsonbuilder.disableHtmlEscaping();
         gsonbuilder.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
         gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         return gsonbuilder.create();
      });
      private static final Field JSON_READER_POS = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
         }
      });
      private static final Field JSON_READER_LINESTART = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
         }
      });

      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new StringTextComponent(p_deserialize_1_.getAsString());
         } else if (!p_deserialize_1_.isJsonObject()) {
            if (p_deserialize_1_.isJsonArray()) {
               JsonArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
               ITextComponent itextcomponent1 = null;

               for(JsonElement jsonelement : jsonarray1) {
                  ITextComponent itextcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
                  if (itextcomponent1 == null) {
                     itextcomponent1 = itextcomponent2;
                  } else {
                     itextcomponent1.func_150257_a(itextcomponent2);
                  }
               }

               return itextcomponent1;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
            }
         } else {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ITextComponent itextcomponent;
            if (jsonobject.has("text")) {
               itextcomponent = new StringTextComponent(JSONUtils.getAsString(jsonobject, "text"));
            } else if (jsonobject.has("translate")) {
               String s = JSONUtils.getAsString(jsonobject, "translate");
               if (jsonobject.has("with")) {
                  JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "with");
                  Object[] aobject = new Object[jsonarray.size()];

                  for(int i = 0; i < aobject.length; ++i) {
                     aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);
                     if (aobject[i] instanceof StringTextComponent) {
                        StringTextComponent stringtextcomponent = (StringTextComponent)aobject[i];
                        if (stringtextcomponent.getStyle().isEmpty() && stringtextcomponent.getSiblings().isEmpty()) {
                           aobject[i] = stringtextcomponent.getText();
                        }
                     }
                  }

                  itextcomponent = new TranslationTextComponent(s, aobject);
               } else {
                  itextcomponent = new TranslationTextComponent(s);
               }
            } else if (jsonobject.has("score")) {
               JsonObject jsonobject1 = JSONUtils.getAsJsonObject(jsonobject, "score");
               if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               itextcomponent = new ScoreTextComponent(JSONUtils.getAsString(jsonobject1, "name"), JSONUtils.getAsString(jsonobject1, "objective"));
               if (jsonobject1.has("value")) {
                  ((ScoreTextComponent)itextcomponent).func_179997_b(JSONUtils.getAsString(jsonobject1, "value"));
               }
            } else if (jsonobject.has("selector")) {
               itextcomponent = new SelectorTextComponent(JSONUtils.getAsString(jsonobject, "selector"));
            } else if (jsonobject.has("keybind")) {
               itextcomponent = new KeybindTextComponent(JSONUtils.getAsString(jsonobject, "keybind"));
            } else {
               if (!jsonobject.has("nbt")) {
                  throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
               }

               String s1 = JSONUtils.getAsString(jsonobject, "nbt");
               boolean flag = JSONUtils.getAsBoolean(jsonobject, "interpret", false);
               if (jsonobject.has("block")) {
                  itextcomponent = new NBTTextComponent.Block(s1, flag, JSONUtils.getAsString(jsonobject, "block"));
               } else if (jsonobject.has("entity")) {
                  itextcomponent = new NBTTextComponent.Entity(s1, flag, JSONUtils.getAsString(jsonobject, "entity"));
               } else {
                  if (!jsonobject.has("storage")) {
                     throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                  }

                  itextcomponent = new NBTTextComponent.Storage(s1, flag, new ResourceLocation(JSONUtils.getAsString(jsonobject, "storage")));
               }
            }

            if (jsonobject.has("extra")) {
               JsonArray jsonarray2 = JSONUtils.getAsJsonArray(jsonobject, "extra");
               if (jsonarray2.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int j = 0; j < jsonarray2.size(); ++j) {
                  itextcomponent.func_150257_a(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
               }
            }

            itextcomponent.func_150255_a(p_deserialize_3_.deserialize(p_deserialize_1_, Style.class));
            return itextcomponent;
         }
      }

      private void serializeStyle(Style p_150695_1_, JsonObject p_150695_2_, JsonSerializationContext p_150695_3_) {
         JsonElement jsonelement = p_150695_3_.serialize(p_150695_1_);
         if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = (JsonObject)jsonelement;

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               p_150695_2_.add(entry.getKey(), entry.getValue());
            }
         }

      }

      public JsonElement serialize(ITextComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (!p_serialize_1_.getStyle().isEmpty()) {
            this.serializeStyle(p_serialize_1_.getStyle(), jsonobject, p_serialize_3_);
         }

         if (!p_serialize_1_.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(ITextComponent itextcomponent : p_serialize_1_.getSiblings()) {
               jsonarray.add(this.serialize(itextcomponent, itextcomponent.getClass(), p_serialize_3_));
            }

            jsonobject.add("extra", jsonarray);
         }

         if (p_serialize_1_ instanceof StringTextComponent) {
            jsonobject.addProperty("text", ((StringTextComponent)p_serialize_1_).getText());
         } else if (p_serialize_1_ instanceof TranslationTextComponent) {
            TranslationTextComponent translationtextcomponent = (TranslationTextComponent)p_serialize_1_;
            jsonobject.addProperty("translate", translationtextcomponent.getKey());
            if (translationtextcomponent.getArgs() != null && translationtextcomponent.getArgs().length > 0) {
               JsonArray jsonarray1 = new JsonArray();

               for(Object object : translationtextcomponent.getArgs()) {
                  if (object instanceof ITextComponent) {
                     jsonarray1.add(this.serialize((ITextComponent)object, object.getClass(), p_serialize_3_));
                  } else {
                     jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonobject.add("with", jsonarray1);
            }
         } else if (p_serialize_1_ instanceof ScoreTextComponent) {
            ScoreTextComponent scoretextcomponent = (ScoreTextComponent)p_serialize_1_;
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", scoretextcomponent.getName());
            jsonobject1.addProperty("objective", scoretextcomponent.getObjective());
            jsonobject1.addProperty("value", scoretextcomponent.getContents());
            jsonobject.add("score", jsonobject1);
         } else if (p_serialize_1_ instanceof SelectorTextComponent) {
            SelectorTextComponent selectortextcomponent = (SelectorTextComponent)p_serialize_1_;
            jsonobject.addProperty("selector", selectortextcomponent.getPattern());
         } else if (p_serialize_1_ instanceof KeybindTextComponent) {
            KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_serialize_1_;
            jsonobject.addProperty("keybind", keybindtextcomponent.getName());
         } else {
            if (!(p_serialize_1_ instanceof NBTTextComponent)) {
               throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
            }

            NBTTextComponent nbttextcomponent = (NBTTextComponent)p_serialize_1_;
            jsonobject.addProperty("nbt", nbttextcomponent.getNbtPath());
            jsonobject.addProperty("interpret", nbttextcomponent.isInterpreting());
            if (p_serialize_1_ instanceof NBTTextComponent.Block) {
               NBTTextComponent.Block nbttextcomponent$block = (NBTTextComponent.Block)p_serialize_1_;
               jsonobject.addProperty("block", nbttextcomponent$block.getPos());
            } else if (p_serialize_1_ instanceof NBTTextComponent.Entity) {
               NBTTextComponent.Entity nbttextcomponent$entity = (NBTTextComponent.Entity)p_serialize_1_;
               jsonobject.addProperty("entity", nbttextcomponent$entity.getSelector());
            } else {
               if (!(p_serialize_1_ instanceof NBTTextComponent.Storage)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
               }

               NBTTextComponent.Storage nbttextcomponent$storage = (NBTTextComponent.Storage)p_serialize_1_;
               jsonobject.addProperty("storage", nbttextcomponent$storage.getId().toString());
            }
         }

         return jsonobject;
      }

      public static String toJson(ITextComponent p_150696_0_) {
         return GSON.toJson(p_150696_0_);
      }

      public static JsonElement toJsonTree(ITextComponent p_200528_0_) {
         return GSON.toJsonTree(p_200528_0_);
      }

      @Nullable
      public static ITextComponent func_150699_a(String p_150699_0_) {
         return JSONUtils.fromJson(GSON, p_150699_0_, ITextComponent.class, false);
      }

      @Nullable
      public static ITextComponent func_197672_a(JsonElement p_197672_0_) {
         return GSON.fromJson(p_197672_0_, ITextComponent.class);
      }

      @Nullable
      public static ITextComponent func_186877_b(String p_186877_0_) {
         return JSONUtils.fromJson(GSON, p_186877_0_, ITextComponent.class, true);
      }

      public static ITextComponent func_197671_a(com.mojang.brigadier.StringReader p_197671_0_) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(p_197671_0_.getRemaining()));
            jsonreader.setLenient(false);
            ITextComponent itextcomponent = GSON.getAdapter(ITextComponent.class).read(jsonreader);
            p_197671_0_.setCursor(p_197671_0_.getCursor() + getPos(jsonreader));
            return itextcomponent;
         } catch (StackOverflowError | IOException ioexception) {
            throw new JsonParseException(ioexception);
         }
      }

      private static int getPos(JsonReader p_197673_0_) {
         try {
            return JSON_READER_POS.getInt(p_197673_0_) - JSON_READER_LINESTART.getInt(p_197673_0_) + 1;
         } catch (IllegalAccessException illegalaccessexception) {
            throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
         }
      }
   }
}