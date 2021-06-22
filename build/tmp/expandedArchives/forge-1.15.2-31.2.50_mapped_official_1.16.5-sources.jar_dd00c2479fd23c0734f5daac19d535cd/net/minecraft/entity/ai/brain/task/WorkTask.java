package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WorkTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private long nextOkStartTime;
   private final int maxDistanceFromPoi;

   public WorkTask(MemoryModuleType<GlobalPos> p_i50342_1_, int p_i50342_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i50342_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.memoryType = p_i50342_1_;
      this.maxDistanceFromPoi = p_i50342_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      Optional<GlobalPos> optional = p_212832_2_.getBrain().getMemory(this.memoryType);
      return optional.isPresent() && Objects.equals(p_212832_1_.func_201675_m().func_186058_p(), optional.get().func_218177_a()) && optional.get().pos().closerThan(p_212832_2_.position(), (double)this.maxDistanceFromPoi);
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.nextOkStartTime) {
         Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_212831_2_, 8, 6));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_0_) -> {
            return new WalkTarget(p_220564_0_, 0.4F, 1);
         }));
         this.nextOkStartTime = p_212831_3_ + 180L;
      }

   }
}