package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger extends AbstractCriterionTrigger<SummonedEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("entity"));
      return new SummonedEntityTrigger.Instance(entitypredicate);
   }

   public void trigger(ServerPlayerEntity p_192229_1_, Entity p_192229_2_) {
      this.func_227070_a_(p_192229_1_.getAdvancements(), (p_227229_2_) -> {
         return p_227229_2_.func_192283_a(p_192229_1_, p_192229_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47371_1_) {
         super(SummonedEntityTrigger.ID);
         this.entity = p_i47371_1_;
      }

      public static SummonedEntityTrigger.Instance summonedEntity(EntityPredicate.Builder p_203937_0_) {
         return new SummonedEntityTrigger.Instance(p_203937_0_.build());
      }

      public boolean func_192283_a(ServerPlayerEntity p_192283_1_, Entity p_192283_2_) {
         return this.entity.matches(p_192283_1_, p_192283_2_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serializeToJson());
         return jsonobject;
      }
   }
}