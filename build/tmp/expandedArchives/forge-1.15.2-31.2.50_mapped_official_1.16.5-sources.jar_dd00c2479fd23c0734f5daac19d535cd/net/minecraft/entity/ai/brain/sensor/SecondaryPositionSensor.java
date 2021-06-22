package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class SecondaryPositionSensor extends Sensor<VillagerEntity> {
   public SecondaryPositionSensor() {
      super(40);
   }

   protected void doTick(ServerWorld p_212872_1_, VillagerEntity p_212872_2_) {
      DimensionType dimensiontype = p_212872_1_.func_201675_m().func_186058_p();
      BlockPos blockpos = new BlockPos(p_212872_2_);
      List<GlobalPos> list = Lists.newArrayList();
      int i = 4;

      for(int j = -4; j <= 4; ++j) {
         for(int k = -2; k <= 2; ++k) {
            for(int l = -4; l <= 4; ++l) {
               BlockPos blockpos1 = blockpos.offset(j, k, l);
               if (p_212872_2_.getVillagerData().getProfession().getSecondaryPoi().contains(p_212872_1_.getBlockState(blockpos1).getBlock())) {
                  list.add(GlobalPos.func_218179_a(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = p_212872_2_.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
      } else {
         brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}