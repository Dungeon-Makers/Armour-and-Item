package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedTrigger extends AbstractCriterionTrigger<BeeNestDestroyedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

   public ResourceLocation getId() {
      return ID;
   }

   public BeeNestDestroyedTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block block = deserializeBlock(p_192166_1_);
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.Instance(block, itempredicate, minmaxbounds$intbound);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226221_0_) {
      if (p_226221_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226221_0_, "block"));
         return Registry.BLOCK.func_218349_b(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_226223_1_, Block p_226223_2_, ItemStack p_226223_3_, int p_226223_4_) {
      this.func_227070_a_(p_226223_1_.getAdvancements(), (p_226220_3_) -> {
         return p_226220_3_.matches(p_226223_2_, p_226223_3_, p_226223_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound numBees;

      public Instance(Block p_i225706_1_, ItemPredicate p_i225706_2_, MinMaxBounds.IntBound p_i225706_3_) {
         super(BeeNestDestroyedTrigger.ID);
         this.block = p_i225706_1_;
         this.item = p_i225706_2_;
         this.numBees = p_i225706_3_;
      }

      public static BeeNestDestroyedTrigger.Instance destroyedBeeNest(Block p_226229_0_, ItemPredicate.Builder p_226229_1_, MinMaxBounds.IntBound p_226229_2_) {
         return new BeeNestDestroyedTrigger.Instance(p_226229_0_, p_226229_1_.build(), p_226229_2_);
      }

      public boolean matches(Block p_226228_1_, ItemStack p_226228_2_, int p_226228_3_) {
         if (this.block != null && p_226228_1_ != this.block) {
            return false;
         } else {
            return !this.item.matches(p_226228_2_) ? false : this.numBees.matches(p_226228_3_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("num_bees_inside", this.numBees.serializeToJson());
         return jsonobject;
      }
   }
}