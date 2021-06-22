package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos relativePos;
   protected boolean replaceClicked = true;

   public BlockItemUseContext(ItemUseContext p_i47813_1_) {
      this(p_i47813_1_.getLevel(), p_i47813_1_.getPlayer(), p_i47813_1_.getHand(), p_i47813_1_.getItemInHand(), p_i47813_1_.hitResult);
   }

   protected BlockItemUseContext(World p_i50056_1_, @Nullable PlayerEntity p_i50056_2_, Hand p_i50056_3_, ItemStack p_i50056_4_, BlockRayTraceResult p_i50056_5_) {
      super(p_i50056_1_, p_i50056_2_, p_i50056_3_, p_i50056_4_, p_i50056_5_);
      this.relativePos = p_i50056_5_.getBlockPos().relative(p_i50056_5_.getDirection());
      this.replaceClicked = p_i50056_1_.getBlockState(p_i50056_5_.getBlockPos()).canBeReplaced(this);
   }

   public static BlockItemUseContext at(BlockItemUseContext p_221536_0_, BlockPos p_221536_1_, Direction p_221536_2_) {
      return new BlockItemUseContext(p_221536_0_.getLevel(), p_221536_0_.getPlayer(), p_221536_0_.getHand(), p_221536_0_.getItemInHand(), new BlockRayTraceResult(new Vec3d((double)p_221536_1_.getX() + 0.5D + (double)p_221536_2_.getStepX() * 0.5D, (double)p_221536_1_.getY() + 0.5D + (double)p_221536_2_.getStepY() * 0.5D, (double)p_221536_1_.getZ() + 0.5D + (double)p_221536_2_.getStepZ() * 0.5D), p_221536_2_, p_221536_1_, false));
   }

   public BlockPos getClickedPos() {
      return this.replaceClicked ? super.getClickedPos() : this.relativePos;
   }

   public boolean canPlace() {
      return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.replaceClicked;
   }

   public Direction getNearestLookingDirection() {
      return Direction.orderedByNearest(this.player)[0];
   }

   public Direction[] getNearestLookingDirections() {
      Direction[] adirection = Direction.orderedByNearest(this.player);
      if (this.replaceClicked) {
         return adirection;
      } else {
         Direction direction = this.getClickedFace();

         int i;
         for(i = 0; i < adirection.length && adirection[i] != direction.getOpposite(); ++i) {
            ;
         }

         if (i > 0) {
            System.arraycopy(adirection, 0, adirection, 1, i);
            adirection[0] = direction.getOpposite();
         }

         return adirection;
      }
   }
}