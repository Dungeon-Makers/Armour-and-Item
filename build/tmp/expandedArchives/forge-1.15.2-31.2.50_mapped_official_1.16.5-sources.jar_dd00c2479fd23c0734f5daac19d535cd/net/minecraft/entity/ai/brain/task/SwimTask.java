package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.server.ServerWorld;

public class SwimTask extends Task<MobEntity> {
   private final float field_220589_a;
   private final float chance;

   public SwimTask(float p_i50339_1_, float p_i50339_2_) {
      super(ImmutableMap.of());
      this.field_220589_a = p_i50339_1_;
      this.chance = p_i50339_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      return p_212832_2_.isInWater() && p_212832_2_.func_212107_bY() > (double)this.field_220589_a || p_212832_2_.isInLava();
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      return this.checkExtraStartConditions(p_212834_1_, p_212834_2_);
   }

   protected void tick(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      if (p_212833_2_.getRandom().nextFloat() < this.chance) {
         p_212833_2_.getJumpControl().jump();
      }

   }
}