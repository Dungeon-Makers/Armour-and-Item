package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger extends AbstractCriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_192166_1_, "recipe"));
      return new RecipeUnlockedTrigger.Instance(resourcelocation);
   }

   public void trigger(ServerPlayerEntity p_192225_1_, IRecipe<?> p_192225_2_) {
      this.func_227070_a_(p_192225_1_.getAdvancements(), (p_227018_1_) -> {
         return p_227018_1_.matches(p_192225_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ResourceLocation recipe;

      public Instance(ResourceLocation p_i48179_1_) {
         super(RecipeUnlockedTrigger.ID);
         this.recipe = p_i48179_1_;
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("recipe", this.recipe.toString());
         return jsonobject;
      }

      public boolean matches(IRecipe<?> p_193215_1_) {
         return this.recipe.equals(p_193215_1_.getId());
      }
   }
}