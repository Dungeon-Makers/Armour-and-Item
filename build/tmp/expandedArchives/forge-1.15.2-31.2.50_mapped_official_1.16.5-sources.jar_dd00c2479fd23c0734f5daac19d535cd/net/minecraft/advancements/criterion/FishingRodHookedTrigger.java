package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingRodHookedTrigger extends AbstractCriterionTrigger<FishingRodHookedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("rod"));
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("entity"));
      ItemPredicate itempredicate1 = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new FishingRodHookedTrigger.Instance(itempredicate, entitypredicate, itempredicate1);
   }

   public void trigger(ServerPlayerEntity p_204820_1_, ItemStack p_204820_2_, FishingBobberEntity p_204820_3_, Collection<ItemStack> p_204820_4_) {
      this.func_227070_a_(p_204820_1_.getAdvancements(), (p_226628_4_) -> {
         return p_226628_4_.func_204830_a(p_204820_1_, p_204820_2_, p_204820_3_, p_204820_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i48916_1_, EntityPredicate p_i48916_2_, ItemPredicate p_i48916_3_) {
         super(FishingRodHookedTrigger.ID);
         this.rod = p_i48916_1_;
         this.entity = p_i48916_2_;
         this.item = p_i48916_3_;
      }

      public static FishingRodHookedTrigger.Instance fishedItem(ItemPredicate p_204829_0_, EntityPredicate p_204829_1_, ItemPredicate p_204829_2_) {
         return new FishingRodHookedTrigger.Instance(p_204829_0_, p_204829_1_, p_204829_2_);
      }

      public boolean func_204830_a(ServerPlayerEntity p_204830_1_, ItemStack p_204830_2_, FishingBobberEntity p_204830_3_, Collection<ItemStack> p_204830_4_) {
         if (!this.rod.matches(p_204830_2_)) {
            return false;
         } else if (!this.entity.matches(p_204830_1_, p_204830_3_.hookedIn)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean flag = false;
               if (p_204830_3_.hookedIn instanceof ItemEntity) {
                  ItemEntity itementity = (ItemEntity)p_204830_3_.hookedIn;
                  if (this.item.matches(itementity.getItem())) {
                     flag = true;
                  }
               }

               for(ItemStack itemstack : p_204830_4_) {
                  if (this.item.matches(itemstack)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("rod", this.rod.serializeToJson());
         jsonobject.add("entity", this.entity.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}