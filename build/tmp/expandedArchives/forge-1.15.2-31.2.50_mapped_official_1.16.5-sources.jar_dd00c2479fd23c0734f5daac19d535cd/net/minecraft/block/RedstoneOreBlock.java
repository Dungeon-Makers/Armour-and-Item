package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneOreBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneOreBlock(Block.Properties p_i48345_1_) {
      super(p_i48345_1_);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   public int func_149750_m(BlockState p_149750_1_) {
      return p_149750_1_.getValue(LIT) ? super.func_149750_m(p_149750_1_) : 0;
   }

   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      interact(p_196270_1_, p_196270_2_, p_196270_3_);
      super.attack(p_196270_1_, p_196270_2_, p_196270_3_, p_196270_4_);
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      interact(p_176199_1_.getBlockState(p_176199_2_), p_176199_1_, p_176199_2_);
      super.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         spawnParticles(p_225533_2_, p_225533_3_);
         return ActionResultType.SUCCESS;
      } else {
         interact(p_225533_1_, p_225533_2_, p_225533_3_);
         return ActionResultType.PASS;
      }
   }

   private static void interact(BlockState p_196500_0_, World p_196500_1_, BlockPos p_196500_2_) {
      spawnParticles(p_196500_1_, p_196500_2_);
      if (!p_196500_0_.getValue(LIT)) {
         p_196500_1_.setBlock(p_196500_2_, p_196500_0_.setValue(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(LIT)) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(LIT, Boolean.valueOf(false)), 3);
      }

   }

   public void spawnAfterBreak(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, int fortune, int silktouch) {
      return silktouch == 0 ? 1 + RANDOM.nextInt(5) : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(LIT)) {
         spawnParticles(p_180655_2_, p_180655_3_);
      }

   }

   private static void spawnParticles(World p_180691_0_, BlockPos p_180691_1_) {
      double d0 = 0.5625D;
      Random random = p_180691_0_.random;

      for(Direction direction : Direction.values()) {
         BlockPos blockpos = p_180691_1_.relative(direction);
         if (!p_180691_0_.getBlockState(blockpos).isSolidRender(p_180691_0_, blockpos)) {
            Direction.Axis direction$axis = direction.getAxis();
            double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getStepX() : (double)random.nextFloat();
            double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getStepY() : (double)random.nextFloat();
            double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getStepZ() : (double)random.nextFloat();
            p_180691_0_.addParticle(RedstoneParticleData.REDSTONE, (double)p_180691_1_.getX() + d1, (double)p_180691_1_.getY() + d2, (double)p_180691_1_.getZ() + d3, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }
}
