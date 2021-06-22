package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
   protected static final VoxelShape[] field_196499_B = new VoxelShape[]{Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   private boolean shouldSignal = true;
   private final Set<BlockPos> field_150179_b = Sets.newHashSet();

   public RedstoneWireBlock(Block.Properties p_i48344_1_) {
      super(p_i48344_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE).setValue(POWER, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return field_196499_B[func_185699_x(p_220053_1_)];
   }

   private static int func_185699_x(BlockState p_185699_0_) {
      int i = 0;
      boolean flag = p_185699_0_.getValue(NORTH) != RedstoneSide.NONE;
      boolean flag1 = p_185699_0_.getValue(EAST) != RedstoneSide.NONE;
      boolean flag2 = p_185699_0_.getValue(SOUTH) != RedstoneSide.NONE;
      boolean flag3 = p_185699_0_.getValue(WEST) != RedstoneSide.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << Direction.NORTH.get2DDataValue();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << Direction.EAST.get2DDataValue();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << Direction.SOUTH.get2DDataValue();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << Direction.WEST.get2DDataValue();
      }

      return i;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getLevel();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      return this.defaultBlockState().setValue(WEST, this.getConnectingSide(iblockreader, blockpos, Direction.WEST)).setValue(EAST, this.getConnectingSide(iblockreader, blockpos, Direction.EAST)).setValue(NORTH, this.getConnectingSide(iblockreader, blockpos, Direction.NORTH)).setValue(SOUTH, this.getConnectingSide(iblockreader, blockpos, Direction.SOUTH));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN) {
         return p_196271_1_;
      } else {
         return p_196271_2_ == Direction.UP ? p_196271_1_.setValue(WEST, this.getConnectingSide(p_196271_4_, p_196271_5_, Direction.WEST)).setValue(EAST, this.getConnectingSide(p_196271_4_, p_196271_5_, Direction.EAST)).setValue(NORTH, this.getConnectingSide(p_196271_4_, p_196271_5_, Direction.NORTH)).setValue(SOUTH, this.getConnectingSide(p_196271_4_, p_196271_5_, Direction.SOUTH)) : p_196271_1_.setValue(PROPERTY_BY_DIRECTION.get(p_196271_2_), this.getConnectingSide(p_196271_4_, p_196271_5_, p_196271_2_));
      }
   }

   public void updateIndirectNeighbourShapes(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = p_196248_1_.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneside != RedstoneSide.NONE && p_196248_2_.getBlockState(blockpos$pooledmutable.set(p_196248_3_).move(direction)).getBlock() != this) {
               blockpos$pooledmutable.move(Direction.DOWN);
               BlockState blockstate = p_196248_2_.getBlockState(blockpos$pooledmutable);
               if (blockstate.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos = blockpos$pooledmutable.relative(direction.getOpposite());
                  BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), p_196248_2_.getBlockState(blockpos), p_196248_2_, blockpos$pooledmutable, blockpos);
                  updateOrDestroy(blockstate, blockstate1, p_196248_2_, blockpos$pooledmutable, p_196248_4_);
               }

               blockpos$pooledmutable.set(p_196248_3_).move(direction).move(Direction.UP);
               BlockState blockstate3 = p_196248_2_.getBlockState(blockpos$pooledmutable);
               if (blockstate3.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos1 = blockpos$pooledmutable.relative(direction.getOpposite());
                  BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), p_196248_2_.getBlockState(blockpos1), p_196248_2_, blockpos$pooledmutable, blockpos1);
                  updateOrDestroy(blockstate3, blockstate2, p_196248_2_, blockpos$pooledmutable, p_196248_4_);
               }
            }
         }
      }

   }

   private RedstoneSide getConnectingSide(IBlockReader p_208074_1_, BlockPos p_208074_2_, Direction p_208074_3_) {
      BlockPos blockpos = p_208074_2_.relative(p_208074_3_);
      BlockState blockstate = p_208074_1_.getBlockState(blockpos);
      BlockPos blockpos1 = p_208074_2_.above();
      BlockState blockstate1 = p_208074_1_.getBlockState(blockpos1);
      if (!blockstate1.isRedstoneConductor(p_208074_1_, blockpos1)) {
         boolean flag = blockstate.isFaceSturdy(p_208074_1_, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
         if (flag && canConnectTo(p_208074_1_.getBlockState(blockpos.above()), p_208074_1_, blockpos.above(), null)) {
            if (blockstate.func_224756_o(p_208074_1_, blockpos)) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !canConnectTo(blockstate, p_208074_1_, blockpos, p_208074_3_) && (blockstate.isRedstoneConductor(p_208074_1_, blockpos) || !canConnectTo(p_208074_1_.getBlockState(blockpos.below()), p_208074_1_, blockpos.below(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      return blockstate.isFaceSturdy(p_196260_2_, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
   }

   private BlockState func_176338_e(World p_176338_1_, BlockPos p_176338_2_, BlockState p_176338_3_) {
      p_176338_3_ = this.func_212568_b(p_176338_1_, p_176338_2_, p_176338_3_);
      List<BlockPos> list = Lists.newArrayList(this.field_150179_b);
      this.field_150179_b.clear();

      for(BlockPos blockpos : list) {
         p_176338_1_.updateNeighborsAt(blockpos, this);
      }

      return p_176338_3_;
   }

   private BlockState func_212568_b(World p_212568_1_, BlockPos p_212568_2_, BlockState p_212568_3_) {
      BlockState blockstate = p_212568_3_;
      int i = p_212568_3_.getValue(POWER);
      this.shouldSignal = false;
      int j = p_212568_1_.getBestNeighborSignal(p_212568_2_);
      this.shouldSignal = true;
      int k = 0;
      if (j < 15) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = p_212568_2_.relative(direction);
            BlockState blockstate1 = p_212568_1_.getBlockState(blockpos);
            k = this.func_212567_a(k, blockstate1);
            BlockPos blockpos1 = p_212568_2_.above();
            if (blockstate1.isRedstoneConductor(p_212568_1_, blockpos) && !p_212568_1_.getBlockState(blockpos1).isRedstoneConductor(p_212568_1_, blockpos1)) {
               k = this.func_212567_a(k, p_212568_1_.getBlockState(blockpos.above()));
            } else if (!blockstate1.isRedstoneConductor(p_212568_1_, blockpos)) {
               k = this.func_212567_a(k, p_212568_1_.getBlockState(blockpos.below()));
            }
         }
      }

      int l = k - 1;
      if (j > l) {
         l = j;
      }

      if (i != l) {
         p_212568_3_ = p_212568_3_.setValue(POWER, Integer.valueOf(l));
         if (p_212568_1_.getBlockState(p_212568_2_) == blockstate) {
            p_212568_1_.setBlock(p_212568_2_, p_212568_3_, 2);
         }

         this.field_150179_b.add(p_212568_2_);

         for(Direction direction1 : Direction.values()) {
            this.field_150179_b.add(p_212568_2_.relative(direction1));
         }
      }

      return p_212568_3_;
   }

   private void checkCornerChangeAt(World p_176344_1_, BlockPos p_176344_2_) {
      if (p_176344_1_.getBlockState(p_176344_2_).getBlock() == this) {
         p_176344_1_.updateNeighborsAt(p_176344_2_, this);

         for(Direction direction : Direction.values()) {
            p_176344_1_.updateNeighborsAt(p_176344_2_.relative(direction), this);
         }

      }
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && !p_220082_2_.isClientSide) {
         this.func_176338_e(p_220082_2_, p_220082_3_, p_220082_1_);

         for(Direction direction : Direction.Plane.VERTICAL) {
            p_220082_2_.updateNeighborsAt(p_220082_3_.relative(direction), this);
         }

         for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(p_220082_2_, p_220082_3_.relative(direction1));
         }

         for(Direction direction2 : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = p_220082_3_.relative(direction2);
            if (p_220082_2_.getBlockState(blockpos).isRedstoneConductor(p_220082_2_, blockpos)) {
               this.checkCornerChangeAt(p_220082_2_, blockpos.above());
            } else {
               this.checkCornerChangeAt(p_220082_2_, blockpos.below());
            }
         }

      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (!p_196243_2_.isClientSide) {
            for(Direction direction : Direction.values()) {
               p_196243_2_.updateNeighborsAt(p_196243_3_.relative(direction), this);
            }

            this.func_176338_e(p_196243_2_, p_196243_3_, p_196243_1_);

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
               this.checkCornerChangeAt(p_196243_2_, p_196243_3_.relative(direction1));
            }

            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
               BlockPos blockpos = p_196243_3_.relative(direction2);
               if (p_196243_2_.getBlockState(blockpos).isRedstoneConductor(p_196243_2_, blockpos)) {
                  this.checkCornerChangeAt(p_196243_2_, blockpos.above());
               } else {
                  this.checkCornerChangeAt(p_196243_2_, blockpos.below());
               }
            }

         }
      }
   }

   private int func_212567_a(int p_212567_1_, BlockState p_212567_2_) {
      if (p_212567_2_.getBlock() != this) {
         return p_212567_1_;
      } else {
         int i = p_212567_2_.getValue(POWER);
         return i > p_212567_1_ ? i : p_212567_1_;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         if (p_220069_1_.canSurvive(p_220069_2_, p_220069_3_)) {
            this.func_176338_e(p_220069_2_, p_220069_3_, p_220069_1_);
         } else {
            dropResources(p_220069_1_, p_220069_2_, p_220069_3_);
            p_220069_2_.removeBlock(p_220069_3_, false);
         }

      }
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return !this.shouldSignal ? 0 : p_176211_1_.getSignal(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      if (!this.shouldSignal) {
         return 0;
      } else {
         int i = p_180656_1_.getValue(POWER);
         if (i == 0) {
            return 0;
         } else if (p_180656_4_ == Direction.UP) {
            return i;
         } else {
            EnumSet<Direction> enumset = EnumSet.noneOf(Direction.class);

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (this.func_176339_d(p_180656_2_, p_180656_3_, direction)) {
                  enumset.add(direction);
               }
            }

            if (p_180656_4_.getAxis().isHorizontal() && enumset.isEmpty()) {
               return i;
            } else {
               return enumset.contains(p_180656_4_) && !enumset.contains(p_180656_4_.getCounterClockWise()) && !enumset.contains(p_180656_4_.getClockWise()) ? i : 0;
            }
         }
      }
   }

   private boolean func_176339_d(IBlockReader p_176339_1_, BlockPos p_176339_2_, Direction p_176339_3_) {
      BlockPos blockpos = p_176339_2_.relative(p_176339_3_);
      BlockState blockstate = p_176339_1_.getBlockState(blockpos);
      boolean flag = blockstate.isRedstoneConductor(p_176339_1_, blockpos);
      BlockPos blockpos1 = p_176339_2_.above();
      boolean flag1 = p_176339_1_.getBlockState(blockpos1).isRedstoneConductor(p_176339_1_, blockpos1);
      if (!flag1 && flag && canConnectTo(p_176339_1_.getBlockState(blockpos.above()), p_176339_1_, blockpos.above(), null)) {
         return true;
      } else if (canConnectTo(blockstate, p_176339_1_, blockpos, p_176339_3_)) {
         return true;
      } else if (blockstate.getBlock() == Blocks.REPEATER && blockstate.getValue(RedstoneDiodeBlock.POWERED) && blockstate.getValue(RedstoneDiodeBlock.FACING) == p_176339_3_) {
         return true;
      } else {
         return !flag && canConnectTo(p_176339_1_.getBlockState(blockpos.below()), p_176339_1_, blockpos.below(), null);
      }
   }

   protected static boolean canConnectTo(BlockState p_176343_0_, IBlockReader world, BlockPos pos, @Nullable Direction p_176343_1_) {
      Block block = p_176343_0_.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (p_176343_0_.getBlock() == Blocks.REPEATER) {
         Direction direction = p_176343_0_.getValue(RepeaterBlock.FACING);
         return direction == p_176343_1_ || direction.getOpposite() == p_176343_1_;
      } else if (Blocks.OBSERVER == p_176343_0_.getBlock()) {
         return p_176343_1_ == p_176343_0_.getValue(ObserverBlock.FACING);
      } else {
         return p_176343_0_.canConnectRedstone(world, pos, p_176343_1_) && p_176343_1_ != null;
      }
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return this.shouldSignal;
   }

   @OnlyIn(Dist.CLIENT)
   public static int func_176337_b(int p_176337_0_) {
      float f = (float)p_176337_0_ / 15.0F;
      float f1 = f * 0.6F + 0.4F;
      if (p_176337_0_ == 0) {
         f1 = 0.3F;
      }

      float f2 = f * f * 0.7F - 0.5F;
      float f3 = f * f * 0.6F - 0.7F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
      return -16777216 | i << 16 | j << 8 | k;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      int i = p_180655_1_.getValue(POWER);
      if (i != 0) {
         double d0 = (double)p_180655_3_.getX() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         double d1 = (double)((float)p_180655_3_.getY() + 0.0625F);
         double d2 = (double)p_180655_3_.getZ() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + 0.4F;
         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         p_180655_2_.addParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(SOUTH)).setValue(EAST, p_185499_1_.getValue(WEST)).setValue(SOUTH, p_185499_1_.getValue(NORTH)).setValue(WEST, p_185499_1_.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(EAST)).setValue(EAST, p_185499_1_.getValue(SOUTH)).setValue(SOUTH, p_185499_1_.getValue(WEST)).setValue(WEST, p_185499_1_.getValue(NORTH));
      case CLOCKWISE_90:
         return p_185499_1_.setValue(NORTH, p_185499_1_.getValue(WEST)).setValue(EAST, p_185499_1_.getValue(NORTH)).setValue(SOUTH, p_185499_1_.getValue(EAST)).setValue(WEST, p_185499_1_.getValue(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return p_185471_1_.setValue(NORTH, p_185471_1_.getValue(SOUTH)).setValue(SOUTH, p_185471_1_.getValue(NORTH));
      case FRONT_BACK:
         return p_185471_1_.setValue(EAST, p_185471_1_.getValue(WEST)).setValue(WEST, p_185471_1_.getValue(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, POWER);
   }
}
