package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger extends AbstractCriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   public BrewedPotionTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Potion potion = null;
      if (p_192166_1_.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_192166_1_, "potion"));
         potion = Registry.POTION.func_218349_b(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         });
      }

      return new BrewedPotionTrigger.Instance(potion);
   }

   public void trigger(ServerPlayerEntity p_192173_1_, Potion p_192173_2_) {
      this.func_227070_a_(p_192173_1_.getAdvancements(), (p_226301_1_) -> {
         return p_226301_1_.matches(p_192173_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Potion potion;

      public Instance(@Nullable Potion p_i47398_1_) {
         super(BrewedPotionTrigger.ID);
         this.potion = p_i47398_1_;
      }

      public static BrewedPotionTrigger.Instance brewedPotion() {
         return new BrewedPotionTrigger.Instance((Potion)null);
      }

      public boolean matches(Potion p_192250_1_) {
         return this.potion == null || this.potion == p_192250_1_;
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }
}