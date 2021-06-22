package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger extends AbstractCriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("durability"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("delta"));
      return new ItemDurabilityTrigger.Instance(itempredicate, minmaxbounds$intbound, minmaxbounds$intbound1);
   }

   public void trigger(ServerPlayerEntity p_193158_1_, ItemStack p_193158_2_, int p_193158_3_) {
      this.func_227070_a_(p_193158_1_.getAdvancements(), (p_226653_2_) -> {
         return p_226653_2_.matches(p_193158_2_, p_193158_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound durability;
      private final MinMaxBounds.IntBound delta;

      public Instance(ItemPredicate p_i49703_1_, MinMaxBounds.IntBound p_i49703_2_, MinMaxBounds.IntBound p_i49703_3_) {
         super(ItemDurabilityTrigger.ID);
         this.item = p_i49703_1_;
         this.durability = p_i49703_2_;
         this.delta = p_i49703_3_;
      }

      public static ItemDurabilityTrigger.Instance changedDurability(ItemPredicate p_211182_0_, MinMaxBounds.IntBound p_211182_1_) {
         return new ItemDurabilityTrigger.Instance(p_211182_0_, p_211182_1_, MinMaxBounds.IntBound.ANY);
      }

      public boolean matches(ItemStack p_193197_1_, int p_193197_2_) {
         if (!this.item.matches(p_193197_1_)) {
            return false;
         } else if (!this.durability.matches(p_193197_1_.getMaxDamage() - p_193197_2_)) {
            return false;
         } else {
            return this.delta.matches(p_193197_1_.getDamageValue() - p_193197_2_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("durability", this.durability.serializeToJson());
         jsonobject.add("delta", this.delta.serializeToJson());
         return jsonobject;
      }
   }
}