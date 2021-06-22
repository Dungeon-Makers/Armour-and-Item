package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger extends AbstractCriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate[] aentitypredicate = EntityPredicate.func_204849_b(p_192166_1_.get("victims"));
      return new ChanneledLightningTrigger.Instance(aentitypredicate);
   }

   public void trigger(ServerPlayerEntity p_204814_1_, Collection<? extends Entity> p_204814_2_) {
      this.func_227070_a_(p_204814_1_.getAdvancements(), (p_226307_2_) -> {
         return p_226307_2_.func_204823_a(p_204814_1_, p_204814_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] victims;

      public Instance(EntityPredicate[] p_i48921_1_) {
         super(ChanneledLightningTrigger.ID);
         this.victims = p_i48921_1_;
      }

      public static ChanneledLightningTrigger.Instance channeledLightning(EntityPredicate... p_204824_0_) {
         return new ChanneledLightningTrigger.Instance(p_204824_0_);
      }

      public boolean func_204823_a(ServerPlayerEntity p_204823_1_, Collection<? extends Entity> p_204823_2_) {
         for(EntityPredicate entitypredicate : this.victims) {
            boolean flag = false;

            for(Entity entity : p_204823_2_) {
               if (entitypredicate.matches(p_204823_1_, entity)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("victims", EntityPredicate.func_204850_a(this.victims));
         return jsonobject;
      }
   }
}