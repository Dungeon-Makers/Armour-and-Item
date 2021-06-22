package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SpawnGolemTask extends Task<VillagerEntity> {
   private long lastCheck;

   public SpawnGolemTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (p_212832_1_.getGameTime() - this.lastCheck < 300L) {
         return false;
      } else if (p_212832_1_.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = p_212832_1_.getGameTime();
         GlobalPos globalpos = p_212832_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return Objects.equals(globalpos.func_218177_a(), p_212832_1_.func_201675_m().func_186058_p()) && globalpos.pos().closerThan(p_212832_2_.position(), 1.73D);
      }
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      Brain<VillagerEntity> brain = p_212831_2_.getBrain();
      brain.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, LongSerializable.func_223463_a(p_212831_3_));
      brain.getMemory(MemoryModuleType.JOB_SITE).ifPresent((p_225460_1_) -> {
         brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(p_225460_1_.pos()));
      });
      p_212831_2_.playWorkSound();
      if (p_212831_2_.shouldRestock()) {
         p_212831_2_.restock();
      }

   }
}