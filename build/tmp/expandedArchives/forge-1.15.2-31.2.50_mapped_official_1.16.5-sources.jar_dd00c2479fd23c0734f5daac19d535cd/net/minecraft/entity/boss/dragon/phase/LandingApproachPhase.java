package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingApproachPhase extends Phase {
   private static final EntityPredicate NEAR_EGG_TARGETING = (new EntityPredicate()).range(128.0D);
   private Path currentPath;
   private Vec3d targetLocation;

   public LandingApproachPhase(EnderDragonEntity p_i46789_1_) {
      super(p_i46789_1_);
   }

   public PhaseType<LandingApproachPhase> getPhase() {
      return PhaseType.LANDING_APPROACH;
   }

   public void begin() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   public void doServerTick() {
      double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
         this.findNewTarget();
      }

   }

   @Nullable
   public Vec3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int i = this.dragon.findClosestNode();
         BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         PlayerEntity playerentity = this.dragon.level.getNearestPlayer(NEAR_EGG_TARGETING, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         int j;
         if (playerentity != null) {
            Vec3d vec3d = (new Vec3d(playerentity.getX(), 0.0D, playerentity.getZ())).normalize();
            j = this.dragon.findClosestNode(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
         } else {
            j = this.dragon.findClosestNode(40.0D, (double)blockpos.getY(), 0.0D);
         }

         PathPoint pathpoint = new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
         this.currentPath = this.dragon.findPath(i, j, pathpoint);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
      if (this.currentPath != null && this.currentPath.isDone()) {
         this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
      }

   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         Vec3d vec3d = this.currentPath.func_186310_f();
         this.currentPath.advance();
         double d0 = vec3d.x;
         double d1 = vec3d.z;

         double d2;
         while(true) {
            d2 = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
            if (!(d2 < vec3d.y)) {
               break;
            }
         }

         this.targetLocation = new Vec3d(d0, d2, d1);
      }

   }
}