package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class NetherTravelTrigger extends AbstractCriterionTrigger<NetherTravelTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");

   public ResourceLocation getId() {
      return ID;
   }

   public NetherTravelTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_192166_1_.get("entered"));
      LocationPredicate locationpredicate1 = LocationPredicate.fromJson(p_192166_1_.get("exited"));
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_192166_1_.get("distance"));
      return new NetherTravelTrigger.Instance(locationpredicate, locationpredicate1, distancepredicate);
   }

   public void trigger(ServerPlayerEntity p_193168_1_, Vec3d p_193168_2_) {
      this.func_227070_a_(p_193168_1_.getAdvancements(), (p_226945_2_) -> {
         return p_226945_2_.matches(p_193168_1_.getLevel(), p_193168_2_, p_193168_1_.getX(), p_193168_1_.getY(), p_193168_1_.getZ());
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public Instance(LocationPredicate p_i47574_1_, LocationPredicate p_i47574_2_, DistancePredicate p_i47574_3_) {
         super(NetherTravelTrigger.ID);
         this.entered = p_i47574_1_;
         this.exited = p_i47574_2_;
         this.distance = p_i47574_3_;
      }

      public static NetherTravelTrigger.Instance travelledThroughNether(DistancePredicate p_203933_0_) {
         return new NetherTravelTrigger.Instance(LocationPredicate.ANY, LocationPredicate.ANY, p_203933_0_);
      }

      public boolean matches(ServerWorld p_193206_1_, Vec3d p_193206_2_, double p_193206_3_, double p_193206_5_, double p_193206_7_) {
         if (!this.entered.matches(p_193206_1_, p_193206_2_.x, p_193206_2_.y, p_193206_2_.z)) {
            return false;
         } else if (!this.exited.matches(p_193206_1_, p_193206_3_, p_193206_5_, p_193206_7_)) {
            return false;
         } else {
            return this.distance.matches(p_193206_2_.x, p_193206_2_.y, p_193206_2_.z, p_193206_3_, p_193206_5_, p_193206_7_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entered", this.entered.serializeToJson());
         jsonobject.add("exited", this.exited.serializeToJson());
         jsonobject.add("distance", this.distance.serializeToJson());
         return jsonobject;
      }
   }
}