package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SleepAtHomeTask extends Task<LivingEntity> {
   private long nextOkStartTime;

   public SleepAtHomeTask() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      if (p_212832_2_.isPassenger()) {
         return false;
      } else {
         Brain<?> brain = p_212832_2_.getBrain();
         GlobalPos globalpos = brain.getMemory(MemoryModuleType.HOME).get();
         if (!Objects.equals(p_212832_1_.func_201675_m().func_186058_p(), globalpos.func_218177_a())) {
            return false;
         } else {
            Optional<LongSerializable> optional = brain.getMemory(MemoryModuleType.LAST_WOKEN);
            if (optional.isPresent() && p_212832_1_.getGameTime() - optional.get().func_223461_a() < 100L) {
               return false;
            } else {
               BlockState blockstate = p_212832_1_.getBlockState(globalpos.pos());
               return globalpos.pos().closerThan(p_212832_2_.position(), 2.0D) && blockstate.getBlock().is(BlockTags.BEDS) && !blockstate.getValue(BedBlock.OCCUPIED);
            }
         }
      }
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      Optional<GlobalPos> optional = p_212834_2_.getBrain().getMemory(MemoryModuleType.HOME);
      if (!optional.isPresent()) {
         return false;
      } else {
         BlockPos blockpos = optional.get().pos();
         return p_212834_2_.getBrain().isActive(Activity.REST) && p_212834_2_.getY() > (double)blockpos.getY() + 0.4D && blockpos.closerThan(p_212834_2_.position(), 1.14D);
      }
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.nextOkStartTime) {
         p_212831_2_.getBrain().getMemory(MemoryModuleType.DOORS_TO_CLOSE).ifPresent((p_225459_2_) -> {
            InteractWithDoorTask.func_225449_a(p_212831_1_, ImmutableList.of(), 0, p_212831_2_, p_212831_2_.getBrain());
         });
         p_212831_2_.startSleeping(p_212831_2_.getBrain().getMemory(MemoryModuleType.HOME).get().pos());
      }

   }

   protected boolean timedOut(long p_220383_1_) {
      return false;
   }

   protected void stop(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      if (p_212835_2_.isSleeping()) {
         p_212835_2_.stopSleeping();
         this.nextOkStartTime = p_212835_3_ + 40L;
      }

   }
}