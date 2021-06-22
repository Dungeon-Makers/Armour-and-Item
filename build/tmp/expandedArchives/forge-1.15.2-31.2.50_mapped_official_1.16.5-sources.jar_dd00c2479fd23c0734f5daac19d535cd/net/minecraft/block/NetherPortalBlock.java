package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public NetherPortalBlock(Block.Properties p_i48352_1_) {
      super(p_i48352_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction.Axis)p_220053_1_.getValue(AXIS)) {
      case Z:
         return Z_AXIS_AABB;
      case X:
      default:
         return X_AXIS_AABB;
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.dimension.func_76569_d() && p_225534_2_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && p_225534_4_.nextInt(2000) < p_225534_2_.getDifficulty().getId()) {
         while(p_225534_2_.getBlockState(p_225534_3_).getBlock() == this) {
            p_225534_3_ = p_225534_3_.below();
         }

         if (p_225534_2_.getBlockState(p_225534_3_).isValidSpawn(p_225534_2_, p_225534_3_, EntityType.field_200785_Y)) {
            Entity entity = EntityType.field_200785_Y.spawn(p_225534_2_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, p_225534_3_.above(), SpawnReason.STRUCTURE, false, false);
            if (entity != null) {
               entity.field_71088_bW = entity.getDimensionChangingDelay();
            }
         }
      }

   }

   public boolean func_176548_d(IWorld p_176548_1_, BlockPos p_176548_2_) {
      NetherPortalBlock.Size netherportalblock$size = this.func_201816_b(p_176548_1_, p_176548_2_);
      if (netherportalblock$size != null && !net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(p_176548_1_, p_176548_2_, netherportalblock$size)) {
         netherportalblock$size.createPortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public NetherPortalBlock.Size func_201816_b(IWorld p_201816_1_, BlockPos p_201816_2_) {
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.X);
      if (netherportalblock$size.isValid() && netherportalblock$size.numPortalBlocks == 0) {
         return netherportalblock$size;
      } else {
         NetherPortalBlock.Size netherportalblock$size1 = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.Z);
         return netherportalblock$size1.isValid() && netherportalblock$size1.numPortalBlocks == 0 ? netherportalblock$size1 : null;
      }
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      Direction.Axis direction$axis = p_196271_2_.getAxis();
      Direction.Axis direction$axis1 = p_196271_1_.getValue(AXIS);
      boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
      return !flag && p_196271_3_.getBlock() != this && !(new NetherPortalBlock.Size(p_196271_4_, p_196271_5_, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.isPassenger() && !p_196262_4_.isVehicle() && p_196262_4_.canChangeDimensions()) {
         p_196262_4_.handleInsidePortal(p_196262_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(100) == 0) {
         p_180655_2_.playLocalSound((double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 0.5D, (double)p_180655_3_.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, p_180655_4_.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)p_180655_3_.getX() + (double)p_180655_4_.nextFloat();
         double d1 = (double)p_180655_3_.getY() + (double)p_180655_4_.nextFloat();
         double d2 = (double)p_180655_3_.getZ() + (double)p_180655_4_.nextFloat();
         double d3 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)p_180655_4_.nextFloat() - 0.5D) * 0.5D;
         int j = p_180655_4_.nextInt(2) * 2 - 1;
         if (p_180655_2_.getBlockState(p_180655_3_.west()).getBlock() != this && p_180655_2_.getBlockState(p_180655_3_.east()).getBlock() != this) {
            d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)j);
         }

         p_180655_2_.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)p_185499_1_.getValue(AXIS)) {
         case Z:
            return p_185499_1_.setValue(AXIS, Direction.Axis.X);
         case X:
            return p_185499_1_.setValue(AXIS, Direction.Axis.Z);
         default:
            return p_185499_1_;
         }
      default:
         return p_185499_1_;
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public static BlockPattern.PatternHelper func_181089_f(IWorld p_181089_0_, BlockPos p_181089_1_) {
      Direction.Axis direction$axis = Direction.Axis.Z;
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(p_181089_0_, p_181089_1_, Direction.Axis.X);
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = BlockPattern.createLevelCache(p_181089_0_, true);
      if (!netherportalblock$size.isValid()) {
         direction$axis = Direction.Axis.X;
         netherportalblock$size = new NetherPortalBlock.Size(p_181089_0_, p_181089_1_, Direction.Axis.Z);
      }

      if (!netherportalblock$size.isValid()) {
         return new BlockPattern.PatternHelper(p_181089_1_, Direction.NORTH, Direction.UP, loadingcache, 1, 1, 1);
      } else {
         int[] aint = new int[Direction.AxisDirection.values().length];
         Direction direction = netherportalblock$size.rightDir.getCounterClockWise();
         BlockPos blockpos = netherportalblock$size.bottomLeft.above(netherportalblock$size.func_181100_a() - 1);

         for(Direction.AxisDirection direction$axisdirection : Direction.AxisDirection.values()) {
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection ? blockpos : blockpos.relative(netherportalblock$size.rightDir, netherportalblock$size.func_181101_b() - 1), Direction.get(direction$axisdirection, direction$axis), Direction.UP, loadingcache, netherportalblock$size.func_181101_b(), netherportalblock$size.func_181100_a(), 1);

            for(int i = 0; i < netherportalblock$size.func_181101_b(); ++i) {
               for(int j = 0; j < netherportalblock$size.func_181100_a(); ++j) {
                  CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.getBlock(i, j, 1);
                  if (!cachedblockinfo.getState().isAir()) {
                     ++aint[direction$axisdirection.ordinal()];
                  }
               }
            }
         }

         Direction.AxisDirection direction$axisdirection1 = Direction.AxisDirection.POSITIVE;

         for(Direction.AxisDirection direction$axisdirection2 : Direction.AxisDirection.values()) {
            if (aint[direction$axisdirection2.ordinal()] < aint[direction$axisdirection1.ordinal()]) {
               direction$axisdirection1 = direction$axisdirection2;
            }
         }

         return new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection1 ? blockpos : blockpos.relative(netherportalblock$size.rightDir, netherportalblock$size.func_181101_b() - 1), Direction.get(direction$axisdirection1, direction$axis), Direction.UP, loadingcache, netherportalblock$size.func_181101_b(), netherportalblock$size.func_181100_a(), 1);
      }
   }

   public static class Size {
      private final IWorld level;
      private final Direction.Axis axis;
      private final Direction rightDir;
      private final Direction field_150863_d;
      private int numPortalBlocks;
      @Nullable
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, Direction.Axis p_i48740_3_) {
         this.level = p_i48740_1_;
         this.axis = p_i48740_3_;
         if (p_i48740_3_ == Direction.Axis.X) {
            this.field_150863_d = Direction.EAST;
            this.rightDir = Direction.WEST;
         } else {
            this.field_150863_d = Direction.NORTH;
            this.rightDir = Direction.SOUTH;
         }

         for(BlockPos blockpos = p_i48740_2_; p_i48740_2_.getY() > blockpos.getY() - 21 && p_i48740_2_.getY() > 0 && this.isEmpty(p_i48740_1_.getBlockState(p_i48740_2_.below())); p_i48740_2_ = p_i48740_2_.below()) {
            ;
         }

         int i = this.func_180120_a(p_i48740_2_, this.field_150863_d) - 1;
         if (i >= 0) {
            this.bottomLeft = p_i48740_2_.relative(this.field_150863_d, i);
            this.width = this.func_180120_a(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.func_150858_a();
         }

      }

      protected int func_180120_a(BlockPos p_180120_1_, Direction p_180120_2_) {
         int i;
         for(i = 0; i < 22; ++i) {
            BlockPos blockpos = p_180120_1_.relative(p_180120_2_, i);
            if (!this.isEmpty(this.level.getBlockState(blockpos)) || !this.level.getBlockState(blockpos.below()).isPortalFrame(this.level, blockpos.below())) {
               break;
            }
         }

         BlockPos framePos = p_180120_1_.relative(p_180120_2_, i);
         return this.level.getBlockState(framePos).isPortalFrame(this.level, framePos) ? i : 0;
      }

      public int func_181100_a() {
         return this.height;
      }

      public int func_181101_b() {
         return this.width;
      }

      protected int func_150858_a() {
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int i = 0; i < this.width; ++i) {
               BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
               BlockState blockstate = this.level.getBlockState(blockpos);
               if (!this.isEmpty(blockstate)) {
                  break label56;
               }

               Block block = blockstate.getBlock();
               if (block == Blocks.NETHER_PORTAL) {
                  ++this.numPortalBlocks;
               }

               if (i == 0) {
                  BlockPos framePos = blockpos.relative(this.field_150863_d);
                  if (!this.level.getBlockState(framePos).isPortalFrame(this.level, framePos)) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  BlockPos framePos = blockpos.relative(this.rightDir);
                  if (!this.level.getBlockState(framePos).isPortalFrame(this.level, framePos)) {
                     break label56;
                  }
               }
            }
         }

         for(int j = 0; j < this.width; ++j) {
            BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
            if (!this.level.getBlockState(framePos).isPortalFrame(this.level, framePos)) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean isEmpty(BlockState p_196900_1_) {
         Block block = p_196900_1_.getBlock();
         return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void createPortalBlocks() {
         for(int i = 0; i < this.width; ++i) {
            BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

            for(int j = 0; j < this.height; ++j) {
               this.level.setBlock(blockpos.above(j), Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.numPortalBlocks >= this.width * this.height;
      }

      public boolean isComplete() {
         return this.isValid() && this.func_196899_f();
      }
   }
}
