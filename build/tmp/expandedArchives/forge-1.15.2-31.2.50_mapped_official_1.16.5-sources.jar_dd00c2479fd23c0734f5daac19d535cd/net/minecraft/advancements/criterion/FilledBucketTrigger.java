package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger extends AbstractCriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new FilledBucketTrigger.Instance(itempredicate);
   }

   public void trigger(ServerPlayerEntity p_204817_1_, ItemStack p_204817_2_) {
      this.func_227070_a_(p_204817_1_.getAdvancements(), (p_226627_1_) -> {
         return p_226627_1_.matches(p_204817_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i48918_1_) {
         super(FilledBucketTrigger.ID);
         this.item = p_i48918_1_;
      }

      public static FilledBucketTrigger.Instance filledBucket(ItemPredicate p_204827_0_) {
         return new FilledBucketTrigger.Instance(p_204827_0_);
      }

      public boolean matches(ItemStack p_204826_1_) {
         return this.item.matches(p_204826_1_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}