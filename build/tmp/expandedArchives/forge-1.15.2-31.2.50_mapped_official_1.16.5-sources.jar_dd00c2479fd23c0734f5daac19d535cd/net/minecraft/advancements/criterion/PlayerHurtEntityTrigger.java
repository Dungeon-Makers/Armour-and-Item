package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger extends AbstractCriterionTrigger<PlayerHurtEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public PlayerHurtEntityTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DamagePredicate damagepredicate = DamagePredicate.fromJson(p_192166_1_.get("damage"));
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("entity"));
      return new PlayerHurtEntityTrigger.Instance(damagepredicate, entitypredicate);
   }

   public void trigger(ServerPlayerEntity p_192220_1_, Entity p_192220_2_, DamageSource p_192220_3_, float p_192220_4_, float p_192220_5_, boolean p_192220_6_) {
      this.func_227070_a_(p_192220_1_.getAdvancements(), (p_226956_6_) -> {
         return p_226956_6_.func_192278_a(p_192220_1_, p_192220_2_, p_192220_3_, p_192220_4_, p_192220_5_, p_192220_6_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public Instance(DamagePredicate p_i47406_1_, EntityPredicate p_i47406_2_) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = p_i47406_1_;
         this.entity = p_i47406_2_;
      }

      public static PlayerHurtEntityTrigger.Instance playerHurtEntity(DamagePredicate.Builder p_203936_0_) {
         return new PlayerHurtEntityTrigger.Instance(p_203936_0_.build(), EntityPredicate.ANY);
      }

      public boolean func_192278_a(ServerPlayerEntity p_192278_1_, Entity p_192278_2_, DamageSource p_192278_3_, float p_192278_4_, float p_192278_5_, boolean p_192278_6_) {
         if (!this.damage.matches(p_192278_1_, p_192278_3_, p_192278_4_, p_192278_5_, p_192278_6_)) {
            return false;
         } else {
            return this.entity.matches(p_192278_1_, p_192278_2_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("damage", this.damage.serializeToJson());
         jsonobject.add("entity", this.entity.serializeToJson());
         return jsonobject;
      }
   }
}