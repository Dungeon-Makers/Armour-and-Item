package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BedBlock extends HorizontalBlock implements ITileEntityProvider {
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final VoxelShape BASE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
   protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape NORTH_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
   protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
   protected static final VoxelShape WEST_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
   protected static final VoxelShape EAST_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
   private final DyeColor color;

   public BedBlock(DyeColor p_i48442_1_, Block.Properties p_i48442_2_) {
      super(p_i48442_2_);
      this.color = p_i48442_1_;
      this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.valueOf(false)));
   }

   public MaterialColor func_180659_g(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return p_180659_1_.getValue(PART) == BedPart.FOOT ? this.color.getMaterialColor() : MaterialColor.WOOL;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Direction getBedOrientation(IBlockReader p_220174_0_, BlockPos p_220174_1_) {
      BlockState blockstate = p_220174_0_.getBlockState(p_220174_1_);
      return blockstate.getBlock() instanceof BedBlock ? blockstate.getValue(FACING) : null;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.CONSUME;
      } else {
         if (p_225533_1_.getValue(PART) != BedPart.HEAD) {
            p_225533_3_ = p_225533_3_.relative(p_225533_1_.getValue(FACING));
            p_225533_1_ = p_225533_2_.getBlockState(p_225533_3_);
            if (p_225533_1_.getBlock() != this) {
               return ActionResultType.CONSUME;
            }
         }

         net.minecraftforge.common.extensions.IForgeDimension.SleepResult sleepResult = p_225533_2_.dimension.canSleepAt(p_225533_4_, p_225533_3_);
         if (sleepResult != net.minecraftforge.common.extensions.IForgeDimension.SleepResult.BED_EXPLODES) {
            if (sleepResult == net.minecraftforge.common.extensions.IForgeDimension.SleepResult.DENY) return ActionResultType.SUCCESS;
            if (p_225533_1_.getValue(OCCUPIED)) {
               if (!this.kickVillagerOutOfBed(p_225533_2_, p_225533_3_)) {
                  p_225533_4_.displayClientMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
               }

               return ActionResultType.SUCCESS;
            } else {
               p_225533_4_.startSleepInBed(p_225533_3_).ifLeft((p_220173_1_) -> {
                  if (p_220173_1_ != null) {
                     p_225533_4_.displayClientMessage(p_220173_1_.getMessage(), true);
                  }

               });
               return ActionResultType.SUCCESS;
            }
         } else {
            p_225533_2_.removeBlock(p_225533_3_, false);
            BlockPos blockpos = p_225533_3_.relative(p_225533_1_.getValue(FACING).getOpposite());
            if (p_225533_2_.getBlockState(blockpos).getBlock() == this) {
               p_225533_2_.removeBlock(blockpos, false);
            }

            p_225533_2_.func_217401_a((Entity)null, DamageSource.func_199683_a(), (double)p_225533_3_.getX() + 0.5D, (double)p_225533_3_.getY() + 0.5D, (double)p_225533_3_.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
            return ActionResultType.SUCCESS;
         }
      }
   }

   private boolean kickVillagerOutOfBed(World p_226861_1_, BlockPos p_226861_2_) {
      List<VillagerEntity> list = p_226861_1_.getEntitiesOfClass(VillagerEntity.class, new AxisAlignedBB(p_226861_2_), LivingEntity::isSleeping);
      if (list.isEmpty()) {
         return false;
      } else {
         list.get(0).stopSleeping();
         return true;
      }
   }

   public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      super.fallOn(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_ * 0.5F);
   }

   public void updateEntityAfterFallOn(IBlockReader p_176216_1_, Entity p_176216_2_) {
      if (p_176216_2_.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(p_176216_1_, p_176216_2_);
      } else {
         this.bounceUp(p_176216_2_);
      }

   }

   private void bounceUp(Entity p_226860_1_) {
      Vec3d vec3d = p_226860_1_.getDeltaMovement();
      if (vec3d.y < 0.0D) {
         double d0 = p_226860_1_ instanceof LivingEntity ? 1.0D : 0.8D;
         p_226860_1_.setDeltaMovement(vec3d.x, -vec3d.y * (double)0.66F * d0, vec3d.z);
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == getNeighbourDirection(p_196271_1_.getValue(PART), p_196271_1_.getValue(FACING))) {
         return p_196271_3_.getBlock() == this && p_196271_3_.getValue(PART) != p_196271_1_.getValue(PART) ? p_196271_1_.setValue(OCCUPIED, p_196271_3_.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   private static Direction getNeighbourDirection(BedPart p_208070_0_, Direction p_208070_1_) {
      return p_208070_0_ == BedPart.FOOT ? p_208070_1_ : p_208070_1_.getOpposite();
   }

   public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.playerDestroy(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.defaultBlockState(), p_180657_5_, p_180657_6_);
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      BedPart bedpart = p_176208_3_.getValue(PART);
      BlockPos blockpos = p_176208_2_.relative(getNeighbourDirection(bedpart, p_176208_3_.getValue(FACING)));
      BlockState blockstate = p_176208_1_.getBlockState(blockpos);
      if (blockstate.getBlock() == this && blockstate.getValue(PART) != bedpart) {
         p_176208_1_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
         p_176208_1_.levelEvent(p_176208_4_, 2001, blockpos, Block.getId(blockstate));
         if (!p_176208_1_.isClientSide && !p_176208_4_.isCreative()) {
            ItemStack itemstack = p_176208_4_.getMainHandItem();
            dropResources(p_176208_3_, p_176208_1_, p_176208_2_, (TileEntity)null, p_176208_4_, itemstack);
            dropResources(blockstate, p_176208_1_, blockpos, (TileEntity)null, p_176208_4_, itemstack);
         }

         p_176208_4_.awardStat(Stats.BLOCK_MINED.get(this));
      }

      super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction direction = p_196258_1_.getHorizontalDirection();
      BlockPos blockpos = p_196258_1_.getClickedPos();
      BlockPos blockpos1 = blockpos.relative(direction);
      return p_196258_1_.getLevel().getBlockState(blockpos1).canBeReplaced(p_196258_1_) ? this.defaultBlockState().setValue(FACING, direction) : null;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Direction direction = getConnectedDirection(p_220053_1_).getOpposite();
      switch(direction) {
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      default:
         return EAST_SHAPE;
      }
   }

   public static Direction getConnectedDirection(BlockState p_226862_0_) {
      Direction direction = p_226862_0_.getValue(FACING);
      return p_226862_0_.getValue(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
   }

   @OnlyIn(Dist.CLIENT)
   public static TileEntityMerger.Type getBlockType(BlockState p_226863_0_) {
      BedPart bedpart = p_226863_0_.getValue(PART);
      return bedpart == BedPart.HEAD ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
   }

   public static Optional<Vec3d> func_220172_a(EntityType<?> p_220172_0_, IWorldReader p_220172_1_, BlockPos p_220172_2_, int p_220172_3_) {
      Direction direction = p_220172_1_.getBlockState(p_220172_2_).getValue(FACING);
      int i = p_220172_2_.getX();
      int j = p_220172_2_.getY();
      int k = p_220172_2_.getZ();

      for(int l = 0; l <= 1; ++l) {
         int i1 = i - direction.getStepX() * l - 1;
         int j1 = k - direction.getStepZ() * l - 1;
         int k1 = i1 + 2;
         int l1 = j1 + 2;

         for(int i2 = i1; i2 <= k1; ++i2) {
            for(int j2 = j1; j2 <= l1; ++j2) {
               BlockPos blockpos = new BlockPos(i2, j, j2);
               Optional<Vec3d> optional = func_220175_a(p_220172_0_, p_220172_1_, blockpos);
               if (optional.isPresent()) {
                  if (p_220172_3_ <= 0) {
                     return optional;
                  }

                  --p_220172_3_;
               }
            }
         }
      }

      return Optional.empty();
   }

   protected static Optional<Vec3d> func_220175_a(EntityType<?> p_220175_0_, IWorldReader p_220175_1_, BlockPos p_220175_2_) {
      VoxelShape voxelshape = p_220175_1_.getBlockState(p_220175_2_).getCollisionShape(p_220175_1_, p_220175_2_);
      if (voxelshape.max(Direction.Axis.Y) > 0.4375D) {
         return Optional.empty();
      } else {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_220175_2_);

         while(blockpos$mutable.getY() >= 0 && p_220175_2_.getY() - blockpos$mutable.getY() <= 2 && p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable).isEmpty()) {
            blockpos$mutable.move(Direction.DOWN);
         }

         VoxelShape voxelshape1 = p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable);
         if (voxelshape1.isEmpty()) {
            return Optional.empty();
         } else {
            double d0 = (double)blockpos$mutable.getY() + voxelshape1.max(Direction.Axis.Y) + 2.0E-7D;
            if ((double)p_220175_2_.getY() - d0 > 2.0D) {
               return Optional.empty();
            } else {
               float f = p_220175_0_.getWidth() / 2.0F;
               Vec3d vec3d = new Vec3d((double)blockpos$mutable.getX() + 0.5D, d0, (double)blockpos$mutable.getZ() + 0.5D);
               return p_220175_1_.noCollision(new AxisAlignedBB(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)p_220175_0_.getHeight(), vec3d.z + (double)f)) ? Optional.of(vec3d) : Optional.empty();
            }
         }
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, PART, OCCUPIED);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new BedTileEntity(this.color);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      if (!p_180633_1_.isClientSide) {
         BlockPos blockpos = p_180633_2_.relative(p_180633_3_.getValue(FACING));
         p_180633_1_.setBlock(blockpos, p_180633_3_.setValue(PART, BedPart.HEAD), 3);
         p_180633_1_.func_195592_c(p_180633_2_, Blocks.AIR);
         p_180633_3_.func_196946_a(p_180633_1_, p_180633_2_, 3);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed(BlockState p_209900_1_, BlockPos p_209900_2_) {
      BlockPos blockpos = p_209900_2_.relative(p_209900_1_.getValue(FACING), p_209900_1_.getValue(PART) == BedPart.HEAD ? 0 : 1);
      return MathHelper.getSeed(blockpos.getX(), p_209900_2_.getY(), blockpos.getZ());
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
