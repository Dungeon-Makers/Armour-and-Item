package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends net.minecraftforge.registries.ForgeRegistryEntry<Block> implements IItemProvider, net.minecraftforge.common.extensions.IForgeBlock {
   protected static final Logger LOGGER = LogManager.getLogger();
   @Deprecated //Forge: Do not use, use GameRegistry
   public static final ObjectIntIdentityMap<BlockState> BLOCK_STATE_REGISTRY = net.minecraftforge.registries.GameData.getBlockStateIDMap();
   private static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
      public Boolean load(VoxelShape p_load_1_) {
         return !VoxelShapes.joinIsNotEmpty(VoxelShapes.block(), p_load_1_, IBooleanFunction.NOT_SAME);
      }
   });
   private static final VoxelShape field_220083_b = VoxelShapes.join(VoxelShapes.block(), box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
   private static final VoxelShape field_220084_c = box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);
   protected final int field_149784_t;
   protected final float field_149782_v;
   protected final float field_149781_w;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final Material material;
   protected final MaterialColor field_181083_K;
   private final float friction;
   private final float speedFactor;
   private final float jumpFactor;
   protected final StateContainer<Block, BlockState> stateDefinition;
   private BlockState defaultBlockState;
   protected final boolean field_196274_w;
   private final boolean dynamicShape;
   private final boolean field_226888_j_;
   @Nullable
   private ResourceLocation drops;
   @Nullable
   private String descriptionId;
   @Nullable
   private Item item;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(2048, 0.25F) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });

   public static int getId(@Nullable BlockState p_196246_0_) {
      if (p_196246_0_ == null) {
         return 0;
      } else {
         int i = BLOCK_STATE_REGISTRY.func_148747_b(p_196246_0_);
         return i == -1 ? 0 : i;
      }
   }

   public static BlockState stateById(int p_196257_0_) {
      BlockState blockstate = BLOCK_STATE_REGISTRY.byId(p_196257_0_);
      return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
   }

   public static Block byItem(@Nullable Item p_149634_0_) {
      return p_149634_0_ instanceof BlockItem ? ((BlockItem)p_149634_0_).getBlock() : Blocks.AIR;
   }

   public static BlockState pushEntitiesUp(BlockState p_199601_0_, BlockState p_199601_1_, World p_199601_2_, BlockPos p_199601_3_) {
      VoxelShape voxelshape = VoxelShapes.joinUnoptimized(p_199601_0_.getCollisionShape(p_199601_2_, p_199601_3_), p_199601_1_.getCollisionShape(p_199601_2_, p_199601_3_), IBooleanFunction.ONLY_SECOND).move((double)p_199601_3_.getX(), (double)p_199601_3_.getY(), (double)p_199601_3_.getZ());

      for(Entity entity : p_199601_2_.getEntities((Entity)null, voxelshape.bounds())) {
         double d0 = VoxelShapes.collide(Direction.Axis.Y, entity.getBoundingBox().move(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
         entity.teleportTo(entity.getX(), entity.getY() + 1.0D + d0, entity.getZ());
      }

      return p_199601_1_;
   }

   public static VoxelShape box(double p_208617_0_, double p_208617_2_, double p_208617_4_, double p_208617_6_, double p_208617_8_, double p_208617_10_) {
      return VoxelShapes.box(p_208617_0_ / 16.0D, p_208617_2_ / 16.0D, p_208617_4_ / 16.0D, p_208617_6_ / 16.0D, p_208617_8_ / 16.0D, p_208617_10_ / 16.0D);
   }

   @Deprecated
   public boolean func_220067_a(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return p_220067_1_.isFaceSturdy(p_220067_2_, p_220067_3_, Direction.UP) && p_220067_1_.getLightValue(p_220067_2_, p_220067_3_) < 14;
   }

   @Deprecated
   public boolean func_196261_e(BlockState p_196261_1_) {
      return false;
   }

   @Deprecated
   public int func_149750_m(BlockState p_149750_1_) {
      return this.field_149784_t;
   }

   @Deprecated
   public Material func_149688_o(BlockState p_149688_1_) {
      return this.material;
   }

   @Deprecated
   public MaterialColor func_180659_g(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return this.field_181083_K;
   }

   @Deprecated
   public void func_196242_c(BlockState p_196242_1_, IWorld p_196242_2_, BlockPos p_196242_3_, int p_196242_4_) {
      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
         for(Direction direction : UPDATE_SHAPE_ORDER) {
            blockpos$pooledmutable.set(p_196242_3_).move(direction);
            BlockState blockstate = p_196242_2_.getBlockState(blockpos$pooledmutable);
            BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), p_196242_1_, p_196242_2_, blockpos$pooledmutable, p_196242_3_);
            updateOrDestroy(blockstate, blockstate1, p_196242_2_, blockpos$pooledmutable, p_196242_4_);
         }
      }

   }

   public boolean is(Tag<Block> p_203417_1_) {
      return p_203417_1_.func_199685_a_(this);
   }

   public static BlockState updateFromNeighbourShapes(BlockState p_199770_0_, IWorld p_199770_1_, BlockPos p_199770_2_) {
      BlockState blockstate = p_199770_0_;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : UPDATE_SHAPE_ORDER) {
         blockpos$mutable.set(p_199770_2_).move(direction);
         blockstate = blockstate.updateShape(direction, p_199770_1_.getBlockState(blockpos$mutable), p_199770_1_, p_199770_2_, blockpos$mutable);
      }

      return blockstate;
   }

   public static void updateOrDestroy(BlockState p_196263_0_, BlockState p_196263_1_, IWorld p_196263_2_, BlockPos p_196263_3_, int p_196263_4_) {
      if (p_196263_1_ != p_196263_0_) {
         if (p_196263_1_.isAir()) {
            if (!p_196263_2_.isClientSide()) {
               p_196263_2_.destroyBlock(p_196263_3_, (p_196263_4_ & 32) == 0);
            }
         } else {
            p_196263_2_.setBlock(p_196263_3_, p_196263_1_, p_196263_4_ & -33);
         }
      }

   }

   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
   }

   @Deprecated
   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_1_;
   }

   @Deprecated
   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_;
   }

   @Deprecated
   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_;
   }

   public Block(Block.Properties p_i48440_1_) {
      StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
      this.createBlockStateDefinition(builder);
      this.material = p_i48440_1_.material;
      this.field_181083_K = p_i48440_1_.field_200954_b;
      this.field_196274_w = p_i48440_1_.hasCollision;
      this.soundType = p_i48440_1_.soundType;
      this.field_149784_t = p_i48440_1_.field_200957_e;
      this.field_149781_w = p_i48440_1_.explosionResistance;
      this.field_149782_v = p_i48440_1_.destroyTime;
      this.isRandomlyTicking = p_i48440_1_.isRandomlyTicking;
      this.harvestLevel = p_i48440_1_.harvestLevel;
      this.harvestTool = p_i48440_1_.harvestTool;
      final ResourceLocation lootTableCache = p_i48440_1_.drops;
      this.lootTableSupplier = lootTableCache != null ? () -> lootTableCache : p_i48440_1_.lootTableSupplier != null ? p_i48440_1_.lootTableSupplier : () -> new ResourceLocation(this.getRegistryName().getNamespace(), "blocks/" + this.getRegistryName().getPath());
      this.friction = p_i48440_1_.friction;
      this.speedFactor = p_i48440_1_.speedFactor;
      this.jumpFactor = p_i48440_1_.jumpFactor;
      this.dynamicShape = p_i48440_1_.dynamicShape;
      this.drops = p_i48440_1_.drops;
      this.field_226888_j_ = p_i48440_1_.canOcclude;
      this.stateDefinition = builder.func_206893_a(BlockState::new);
      this.registerDefaultState(this.stateDefinition.any());
   }

   public static boolean isExceptionForConnection(Block p_220073_0_) {
      return p_220073_0_ instanceof LeavesBlock || p_220073_0_ == Blocks.BARRIER || p_220073_0_ == Blocks.CARVED_PUMPKIN || p_220073_0_ == Blocks.JACK_O_LANTERN || p_220073_0_ == Blocks.MELON || p_220073_0_ == Blocks.PUMPKIN || p_220073_0_.is(BlockTags.SHULKER_BOXES);
   }

   @Deprecated
   public boolean func_220081_d(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return p_220081_1_.getMaterial().isSolidBlocking() && p_220081_1_.func_224756_o(p_220081_2_, p_220081_3_) && !p_220081_1_.isSignalSource();
   }

   @Deprecated
   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return this.material.blocksMotion() && p_229869_1_.func_224756_o(p_229869_2_, p_229869_3_);
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean func_229870_f_(BlockState p_229870_1_, IBlockReader p_229870_2_, BlockPos p_229870_3_) {
      return p_229870_1_.isSuffocating(p_229870_2_, p_229870_3_);
   }

   @Deprecated
   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return !p_196266_1_.func_224756_o(p_196266_2_, p_196266_3_);
      case WATER:
         return p_196266_2_.getFluidState(p_196266_3_).is(FluidTags.WATER);
      case AIR:
         return !p_196266_1_.func_224756_o(p_196266_2_, p_196266_3_);
      default:
         return false;
      }
   }

   @Deprecated
   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Deprecated
   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_1_.getMaterial().isReplaceable() && (p_196253_2_.getItemInHand().isEmpty() || p_196253_2_.getItemInHand().getItem() != this.asItem());
   }

   @Deprecated
   public boolean canBeReplaced(BlockState p_225541_1_, Fluid p_225541_2_) {
      return this.material.isReplaceable() || !this.material.isSolid();
   }

   @Deprecated
   public float func_176195_g(BlockState p_176195_1_, IBlockReader p_176195_2_, BlockPos p_176195_3_) {
      return this.field_149782_v;
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return this.isRandomlyTicking;
   }

   @Deprecated //Forge: New State sensitive version.
   public boolean func_149716_u() {
      return hasTileEntity(defaultBlockState());
   }

   @Deprecated
   public boolean func_201783_b(BlockState p_201783_1_, IBlockReader p_201783_2_, BlockPos p_201783_3_) {
      return false;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean func_225543_m_(BlockState p_225543_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean shouldRenderFace(BlockState p_176225_0_, IBlockReader p_176225_1_, BlockPos p_176225_2_, Direction p_176225_3_) {
      BlockPos blockpos = p_176225_2_.relative(p_176225_3_);
      BlockState blockstate = p_176225_1_.getBlockState(blockpos);
      if (p_176225_0_.skipRendering(blockstate, p_176225_3_)) {
         return false;
      } else if (blockstate.canOcclude()) {
         Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(p_176225_0_, blockstate, p_176225_3_);
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         } else {
            VoxelShape voxelshape = p_176225_0_.getFaceOcclusionShape(p_176225_1_, p_176225_2_, p_176225_3_);
            VoxelShape voxelshape1 = blockstate.getFaceOcclusionShape(p_176225_1_, blockpos, p_176225_3_.getOpposite());
            boolean flag = VoxelShapes.joinIsNotEmpty(voxelshape, voxelshape1, IBooleanFunction.ONLY_FIRST);
            if (object2bytelinkedopenhashmap.size() == 2048) {
               object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
            return flag;
         }
      } else {
         return true;
      }
   }

   @Deprecated
   public final boolean func_200124_e(BlockState p_200124_1_) {
      return this.field_226888_j_;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return false;
   }

   @Deprecated
   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.block();
   }

   @Deprecated
   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.field_196274_w ? p_220071_1_.getShape(p_220071_2_, p_220071_3_) : VoxelShapes.empty();
   }

   @Deprecated
   public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return p_196247_1_.getShape(p_196247_2_, p_196247_3_);
   }

   @Deprecated
   public VoxelShape getInteractionShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return VoxelShapes.empty();
   }

   public static boolean canSupportRigidBlock(IBlockReader p_220064_0_, BlockPos p_220064_1_) {
      BlockState blockstate = p_220064_0_.getBlockState(p_220064_1_);
      return !blockstate.is(BlockTags.LEAVES) && !VoxelShapes.joinIsNotEmpty(blockstate.getCollisionShape(p_220064_0_, p_220064_1_).getFaceShape(Direction.UP), field_220083_b, IBooleanFunction.ONLY_SECOND);
   }

   public static boolean canSupportCenter(IWorldReader p_220055_0_, BlockPos p_220055_1_, Direction p_220055_2_) {
      BlockState blockstate = p_220055_0_.getBlockState(p_220055_1_);
      return !blockstate.is(BlockTags.LEAVES) && !VoxelShapes.joinIsNotEmpty(blockstate.getCollisionShape(p_220055_0_, p_220055_1_).getFaceShape(p_220055_2_), field_220084_c, IBooleanFunction.ONLY_SECOND);
   }

   public static boolean func_220056_d(BlockState p_220056_0_, IBlockReader p_220056_1_, BlockPos p_220056_2_, Direction p_220056_3_) {
      return !p_220056_0_.is(BlockTags.LEAVES) && isFaceFull(p_220056_0_.getCollisionShape(p_220056_1_, p_220056_2_), p_220056_3_);
   }

   public static boolean isFaceFull(VoxelShape p_208061_0_, Direction p_208061_1_) {
      VoxelShape voxelshape = p_208061_0_.getFaceShape(p_208061_1_);
      return isShapeFullBlock(voxelshape);
   }

   public static boolean isShapeFullBlock(VoxelShape p_208062_0_) {
      return SHAPE_FULL_BLOCK_CACHE.getUnchecked(p_208062_0_);
   }

   @Deprecated
   public final boolean func_200012_i(BlockState p_200012_1_, IBlockReader p_200012_2_, BlockPos p_200012_3_) {
      return p_200012_1_.canOcclude() ? isShapeFullBlock(p_200012_1_.getBlockSupportShape(p_200012_2_, p_200012_3_)) : false;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return !isShapeFullBlock(p_200123_1_.getShape(p_200123_2_, p_200123_3_)) && p_200123_1_.getFluidState().isEmpty();
   }

   @Deprecated
   public int getLightBlock(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      if (p_200011_1_.isSolidRender(p_200011_2_, p_200011_3_)) {
         return p_200011_2_.getMaxLightLevel();
      } else {
         return p_200011_1_.propagatesSkylightDown(p_200011_2_, p_200011_3_) ? 0 : 1;
      }
   }

   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return false;
   }

   @Deprecated
   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      this.tick(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_);
   }

   @Deprecated
   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
   }

   public void destroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
   }

   @Deprecated
   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      DebugPacketSender.sendNeighborsUpdatePacket(p_220069_2_, p_220069_3_);
   }

   public int getTickDelay(IWorldReader p_149738_1_) {
      return 10;
   }

   @Nullable
   @Deprecated
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return null;
   }

   @Deprecated
   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
   }

   @Deprecated
   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.hasTileEntity() && (p_196243_1_.getBlock() != p_196243_4_.getBlock() || !p_196243_4_.hasTileEntity())) {
         p_196243_2_.removeBlockEntity(p_196243_3_);
      }
   }

   @Deprecated
   public float getDestroyProgress(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      float f = p_180647_1_.getDestroySpeed(p_180647_3_, p_180647_4_);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_) ? 30 : 100;
         return p_180647_2_.getDigSpeed(p_180647_1_, p_180647_4_) / f / (float)i;
      }
   }

   @Deprecated
   public void spawnAfterBreak(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
   }

   public ResourceLocation getLootTable() {
      if (this.drops == null) {
         this.drops = this.lootTableSupplier.get();
      }

      return this.drops;
   }

   @Deprecated
   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      ResourceLocation resourcelocation = this.getLootTable();
      if (resourcelocation == LootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext lootcontext = p_220076_2_.withParameter(LootParameters.BLOCK_STATE, p_220076_1_).create(LootParameterSets.BLOCK);
         ServerWorld serverworld = lootcontext.getLevel();
         LootTable loottable = serverworld.getServer().getLootTables().get(resourcelocation);
         return loottable.getRandomItems(lootcontext);
      }
   }

   public static List<ItemStack> getDrops(BlockState p_220070_0_, ServerWorld p_220070_1_, BlockPos p_220070_2_, @Nullable TileEntity p_220070_3_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220070_1_)).withRandom(p_220070_1_.random).withParameter(LootParameters.field_216286_f, p_220070_2_).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, p_220070_3_);
      return p_220070_0_.getDrops(lootcontext$builder);
   }

   public static List<ItemStack> getDrops(BlockState p_220077_0_, ServerWorld p_220077_1_, BlockPos p_220077_2_, @Nullable TileEntity p_220077_3_, @Nullable Entity p_220077_4_, ItemStack p_220077_5_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220077_1_)).withRandom(p_220077_1_.random).withParameter(LootParameters.field_216286_f, p_220077_2_).withParameter(LootParameters.TOOL, p_220077_5_).withOptionalParameter(LootParameters.THIS_ENTITY, p_220077_4_).withOptionalParameter(LootParameters.BLOCK_ENTITY, p_220077_3_);
      return p_220077_0_.getDrops(lootcontext$builder);
   }

   public static void dropResources(BlockState p_220075_0_, World p_220075_1_, BlockPos p_220075_2_) {
      if (p_220075_1_ instanceof ServerWorld) {
         getDrops(p_220075_0_, (ServerWorld)p_220075_1_, p_220075_2_, (TileEntity)null).forEach((p_220079_2_) -> {
            popResource(p_220075_1_, p_220075_2_, p_220079_2_);
         });
      }

      p_220075_0_.spawnAfterBreak(p_220075_1_, p_220075_2_, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState p_220059_0_, World p_220059_1_, BlockPos p_220059_2_, @Nullable TileEntity p_220059_3_) {
      if (p_220059_1_ instanceof ServerWorld) {
         getDrops(p_220059_0_, (ServerWorld)p_220059_1_, p_220059_2_, p_220059_3_).forEach((p_220061_2_) -> {
            popResource(p_220059_1_, p_220059_2_, p_220061_2_);
         });
      }

      p_220059_0_.spawnAfterBreak(p_220059_1_, p_220059_2_, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState p_220054_0_, World p_220054_1_, BlockPos p_220054_2_, @Nullable TileEntity p_220054_3_, Entity p_220054_4_, ItemStack p_220054_5_) {
      if (p_220054_1_ instanceof ServerWorld) {
         getDrops(p_220054_0_, (ServerWorld)p_220054_1_, p_220054_2_, p_220054_3_, p_220054_4_, p_220054_5_).forEach((p_220057_2_) -> {
            popResource(p_220054_1_, p_220054_2_, p_220057_2_);
         });
      }

      p_220054_0_.spawnAfterBreak(p_220054_1_, p_220054_2_, p_220054_5_);
   }

   public static void popResource(World p_180635_0_, BlockPos p_180635_1_, ItemStack p_180635_2_) {
      if (!p_180635_0_.isClientSide && !p_180635_2_.isEmpty() && p_180635_0_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_180635_0_.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
         float f = 0.5F;
         double d0 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         double d1 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         double d2 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         ItemEntity itementity = new ItemEntity(p_180635_0_, (double)p_180635_1_.getX() + d0, (double)p_180635_1_.getY() + d1, (double)p_180635_1_.getZ() + d2, p_180635_2_);
         itementity.setDefaultPickUpDelay();
         p_180635_0_.addFreshEntity(itementity);
      }
   }

   public void popExperience(World p_180637_1_, BlockPos p_180637_2_, int p_180637_3_) {
      if (!p_180637_1_.isClientSide && p_180637_1_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_180637_1_.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
         while(p_180637_3_ > 0) {
            int i = ExperienceOrbEntity.getExperienceValue(p_180637_3_);
            p_180637_3_ -= i;
            p_180637_1_.addFreshEntity(new ExperienceOrbEntity(p_180637_1_, (double)p_180637_2_.getX() + 0.5D, (double)p_180637_2_.getY() + 0.5D, (double)p_180637_2_.getZ() + 0.5D, i));
         }
      }

   }

   @Deprecated //Forge: State sensitive version
   public float getExplosionResistance() {
      return this.field_149781_w;
   }

   public void wasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
   }

   @Deprecated
   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return true;
   }

   @Deprecated
   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return ActionResultType.PASS;
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState();
   }

   @Deprecated
   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
   }

   @Deprecated
   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return 0;
   }

   @Deprecated
   public boolean isSignalSource(BlockState p_149744_1_) {
      return false;
   }

   @Deprecated
   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
   }

   @Deprecated
   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return 0;
   }

   public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      p_180657_2_.awardStat(Stats.BLOCK_MINED.get(this));
      p_180657_2_.causeFoodExhaustion(0.005F);
      dropResources(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
   }

   public boolean isPossibleToRespawnInThis() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_200291_n() {
      return new TranslationTextComponent(this.getDescriptionId());
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
      }

      return this.descriptionId;
   }

   @Deprecated
   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      return false;
   }

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return this.material.getPushReaction();
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return p_220080_1_.func_224756_o(p_220080_2_, p_220080_3_) ? 0.2F : 1.0F;
   }

   public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.causeFallDamage(p_180658_4_, 1.0F);
   }

   public void updateEntityAfterFallOn(IBlockReader p_176216_1_, Entity p_176216_2_) {
      p_176216_2_.setDeltaMovement(p_176216_2_.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
   }

   @Deprecated // Forge: Use more sensitive version below: getPickBlock
   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this);
   }

   public void fillItemCategory(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
      p_149666_2_.add(new ItemStack(this));
   }

   @Deprecated
   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Deprecated //Forge: Use more sensitive version
   public float getFriction() {
      return this.friction;
   }

   public float getSpeedFactor() {
      return this.speedFactor;
   }

   public float getJumpFactor() {
      return this.jumpFactor;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public long getSeed(BlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getSeed(p_209900_2_);
   }

   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      p_176208_1_.levelEvent(p_176208_4_, 2001, p_176208_2_, getId(p_176208_3_));
   }

   public void handleRain(World p_176224_1_, BlockPos p_176224_2_) {
   }

   @Deprecated //Forge: Use more sensitive version
   public boolean dropFromExplosion(Explosion p_149659_1_) {
      return true;
   }

   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return false;
   }

   @Deprecated
   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return 0;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
   }

   public StateContainer<Block, BlockState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(BlockState p_180632_1_) {
      this.defaultBlockState = p_180632_1_;
   }

   public final BlockState defaultBlockState() {
      return this.defaultBlockState;
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.NONE;
   }

   @Deprecated
   public Vec3d func_190949_e(BlockState p_190949_1_, IBlockReader p_190949_2_, BlockPos p_190949_3_) {
      Block.OffsetType block$offsettype = this.getOffsetType();
      if (block$offsettype == Block.OffsetType.NONE) {
         return Vec3d.ZERO;
      } else {
         long i = MathHelper.getSeed(p_190949_3_.getX(), 0, p_190949_3_.getZ());
         return new Vec3d(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, block$offsettype == Block.OffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
   }

   @Deprecated //Forge: Use more sensitive version {@link IForgeBlockState#getSoundType(IWorldReader, BlockPos, Entity) }
   public SoundType getSoundType(BlockState p_220072_1_) {
      return this.soundType;
   }

   public Item asItem() {
      if (this.item == null) {
         this.item = Item.byBlock(this);
      }

      return this.item.delegate.get(); //Forge: Vanilla caches the items, update with registry replacements.
   }

   public boolean hasDynamicShape() {
      return this.dynamicShape;
   }

   public String toString() {
      return "Block{" + getRegistryName() + "}";
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
   }

   /* ======================================== FORGE START =====================================*/
   protected Random RANDOM = new Random();
   private net.minecraftforge.common.ToolType harvestTool;
   private int harvestLevel;
   private final net.minecraftforge.common.util.ReverseTagWrapper<Block> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, BlockTags::getGeneration, BlockTags::getAllTags);
   private final java.util.function.Supplier<ResourceLocation> lootTableSupplier;

   @Override
   public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.friction;
   }

   @Nullable
   @Override
   public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
      return harvestTool; //TODO: RE-Evaluate
   }

   @Override
   public int getHarvestLevel(BlockState state) {
      return harvestLevel; //TODO: RE-Evaluate
   }

   @Override
   public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
       BlockState plant = plantable.getPlant(world, pos.relative(facing));
       net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));

       if (plant.getBlock() == Blocks.CACTUS)
           return this.getBlock() == Blocks.CACTUS || this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.RED_SAND;

       if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
           return true;

       if (plantable instanceof BushBlock && ((BushBlock)plantable).mayPlaceOn(state, world, pos))
           return true;

       switch (type) {
           case Desert: return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.TERRACOTTA || this.getBlock() instanceof GlazedTerracottaBlock;
           case Nether: return this.getBlock() == Blocks.SOUL_SAND;
           case Crop:   return this.getBlock() == Blocks.FARMLAND;
           case Cave:   return Block.func_220056_d(state, world, pos, Direction.UP);
           case Plains: return this.getBlock() == Blocks.GRASS_BLOCK || net.minecraftforge.common.Tags.Blocks.DIRT.func_199685_a_(this) || this.getBlock() == Blocks.FARMLAND;
           case Water:  return state.getMaterial() == Material.WATER; //&& state.getValue(BlockLiquidWrapper)
           case Beach:
               boolean isBeach = this.getBlock() == Blocks.GRASS_BLOCK || net.minecraftforge.common.Tags.Blocks.DIRT.func_199685_a_(this) || this.getBlock() == Blocks.SAND;
               boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                       world.getBlockState(pos.south()).getMaterial() == Material.WATER);
               return isBeach && hasWater;
       }
       return false;
   }

   @Override
   public final java.util.Set<ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   static {
      net.minecraftforge.common.ForgeHooks.setBlockToolSetter((block, tool, level) -> {
         block.harvestTool = tool;
         block.harvestLevel = level;
      });
   }
   /* ========================================= FORGE END ======================================*/

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Properties {
      private Material material;
      private MaterialColor field_200954_b;
      private boolean hasCollision = true;
      private SoundType soundType = SoundType.STONE;
      private int field_200957_e;
      private float explosionResistance;
      private float destroyTime;
      private boolean isRandomlyTicking;
      private float friction = 0.6F;
      private float speedFactor = 1.0F;
      private float jumpFactor = 1.0F;
      private ResourceLocation drops;
      private boolean canOcclude = true;
      private boolean dynamicShape;
      private int harvestLevel = -1;
      private net.minecraftforge.common.ToolType harvestTool;
      private java.util.function.Supplier<ResourceLocation> lootTableSupplier;

      private Properties(Material p_i48616_1_, MaterialColor p_i48616_2_) {
         this.material = p_i48616_1_;
         this.field_200954_b = p_i48616_2_;
      }

      public static Block.Properties of(Material p_200945_0_) {
         return of(p_200945_0_, p_200945_0_.getColor());
      }

      public static Block.Properties of(Material p_200952_0_, DyeColor p_200952_1_) {
         return of(p_200952_0_, p_200952_1_.getMaterialColor());
      }

      public static Block.Properties of(Material p_200949_0_, MaterialColor p_200949_1_) {
         return new Block.Properties(p_200949_0_, p_200949_1_);
      }

      public static Block.Properties copy(Block p_200950_0_) {
         Block.Properties block$properties = new Block.Properties(p_200950_0_.material, p_200950_0_.field_181083_K);
         block$properties.material = p_200950_0_.material;
         block$properties.destroyTime = p_200950_0_.field_149782_v;
         block$properties.explosionResistance = p_200950_0_.field_149781_w;
         block$properties.hasCollision = p_200950_0_.field_196274_w;
         block$properties.isRandomlyTicking = p_200950_0_.isRandomlyTicking;
         block$properties.field_200957_e = p_200950_0_.field_149784_t;
         block$properties.field_200954_b = p_200950_0_.field_181083_K;
         block$properties.soundType = p_200950_0_.soundType;
         block$properties.friction = p_200950_0_.getFriction();
         block$properties.speedFactor = p_200950_0_.getSpeedFactor();
         block$properties.dynamicShape = p_200950_0_.dynamicShape;
         block$properties.canOcclude = p_200950_0_.field_226888_j_;
         block$properties.harvestLevel = p_200950_0_.harvestLevel;
         block$properties.harvestTool = p_200950_0_.harvestTool;
         return block$properties;
      }

      public Block.Properties noCollission() {
         this.hasCollision = false;
         this.canOcclude = false;
         return this;
      }

      public Block.Properties noOcclusion() {
         this.canOcclude = false;
         return this;
      }

      public Block.Properties friction(float p_200941_1_) {
         this.friction = p_200941_1_;
         return this;
      }

      public Block.Properties speedFactor(float p_226897_1_) {
         this.speedFactor = p_226897_1_;
         return this;
      }

      public Block.Properties jumpFactor(float p_226898_1_) {
         this.jumpFactor = p_226898_1_;
         return this;
      }

      public Block.Properties sound(SoundType p_200947_1_) {
         this.soundType = p_200947_1_;
         return this;
      }

      public Block.Properties func_200951_a(int p_200951_1_) {
         this.field_200957_e = p_200951_1_;
         return this;
      }

      public Block.Properties strength(float p_200948_1_, float p_200948_2_) {
         this.destroyTime = p_200948_1_;
         this.explosionResistance = Math.max(0.0F, p_200948_2_);
         return this;
      }

      protected Block.Properties instabreak() {
         return this.strength(0.0F);
      }

      public Block.Properties strength(float p_200943_1_) {
         this.strength(p_200943_1_, p_200943_1_);
         return this;
      }

      public Block.Properties randomTicks() {
         this.isRandomlyTicking = true;
         return this;
      }

      public Block.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      public Block.Properties harvestLevel(int harvestLevel) {
          this.harvestLevel = harvestLevel;
          return this;
      }

      public Block.Properties harvestTool(net.minecraftforge.common.ToolType harvestTool) {
          this.harvestTool = harvestTool;
          return this;
      }

      public Block.Properties noDrops() {
         this.drops = LootTables.EMPTY;
         return this;
      }

      public Block.Properties dropsLike(Block p_222379_1_) {
         this.lootTableSupplier = () -> p_222379_1_.delegate.get().getLootTable();
         return this;
      }
   }

   public static final class RenderSideCacheKey {
      private final BlockState first;
      private final BlockState second;
      private final Direction direction;

      public RenderSideCacheKey(BlockState p_i49791_1_, BlockState p_i49791_2_, Direction p_i49791_3_) {
         this.first = p_i49791_1_;
         this.second = p_i49791_2_;
         this.direction = p_i49791_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey)) {
            return false;
         } else {
            Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey)p_equals_1_;
            return this.first == block$rendersidecachekey.first && this.second == block$rendersidecachekey.second && this.direction == block$rendersidecachekey.direction;
         }
      }

      public int hashCode() {
         int i = this.first.hashCode();
         i = 31 * i + this.second.hashCode();
         i = 31 * i + this.direction.hashCode();
         return i;
      }
   }
}
