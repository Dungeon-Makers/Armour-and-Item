package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeavesBlock extends Block implements net.minecraftforge.common.IShearable {
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

   public LeavesBlock(Block.Properties p_i48370_1_) {
      super(p_i48370_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(7)).setValue(PERSISTENT, Boolean.valueOf(false)));
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return p_149653_1_.getValue(DISTANCE) == 7 && !p_149653_1_.getValue(PERSISTENT);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (!p_225542_1_.getValue(PERSISTENT) && p_225542_1_.getValue(DISTANCE) == 7) {
         dropResources(p_225542_1_, p_225542_2_, p_225542_3_);
         p_225542_2_.removeBlock(p_225542_3_, false);
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      p_225534_2_.setBlock(p_225534_3_, updateDistance(p_225534_1_, p_225534_2_, p_225534_3_), 3);
   }

   public int getLightBlock(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return 1;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      int i = getDistanceAt(p_196271_3_) + 1;
      if (i != 1 || p_196271_1_.getValue(DISTANCE) != i) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return p_196271_1_;
   }

   private static BlockState updateDistance(BlockState p_208493_0_, IWorld p_208493_1_, BlockPos p_208493_2_) {
      int i = 7;

      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
         for(Direction direction : Direction.values()) {
            blockpos$pooledmutable.set(p_208493_2_).move(direction);
            i = Math.min(i, getDistanceAt(p_208493_1_.getBlockState(blockpos$pooledmutable)) + 1);
            if (i == 1) {
               break;
            }
         }
      }

      return p_208493_0_.setValue(DISTANCE, Integer.valueOf(i));
   }

   private static int getDistanceAt(BlockState p_208492_0_) {
      if (BlockTags.LOGS.func_199685_a_(p_208492_0_.getBlock())) {
         return 0;
      } else {
         return p_208492_0_.getBlock() instanceof LeavesBlock ? p_208492_0_.getValue(DISTANCE) : 7;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_2_.isRainingAt(p_180655_3_.above())) {
         if (p_180655_4_.nextInt(15) == 1) {
            BlockPos blockpos = p_180655_3_.below();
            BlockState blockstate = p_180655_2_.getBlockState(blockpos);
            if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(p_180655_2_, blockpos, Direction.UP)) {
               double d0 = (double)((float)p_180655_3_.getX() + p_180655_4_.nextFloat());
               double d1 = (double)p_180655_3_.getY() - 0.05D;
               double d2 = (double)((float)p_180655_3_.getZ() + p_180655_4_.nextFloat());
               p_180655_2_.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }

   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return false;
   }

   public boolean func_220067_a(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_4_ == EntityType.OCELOT || p_220067_4_ == EntityType.PARROT;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(DISTANCE, PERSISTENT);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return updateDistance(this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)), p_196258_1_.getLevel(), p_196258_1_.getClickedPos());
   }
}
