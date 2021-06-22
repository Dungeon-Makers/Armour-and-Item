package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vec3d targetLocation;

   public TakeoffPhase(EnderDragonEntity p_i46783_1_) {
      super(p_i46783_1_);
   }

   public void doServerTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!blockpos.closerThan(this.dragon.position(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void begin() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int i = this.dragon.findClosestNode();
      Vec3d vec3d = this.dragon.getHeadLookVector(1.0F);
      int j = this.dragon.findClosestNode(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
      if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
         j = j % 12;
         if (j < 0) {
            j += 12;
         }
      } else {
         j = j - 12;
         j = j & 7;
         j = j + 12;
      }

      this.currentPath = this.dragon.findPath(i, j, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.advance();
         if (!this.currentPath.isDone()) {
            Vec3d vec3d = this.currentPath.func_186310_f();
            this.currentPath.advance();

            double d0;
            while(true) {
               d0 = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
               if (!(d0 < vec3d.y)) {
                  break;
               }
            }

            this.targetLocation = new Vec3d(vec3d.x, d0, vec3d.z);
         }
      }

   }

   @Nullable
   public Vec3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getPhase() {
      return PhaseType.TAKEOFF;
   }
}