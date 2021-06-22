package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
   private final TameableEntity mob;
   private boolean field_75271_b;

   public SitGoal(TameableEntity p_i1654_1_) {
      this.mob = p_i1654_1_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canContinueToUse() {
      return this.field_75271_b;
   }

   public boolean canUse() {
      if (!this.mob.isTame()) {
         return false;
      } else if (this.mob.isInWaterOrBubble()) {
         return false;
      } else if (!this.mob.onGround) {
         return false;
      } else {
         LivingEntity livingentity = this.mob.getOwner();
         if (livingentity == null) {
            return true;
         } else {
            return this.mob.distanceToSqr(livingentity) < 144.0D && livingentity.getLastHurtByMob() != null ? false : this.field_75271_b;
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
      this.mob.func_70904_g(true);
   }

   public void stop() {
      this.mob.func_70904_g(false);
   }

   public void func_75270_a(boolean p_75270_1_) {
      this.field_75271_b = p_75270_1_;
   }
}