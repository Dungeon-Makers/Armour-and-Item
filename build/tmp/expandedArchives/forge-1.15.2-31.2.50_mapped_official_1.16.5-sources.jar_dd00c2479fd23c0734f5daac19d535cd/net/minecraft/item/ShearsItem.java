package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties p_i48471_1_) {
      super(p_i48471_1_);
   }

   public boolean mineBlock(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if (!p_179218_2_.isClientSide) {
         p_179218_1_.hurtAndBreak(1, p_179218_5_, (p_220036_0_) -> {
            p_220036_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
         });
      }

      Block block = p_179218_3_.getBlock();
      return !p_179218_3_.is(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.is(BlockTags.WOOL) ? super.mineBlock(p_179218_1_, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_) : true;
   }

   public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
      Block block = p_150897_1_.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      Block block = p_150893_2_.getBlock();
      if (block != Blocks.COBWEB && !p_150893_2_.is(BlockTags.LEAVES)) {
         return block.is(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(p_150893_1_, p_150893_2_);
      } else {
         return 15.0F;
      }
   }

   @SuppressWarnings("deprecation")
   @Override
   public boolean interactLivingEntity(ItemStack stack, net.minecraft.entity.player.PlayerEntity playerIn, LivingEntity entity, net.minecraft.util.Hand hand) {
      if (entity.level.isClientSide) return false;
      if (entity instanceof net.minecraftforge.common.IShearable) {
         net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
         BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
         if (target.isShearable(stack, entity.level, pos)) {
            java.util.List<ItemStack> drops = target.onSheared(stack, entity.level, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.enchantment.Enchantments.BLOCK_FORTUNE, stack));
            java.util.Random rand = new java.util.Random();
            drops.forEach(d -> {
               net.minecraft.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
               ent.setDeltaMovement(ent.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
            });
            stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(hand));
         }
         return true;
      }
      return false;
   }
}
