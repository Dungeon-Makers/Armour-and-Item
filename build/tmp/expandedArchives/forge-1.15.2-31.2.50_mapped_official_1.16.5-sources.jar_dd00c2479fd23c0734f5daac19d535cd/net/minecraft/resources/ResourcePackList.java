package net.minecraft.resources;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class ResourcePackList<T extends ResourcePackInfo> implements AutoCloseable {
   private final Set<IPackFinder> sources = Sets.newHashSet();
   private final Map<String, T> available = Maps.newLinkedHashMap();
   private final List<T> selected = Lists.newLinkedList();
   private final ResourcePackInfo.IFactory<T> constructor;

   public ResourcePackList(ResourcePackInfo.IFactory<T> p_i47909_1_) {
      this.constructor = p_i47909_1_;
   }

   public void reload() {
      this.close();
      Set<String> set = this.selected.stream().map(ResourcePackInfo::getId).collect(Collectors.toCollection(LinkedHashSet::new));
      this.available.clear();
      this.selected.clear();

      for(IPackFinder ipackfinder : this.sources) {
         ipackfinder.func_195730_a(this.available, this.constructor);
      }

      this.func_198986_e();
      this.selected.addAll(set.stream().map(this.available::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

      for(T t : this.available.values()) {
         if (t.isRequired() && !this.selected.contains(t)) {
            t.getDefaultPosition().insert(this.selected, t, Functions.identity(), false);
         }
      }

   }

   private void func_198986_e() {
      List<Entry<String, T>> list = Lists.newArrayList(this.available.entrySet());
      this.available.clear();
      list.stream().sorted(net.minecraftforge.fml.packs.ResourcePackLoader.getSorter()).forEachOrdered((p_198984_1_) -> {
         ResourcePackInfo resourcepackinfo = (ResourcePackInfo)this.available.put(p_198984_1_.getKey(), p_198984_1_.getValue());
      });
   }

   public void setSelected(Collection<T> p_198985_1_) {
      this.selected.clear();
      this.selected.addAll(p_198985_1_);

      for(T t : this.available.values()) {
         if (t.isRequired() && !this.selected.contains(t)) {
            t.getDefaultPosition().insert(this.selected, t, Functions.identity(), false);
         }
      }

   }

   public Collection<T> getAvailablePacks() {
      return this.available.values();
   }

   public Collection<T> func_198979_c() {
      Collection<T> collection = Lists.newArrayList(this.available.values());
      collection.removeAll(this.selected);
      return collection;
   }

   public Collection<T> getSelectedPacks() {
      return this.selected;
   }

   @Nullable
   public T getPack(String p_198981_1_) {
      return (T)(this.available.get(p_198981_1_));
   }

   public void func_198982_a(IPackFinder p_198982_1_) {
      this.sources.add(p_198982_1_);
   }

   public void close() {
      this.available.values().forEach(ResourcePackInfo::close);
   }
}
