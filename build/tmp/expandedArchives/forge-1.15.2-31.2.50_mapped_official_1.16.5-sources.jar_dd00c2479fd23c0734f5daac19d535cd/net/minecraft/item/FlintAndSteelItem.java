package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties p_i48493_1_) {
      super(p_i48493_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      PlayerEntity playerentity = p_195939_1_.getPlayer();
      IWorld iworld = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = iworld.getBlockState(blockpos);
      if (func_219997_a(blockstate)) {
         iworld.playSound(playerentity, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         iworld.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
         if (playerentity != null) {
            p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_219999_1_) -> {
               p_219999_1_.broadcastBreakEvent(p_195939_1_.getHand());
            });
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockPos blockpos1 = blockpos.relative(p_195939_1_.getClickedFace());
         if (func_219996_a(iworld.getBlockState(blockpos1), iworld, blockpos1)) {
            iworld.playSound(playerentity, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            BlockState blockstate1 = ((FireBlock)Blocks.FIRE).getStateForPlacement(iworld, blockpos1);
            iworld.setBlock(blockpos1, blockstate1, 11);
            ItemStack itemstack = p_195939_1_.getItemInHand();
            if (playerentity instanceof ServerPlayerEntity) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
               itemstack.hurtAndBreak(1, playerentity, (p_219998_1_) -> {
                  p_219998_1_.broadcastBreakEvent(p_195939_1_.getHand());
               });
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   public static boolean func_219997_a(BlockState p_219997_0_) {
      return p_219997_0_.getBlock() == Blocks.CAMPFIRE && !p_219997_0_.getValue(BlockStateProperties.WATERLOGGED) && !p_219997_0_.getValue(BlockStateProperties.LIT);
   }

   public static boolean func_219996_a(BlockState p_219996_0_, IWorld p_219996_1_, BlockPos p_219996_2_) {
      BlockState blockstate = ((FireBlock)Blocks.FIRE).getStateForPlacement(p_219996_1_, p_219996_2_);
      boolean flag = false;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos framePos = p_219996_2_.relative(direction);
         if (p_219996_1_.getBlockState(framePos).isPortalFrame(p_219996_1_, framePos) && ((NetherPortalBlock)Blocks.NETHER_PORTAL).func_201816_b(p_219996_1_, p_219996_2_) != null) {
            flag = true;
         }
      }

      return p_219996_0_.isAir() && (blockstate.canSurvive(p_219996_1_, p_219996_2_) || flag);
   }
}
