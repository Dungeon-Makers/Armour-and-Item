package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
   public WaterAvoidingRandomFlyingGoal(CreatureEntity p_i47413_1_, double p_i47413_2_) {
      super(p_i47413_1_, p_i47413_2_);
   }

   @Nullable
   protected Vec3d getPosition() {
      Vec3d vec3d = null;
      if (this.mob.isInWater()) {
         vec3d = RandomPositionGenerator.getLandPos(this.mob, 15, 15);
      }

      if (this.mob.getRandom().nextFloat() >= this.probability) {
         vec3d = this.getTreePos();
      }

      return vec3d == null ? super.getPosition() : vec3d;
   }

   @Nullable
   private Vec3d getTreePos() {
      BlockPos blockpos = new BlockPos(this.mob);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
      Iterable<BlockPos> iterable = BlockPos.betweenClosed(MathHelper.floor(this.mob.getX() - 3.0D), MathHelper.floor(this.mob.getY() - 6.0D), MathHelper.floor(this.mob.getZ() - 3.0D), MathHelper.floor(this.mob.getX() + 3.0D), MathHelper.floor(this.mob.getY() + 6.0D), MathHelper.floor(this.mob.getZ() + 3.0D));
      Iterator iterator = iterable.iterator();

      BlockPos blockpos1;
      while(true) {
         if (!iterator.hasNext()) {
            return null;
         }

         blockpos1 = (BlockPos)iterator.next();
         if (!blockpos.equals(blockpos1)) {
            Block block = this.mob.level.getBlockState(blockpos$mutable1.set(blockpos1).move(Direction.DOWN)).getBlock();
            boolean flag = block instanceof LeavesBlock || block.is(BlockTags.LOGS);
            if (flag && this.mob.level.isEmptyBlock(blockpos1) && this.mob.level.isEmptyBlock(blockpos$mutable.set(blockpos1).move(Direction.UP))) {
               break;
            }
         }
      }

      return new Vec3d(blockpos1);
   }
}