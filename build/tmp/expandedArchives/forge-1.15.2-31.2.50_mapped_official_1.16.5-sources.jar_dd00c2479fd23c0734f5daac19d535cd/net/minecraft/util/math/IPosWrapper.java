package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;

public interface IPosWrapper {
   BlockPos currentBlockPosition();

   Vec3d currentPosition();

   boolean isVisibleBy(LivingEntity p_220610_1_);
}