package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FleeSunGoal extends Goal {
   protected final CreatureEntity mob;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;
   private final World level;

   public FleeSunGoal(CreatureEntity p_i1623_1_, double p_i1623_2_) {
      this.mob = p_i1623_1_;
      this.speedModifier = p_i1623_2_;
      this.level = p_i1623_1_.level;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.getTarget() != null) {
         return false;
      } else if (!this.level.isDay()) {
         return false;
      } else if (!this.mob.isOnFire()) {
         return false;
      } else if (!this.level.canSeeSky(new BlockPos(this.mob))) {
         return false;
      } else {
         return !this.mob.getItemBySlot(EquipmentSlotType.HEAD).isEmpty() ? false : this.setWantedPos();
      }
   }

   protected boolean setWantedPos() {
      Vec3d vec3d = this.getHidePos();
      if (vec3d == null) {
         return false;
      } else {
         this.wantedX = vec3d.x;
         this.wantedY = vec3d.y;
         this.wantedZ = vec3d.z;
         return true;
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   @Nullable
   protected Vec3d getHidePos() {
      Random random = this.mob.getRandom();
      BlockPos blockpos = new BlockPos(this.mob);

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
         if (!this.level.canSeeSky(blockpos1) && this.mob.getWalkTargetValue(blockpos1) < 0.0F) {
            return new Vec3d(blockpos1);
         }
      }

      return null;
   }
}