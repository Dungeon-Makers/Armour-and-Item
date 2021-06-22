package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnterBlockTrigger extends AbstractCriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   public EnterBlockTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block block = deserializeBlock(p_192166_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_192166_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_226548_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226548_1_);
         });
      }

      return new EnterBlockTrigger.Instance(block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226550_0_) {
      if (p_226550_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226550_0_, "block"));
         return Registry.BLOCK.func_218349_b(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_192193_1_, BlockState p_192193_2_) {
      this.func_227070_a_(p_192193_1_.getAdvancements(), (p_226549_1_) -> {
         return p_226549_1_.matches(p_192193_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;

      public Instance(@Nullable Block p_i225733_1_, StatePropertiesPredicate p_i225733_2_) {
         super(EnterBlockTrigger.ID);
         this.block = p_i225733_1_;
         this.state = p_i225733_2_;
      }

      public static EnterBlockTrigger.Instance entersBlock(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(p_203920_0_, StatePropertiesPredicate.ANY);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_192260_1_) {
         if (this.block != null && p_192260_1_.getBlock() != this.block) {
            return false;
         } else {
            return this.state.matches(p_192260_1_);
         }
      }
   }
}