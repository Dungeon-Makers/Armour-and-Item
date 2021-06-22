package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger extends AbstractCriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("level"));
      return new ConstructBeaconTrigger.Instance(minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_192180_1_, BeaconTileEntity p_192180_2_) {
      this.func_227070_a_(p_192180_1_.getAdvancements(), (p_226308_1_) -> {
         return p_226308_1_.matches(p_192180_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound level;

      public Instance(MinMaxBounds.IntBound p_i49736_1_) {
         super(ConstructBeaconTrigger.ID);
         this.level = p_i49736_1_;
      }

      public static ConstructBeaconTrigger.Instance constructedBeacon(MinMaxBounds.IntBound p_203912_0_) {
         return new ConstructBeaconTrigger.Instance(p_203912_0_);
      }

      public boolean matches(BeaconTileEntity p_192252_1_) {
         return this.level.matches(p_192252_1_.getLevels());
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}