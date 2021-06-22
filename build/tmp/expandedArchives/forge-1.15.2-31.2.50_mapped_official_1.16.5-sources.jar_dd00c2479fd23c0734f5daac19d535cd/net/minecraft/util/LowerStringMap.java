package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap<V> implements Map<String, V> {
   private final Map<String, V> map = Maps.newLinkedHashMap();

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object p_containsKey_1_) {
      return this.map.containsKey(p_containsKey_1_.toString().toLowerCase(Locale.ROOT));
   }

   public boolean containsValue(Object p_containsValue_1_) {
      return this.map.containsValue(p_containsValue_1_);
   }

   public V get(Object p_get_1_) {
      return this.map.get(p_get_1_.toString().toLowerCase(Locale.ROOT));
   }

   public V put(String p_put_1_, V p_put_2_) {
      return this.map.put(p_put_1_.toLowerCase(Locale.ROOT), p_put_2_);
   }

   public V remove(Object p_remove_1_) {
      return this.map.remove(p_remove_1_.toString().toLowerCase(Locale.ROOT));
   }

   public void putAll(Map<? extends String, ? extends V> p_putAll_1_) {
      for(Entry<? extends String, ? extends V> entry : p_putAll_1_.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public void clear() {
      this.map.clear();
   }

   public Set<String> keySet() {
      return this.map.keySet();
   }

   public Collection<V> values() {
      return this.map.values();
   }

   public Set<Entry<String, V>> entrySet() {
      return this.map.entrySet();
   }
}