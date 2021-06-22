package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class WaterAvoidingRandomWalkingGoal extends RandomWalkingGoal {
   protected final float probability;

   public WaterAvoidingRandomWalkingGoal(CreatureEntity p_i47301_1_, double p_i47301_2_) {
      this(p_i47301_1_, p_i47301_2_, 0.001F);
   }

   public WaterAvoidingRandomWalkingGoal(CreatureEntity p_i47302_1_, double p_i47302_2_, float p_i47302_4_) {
      super(p_i47302_1_, p_i47302_2_);
      this.probability = p_i47302_4_;
   }

   @Nullable
   protected Vec3d getPosition() {
      if (this.mob.isInWaterOrBubble()) {
         Vec3d vec3d = RandomPositionGenerator.getLandPos(this.mob, 15, 7);
         return vec3d == null ? super.getPosition() : vec3d;
      } else {
         return this.mob.getRandom().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.mob, 10, 7) : super.getPosition();
      }
   }
}