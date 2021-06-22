package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final IntIdentityHashBiMap<T> field_148759_a = new IntIdentityHashBiMap<>(256);
   protected final BiMap<ResourceLocation, T> storage = HashBiMap.create();
   protected Object[] randomCache;
   private int nextId;

   public <V extends T> V registerMapping(int p_218382_1_, ResourceLocation p_218382_2_, V p_218382_3_) {
      this.field_148759_a.addMapping((T)p_218382_3_, p_218382_1_);
      Validate.notNull(p_218382_2_);
      Validate.notNull(p_218382_3_);
      this.randomCache = null;
      if (this.storage.containsKey(p_218382_2_)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", (Object)p_218382_2_);
      }

      this.storage.put(p_218382_2_, (T)p_218382_3_);
      if (this.nextId <= p_218382_1_) {
         this.nextId = p_218382_1_ + 1;
      }

      return p_218382_3_;
   }

   public <V extends T> V register(ResourceLocation p_218381_1_, V p_218381_2_) {
      return this.registerMapping(this.nextId, p_218381_1_, p_218381_2_);
   }

   @Nullable
   public ResourceLocation getKey(T p_177774_1_) {
      return this.storage.inverse().get(p_177774_1_);
   }

   public int getId(@Nullable T p_148757_1_) {
      return this.field_148759_a.func_186815_a(p_148757_1_);
   }

   @Nullable
   public T byId(int p_148745_1_) {
      return this.field_148759_a.byId(p_148745_1_);
   }

   public Iterator<T> iterator() {
      return this.field_148759_a.iterator();
   }

   @Nullable
   public T get(@Nullable ResourceLocation p_82594_1_) {
      return this.storage.get(p_82594_1_);
   }

   public Optional<T> func_218349_b(@Nullable ResourceLocation p_218349_1_) {
      return Optional.ofNullable(this.storage.get(p_218349_1_));
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.storage.keySet());
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   @Nullable
   public T getRandom(Random p_186801_1_) {
      if (this.randomCache == null) {
         Collection<?> collection = this.storage.values();
         if (collection.isEmpty()) {
            return (T)null;
         }

         this.randomCache = collection.toArray(new Object[collection.size()]);
      }

      return (T)this.randomCache[p_186801_1_.nextInt(this.randomCache.length)];
   }

   @OnlyIn(Dist.CLIENT)
   public boolean containsKey(ResourceLocation p_212607_1_) {
      return this.storage.containsKey(p_212607_1_);
   }
}