package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<Tag<T>, Tag.Builder<T>> builders = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator p_i49827_1_, Registry<T> p_i49827_2_) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
   }

   protected abstract void addTags();

   public void run(DirectoryCache p_200398_1_) {
      this.builders.clear();
      this.addTags();
      TagCollection<T> tagcollection = new TagCollection<>((p_200428_0_) -> {
         return Optional.empty();
      }, "", false, "generated");
      Map<ResourceLocation, Tag.Builder<T>> map = this.builders.entrySet().stream().collect(Collectors.toMap((p_223475_0_) -> {
         return p_223475_0_.getKey().func_199886_b();
      }, Entry::getValue));
      tagcollection.func_219779_a(map);
      tagcollection.func_200039_c().forEach((p_223474_2_, p_223474_3_) -> {
         JsonObject jsonobject = p_223474_3_.func_200571_a(this.registry::getKey);
         Path path = this.getPath(p_223474_2_);
         if (path == null) return; //Forge: Allow running this data provider without writing it. Recipe provider needs valid tags.

         try {
            String s = GSON.toJson((JsonElement)jsonobject);
            String s1 = SHA1.hashUnencodedChars(s).toString();
            if (!Objects.equals(p_200398_1_.getHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                  bufferedwriter.write(s);
               }
            }

            p_200398_1_.putNew(path, s1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save tags to {}", path, ioexception);
         }

      });
      this.func_200429_a(tagcollection);
   }

   protected abstract void func_200429_a(TagCollection<T> p_200429_1_);

   protected abstract Path getPath(ResourceLocation p_200431_1_);

   protected Tag.Builder<T> func_200426_a(Tag<T> p_200426_1_) {
      return this.builders.computeIfAbsent(p_200426_1_, (p_200427_0_) -> {
         return Tag.Builder.tag();
      });
   }
}
