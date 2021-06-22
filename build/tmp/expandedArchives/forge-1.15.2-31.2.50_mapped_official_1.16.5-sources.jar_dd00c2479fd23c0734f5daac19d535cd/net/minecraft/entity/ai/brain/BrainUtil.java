package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class BrainUtil {
   public static void lockGazeAndWalkToEachOther(LivingEntity p_220618_0_, LivingEntity p_220618_1_) {
      lookAtEachOther(p_220618_0_, p_220618_1_);
      setWalkAndLookTargetMemoriesToEachOther(p_220618_0_, p_220618_1_);
   }

   public static boolean entityIsVisible(Brain<?> p_220619_0_, LivingEntity p_220619_1_) {
      return p_220619_0_.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).filter((p_220614_1_) -> {
         return p_220614_1_.contains(p_220619_1_);
      }).isPresent();
   }

   public static boolean targetIsValid(Brain<?> p_220623_0_, MemoryModuleType<? extends LivingEntity> p_220623_1_, EntityType<?> p_220623_2_) {
      return p_220623_0_.getMemory(p_220623_1_).filter((p_220622_1_) -> {
         return p_220622_1_.getType() == p_220623_2_;
      }).filter(LivingEntity::isAlive).filter((p_220615_1_) -> {
         return entityIsVisible(p_220623_0_, p_220615_1_);
      }).isPresent();
   }

   public static void lookAtEachOther(LivingEntity p_220616_0_, LivingEntity p_220616_1_) {
      lookAtEntity(p_220616_0_, p_220616_1_);
      lookAtEntity(p_220616_1_, p_220616_0_);
   }

   public static void lookAtEntity(LivingEntity p_220625_0_, LivingEntity p_220625_1_) {
      p_220625_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220625_1_));
   }

   public static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity p_220626_0_, LivingEntity p_220626_1_) {
      int i = 2;
      func_220621_a(p_220626_0_, p_220626_1_, 2);
      func_220621_a(p_220626_1_, p_220626_0_, 2);
   }

   public static void func_220621_a(LivingEntity p_220621_0_, LivingEntity p_220621_1_, int p_220621_2_) {
      float f = (float)p_220621_0_.getAttribute(SharedMonsterAttributes.field_111263_d).getValue();
      EntityPosWrapper entityposwrapper = new EntityPosWrapper(p_220621_1_);
      WalkTarget walktarget = new WalkTarget(entityposwrapper, f, p_220621_2_);
      p_220621_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, entityposwrapper);
      p_220621_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void func_220624_a(LivingEntity p_220624_0_, ItemStack p_220624_1_, LivingEntity p_220624_2_) {
      double d0 = p_220624_0_.getEyeY() - (double)0.3F;
      ItemEntity itementity = new ItemEntity(p_220624_0_.level, p_220624_0_.getX(), d0, p_220624_0_.getZ(), p_220624_1_);
      BlockPos blockpos = new BlockPos(p_220624_2_);
      BlockPos blockpos1 = new BlockPos(p_220624_0_);
      float f = 0.3F;
      Vec3d vec3d = new Vec3d(blockpos.subtract(blockpos1));
      vec3d = vec3d.normalize().scale((double)0.3F);
      itementity.setDeltaMovement(vec3d);
      itementity.setDefaultPickUpDelay();
      p_220624_0_.level.addFreshEntity(itementity);
   }

   public static SectionPos findSectionClosestToVillage(ServerWorld p_220617_0_, SectionPos p_220617_1_, int p_220617_2_) {
      int i = p_220617_0_.sectionsToVillage(p_220617_1_);
      return SectionPos.cube(p_220617_1_, p_220617_2_).filter((p_220620_2_) -> {
         return p_220617_0_.sectionsToVillage(p_220620_2_) < i;
      }).min(Comparator.comparingInt(p_220617_0_::sectionsToVillage)).orElse(p_220617_1_);
   }
}