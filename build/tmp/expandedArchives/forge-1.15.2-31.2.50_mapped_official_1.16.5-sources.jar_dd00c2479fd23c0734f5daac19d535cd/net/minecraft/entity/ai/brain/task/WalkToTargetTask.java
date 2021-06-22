package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WalkToTargetTask extends Task<MobEntity> {
   @Nullable
   private Path path;
   @Nullable
   private BlockPos lastTargetPos;
   private float speedModifier;
   private int field_220491_d;

   public WalkToTargetTask(int p_i50356_1_) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50356_1_);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      Brain<?> brain = p_212832_2_.getBrain();
      WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
      if (!this.reachedTarget(p_212832_2_, walktarget) && this.tryComputePath(p_212832_2_, walktarget, p_212832_1_.getGameTime())) {
         this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
         return true;
      } else {
         brain.eraseMemory(MemoryModuleType.WALK_TARGET);
         return false;
      }
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      if (this.path != null && this.lastTargetPos != null) {
         Optional<WalkTarget> optional = p_212834_2_.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigator pathnavigator = p_212834_2_.getNavigation();
         return !pathnavigator.isDone() && optional.isPresent() && !this.reachedTarget(p_212834_2_, optional.get());
      } else {
         return false;
      }
   }

   protected void stop(ServerWorld p_212835_1_, MobEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getNavigation().stop();
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.PATH);
      this.path = null;
   }

   protected void start(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().setMemory(MemoryModuleType.PATH, this.path);
      p_212831_2_.getNavigation().moveTo(this.path, (double)this.speedModifier);
      this.field_220491_d = p_212831_1_.getRandom().nextInt(10);
   }

   protected void tick(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      --this.field_220491_d;
      if (this.field_220491_d <= 0) {
         Path path = p_212833_2_.getNavigation().getPath();
         Brain<?> brain = p_212833_2_.getBrain();
         if (this.path != path) {
            this.path = path;
            brain.setMemory(MemoryModuleType.PATH, path);
         }

         if (path != null && this.lastTargetPos != null) {
            WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
            if (walktarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(p_212833_2_, walktarget, p_212833_1_.getGameTime())) {
               this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
               this.start(p_212833_1_, p_212833_2_, p_212833_3_);
            }

         }
      }
   }

   private boolean tryComputePath(MobEntity p_220487_1_, WalkTarget p_220487_2_, long p_220487_3_) {
      BlockPos blockpos = p_220487_2_.getTarget().currentBlockPosition();
      this.path = p_220487_1_.getNavigation().createPath(blockpos, 0);
      this.speedModifier = p_220487_2_.getSpeedModifier();
      if (!this.reachedTarget(p_220487_1_, p_220487_2_)) {
         Brain<?> brain = p_220487_1_.getBrain();
         boolean flag = this.path != null && this.path.canReach();
         if (flag) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
         } else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_220487_3_);
         }

         if (this.path != null) {
            return true;
         }

         Vec3d vec3d = RandomPositionGenerator.getPosTowards((CreatureEntity)p_220487_1_, 10, 7, new Vec3d(blockpos));
         if (vec3d != null) {
            this.path = p_220487_1_.getNavigation().createPath(vec3d.x, vec3d.y, vec3d.z, 0);
            return this.path != null;
         }
      }

      return false;
   }

   private boolean reachedTarget(MobEntity p_220486_1_, WalkTarget p_220486_2_) {
      return p_220486_2_.getTarget().currentBlockPosition().distManhattan(new BlockPos(p_220486_1_)) <= p_220486_2_.getCloseEnoughDist();
   }
}