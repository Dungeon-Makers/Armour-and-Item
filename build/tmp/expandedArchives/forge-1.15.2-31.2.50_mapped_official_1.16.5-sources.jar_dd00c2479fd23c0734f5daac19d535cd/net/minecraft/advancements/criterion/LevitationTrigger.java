package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class LevitationTrigger extends AbstractCriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_192166_1_.get("distance"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("duration"));
      return new LevitationTrigger.Instance(distancepredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_193162_1_, Vec3d p_193162_2_, int p_193162_3_) {
      this.func_227070_a_(p_193162_1_.getAdvancements(), (p_226852_3_) -> {
         return p_226852_3_.matches(p_193162_1_, p_193162_2_, p_193162_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(DistancePredicate p_i49729_1_, MinMaxBounds.IntBound p_i49729_2_) {
         super(LevitationTrigger.ID);
         this.distance = p_i49729_1_;
         this.duration = p_i49729_2_;
      }

      public static LevitationTrigger.Instance levitated(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(p_203930_0_, MinMaxBounds.IntBound.ANY);
      }

      public boolean matches(ServerPlayerEntity p_193201_1_, Vec3d p_193201_2_, int p_193201_3_) {
         if (!this.distance.matches(p_193201_2_.x, p_193201_2_.y, p_193201_2_.z, p_193201_1_.getX(), p_193201_1_.getY(), p_193201_1_.getZ())) {
            return false;
         } else {
            return this.duration.matches(p_193201_3_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("distance", this.distance.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         return jsonobject;
      }
   }
}