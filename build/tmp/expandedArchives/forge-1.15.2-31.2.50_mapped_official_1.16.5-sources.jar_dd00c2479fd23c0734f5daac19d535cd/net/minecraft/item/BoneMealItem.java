package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoneMealItem extends Item {
   public BoneMealItem(Item.Properties p_i50055_1_) {
      super(p_i50055_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockPos blockpos1 = blockpos.relative(p_195939_1_.getClickedFace());
      if (applyBonemeal(p_195939_1_.getItemInHand(), world, blockpos, p_195939_1_.getPlayer())) {
         if (!world.isClientSide) {
            world.levelEvent(2005, blockpos, 0);
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockState blockstate = world.getBlockState(blockpos);
         boolean flag = blockstate.isFaceSturdy(world, blockpos, p_195939_1_.getClickedFace());
         if (flag && growWaterPlant(p_195939_1_.getItemInHand(), world, blockpos1, p_195939_1_.getClickedFace())) {
            if (!world.isClientSide) {
               world.levelEvent(2005, blockpos1, 0);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   @Deprecated //Forge: Use Player/Hand version
   public static boolean growCrop(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_) {
      if (p_195966_1_ instanceof net.minecraft.world.server.ServerWorld)
         return applyBonemeal(p_195966_0_, p_195966_1_, p_195966_2_, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.world.server.ServerWorld)p_195966_1_));
      return false;
   }

   public static boolean applyBonemeal(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_, net.minecraft.entity.player.PlayerEntity player) {
      BlockState blockstate = p_195966_1_.getBlockState(p_195966_2_);
      int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, p_195966_1_, p_195966_2_, blockstate, p_195966_0_);
      if (hook != 0) return hook > 0;
      if (blockstate.getBlock() instanceof IGrowable) {
         IGrowable igrowable = (IGrowable)blockstate.getBlock();
         if (igrowable.isValidBonemealTarget(p_195966_1_, p_195966_2_, blockstate, p_195966_1_.isClientSide)) {
            if (p_195966_1_ instanceof ServerWorld) {
               if (igrowable.isBonemealSuccess(p_195966_1_, p_195966_1_.random, p_195966_2_, blockstate)) {
                  igrowable.performBonemeal((ServerWorld)p_195966_1_, p_195966_1_.random, p_195966_2_, blockstate);
               }

               p_195966_0_.shrink(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean growWaterPlant(ItemStack p_203173_0_, World p_203173_1_, BlockPos p_203173_2_, @Nullable Direction p_203173_3_) {
      if (p_203173_1_.getBlockState(p_203173_2_).getBlock() == Blocks.WATER && p_203173_1_.getFluidState(p_203173_2_).getAmount() == 8) {
         if (!(p_203173_1_ instanceof ServerWorld)) {
            return true;
         } else {
            label80:
            for(int i = 0; i < 128; ++i) {
               BlockPos blockpos = p_203173_2_;
               Biome biome = p_203173_1_.getBiome(p_203173_2_);
               BlockState blockstate = Blocks.SEAGRASS.defaultBlockState();

               for(int j = 0; j < i / 16; ++j) {
                  blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  biome = p_203173_1_.getBiome(blockpos);
                  if (p_203173_1_.getBlockState(blockpos).func_224756_o(p_203173_1_, blockpos)) {
                     continue label80;
                  }
               }

               // FORGE: Use BiomeDictionary here to allow modded warm ocean biomes to spawn coral from bonemeal
               if (net.minecraftforge.common.BiomeDictionary.hasType(biome, net.minecraftforge.common.BiomeDictionary.Type.OCEAN)
                       && net.minecraftforge.common.BiomeDictionary.hasType(biome, net.minecraftforge.common.BiomeDictionary.Type.HOT)) {
                  if (i == 0 && p_203173_3_ != null && p_203173_3_.getAxis().isHorizontal()) {
                     blockstate = BlockTags.WALL_CORALS.getRandomElement(p_203173_1_.random).defaultBlockState().setValue(DeadCoralWallFanBlock.FACING, p_203173_3_);
                  } else if (random.nextInt(4) == 0) {
                     blockstate = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random).defaultBlockState();
                  }
               }

               if (blockstate.getBlock().is(BlockTags.WALL_CORALS)) {
                  for(int k = 0; !blockstate.canSurvive(p_203173_1_, blockpos) && k < 4; ++k) {
                     blockstate = blockstate.setValue(DeadCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                  }
               }

               if (blockstate.canSurvive(p_203173_1_, blockpos)) {
                  BlockState blockstate1 = p_203173_1_.getBlockState(blockpos);
                  if (blockstate1.getBlock() == Blocks.WATER && p_203173_1_.getFluidState(blockpos).getAmount() == 8) {
                     p_203173_1_.setBlock(blockpos, blockstate, 3);
                  } else if (blockstate1.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
                     ((IGrowable)Blocks.SEAGRASS).performBonemeal((ServerWorld)p_203173_1_, random, blockpos, blockstate1);
                  }
               }
            }

            p_203173_0_.shrink(1);
            return true;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addGrowthParticles(IWorld p_195965_0_, BlockPos p_195965_1_, int p_195965_2_) {
      if (p_195965_2_ == 0) {
         p_195965_2_ = 15;
      }

      BlockState blockstate = p_195965_0_.getBlockState(p_195965_1_);
      if (!blockstate.isAir(p_195965_0_, p_195965_1_)) {
         for(int i = 0; i < p_195965_2_; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            p_195965_0_.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)((float)p_195965_1_.getX() + random.nextFloat()), (double)p_195965_1_.getY() + (double)random.nextFloat() * blockstate.getShape(p_195965_0_, p_195965_1_).max(Direction.Axis.Y), (double)((float)p_195965_1_.getZ() + random.nextFloat()), d0, d1, d2);
         }

      }
   }
}
