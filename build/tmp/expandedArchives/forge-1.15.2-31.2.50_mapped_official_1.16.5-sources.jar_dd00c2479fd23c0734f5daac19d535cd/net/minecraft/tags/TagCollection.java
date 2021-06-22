package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger field_199918_a = LogManager.getLogger();
   private static final Gson field_199919_b = new Gson();
   private static final int field_199920_c = ".json".length();
   private Map<ResourceLocation, Tag<T>> field_199921_d = ImmutableMap.of();
   private final Function<ResourceLocation, Optional<T>> field_200040_e;
   private final String field_199923_f;
   private final boolean field_200041_g;
   private final String field_200157_i;

   public TagCollection(Function<ResourceLocation, Optional<T>> p_i50686_1_, String p_i50686_2_, boolean p_i50686_3_, String p_i50686_4_) {
      this.field_200040_e = p_i50686_1_;
      this.field_199923_f = p_i50686_2_;
      this.field_200041_g = p_i50686_3_;
      this.field_200157_i = p_i50686_4_;
   }

   @Nullable
   public Tag<T> getTag(ResourceLocation p_199910_1_) {
      return this.field_199921_d.get(p_199910_1_);
   }

   public Tag<T> func_199915_b(ResourceLocation p_199915_1_) {
      Tag<T> tag = this.field_199921_d.get(p_199915_1_);
      return tag == null ? new Tag<>(p_199915_1_) : tag;
   }

   public Collection<ResourceLocation> getAvailableTags() {
      return this.field_199921_d.keySet();
   }

   public Collection<ResourceLocation> getMatchingTags(T p_199913_1_) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> entry : this.field_199921_d.entrySet()) {
         if (entry.getValue().func_199685_a_(p_199913_1_)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> func_219781_a(IResourceManager p_219781_1_, Executor p_219781_2_) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_219781_1_.listResources(this.field_199923_f, (p_199916_0_) -> {
            return p_199916_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.field_199923_f.length() + 1, s.length() - field_199920_c));

            try {
               for(IResource iresource : p_219781_1_.getResources(resourcelocation)) {
                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     JsonObject jsonobject = JSONUtils.fromJson(field_199919_b, reader, JsonObject.class);
                     if (jsonobject == null) {
                        field_199918_a.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.field_200157_i, resourcelocation1, resourcelocation, iresource.getSourceName());
                     } else {
                        map.computeIfAbsent(resourcelocation1, (p_222990_1_) -> {
                           return Util.make(Tag.Builder.tag(), (p_222989_1_) -> {
                              p_222989_1_.func_200045_a(this.field_200041_g);
                           });
                        }).func_219783_a(this.field_200040_e, jsonobject);
                     }
                  } catch (RuntimeException | IOException ioexception) {
                     field_199918_a.error("Couldn't read {} tag list {} from {} in data pack {}", this.field_200157_i, resourcelocation1, resourcelocation, iresource.getSourceName(), ioexception);
                  } finally {
                     IOUtils.closeQuietly((Closeable)iresource);
                  }
               }
            } catch (IOException ioexception1) {
               field_199918_a.error("Couldn't read {} tag list {} from {}", this.field_200157_i, resourcelocation1, resourcelocation, ioexception1);
            }
         }

         return map;
      }, p_219781_2_);
   }

   public void func_219779_a(Map<ResourceLocation, Tag.Builder<T>> p_219779_1_) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();

      while(!p_219779_1_.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Tag.Builder<T>>> iterator = p_219779_1_.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> entry = iterator.next();
            Tag.Builder<T> builder = entry.getValue();
            if (builder.func_200160_a(map::get)) {
               flag = true;
               ResourceLocation resourcelocation = entry.getKey();
               map.put(resourcelocation, builder.func_200051_a(resourcelocation));
               iterator.remove();
            }
         }

         if (!flag) {
            p_219779_1_.forEach((p_223506_1_, p_223506_2_) -> {
               field_199918_a.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.field_200157_i, p_223506_1_);
            });
            break;
         }
      }

      p_219779_1_.forEach((p_223505_1_, p_223505_2_) -> {
         Tag tag = map.put(p_223505_1_, p_223505_2_.func_200051_a(p_223505_1_));
      });
      this.func_223507_b(map);
   }

   protected void func_223507_b(Map<ResourceLocation, Tag<T>> p_223507_1_) {
      this.field_199921_d = ImmutableMap.copyOf(p_223507_1_);
   }

   public Map<ResourceLocation, Tag<T>> func_200039_c() {
      return this.field_199921_d;
   }

   public Function<ResourceLocation, Optional<T>> getEntryLookup() {
       return this.field_200040_e;
   }
}
