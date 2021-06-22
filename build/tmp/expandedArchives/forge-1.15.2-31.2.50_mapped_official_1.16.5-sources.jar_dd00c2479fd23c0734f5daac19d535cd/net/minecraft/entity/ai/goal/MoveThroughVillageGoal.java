package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageGoal extends Goal {
   protected final CreatureEntity mob;
   private final double speedModifier;
   private Path path;
   private BlockPos poiPos;
   private final boolean onlyAtNight;
   private final List<BlockPos> visited = Lists.newArrayList();
   private final int distanceToPoi;
   private final BooleanSupplier canDealWithDoors;

   public MoveThroughVillageGoal(CreatureEntity p_i50324_1_, double p_i50324_2_, boolean p_i50324_4_, int p_i50324_5_, BooleanSupplier p_i50324_6_) {
      this.mob = p_i50324_1_;
      this.speedModifier = p_i50324_2_;
      this.onlyAtNight = p_i50324_4_;
      this.distanceToPoi = p_i50324_5_;
      this.canDealWithDoors = p_i50324_6_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      if (!(p_i50324_1_.getNavigation() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean canUse() {
      this.updateVisited();
      if (this.onlyAtNight && this.mob.level.isDay()) {
         return false;
      } else {
         ServerWorld serverworld = (ServerWorld)this.mob.level;
         BlockPos blockpos = new BlockPos(this.mob);
         if (!serverworld.func_217471_a(blockpos, 6)) {
            return false;
         } else {
            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.mob, 15, 7, (p_220734_3_) -> {
               if (!serverworld.isVillage(p_220734_3_)) {
                  return Double.NEGATIVE_INFINITY;
               } else {
                  Optional<BlockPos> optional1 = serverworld.getPoiManager().find(PointOfInterestType.ALL, this::hasNotVisited, p_220734_3_, 10, PointOfInterestManager.Status.IS_OCCUPIED);
                  return !optional1.isPresent() ? Double.NEGATIVE_INFINITY : -optional1.get().distSqr(blockpos);
               }
            });
            if (vec3d == null) {
               return false;
            } else {
               Optional<BlockPos> optional = serverworld.getPoiManager().find(PointOfInterestType.ALL, this::hasNotVisited, new BlockPos(vec3d), 10, PointOfInterestManager.Status.IS_OCCUPIED);
               if (!optional.isPresent()) {
                  return false;
               } else {
                  this.poiPos = optional.get().immutable();
                  GroundPathNavigator groundpathnavigator = (GroundPathNavigator)this.mob.getNavigation();
                  boolean flag = groundpathnavigator.canOpenDoors();
                  groundpathnavigator.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                  this.path = groundpathnavigator.createPath(this.poiPos, 0);
                  groundpathnavigator.setCanOpenDoors(flag);
                  if (this.path == null) {
                     Vec3d vec3d1 = RandomPositionGenerator.getPosTowards(this.mob, 10, 7, new Vec3d(this.poiPos));
                     if (vec3d1 == null) {
                        return false;
                     }

                     groundpathnavigator.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                     this.path = this.mob.getNavigation().createPath(vec3d1.x, vec3d1.y, vec3d1.z, 0);
                     groundpathnavigator.setCanOpenDoors(flag);
                     if (this.path == null) {
                        return false;
                     }
                  }

                  for(int i = 0; i < this.path.getNodeCount(); ++i) {
                     PathPoint pathpoint = this.path.getNode(i);
                     BlockPos blockpos1 = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
                     if (InteractDoorGoal.func_220695_a(this.mob.level, blockpos1)) {
                        this.path = this.mob.getNavigation().createPath((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z, 0);
                        break;
                     }
                  }

                  return this.path != null;
               }
            }
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.mob.getNavigation().isDone()) {
         return false;
      } else {
         return !this.poiPos.closerThan(this.mob.position(), (double)(this.mob.getBbWidth() + (float)this.distanceToPoi));
      }
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.path, this.speedModifier);
   }

   public void stop() {
      if (this.mob.getNavigation().isDone() || this.poiPos.closerThan(this.mob.position(), (double)this.distanceToPoi)) {
         this.visited.add(this.poiPos);
      }

   }

   private boolean hasNotVisited(BlockPos p_220733_1_) {
      for(BlockPos blockpos : this.visited) {
         if (Objects.equals(p_220733_1_, blockpos)) {
            return false;
         }
      }

      return true;
   }

   private void updateVisited() {
      if (this.visited.size() > 15) {
         this.visited.remove(0);
      }

   }
}