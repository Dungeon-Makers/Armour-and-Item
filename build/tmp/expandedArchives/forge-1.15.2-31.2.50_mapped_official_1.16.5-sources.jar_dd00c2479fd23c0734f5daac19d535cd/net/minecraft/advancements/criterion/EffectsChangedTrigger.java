package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger extends AbstractCriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(p_192166_1_.get("effects"));
      return new EffectsChangedTrigger.Instance(mobeffectspredicate);
   }

   public void trigger(ServerPlayerEntity p_193153_1_) {
      this.func_227070_a_(p_193153_1_.getAdvancements(), (p_226524_1_) -> {
         return p_226524_1_.matches(p_193153_1_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(MobEffectsPredicate p_i47545_1_) {
         super(EffectsChangedTrigger.ID);
         this.effects = p_i47545_1_;
      }

      public static EffectsChangedTrigger.Instance hasEffects(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(p_203917_0_);
      }

      public boolean matches(ServerPlayerEntity p_193195_1_) {
         return this.effects.matches(p_193195_1_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("effects", this.effects.serializeToJson());
         return jsonobject;
      }
   }
}