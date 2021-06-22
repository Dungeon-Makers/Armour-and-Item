package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageAtNightGoal extends Goal {
   private final CreatureEntity mob;
   private final int interval;
   @Nullable
   private BlockPos wantedPos;

   public MoveThroughVillageAtNightGoal(CreatureEntity p_i50321_1_, int p_i50321_2_) {
      this.mob = p_i50321_1_;
      this.interval = p_i50321_2_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else if (this.mob.level.isDay()) {
         return false;
      } else if (this.mob.getRandom().nextInt(this.interval) != 0) {
         return false;
      } else {
         ServerWorld serverworld = (ServerWorld)this.mob.level;
         BlockPos blockpos = new BlockPos(this.mob);
         if (!serverworld.func_217471_a(blockpos, 6)) {
            return false;
         } else {
            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.mob, 15, 7, (p_220755_1_) -> {
               return (double)(-serverworld.sectionsToVillage(SectionPos.of(p_220755_1_)));
            });
            this.wantedPos = vec3d == null ? null : new BlockPos(vec3d);
            return this.wantedPos != null;
         }
      }
   }

   public boolean canContinueToUse() {
      return this.wantedPos != null && !this.mob.getNavigation().isDone() && this.mob.getNavigation().getTargetPos().equals(this.wantedPos);
   }

   public void tick() {
      if (this.wantedPos != null) {
         PathNavigator pathnavigator = this.mob.getNavigation();
         if (pathnavigator.isDone() && !this.wantedPos.closerThan(this.mob.position(), 10.0D)) {
            Vec3d vec3d = new Vec3d(this.wantedPos);
            Vec3d vec3d1 = this.mob.position();
            Vec3d vec3d2 = vec3d1.subtract(vec3d);
            vec3d = vec3d2.scale(0.4D).add(vec3d);
            Vec3d vec3d3 = vec3d.subtract(vec3d1).normalize().scale(10.0D).add(vec3d1);
            BlockPos blockpos = new BlockPos(vec3d3);
            blockpos = this.mob.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);
            if (!pathnavigator.moveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D)) {
               this.moveRandomly();
            }
         }

      }
   }

   private void moveRandomly() {
      Random random = this.mob.getRandom();
      BlockPos blockpos = this.mob.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.mob)).offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
      this.mob.getNavigation().moveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
   }
}