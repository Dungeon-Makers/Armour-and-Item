package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class PositionTrigger extends AbstractCriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;

   public PositionTrigger(ResourceLocation p_i47432_1_) {
      this.id = p_i47432_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public PositionTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_192166_1_);
      return new PositionTrigger.Instance(this.id, locationpredicate);
   }

   public void trigger(ServerPlayerEntity p_192215_1_) {
      this.func_227070_a_(p_192215_1_.getAdvancements(), (p_226923_1_) -> {
         return p_226923_1_.matches(p_192215_1_.getLevel(), p_192215_1_.getX(), p_192215_1_.getY(), p_192215_1_.getZ());
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation p_i47544_1_, LocationPredicate p_i47544_2_) {
         super(p_i47544_1_);
         this.location = p_i47544_2_;
      }

      public static PositionTrigger.Instance located(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, p_203932_0_);
      }

      public static PositionTrigger.Instance sleptInBed() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static PositionTrigger.Instance raidWon() {
         return new PositionTrigger.Instance(CriteriaTriggers.RAID_WIN.id, LocationPredicate.ANY);
      }

      public boolean matches(ServerWorld p_193204_1_, double p_193204_2_, double p_193204_4_, double p_193204_6_) {
         return this.location.matches(p_193204_1_, p_193204_2_, p_193204_4_, p_193204_6_);
      }

      public JsonElement func_200288_b() {
         return this.location.serializeToJson();
      }
   }
}