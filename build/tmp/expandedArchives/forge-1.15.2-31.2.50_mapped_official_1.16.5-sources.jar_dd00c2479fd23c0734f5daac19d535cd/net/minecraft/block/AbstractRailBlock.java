package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
   protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean isStraight;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(BlockState p_208487_0_) {
      return p_208487_0_.is(BlockTags.RAILS);
   }

   protected AbstractRailBlock(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.isStraight = p_i48444_1_;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      RailShape railshape = p_220053_1_.getBlock() == this ? getRailDirection(p_220053_1_, p_220053_2_, p_220053_3_, null) : null;
      return railshape != null && railshape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return canSupportRigidBlock(p_196260_2_, p_196260_3_.below());
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         p_220082_1_ = this.updateDir(p_220082_2_, p_220082_3_, p_220082_1_, true);
         if (this.isStraight) {
            p_220082_1_.neighborChanged(p_220082_2_, p_220082_3_, this, p_220082_3_, p_220082_5_);
         }

      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         RailShape railshape = getRailDirection(p_220069_1_, p_220069_2_, p_220069_3_, null);
         boolean flag = false;
         BlockPos blockpos = p_220069_3_.below();
         if (!canSupportRigidBlock(p_220069_2_, blockpos)) {
            flag = true;
         }

         BlockPos blockpos1 = p_220069_3_.east();
         if (railshape == RailShape.ASCENDING_EAST && !canSupportRigidBlock(p_220069_2_, blockpos1)) {
            flag = true;
         } else {
            BlockPos blockpos2 = p_220069_3_.west();
            if (railshape == RailShape.ASCENDING_WEST && !canSupportRigidBlock(p_220069_2_, blockpos2)) {
               flag = true;
            } else {
               BlockPos blockpos3 = p_220069_3_.north();
               if (railshape == RailShape.ASCENDING_NORTH && !canSupportRigidBlock(p_220069_2_, blockpos3)) {
                  flag = true;
               } else {
                  BlockPos blockpos4 = p_220069_3_.south();
                  if (railshape == RailShape.ASCENDING_SOUTH && !canSupportRigidBlock(p_220069_2_, blockpos4)) {
                     flag = true;
                  }
               }
            }
         }

         if (flag && !p_220069_2_.isEmptyBlock(p_220069_3_)) {
            if (!p_220069_6_) {
               dropResources(p_220069_1_, p_220069_2_, p_220069_3_);
            }

            p_220069_2_.removeBlock(p_220069_3_, p_220069_6_);
         } else {
            this.updateState(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_);
         }

      }
   }

   protected void updateState(BlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
   }

   protected BlockState updateDir(World p_208489_1_, BlockPos p_208489_2_, BlockState p_208489_3_, boolean p_208489_4_) {
      if (p_208489_1_.isClientSide) {
         return p_208489_3_;
      } else {
         RailShape railshape = p_208489_3_.getValue(this.getShapeProperty());
         return (new RailState(p_208489_1_, p_208489_2_, p_208489_3_)).place(p_208489_1_.hasNeighborSignal(p_208489_2_), p_208489_4_, railshape).getState();
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (getRailDirection(p_196243_1_, p_196243_2_, p_196243_3_, null).isAscending()) {
            p_196243_2_.updateNeighborsAt(p_196243_3_.above(), this);
         }

         if (this.isStraight) {
            p_196243_2_.updateNeighborsAt(p_196243_3_, this);
            p_196243_2_.updateNeighborsAt(p_196243_3_.below(), this);
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = super.defaultBlockState();
      Direction direction = p_196258_1_.getHorizontalDirection();
      boolean flag = direction == Direction.EAST || direction == Direction.WEST;
      return blockstate.setValue(this.getShapeProperty(), flag ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
   }

   //Forge: Use getRailDirection(IBlockAccess, BlockPos, IBlockState, EntityMinecart) for enhanced ability
   public abstract IProperty<RailShape> getShapeProperty();

   /* ======================================== FORGE START =====================================*/
   /**
    * Return true if the rail can make corners.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make corners.
    */
   public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos)
   {
       return !this.isStraight;
   }

   /**
    * Returns true if the rail can make up and down slopes.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make slopes.
    */
   public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
       return true;
   }

   /**
    * Return the rail's direction.
    * Can be used to make the cart think the rail is a different shape,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * @param world The world.
    * @param pos Block's position in world
    * @param state The BlockState
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @return The direction.
    */
   public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @javax.annotation.Nullable net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
       return state.getValue(getShapeProperty());
   }

   /**
    * Returns the max speed of the rail at the specified position.
    * @param world The world.
    * @param cart The cart on the rail, may be null.
    * @param pos Block's position in world
    * @return The max speed of the current rail.
    */
   public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
       return 0.4f;
   }

   /**
    * This function is called by any minecart that passes over this rail.
    * It is called once per update tick that the minecart is on the rail.
    * @param world The world.
    * @param cart The cart on the rail.
    * @param pos Block's position in world
    */
   public void onMinecartPass(BlockState state, World world, BlockPos pos, net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) { }
}
