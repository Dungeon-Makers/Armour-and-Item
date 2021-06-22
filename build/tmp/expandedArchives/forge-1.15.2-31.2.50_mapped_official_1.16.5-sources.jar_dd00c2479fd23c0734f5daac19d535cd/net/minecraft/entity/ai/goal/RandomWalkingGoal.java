package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class RandomWalkingGoal extends Goal {
   protected final CreatureEntity mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected final double speedModifier;
   protected int interval;
   protected boolean forceTrigger;

   public RandomWalkingGoal(CreatureEntity p_i1648_1_, double p_i1648_2_) {
      this(p_i1648_1_, p_i1648_2_, 120);
   }

   public RandomWalkingGoal(CreatureEntity p_i45887_1_, double p_i45887_2_, int p_i45887_4_) {
      this.mob = p_i45887_1_;
      this.speedModifier = p_i45887_2_;
      this.interval = p_i45887_4_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else {
         if (!this.forceTrigger) {
            if (this.mob.getNoActionTime() >= 100) {
               return false;
            }

            if (this.mob.getRandom().nextInt(this.interval) != 0) {
               return false;
            }
         }

         Vec3d vec3d = this.getPosition();
         if (vec3d == null) {
            return false;
         } else {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            this.forceTrigger = false;
            return true;
         }
      }
   }

   @Nullable
   protected Vec3d getPosition() {
      return RandomPositionGenerator.getPos(this.mob, 10, 7);
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   public void stop() {
      this.mob.getNavigation().stop();
      super.stop();
   }

   public void trigger() {
      this.forceTrigger = true;
   }

   public void setInterval(int p_179479_1_) {
      this.interval = p_179479_1_;
   }
}