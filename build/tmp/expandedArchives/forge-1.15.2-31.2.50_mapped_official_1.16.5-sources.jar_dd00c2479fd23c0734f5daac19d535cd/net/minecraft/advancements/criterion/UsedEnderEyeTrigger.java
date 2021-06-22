package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger extends AbstractCriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedEnderEyeTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(p_192166_1_.get("distance"));
      return new UsedEnderEyeTrigger.Instance(minmaxbounds$floatbound);
   }

   public void trigger(ServerPlayerEntity p_192239_1_, BlockPos p_192239_2_) {
      double d0 = p_192239_1_.getX() - (double)p_192239_2_.getX();
      double d1 = p_192239_1_.getZ() - (double)p_192239_2_.getZ();
      double d2 = d0 * d0 + d1 * d1;
      this.func_227070_a_(p_192239_1_.getAdvancements(), (p_227325_2_) -> {
         return p_227325_2_.matches(d2);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.FloatBound level;

      public Instance(MinMaxBounds.FloatBound p_i49730_1_) {
         super(UsedEnderEyeTrigger.ID);
         this.level = p_i49730_1_;
      }

      public boolean matches(double p_192288_1_) {
         return this.level.matchesSqr(p_192288_1_);
      }
   }
}