package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class CuredZombieVillagerTrigger extends AbstractCriterionTrigger<CuredZombieVillagerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

   public ResourceLocation getId() {
      return ID;
   }

   public CuredZombieVillagerTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("zombie"));
      EntityPredicate entitypredicate1 = EntityPredicate.fromJson(p_192166_1_.get("villager"));
      return new CuredZombieVillagerTrigger.Instance(entitypredicate, entitypredicate1);
   }

   public void trigger(ServerPlayerEntity p_192183_1_, ZombieEntity p_192183_2_, VillagerEntity p_192183_3_) {
      this.func_227070_a_(p_192183_1_.getAdvancements(), (p_226331_3_) -> {
         return p_226331_3_.func_192254_a(p_192183_1_, p_192183_2_, p_192183_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate zombie;
      private final EntityPredicate villager;

      public Instance(EntityPredicate p_i47459_1_, EntityPredicate p_i47459_2_) {
         super(CuredZombieVillagerTrigger.ID);
         this.zombie = p_i47459_1_;
         this.villager = p_i47459_2_;
      }

      public static CuredZombieVillagerTrigger.Instance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.Instance(EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean func_192254_a(ServerPlayerEntity p_192254_1_, ZombieEntity p_192254_2_, VillagerEntity p_192254_3_) {
         if (!this.zombie.matches(p_192254_1_, p_192254_2_)) {
            return false;
         } else {
            return this.villager.matches(p_192254_1_, p_192254_3_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("zombie", this.zombie.serializeToJson());
         jsonobject.add("villager", this.villager.serializeToJson());
         return jsonobject;
      }
   }
}