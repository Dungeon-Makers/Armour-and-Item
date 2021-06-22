package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class BredAnimalsTrigger extends AbstractCriterionTrigger<BredAnimalsTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public ResourceLocation getId() {
      return ID;
   }

   public BredAnimalsTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_192166_1_.get("parent"));
      EntityPredicate entitypredicate1 = EntityPredicate.fromJson(p_192166_1_.get("partner"));
      EntityPredicate entitypredicate2 = EntityPredicate.fromJson(p_192166_1_.get("child"));
      return new BredAnimalsTrigger.Instance(entitypredicate, entitypredicate1, entitypredicate2);
   }

   public void trigger(ServerPlayerEntity p_192168_1_, AnimalEntity p_192168_2_, @Nullable AnimalEntity p_192168_3_, @Nullable AgeableEntity p_192168_4_) {
      this.func_227070_a_(p_192168_1_.getAdvancements(), (p_226253_4_) -> {
         return p_226253_4_.func_192246_a(p_192168_1_, p_192168_2_, p_192168_3_, p_192168_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public Instance(EntityPredicate p_i47408_1_, EntityPredicate p_i47408_2_, EntityPredicate p_i47408_3_) {
         super(BredAnimalsTrigger.ID);
         this.parent = p_i47408_1_;
         this.partner = p_i47408_2_;
         this.child = p_i47408_3_;
      }

      public static BredAnimalsTrigger.Instance bredAnimals() {
         return new BredAnimalsTrigger.Instance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.Instance bredAnimals(EntityPredicate.Builder p_203909_0_) {
         return new BredAnimalsTrigger.Instance(p_203909_0_.build(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean func_192246_a(ServerPlayerEntity p_192246_1_, AnimalEntity p_192246_2_, @Nullable AnimalEntity p_192246_3_, @Nullable AgeableEntity p_192246_4_) {
         if (!this.child.matches(p_192246_1_, p_192246_4_)) {
            return false;
         } else {
            return this.parent.matches(p_192246_1_, p_192246_2_) && this.partner.matches(p_192246_1_, p_192246_3_) || this.parent.matches(p_192246_1_, p_192246_3_) && this.partner.matches(p_192246_1_, p_192246_2_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("parent", this.parent.serializeToJson());
         jsonobject.add("partner", this.partner.serializeToJson());
         jsonobject.add("child", this.child.serializeToJson());
         return jsonobject;
      }
   }
}