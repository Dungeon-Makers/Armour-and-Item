package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TickTrigger extends AbstractCriterionTrigger<TickTrigger.Instance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");

   public ResourceLocation getId() {
      return ID;
   }

   public TickTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new TickTrigger.Instance();
   }

   public void trigger(ServerPlayerEntity p_193182_1_) {
      this.func_227071_b_(p_193182_1_.getAdvancements());
   }

   public static class Instance extends CriterionInstance {
      public Instance() {
         super(TickTrigger.ID);
      }
   }
}