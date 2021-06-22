package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RightClickBlockWithItemTrigger extends AbstractCriterionTrigger<RightClickBlockWithItemTrigger.Instance> {
   private final ResourceLocation field_226692_a_;

   public RightClickBlockWithItemTrigger(ResourceLocation p_i225742_1_) {
      this.field_226692_a_ = p_i225742_1_;
   }

   public ResourceLocation getId() {
      return this.field_226692_a_;
   }

   public RightClickBlockWithItemTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      BlockPredicate blockpredicate = BlockPredicate.fromJson(p_192166_1_.get("block"));
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_192166_1_.get("state"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new RightClickBlockWithItemTrigger.Instance(this.field_226692_a_, blockpredicate, statepropertiespredicate, itempredicate);
   }

   public void trigger(ServerPlayerEntity p_226695_1_, BlockPos p_226695_2_, ItemStack p_226695_3_) {
      BlockState blockstate = p_226695_1_.getLevel().getBlockState(p_226695_2_);
      this.func_227070_a_(p_226695_1_.getAdvancements(), (p_226694_4_) -> {
         return p_226694_4_.matches(blockstate, p_226695_1_.getLevel(), p_226695_2_, p_226695_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final BlockPredicate field_226696_a_;
      private final StatePropertiesPredicate field_226697_b_;
      private final ItemPredicate item;

      public Instance(ResourceLocation p_i225743_1_, BlockPredicate p_i225743_2_, StatePropertiesPredicate p_i225743_3_, ItemPredicate p_i225743_4_) {
         super(p_i225743_1_);
         this.field_226696_a_ = p_i225743_2_;
         this.field_226697_b_ = p_i225743_3_;
         this.item = p_i225743_4_;
      }

      public static RightClickBlockWithItemTrigger.Instance func_226699_a_(BlockPredicate.Builder p_226699_0_, ItemPredicate.Builder p_226699_1_) {
         return new RightClickBlockWithItemTrigger.Instance(CriteriaTriggers.field_229863_J_.field_226692_a_, p_226699_0_.build(), StatePropertiesPredicate.ANY, p_226699_1_.build());
      }

      public boolean matches(BlockState p_226700_1_, ServerWorld p_226700_2_, BlockPos p_226700_3_, ItemStack p_226700_4_) {
         if (!this.field_226696_a_.matches(p_226700_2_, p_226700_3_)) {
            return false;
         } else if (!this.field_226697_b_.matches(p_226700_1_)) {
            return false;
         } else {
            return this.item.matches(p_226700_4_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("block", this.field_226696_a_.serializeToJson());
         jsonobject.add("state", this.field_226697_b_.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}