package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger extends AbstractCriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public InventoryChangeTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      JsonObject jsonobject = JSONUtils.getAsJsonObject(p_192166_1_, "slots", new JsonObject());
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("occupied"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("full"));
      MinMaxBounds.IntBound minmaxbounds$intbound2 = MinMaxBounds.IntBound.fromJson(jsonobject.get("empty"));
      ItemPredicate[] aitempredicate = ItemPredicate.fromJsonArray(p_192166_1_.get("items"));
      return new InventoryChangeTrigger.Instance(minmaxbounds$intbound, minmaxbounds$intbound1, minmaxbounds$intbound2, aitempredicate);
   }

   public void func_192208_a(ServerPlayerEntity p_192208_1_, PlayerInventory p_192208_2_) {
      this.func_227070_a_(p_192208_1_.getAdvancements(), (p_226650_1_) -> {
         return p_226650_1_.func_192265_a(p_192208_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound slotsOccupied;
      private final MinMaxBounds.IntBound slotsFull;
      private final MinMaxBounds.IntBound slotsEmpty;
      private final ItemPredicate[] predicates;

      public Instance(MinMaxBounds.IntBound p_i49710_1_, MinMaxBounds.IntBound p_i49710_2_, MinMaxBounds.IntBound p_i49710_3_, ItemPredicate[] p_i49710_4_) {
         super(InventoryChangeTrigger.ID);
         this.slotsOccupied = p_i49710_1_;
         this.slotsFull = p_i49710_2_;
         this.slotsEmpty = p_i49710_3_;
         this.predicates = p_i49710_4_;
      }

      public static InventoryChangeTrigger.Instance hasItems(ItemPredicate... p_203923_0_) {
         return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, p_203923_0_);
      }

      public static InventoryChangeTrigger.Instance hasItems(IItemProvider... p_203922_0_) {
         ItemPredicate[] aitempredicate = new ItemPredicate[p_203922_0_.length];

         for(int i = 0; i < p_203922_0_.length; ++i) {
            aitempredicate[i] = new ItemPredicate((Tag<Item>)null, p_203922_0_[i].asItem(), MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NBTPredicate.ANY);
         }

         return hasItems(aitempredicate);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("occupied", this.slotsOccupied.serializeToJson());
            jsonobject1.add("full", this.slotsFull.serializeToJson());
            jsonobject1.add("empty", this.slotsEmpty.serializeToJson());
            jsonobject.add("slots", jsonobject1);
         }

         if (this.predicates.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ItemPredicate itempredicate : this.predicates) {
               jsonarray.add(itempredicate.serializeToJson());
            }

            jsonobject.add("items", jsonarray);
         }

         return jsonobject;
      }

      public boolean func_192265_a(PlayerInventory p_192265_1_) {
         int i = 0;
         int j = 0;
         int k = 0;
         List<ItemPredicate> list = Lists.newArrayList(this.predicates);

         for(int l = 0; l < p_192265_1_.getContainerSize(); ++l) {
            ItemStack itemstack = p_192265_1_.getItem(l);
            if (itemstack.isEmpty()) {
               ++j;
            } else {
               ++k;
               if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                  ++i;
               }

               Iterator<ItemPredicate> iterator = list.iterator();

               while(iterator.hasNext()) {
                  ItemPredicate itempredicate = iterator.next();
                  if (itempredicate.matches(itemstack)) {
                     iterator.remove();
                  }
               }
            }
         }

         if (!this.slotsFull.matches(i)) {
            return false;
         } else if (!this.slotsEmpty.matches(j)) {
            return false;
         } else if (!this.slotsOccupied.matches(k)) {
            return false;
         } else {
            return list.isEmpty();
         }
      }
   }
}