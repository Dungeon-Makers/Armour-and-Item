package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class Tag<T> {
   private final ResourceLocation field_199888_a;
   private final Set<T> field_199889_b;
   private final Collection<Tag.ITagEntry<T>> field_200150_c;
   private boolean replace = false;

   public Tag(ResourceLocation p_i48236_1_) {
      this.field_199888_a = p_i48236_1_;
      this.field_199889_b = Collections.emptySet();
      this.field_200150_c = Collections.emptyList();
   }

   public Tag(ResourceLocation p_i48224_1_, Collection<Tag.ITagEntry<T>> p_i48224_2_, boolean p_i48224_3_) {
      this(p_i48224_1_, p_i48224_2_, p_i48224_3_, false);
   }
   private Tag(ResourceLocation p_i48224_1_, Collection<Tag.ITagEntry<T>> p_i48224_2_, boolean p_i48224_3_, boolean replace) {
      this.field_199888_a = p_i48224_1_;
      this.field_199889_b = (Set<T>)(p_i48224_3_ ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.field_200150_c = p_i48224_2_;

      for(Tag.ITagEntry<T> itagentry : p_i48224_2_) {
         itagentry.func_200162_a(this.field_199889_b);
      }

   }

   public JsonObject func_200571_a(Function<T, ResourceLocation> p_200571_1_) {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();

      for(Tag.ITagEntry<T> itagentry : this.field_200150_c) {
         if (!(itagentry instanceof net.minecraftforge.common.data.IOptionalTagEntry))
         itagentry.func_200576_a(jsonarray, p_200571_1_);
      }
      JsonArray optional = new JsonArray();
      for(Tag.ITagEntry<T> itagentry : this.field_200150_c) {
         if (itagentry instanceof net.minecraftforge.common.data.IOptionalTagEntry)
            itagentry.func_200576_a(optional, p_200571_1_);
      }

      jsonobject.addProperty("replace", replace);
      jsonobject.add("values", jsonarray);
      if (optional.size() > 0) jsonobject.add("optional", optional);
      return jsonobject;
   }

   public boolean func_199685_a_(T p_199685_1_) {
      return this.field_199889_b.contains(p_199685_1_);
   }

   public Collection<T> func_199885_a() {
      return this.field_199889_b;
   }

   public Collection<Tag.ITagEntry<T>> func_200570_b() {
      return this.field_200150_c;
   }

   public T getRandomElement(Random p_205596_1_) {
      List<T> list = Lists.newArrayList(this.func_199885_a());
      return list.get(p_205596_1_.nextInt(list.size()));
   }

   public ResourceLocation func_199886_b() {
      return this.field_199888_a;
   }

   public static class Builder<T> implements net.minecraftforge.common.extensions.IForgeTagBuilder<T> {
      private final Set<Tag.ITagEntry<T>> field_200052_a = Sets.newLinkedHashSet();
      private boolean field_200053_b;
      private boolean replace = false;

      public static <T> Tag.Builder<T> tag() {
         return new Tag.Builder<>();
      }

      public Tag.Builder<T> func_200575_a(Tag.ITagEntry<T> p_200575_1_) {
         this.field_200052_a.add(p_200575_1_);
         return this;
      }

      public Tag.Builder<T> func_200048_a(T p_200048_1_) {
         this.field_200052_a.add(new Tag.ListEntry<>(Collections.singleton(p_200048_1_)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> func_200573_a(T... p_200573_1_) {
         this.field_200052_a.add(new Tag.ListEntry<>(Lists.newArrayList(p_200573_1_)));
         return this;
      }

      public Tag.Builder<T> func_200574_a(Tag<T> p_200574_1_) {
         this.field_200052_a.add(new Tag.TagEntry<>(p_200574_1_));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> add(Tag<T>... tags) {
         for (Tag<T> tag : tags)
            func_200574_a(tag);
         return this;
      }

      public Tag.Builder<T> replace(boolean value) {
         this.replace = value;
         return this;
      }

      public Tag.Builder<T> replace() {
         return replace(true);
      }

      public Tag.Builder<T> func_200045_a(boolean p_200045_1_) {
         this.field_200053_b = p_200045_1_;
         return this;
      }

      public boolean func_200160_a(Function<ResourceLocation, Tag<T>> p_200160_1_) {
         for(Tag.ITagEntry<T> itagentry : this.field_200052_a) {
            if (!itagentry.func_200161_a(p_200160_1_)) {
               return false;
            }
         }

         return true;
      }

      public Tag<T> func_200051_a(ResourceLocation p_200051_1_) {
         return new Tag<>(p_200051_1_, this.field_200052_a, this.field_200053_b, this.replace);
      }

      public Tag.Builder<T> func_219783_a(Function<ResourceLocation, Optional<T>> p_219783_1_, JsonObject p_219783_2_) {
         JsonArray jsonarray = JSONUtils.getAsJsonArray(p_219783_2_, "values");
         List<Tag.ITagEntry<T>> list = Lists.newArrayList();

         for(JsonElement jsonelement : jsonarray) {
            String s = JSONUtils.convertToString(jsonelement, "value");
            if (s.startsWith("#")) {
               list.add(new Tag.TagEntry<>(new ResourceLocation(s.substring(1))));
            } else {
               ResourceLocation resourcelocation = new ResourceLocation(s);
               list.add(new Tag.ListEntry<>(Collections.singleton(p_219783_1_.apply(resourcelocation).orElseThrow(() -> {
                  return new JsonParseException("Unknown value '" + resourcelocation + "'");
               }))));
            }
         }

         if (JSONUtils.getAsBoolean(p_219783_2_, "replace", false)) {
            this.field_200052_a.clear();
         }

         this.field_200052_a.addAll(list);
         net.minecraftforge.common.ForgeHooks.deserializeTagAdditions(this, p_219783_1_, p_219783_2_);
         return this;
      }
      public Tag.Builder<T> remove(Tag.ITagEntry<T> e) { this.field_200052_a.remove(e); return this; }
   }

   public interface ITagEntry<T> {
      default boolean func_200161_a(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         return true;
      }

      void func_200162_a(Collection<T> p_200162_1_);

      void func_200576_a(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_);
   }

   public static class ListEntry<T> implements Tag.ITagEntry<T> {
      private final Collection<T> field_200165_a;

      public ListEntry(Collection<T> p_i48227_1_) {
         this.field_200165_a = p_i48227_1_;
      }

      public void func_200162_a(Collection<T> p_200162_1_) {
         p_200162_1_.addAll(this.field_200165_a);
      }

      public void func_200576_a(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         for(T t : this.field_200165_a) {
            ResourceLocation resourcelocation = p_200576_2_.apply(t);
            if (resourcelocation == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            p_200576_1_.add(resourcelocation.toString());
         }

      }

      public Collection<T> func_200578_a() {
         return this.field_200165_a;
      }
      @Override public int hashCode() { return this.field_200165_a.hashCode(); }
      @Override public boolean equals(Object o) { return o == this || (o instanceof Tag.ListEntry && this.field_200165_a.equals(((Tag.ListEntry) o).field_200165_a)); }
   }

   public static class TagEntry<T> implements Tag.ITagEntry<T> {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag<T> field_200164_b;

      public TagEntry(ResourceLocation p_i48228_1_) {
         this.id = p_i48228_1_;
      }

      public TagEntry(Tag<T> p_i48229_1_) {
         this.id = p_i48229_1_.func_199886_b();
         this.field_200164_b = p_i48229_1_;
      }

      public boolean func_200161_a(Function<ResourceLocation, Tag<T>> p_200161_1_) {
         if (this.field_200164_b == null) {
            this.field_200164_b = p_200161_1_.apply(this.id);
         }

         return this.field_200164_b != null;
      }

      public void func_200162_a(Collection<T> p_200162_1_) {
         if (this.field_200164_b == null) {
            throw Util.pauseInIde((new IllegalStateException("Cannot build unresolved tag entry")));
         } else {
            p_200162_1_.addAll(this.field_200164_b.func_199885_a());
         }
      }

      public ResourceLocation func_200577_a() {
         if (this.field_200164_b != null) {
            return this.field_200164_b.func_199886_b();
         } else if (this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void func_200576_a(JsonArray p_200576_1_, Function<T, ResourceLocation> p_200576_2_) {
         p_200576_1_.add("#" + this.func_200577_a());
      }
      @Override public int hashCode() { return java.util.Objects.hashCode(this.id); }
      @Override public boolean equals(Object o) { return o == this || (o instanceof Tag.TagEntry && java.util.Objects.equals(this.id, ((Tag.TagEntry) o).id)); }
   }
}
