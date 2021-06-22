package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
   protected void doTick(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      DimensionType dimensiontype = p_212872_1_.func_201675_m().func_186058_p();
      BlockPos blockpos = new BlockPos(p_212872_2_);
      List<GlobalPos> list = Lists.newArrayList();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos1 = blockpos.offset(i, j, k);
               if (p_212872_1_.getBlockState(blockpos1).is(BlockTags.WOODEN_DOORS)) {
                  list.add(GlobalPos.func_218179_a(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = p_212872_2_.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(MemoryModuleType.INTERACTABLE_DOORS, list);
      } else {
         brain.eraseMemory(MemoryModuleType.INTERACTABLE_DOORS);
      }

   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
   }
}