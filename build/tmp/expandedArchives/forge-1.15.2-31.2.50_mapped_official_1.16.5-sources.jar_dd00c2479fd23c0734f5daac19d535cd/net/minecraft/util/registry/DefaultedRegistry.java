package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
   private final ResourceLocation defaultKey;
   private T defaultValue;

   public DefaultedRegistry(String p_i50797_1_) {
      this.defaultKey = new ResourceLocation(p_i50797_1_);
   }

   public <V extends T> V registerMapping(int p_218382_1_, ResourceLocation p_218382_2_, V p_218382_3_) {
      if (this.defaultKey.equals(p_218382_2_)) {
         this.defaultValue = (T)p_218382_3_;
      }

      return super.registerMapping(p_218382_1_, p_218382_2_, p_218382_3_);
   }

   public int getId(@Nullable T p_148757_1_) {
      int i = super.getId(p_148757_1_);
      return i == -1 ? super.getId(this.defaultValue) : i;
   }

   @Nonnull
   public ResourceLocation getKey(T p_177774_1_) {
      ResourceLocation resourcelocation = super.getKey(p_177774_1_);
      return resourcelocation == null ? this.defaultKey : resourcelocation;
   }

   @Nonnull
   public T get(@Nullable ResourceLocation p_82594_1_) {
      T t = (T)super.get(p_82594_1_);
      return (T)(t == null ? this.defaultValue : t);
   }

   @Nonnull
   public T byId(int p_148745_1_) {
      T t = (T)super.byId(p_148745_1_);
      return (T)(t == null ? this.defaultValue : t);
   }

   @Nonnull
   public T getRandom(Random p_186801_1_) {
      T t = (T)super.getRandom(p_186801_1_);
      return (T)(t == null ? this.defaultValue : t);
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}