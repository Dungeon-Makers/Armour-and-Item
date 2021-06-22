package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties p_i48499_1_) {
      super(p_i48499_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      boolean flag = false;
      if (blockstate.getBlock() == Blocks.CAMPFIRE) {
         if (!blockstate.getValue(CampfireBlock.LIT) && !blockstate.getValue(CampfireBlock.WATERLOGGED)) {
            this.playSound(world, blockpos);
            world.setBlockAndUpdate(blockpos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(true)));
            flag = true;
         }
      } else {
         blockpos = blockpos.relative(p_195939_1_.getClickedFace());
         if (world.getBlockState(blockpos).isAir()) {
            this.playSound(world, blockpos);
            world.setBlockAndUpdate(blockpos, ((FireBlock)Blocks.FIRE).getStateForPlacement(world, blockpos));
            flag = true;
         }
      }

      if (flag) {
         p_195939_1_.getItemInHand().shrink(1);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.FAIL;
      }
   }

   private void playSound(World p_219995_1_, BlockPos p_219995_2_) {
      p_219995_1_.playSound((PlayerEntity)null, p_219995_2_, SoundEvents.FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}