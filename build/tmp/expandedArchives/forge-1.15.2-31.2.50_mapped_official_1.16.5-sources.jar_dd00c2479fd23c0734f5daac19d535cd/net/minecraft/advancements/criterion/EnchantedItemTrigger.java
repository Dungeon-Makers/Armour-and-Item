package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger extends AbstractCriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("levels"));
      return new EnchantedItemTrigger.Instance(itempredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_192190_1_, ItemStack p_192190_2_, int p_192190_3_) {
      this.func_227070_a_(p_192190_1_.getAdvancements(), (p_226528_2_) -> {
         return p_226528_2_.matches(p_192190_2_, p_192190_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(ItemPredicate p_i49731_1_, MinMaxBounds.IntBound p_i49731_2_) {
         super(EnchantedItemTrigger.ID);
         this.item = p_i49731_1_;
         this.levels = p_i49731_2_;
      }

      public static EnchantedItemTrigger.Instance enchantedItem() {
         return new EnchantedItemTrigger.Instance(ItemPredicate.ANY, MinMaxBounds.IntBound.ANY);
      }

      public boolean matches(ItemStack p_192257_1_, int p_192257_2_) {
         if (!this.item.matches(p_192257_1_)) {
            return false;
         } else {
            return this.levels.matches(p_192257_2_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("levels", this.levels.serializeToJson());
         return jsonobject;
      }
   }
}