package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class ChangeDimensionTrigger extends AbstractCriterionTrigger<ChangeDimensionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

   public ResourceLocation getId() {
      return ID;
   }

   public ChangeDimensionTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DimensionType dimensiontype = p_192166_1_.has("from") ? DimensionType.func_193417_a(new ResourceLocation(JSONUtils.getAsString(p_192166_1_, "from"))) : null;
      DimensionType dimensiontype1 = p_192166_1_.has("to") ? DimensionType.func_193417_a(new ResourceLocation(JSONUtils.getAsString(p_192166_1_, "to"))) : null;
      return new ChangeDimensionTrigger.Instance(dimensiontype, dimensiontype1);
   }

   public void func_193143_a(ServerPlayerEntity p_193143_1_, DimensionType p_193143_2_, DimensionType p_193143_3_) {
      this.func_227070_a_(p_193143_1_.getAdvancements(), (p_226305_2_) -> {
         return p_226305_2_.func_193190_a(p_193143_2_, p_193143_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public Instance(@Nullable DimensionType p_i47475_1_, @Nullable DimensionType p_i47475_2_) {
         super(ChangeDimensionTrigger.ID);
         this.from = p_i47475_1_;
         this.to = p_i47475_2_;
      }

      public static ChangeDimensionTrigger.Instance func_203911_a(DimensionType p_203911_0_) {
         return new ChangeDimensionTrigger.Instance((DimensionType)null, p_203911_0_);
      }

      public boolean func_193190_a(DimensionType p_193190_1_, DimensionType p_193190_2_) {
         if (this.from != null && this.from != p_193190_1_) {
            return false;
         } else {
            return this.to == null || this.to == p_193190_2_;
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         if (this.from != null) {
            jsonobject.addProperty("from", DimensionType.func_212678_a(this.from).toString());
         }

         if (this.to != null) {
            jsonobject.addProperty("to", DimensionType.func_212678_a(this.to).toString());
         }

         return jsonobject;
      }
   }
}