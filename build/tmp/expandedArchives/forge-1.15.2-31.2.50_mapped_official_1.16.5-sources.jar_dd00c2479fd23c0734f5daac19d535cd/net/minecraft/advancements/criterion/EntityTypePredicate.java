package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate() {
      public boolean matches(EntityType<?> p_209368_1_) {
         return true;
      }

      public JsonElement serializeToJson() {
         return JsonNull.INSTANCE;
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ");

   public abstract boolean matches(EntityType<?> p_209368_1_);

   public abstract JsonElement serializeToJson();

   public static EntityTypePredicate fromJson(@Nullable JsonElement p_209370_0_) {
      if (p_209370_0_ != null && !p_209370_0_.isJsonNull()) {
         String s = JSONUtils.convertToString(p_209370_0_, "type");
         if (s.startsWith("#")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
            Tag<EntityType<?>> tag = EntityTypeTags.getAllTags().func_199915_b(resourcelocation1);
            return new EntityTypePredicate.TagPredicate(tag);
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType<?> entitytype = Registry.ENTITY_TYPE.func_218349_b(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown entity type '" + resourcelocation + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.keySet()));
            });
            return new EntityTypePredicate.TypePredicate(entitytype);
         }
      } else {
         return ANY;
      }
   }

   public static EntityTypePredicate of(EntityType<?> p_217999_0_) {
      return new EntityTypePredicate.TypePredicate(p_217999_0_);
   }

   public static EntityTypePredicate of(Tag<EntityType<?>> p_217998_0_) {
      return new EntityTypePredicate.TagPredicate(p_217998_0_);
   }

   static class TagPredicate extends EntityTypePredicate {
      private final Tag<EntityType<?>> tag;

      public TagPredicate(Tag<EntityType<?>> p_i50558_1_) {
         this.tag = p_i50558_1_;
      }

      public boolean matches(EntityType<?> p_209368_1_) {
         return this.tag.func_199685_a_(p_209368_1_);
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive("#" + this.tag.func_199886_b().toString());
      }
   }

   static class TypePredicate extends EntityTypePredicate {
      private final EntityType<?> type;

      public TypePredicate(EntityType<?> p_i50556_1_) {
         this.type = p_i50556_1_;
      }

      public boolean matches(EntityType<?> p_209368_1_) {
         return this.type == p_209368_1_;
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
      }
   }
}