package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger extends AbstractCriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("entity"));
      return new TameAnimalTrigger.Instance(entitypredicate);
   }

   public void trigger(ServerPlayerEntity p_193178_1_, AnimalEntity p_193178_2_) {
      this.func_227070_a_(p_193178_1_.getAdvancements(), (p_227251_2_) -> {
         return p_227251_2_.func_193216_a(p_193178_1_, p_193178_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47513_1_) {
         super(TameAnimalTrigger.ID);
         this.entity = p_i47513_1_;
      }

      public static TameAnimalTrigger.Instance tamedAnimal() {
         return new TameAnimalTrigger.Instance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.Instance tamedAnimal(EntityPredicate p_215124_0_) {
         return new TameAnimalTrigger.Instance(p_215124_0_);
      }

      public boolean func_193216_a(ServerPlayerEntity p_193216_1_, AnimalEntity p_193216_2_) {
         return this.entity.matches(p_193216_1_, p_193216_2_);
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serializeToJson());
         return jsonobject;
      }
   }
}