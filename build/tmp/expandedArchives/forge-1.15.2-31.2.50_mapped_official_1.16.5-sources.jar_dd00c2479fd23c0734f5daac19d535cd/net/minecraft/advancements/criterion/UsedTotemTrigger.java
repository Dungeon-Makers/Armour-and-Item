package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger extends AbstractCriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new UsedTotemTrigger.Instance(itempredicate);
   }

   public void trigger(ServerPlayerEntity p_193187_1_, ItemStack p_193187_2_) {
      this.func_227070_a_(p_193187_1_.getAdvancements(), (p_227409_1_) -> {
         return p_227409_1_.matches(p_193187_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47564_1_) {
         super(UsedTotemTrigger.ID);
         this.item = p_i47564_1_;
      }

      public static UsedTotemTrigger.Instance usedTotem(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.item().of(p_203941_0_).build());
      }

      public boolean matches(ItemStack p_193218_1_) {
         return this.item.matches(p_193218_1_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}