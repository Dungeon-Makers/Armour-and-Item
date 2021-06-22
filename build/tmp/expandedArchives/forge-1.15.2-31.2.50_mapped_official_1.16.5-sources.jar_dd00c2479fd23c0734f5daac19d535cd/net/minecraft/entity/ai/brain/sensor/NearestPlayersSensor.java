package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

public class NearestPlayersSensor extends Sensor<LivingEntity> {
   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      List<PlayerEntity> list = p_212872_1_.players().stream().filter(EntityPredicates.NO_SPECTATORS).filter((p_220979_1_) -> {
         return p_212872_2_.distanceToSqr(p_220979_1_) < 256.0D;
      }).sorted(Comparator.comparingDouble(p_212872_2_::distanceToSqr)).collect(Collectors.toList());
      Brain<?> brain = p_212872_2_.getBrain();
      brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, list);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list.stream().filter(p_212872_2_::canSee).findFirst());
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER);
   }
}