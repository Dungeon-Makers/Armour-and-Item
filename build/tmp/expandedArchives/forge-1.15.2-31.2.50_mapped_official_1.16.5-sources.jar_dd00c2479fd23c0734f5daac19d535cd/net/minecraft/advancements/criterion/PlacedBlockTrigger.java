package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class PlacedBlockTrigger extends AbstractCriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block block = deserializeBlock(p_192166_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_192166_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_226948_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226948_1_ + ":");
         });
      }

      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_192166_1_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_192166_1_.get("item"));
      return new PlacedBlockTrigger.Instance(block, statepropertiespredicate, locationpredicate, itempredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226950_0_) {
      if (p_226950_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226950_0_, "block"));
         return Registry.BLOCK.func_218349_b(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_193173_1_, BlockPos p_193173_2_, ItemStack p_193173_3_) {
      BlockState blockstate = p_193173_1_.getLevel().getBlockState(p_193173_2_);
      this.func_227070_a_(p_193173_1_.getAdvancements(), (p_226949_4_) -> {
         return p_226949_4_.matches(blockstate, p_193173_2_, p_193173_1_.getLevel(), p_193173_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(@Nullable Block p_i225765_1_, StatePropertiesPredicate p_i225765_2_, LocationPredicate p_i225765_3_, ItemPredicate p_i225765_4_) {
         super(PlacedBlockTrigger.ID);
         this.block = p_i225765_1_;
         this.state = p_i225765_2_;
         this.location = p_i225765_3_;
         this.item = p_i225765_4_;
      }

      public static PlacedBlockTrigger.Instance placedBlock(Block p_203934_0_) {
         return new PlacedBlockTrigger.Instance(p_203934_0_, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState p_193210_1_, BlockPos p_193210_2_, ServerWorld p_193210_3_, ItemStack p_193210_4_) {
         if (this.block != null && p_193210_1_.getBlock() != this.block) {
            return false;
         } else if (!this.state.matches(p_193210_1_)) {
            return false;
         } else if (!this.location.matches(p_193210_3_, (float)p_193210_2_.getX(), (float)p_193210_2_.getY(), (float)p_193210_2_.getZ())) {
            return false;
         } else {
            return this.item.matches(p_193210_4_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}