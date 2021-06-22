package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LilyPadItem extends BlockItem {
   public LilyPadItem(Block p_i48456_1_, Item.Properties p_i48456_2_) {
      super(p_i48456_1_, p_i48456_2_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      return ActionResultType.PASS;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.pass(itemstack);
      } else {
         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            Direction direction = blockraytraceresult.getDirection();
            if (!p_77659_1_.mayInteract(p_77659_2_, blockpos) || !p_77659_2_.mayUseItemAt(blockpos.relative(direction), direction, itemstack)) {
               return ActionResult.fail(itemstack);
            }

            BlockPos blockpos1 = blockpos.above();
            BlockState blockstate = p_77659_1_.getBlockState(blockpos);
            Material material = blockstate.getMaterial();
            IFluidState ifluidstate = p_77659_1_.getFluidState(blockpos);
            if ((ifluidstate.getType() == Fluids.WATER || material == Material.ICE) && p_77659_1_.isEmptyBlock(blockpos1)) {

               // special case for handling block placement with water lilies
               net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(p_77659_1_, blockpos1);
               p_77659_1_.setBlock(blockpos1, Blocks.LILY_PAD.defaultBlockState(), 11);
               if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(p_77659_2_, blocksnapshot, net.minecraft.util.Direction.UP)) {
                  blocksnapshot.restore(true, false);
                  return ActionResult.fail(itemstack);
               }

               if (p_77659_2_ instanceof ServerPlayerEntity) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)p_77659_2_, blockpos1, itemstack);
               }

               if (!p_77659_2_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
               p_77659_1_.playSound(p_77659_2_, blockpos, SoundEvents.LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return ActionResult.success(itemstack);
            }
         }

         return ActionResult.fail(itemstack);
      }
   }
}
