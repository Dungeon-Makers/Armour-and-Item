package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

public class LeapAtTargetGoal extends Goal {
   private final MobEntity mob;
   private LivingEntity target;
   private final float yd;

   public LeapAtTargetGoal(MobEntity p_i1630_1_, float p_i1630_2_) {
      this.mob = p_i1630_1_;
      this.yd = p_i1630_2_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else {
         this.target = this.mob.getTarget();
         if (this.target == null) {
            return false;
         } else {
            double d0 = this.mob.distanceToSqr(this.target);
            if (!(d0 < 4.0D) && !(d0 > 16.0D)) {
               if (!this.mob.onGround) {
                  return false;
               } else {
                  return this.mob.getRandom().nextInt(5) == 0;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.onGround;
   }

   public void start() {
      Vec3d vec3d = this.mob.getDeltaMovement();
      Vec3d vec3d1 = new Vec3d(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
      if (vec3d1.lengthSqr() > 1.0E-7D) {
         vec3d1 = vec3d1.normalize().scale(0.4D).add(vec3d.scale(0.2D));
      }

      this.mob.setDeltaMovement(vec3d1.x, (double)this.yd, vec3d1.z);
   }
}