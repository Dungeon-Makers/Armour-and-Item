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

public class SlideDownBlockTrigger extends AbstractCriterionTrigger<SlideDownBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return ID;
   }

   public SlideDownBlockTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block block = deserializeBlock(p_192166_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_192166_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_227148_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_227148_1_);
         });
      }

      return new SlideDownBlockTrigger.Instance(block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_227150_0_) {
      if (p_227150_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_227150_0_, "block"));
         return Registry.BLOCK.func_218349_b(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_227152_1_, BlockState p_227152_2_) {
      this.func_227070_a_(p_227152_1_.getAdvancements(), (p_227149_1_) -> {
         return p_227149_1_.matches(p_227152_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;

      public Instance(@Nullable Block p_i225786_1_, StatePropertiesPredicate p_i225786_2_) {
         super(SlideDownBlockTrigger.ID);
         this.block = p_i225786_1_;
         this.state = p_i225786_2_;
      }

      public static SlideDownBlockTrigger.Instance slidesDownBlock(Block p_227156_0_) {
         return new SlideDownBlockTrigger.Instance(p_227156_0_, StatePropertiesPredicate.ANY);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_227157_1_) {
         if (this.block != null && p_227157_1_.getBlock() != this.block) {
            return false;
         } else {
            return this.state.matches(p_227157_1_);
         }
      }
   }
}