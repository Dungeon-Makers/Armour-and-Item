package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class MoveTowardsRestrictionGoal extends Goal {
   private final CreatureEntity mob;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;

   public MoveTowardsRestrictionGoal(CreatureEntity p_i2347_1_, double p_i2347_2_) {
      this.mob = p_i2347_1_;
      this.speedModifier = p_i2347_2_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isWithinRestriction()) {
         return false;
      } else {
         Vec3d vec3d = RandomPositionGenerator.getPosTowards(this.mob, 16, 7, new Vec3d(this.mob.getRestrictCenter()));
         if (vec3d == null) {
            return false;
         } else {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }
}