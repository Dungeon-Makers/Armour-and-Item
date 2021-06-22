package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WeightedList<U> {
   protected final List<WeightedList<U>.Entry<? extends U>> entries = Lists.newArrayList();
   private final Random random;

   public WeightedList(Random p_i50335_1_) {
      this.random = p_i50335_1_;
   }

   public WeightedList() {
      this(new Random());
   }

   public <T> WeightedList(Dynamic<T> p_i225709_1_, Function<Dynamic<T>, U> p_i225709_2_) {
      this();
      p_i225709_1_.asStream().forEach((p_226316_2_) -> {
         p_226316_2_.get("data").map((p_226317_3_) -> {
            U u = p_i225709_2_.apply(p_226317_3_);
            int i = p_226316_2_.get("weight").asInt(1);
            return (U)this.add(u, i);
         });
      });
   }

   public <T> T func_226310_a_(DynamicOps<T> p_226310_1_, Function<U, Dynamic<T>> p_226310_2_) {
      return p_226310_1_.createList(this.func_226319_c_().map((p_226311_2_) -> {
         return p_226310_1_.createMap(ImmutableMap.<T, T>builder().put(p_226310_1_.createString("data"), p_226310_2_.apply((U)p_226311_2_.getData()).getValue()).put(p_226310_1_.createString("weight"), p_226310_1_.createInt(p_226311_2_.getWeight())).build());
      }));
   }

   public WeightedList<U> add(U p_226313_1_, int p_226313_2_) {
      this.entries.add(new WeightedList.Entry(p_226313_1_, p_226313_2_));
      return this;
   }

   public WeightedList<U> shuffle() {
      return this.shuffle(this.random);
   }

   public WeightedList<U> shuffle(Random p_226314_1_) {
      this.entries.forEach((p_226315_1_) -> {
         p_226315_1_.setRandom(p_226314_1_.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble((p_226312_0_) -> {
         return p_226312_0_.getRandWeight();
      }));
      return this;
   }

   public Stream<? extends U> stream() {
      return this.entries.stream().map(WeightedList.Entry::getData);
   }

   public Stream<WeightedList<U>.Entry<? extends U>> func_226319_c_() {
      return this.entries.stream();
   }

   public U getOne(Random p_226318_1_) {
      return (U)this.shuffle(p_226318_1_).stream().findFirst().orElseThrow(RuntimeException::new);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   public class Entry<T> {
      private final T data;
      private final int weight;
      private double randWeight;

      private Entry(T p_i50545_2_, int p_i50545_3_) {
         this.weight = p_i50545_3_;
         this.data = p_i50545_2_;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      private void setRandom(float p_220648_1_) {
         this.randWeight = -Math.pow((double)p_220648_1_, (double)(1.0F / (float)this.weight));
      }

      public T getData() {
         return this.data;
      }

      public int getWeight() {
         return this.weight;
      }

      public String toString() {
         return "" + this.weight + ":" + this.data;
      }
   }
}