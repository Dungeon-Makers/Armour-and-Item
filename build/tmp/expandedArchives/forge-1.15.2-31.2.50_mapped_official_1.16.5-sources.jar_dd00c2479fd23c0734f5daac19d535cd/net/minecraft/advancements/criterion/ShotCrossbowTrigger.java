package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger extends AbstractCriterionTrigger<ShotCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public ShotCrossbowTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new ShotCrossbowTrigger.Instance(itempredicate);
   }

   public void trigger(ServerPlayerEntity p_215111_1_, ItemStack p_215111_2_) {
      this.func_227070_a_(p_215111_1_.getAdvancements(), (p_227037_1_) -> {
         return p_227037_1_.matches(p_215111_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i50604_1_) {
         super(ShotCrossbowTrigger.ID);
         this.item = p_i50604_1_;
      }

      public static ShotCrossbowTrigger.Instance shotCrossbow(IItemProvider p_215122_0_) {
         return new ShotCrossbowTrigger.Instance(ItemPredicate.Builder.item().of(p_215122_0_).build());
      }

      public boolean matches(ItemStack p_215121_1_) {
         return this.item.matches(p_215121_1_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}