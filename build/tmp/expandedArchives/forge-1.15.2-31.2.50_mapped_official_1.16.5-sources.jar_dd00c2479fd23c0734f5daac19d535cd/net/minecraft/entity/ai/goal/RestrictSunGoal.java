package net.minecraft.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.pathfinding.GroundPathNavigator;

public class RestrictSunGoal extends Goal {
   private final CreatureEntity mob;

   public RestrictSunGoal(CreatureEntity p_i1652_1_) {
      this.mob = p_i1652_1_;
   }

   public boolean canUse() {
      return this.mob.level.isDay() && this.mob.getItemBySlot(EquipmentSlotType.HEAD).isEmpty() && this.mob.getNavigation() instanceof GroundPathNavigator;
   }

   public void start() {
      ((GroundPathNavigator)this.mob.getNavigation()).setAvoidSun(true);
   }

   public void stop() {
      ((GroundPathNavigator)this.mob.getNavigation()).setAvoidSun(false);
   }
}