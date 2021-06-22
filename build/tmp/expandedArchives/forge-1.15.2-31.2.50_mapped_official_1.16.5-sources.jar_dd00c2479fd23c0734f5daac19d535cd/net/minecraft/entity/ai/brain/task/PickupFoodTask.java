package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class PickupFoodTask extends Task<VillagerEntity> {
   private List<ItemEntity> field_225452_a = Lists.newArrayList();

   public PickupFoodTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      this.field_225452_a = p_212832_1_.getEntitiesOfClass(ItemEntity.class, p_212832_2_.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
      return !this.field_225452_a.isEmpty();
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      ItemEntity itementity = this.field_225452_a.get(p_212831_1_.random.nextInt(this.field_225452_a.size()));
      if (p_212831_2_.func_223717_b(itementity.getItem().getItem())) {
         Vec3d vec3d = itementity.position();
         p_212831_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(new BlockPos(vec3d)));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.5F, 0));
      }

   }
}