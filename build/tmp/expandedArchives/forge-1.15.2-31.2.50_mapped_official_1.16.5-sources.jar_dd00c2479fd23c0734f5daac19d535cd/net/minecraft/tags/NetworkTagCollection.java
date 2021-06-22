package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagCollection<T> extends TagCollection<T> {
   private final Registry<T> field_200044_a;

   public NetworkTagCollection(Registry<T> p_i49817_1_, String p_i49817_2_, String p_i49817_3_) {
      super(p_i49817_1_::func_218349_b, p_i49817_2_, false, p_i49817_3_);
      this.field_200044_a = p_i49817_1_;
   }

   public void func_200042_a(PacketBuffer p_200042_1_) {
      Map<ResourceLocation, Tag<T>> map = this.func_200039_c();
      p_200042_1_.writeVarInt(map.size());

      for(Entry<ResourceLocation, Tag<T>> entry : map.entrySet()) {
         p_200042_1_.writeResourceLocation(entry.getKey());
         p_200042_1_.writeVarInt(entry.getValue().func_199885_a().size());

         for(T t : entry.getValue().func_199885_a()) {
            p_200042_1_.writeVarInt(this.field_200044_a.getId(t));
         }
      }

   }

   public void func_200043_b(PacketBuffer p_200043_1_) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();
      int i = p_200043_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = p_200043_1_.readResourceLocation();
         int k = p_200043_1_.readVarInt();
         Tag.Builder<T> builder = Tag.Builder.tag();

         for(int l = 0; l < k; ++l) {
            builder.func_200048_a(this.field_200044_a.byId(p_200043_1_.readVarInt()));
         }

         map.put(resourcelocation, builder.func_200051_a(resourcelocation));
      }

      this.func_223507_b(map);
   }
}