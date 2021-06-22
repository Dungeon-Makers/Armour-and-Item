package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger extends AbstractCriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new ConsumeItemTrigger.Instance(ItemPredicate.fromJson(p_192166_1_.get("item")));
   }

   public void trigger(ServerPlayerEntity p_193148_1_, ItemStack p_193148_2_) {
      this.func_227070_a_(p_193148_1_.getAdvancements(), (p_226325_1_) -> {
         return p_226325_1_.matches(p_193148_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47562_1_) {
         super(ConsumeItemTrigger.ID);
         this.item = p_i47562_1_;
      }

      public static ConsumeItemTrigger.Instance usedItem() {
         return new ConsumeItemTrigger.Instance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.Instance usedItem(IItemProvider p_203913_0_) {
         return new ConsumeItemTrigger.Instance(new ItemPredicate((Tag<Item>)null, p_203913_0_.asItem(), MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NBTPredicate.ANY));
      }

      public boolean matches(ItemStack p_193193_1_) {
         return this.item.matches(p_193193_1_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}