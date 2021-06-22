package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class IceBlock extends BreakableBlock {
   public IceBlock(Block.Properties p_i48375_1_) {
      super(p_i48375_1_);
   }

   public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.playerDestroy(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) == 0) {
         if (p_180657_1_.dimension.func_177500_n()) {
            p_180657_1_.removeBlock(p_180657_3_, false);
            return;
         }

         Material material = p_180657_1_.getBlockState(p_180657_3_.below()).getMaterial();
         if (material.blocksMotion() || material.isLiquid()) {
            p_180657_1_.setBlockAndUpdate(p_180657_3_, Blocks.WATER.defaultBlockState());
         }
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.getBrightness(LightType.BLOCK, p_225534_3_) > 11 - p_225534_1_.getLightBlock(p_225534_2_, p_225534_3_)) {
         this.melt(p_225534_1_, p_225534_2_, p_225534_3_);
      }

   }

   protected void melt(BlockState p_196454_1_, World p_196454_2_, BlockPos p_196454_3_) {
      if (p_196454_2_.dimension.func_177500_n()) {
         p_196454_2_.removeBlock(p_196454_3_, false);
      } else {
         p_196454_2_.setBlockAndUpdate(p_196454_3_, Blocks.WATER.defaultBlockState());
         p_196454_2_.neighborChanged(p_196454_3_, Blocks.WATER, p_196454_3_);
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }

   public boolean func_220067_a(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_4_ == EntityType.POLAR_BEAR;
   }
}