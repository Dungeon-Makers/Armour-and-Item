package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.MobEntity;

public class SwimGoal extends Goal {
   private final MobEntity mob;

   public SwimGoal(MobEntity p_i1624_1_) {
      this.mob = p_i1624_1_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
      p_i1624_1_.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      double d0 = (double)this.mob.getEyeHeight() < 0.4D ? 0.2D : 0.4D;
      return this.mob.isInWater() && this.mob.func_212107_bY() > d0 || this.mob.isInLava();
   }

   public void tick() {
      if (this.mob.getRandom().nextFloat() < 0.8F) {
         this.mob.getJumpControl().jump();
      }

   }
}