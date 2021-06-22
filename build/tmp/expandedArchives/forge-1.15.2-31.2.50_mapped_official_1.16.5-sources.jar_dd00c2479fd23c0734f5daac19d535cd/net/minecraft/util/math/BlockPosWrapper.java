package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;

public class BlockPosWrapper implements IPosWrapper {
   private final BlockPos blockPos;
   private final Vec3d centerPosition;

   public BlockPosWrapper(BlockPos p_i50371_1_) {
      this.blockPos = p_i50371_1_;
      this.centerPosition = new Vec3d((double)p_i50371_1_.getX() + 0.5D, (double)p_i50371_1_.getY() + 0.5D, (double)p_i50371_1_.getZ() + 0.5D);
   }

   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   public Vec3d currentPosition() {
      return this.centerPosition;
   }

   public boolean isVisibleBy(LivingEntity p_220610_1_) {
      return true;
   }

   public String toString() {
      return "BlockPosWrapper{pos=" + this.blockPos + ", lookAt=" + this.centerPosition + '}';
   }
}