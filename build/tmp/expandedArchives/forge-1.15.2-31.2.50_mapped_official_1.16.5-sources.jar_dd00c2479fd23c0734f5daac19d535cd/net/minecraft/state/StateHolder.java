package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class StateHolder<O, S> implements IStateHolder<S> {
   private static final Function<Entry<IProperty<?>, Comparable<?>>, String> field_177233_b = new Function<Entry<IProperty<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<IProperty<?>, Comparable<?>> p_apply_1_) {
         if (p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty<?> iproperty = p_apply_1_.getKey();
            return iproperty.getName() + "=" + this.func_185886_a(iproperty, p_apply_1_.getValue());
         }
      }

      private <T extends Comparable<T>> String func_185886_a(IProperty<T> p_185886_1_, Comparable<?> p_185886_2_) {
         return p_185886_1_.getName((T)p_185886_2_);
      }
   };
   protected final O field_206876_a;
   private final ImmutableMap<IProperty<?>, Comparable<?>> field_206877_c;
   private Table<IProperty<?>, Comparable<?>, S> field_206879_e;

   protected StateHolder(O p_i49008_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49008_2_) {
      this.field_206876_a = p_i49008_1_;
      this.field_206877_c = p_i49008_2_;
   }

   public <T extends Comparable<T>> S func_177231_a(IProperty<T> p_177231_1_) {
      return (S)this.setValue(p_177231_1_, (T)(func_177232_a(p_177231_1_.getPossibleValues(), this.getValue(p_177231_1_))));
   }

   protected static <T> T func_177232_a(Collection<T> p_177232_0_, T p_177232_1_) {
      Iterator<T> iterator = p_177232_0_.iterator();

      while(iterator.hasNext()) {
         if (iterator.next().equals(p_177232_1_)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return p_177232_0_.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(this.field_206876_a);
      if (!this.getValues().isEmpty()) {
         stringbuilder.append('[');
         stringbuilder.append(this.getValues().entrySet().stream().map(field_177233_b).collect(Collectors.joining(",")));
         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   public Collection<IProperty<?>> func_206869_a() {
      return Collections.unmodifiableCollection(this.field_206877_c.keySet());
   }

   public <T extends Comparable<T>> boolean func_196959_b(IProperty<T> p_196959_1_) {
      return this.field_206877_c.containsKey(p_196959_1_);
   }

   public <T extends Comparable<T>> T getValue(IProperty<T> p_177229_1_) {
      Comparable<?> comparable = this.field_206877_c.get(p_177229_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + p_177229_1_ + " as it does not exist in " + this.field_206876_a);
      } else {
         return (T)(p_177229_1_.getValueClass().cast(comparable));
      }
   }

   public <T extends Comparable<T>, V extends T> S setValue(IProperty<T> p_206870_1_, V p_206870_2_) {
      Comparable<?> comparable = this.field_206877_c.get(p_206870_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " as it does not exist in " + this.field_206876_a);
      } else if (comparable == p_206870_2_) {
         return (S)this;
      } else {
         S s = this.field_206879_e.get(p_206870_1_, p_206870_2_);
         if (s == null) {
            throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " to " + p_206870_2_ + " on " + this.field_206876_a + ", it is not an allowed value");
         } else {
            return s;
         }
      }
   }

   public void func_206874_a(Map<Map<IProperty<?>, Comparable<?>>, S> p_206874_1_) {
      if (this.field_206879_e != null) {
         throw new IllegalStateException();
      } else {
         Table<IProperty<?>, Comparable<?>, S> table = HashBasedTable.create();

         for(Entry<IProperty<?>, Comparable<?>> entry : this.field_206877_c.entrySet()) {
            IProperty<?> iproperty = entry.getKey();

            for(Comparable<?> comparable : iproperty.getPossibleValues()) {
               if (comparable != entry.getValue()) {
                  table.put(iproperty, comparable, p_206874_1_.get(this.func_206875_b(iproperty, comparable)));
               }
            }
         }

         this.field_206879_e = (Table<IProperty<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
      }
   }

   private Map<IProperty<?>, Comparable<?>> func_206875_b(IProperty<?> p_206875_1_, Comparable<?> p_206875_2_) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap(this.field_206877_c);
      map.put(p_206875_1_, p_206875_2_);
      return map;
   }

   public ImmutableMap<IProperty<?>, Comparable<?>> getValues() {
      return this.field_206877_c;
   }
}