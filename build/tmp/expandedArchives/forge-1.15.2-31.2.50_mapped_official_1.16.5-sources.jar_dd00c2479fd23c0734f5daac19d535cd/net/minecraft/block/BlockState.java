package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockState extends StateHolder<Block, BlockState> implements IStateHolder<BlockState>, net.minecraftforge.common.extensions.IForgeBlockState {
   @Nullable
   private BlockState.Cache cache;
   private final int lightEmission;
   private final boolean useShapeForLightOcclusion;

   public BlockState(Block p_i49958_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49958_2_) {
      super(p_i49958_1_, p_i49958_2_);
      this.lightEmission = p_i49958_1_.func_149750_m(this);
      this.useShapeForLightOcclusion = p_i49958_1_.useShapeForLightOcclusion(this);
   }

   public void initCache() {
      if (!this.getBlock().hasDynamicShape()) {
         this.cache = new BlockState.Cache(this);
      }

   }

   public Block getBlock() {
      return this.field_206876_a;
   }

   public Material getMaterial() {
      return this.getBlock().func_149688_o(this);
   }

   public boolean isValidSpawn(IBlockReader p_215688_1_, BlockPos p_215688_2_, EntityType<?> p_215688_3_) {
      return this.getBlock().func_220067_a(this, p_215688_1_, p_215688_2_, p_215688_3_);
   }

   public boolean propagatesSkylightDown(IBlockReader p_200131_1_, BlockPos p_200131_2_) {
      return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this, p_200131_1_, p_200131_2_);
   }

   public int getLightBlock(IBlockReader p_200016_1_, BlockPos p_200016_2_) {
      return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this, p_200016_1_, p_200016_2_);
   }

   public VoxelShape getFaceOcclusionShape(IBlockReader p_215702_1_, BlockPos p_215702_2_, Direction p_215702_3_) {
      return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[p_215702_3_.ordinal()] : VoxelShapes.getFaceShape(this.getBlockSupportShape(p_215702_1_, p_215702_2_), p_215702_3_);
   }

   public boolean hasLargeCollisionShape() {
      return this.cache == null || this.cache.largeCollisionShape;
   }

   public boolean useShapeForLightOcclusion() {
      return this.useShapeForLightOcclusion;
   }

   public int getLightEmission() {
      return this.lightEmission;
   }

   /** @deprecated use {@link BlockState#isAir(IBlockReader, BlockPos) */
   @Deprecated
   public boolean isAir() {
      return this.getBlock().func_196261_e(this);
   }

   /** @deprecated use {@link BlockState#rotate(IWorld, BlockPos, Rotation) */
   @Deprecated
   public MaterialColor getMapColor(IBlockReader p_185909_1_, BlockPos p_185909_2_) {
      return this.getBlock().func_180659_g(this, p_185909_1_, p_185909_2_);
   }

   public BlockState rotate(Rotation p_185907_1_) {
      return this.getBlock().rotate(this, p_185907_1_);
   }

   public BlockState mirror(Mirror p_185902_1_) {
      return this.getBlock().mirror(this, p_185902_1_);
   }

   public BlockRenderType getRenderShape() {
      return this.getBlock().getRenderShape(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean emissiveRendering() {
      return this.getBlock().func_225543_m_(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(IBlockReader p_215703_1_, BlockPos p_215703_2_) {
      return this.getBlock().getShadeBrightness(this, p_215703_1_, p_215703_2_);
   }

   public boolean isRedstoneConductor(IBlockReader p_215686_1_, BlockPos p_215686_2_) {
      return this.getBlock().func_220081_d(this, p_215686_1_, p_215686_2_);
   }

   public boolean isSignalSource() {
      return this.getBlock().isSignalSource(this);
   }

   public int getSignal(IBlockReader p_185911_1_, BlockPos p_185911_2_, Direction p_185911_3_) {
      return this.getBlock().getSignal(this, p_185911_1_, p_185911_2_, p_185911_3_);
   }

   public boolean hasAnalogOutputSignal() {
      return this.getBlock().hasAnalogOutputSignal(this);
   }

   public int getAnalogOutputSignal(World p_185888_1_, BlockPos p_185888_2_) {
      return this.getBlock().getAnalogOutputSignal(this, p_185888_1_, p_185888_2_);
   }

   public float getDestroySpeed(IBlockReader p_185887_1_, BlockPos p_185887_2_) {
      return this.getBlock().func_176195_g(this, p_185887_1_, p_185887_2_);
   }

   public float getDestroyProgress(PlayerEntity p_185903_1_, IBlockReader p_185903_2_, BlockPos p_185903_3_) {
      return this.getBlock().getDestroyProgress(this, p_185903_1_, p_185903_2_, p_185903_3_);
   }

   public int getDirectSignal(IBlockReader p_185893_1_, BlockPos p_185893_2_, Direction p_185893_3_) {
      return this.getBlock().getDirectSignal(this, p_185893_1_, p_185893_2_, p_185893_3_);
   }

   public PushReaction getPistonPushReaction() {
      return this.getBlock().getPistonPushReaction(this);
   }

   public boolean isSolidRender(IBlockReader p_200015_1_, BlockPos p_200015_2_) {
      return this.cache != null ? this.cache.solidRender : this.getBlock().func_200012_i(this, p_200015_1_, p_200015_2_);
   }

   public boolean canOcclude() {
      return this.cache != null ? this.cache.field_222498_b : this.getBlock().func_200124_e(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean skipRendering(BlockState p_200017_1_, Direction p_200017_2_) {
      return this.getBlock().skipRendering(this, p_200017_1_, p_200017_2_);
   }

   public VoxelShape getShape(IBlockReader p_196954_1_, BlockPos p_196954_2_) {
      return this.getShape(p_196954_1_, p_196954_2_, ISelectionContext.empty());
   }

   public VoxelShape getShape(IBlockReader p_215700_1_, BlockPos p_215700_2_, ISelectionContext p_215700_3_) {
      return this.getBlock().getShape(this, p_215700_1_, p_215700_2_, p_215700_3_);
   }

   public VoxelShape getCollisionShape(IBlockReader p_196952_1_, BlockPos p_196952_2_) {
      return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(p_196952_1_, p_196952_2_, ISelectionContext.empty());
   }

   public VoxelShape getCollisionShape(IBlockReader p_215685_1_, BlockPos p_215685_2_, ISelectionContext p_215685_3_) {
      return this.getBlock().getCollisionShape(this, p_215685_1_, p_215685_2_, p_215685_3_);
   }

   public VoxelShape getBlockSupportShape(IBlockReader p_196951_1_, BlockPos p_196951_2_) {
      return this.getBlock().getOcclusionShape(this, p_196951_1_, p_196951_2_);
   }

   public VoxelShape getVisualShape(IBlockReader p_199611_1_, BlockPos p_199611_2_) {
      return this.getBlock().getInteractionShape(this, p_199611_1_, p_199611_2_);
   }

   public final boolean entityCanStandOnFace(IBlockReader p_215682_1_, BlockPos p_215682_2_, Entity p_215682_3_) {
      return Block.isFaceFull(this.getCollisionShape(p_215682_1_, p_215682_2_, ISelectionContext.of(p_215682_3_)), Direction.UP);
   }

   public Vec3d getOffset(IBlockReader p_191059_1_, BlockPos p_191059_2_) {
      return this.getBlock().func_190949_e(this, p_191059_1_, p_191059_2_);
   }

   public boolean func_189547_a(World p_189547_1_, BlockPos p_189547_2_, int p_189547_3_, int p_189547_4_) {
      return this.getBlock().triggerEvent(this, p_189547_1_, p_189547_2_, p_189547_3_, p_189547_4_);
   }

   public void neighborChanged(World p_215697_1_, BlockPos p_215697_2_, Block p_215697_3_, BlockPos p_215697_4_, boolean p_215697_5_) {
      this.getBlock().neighborChanged(this, p_215697_1_, p_215697_2_, p_215697_3_, p_215697_4_, p_215697_5_);
   }

   public void func_196946_a(IWorld p_196946_1_, BlockPos p_196946_2_, int p_196946_3_) {
      this.getBlock().func_196242_c(this, p_196946_1_, p_196946_2_, p_196946_3_);
   }

   public void updateIndirectNeighbourShapes(IWorld p_196948_1_, BlockPos p_196948_2_, int p_196948_3_) {
      this.getBlock().updateIndirectNeighbourShapes(this, p_196948_1_, p_196948_2_, p_196948_3_);
   }

   public void onPlace(World p_215705_1_, BlockPos p_215705_2_, BlockState p_215705_3_, boolean p_215705_4_) {
      this.getBlock().onPlace(this, p_215705_1_, p_215705_2_, p_215705_3_, p_215705_4_);
   }

   public void onRemove(World p_196947_1_, BlockPos p_196947_2_, BlockState p_196947_3_, boolean p_196947_4_) {
      this.getBlock().onRemove(this, p_196947_1_, p_196947_2_, p_196947_3_, p_196947_4_);
   }

   public void tick(ServerWorld p_227033_1_, BlockPos p_227033_2_, Random p_227033_3_) {
      this.getBlock().tick(this, p_227033_1_, p_227033_2_, p_227033_3_);
   }

   public void randomTick(ServerWorld p_227034_1_, BlockPos p_227034_2_, Random p_227034_3_) {
      this.getBlock().randomTick(this, p_227034_1_, p_227034_2_, p_227034_3_);
   }

   public void entityInside(World p_196950_1_, BlockPos p_196950_2_, Entity p_196950_3_) {
      this.getBlock().entityInside(this, p_196950_1_, p_196950_2_, p_196950_3_);
   }

   public void spawnAfterBreak(World p_215706_1_, BlockPos p_215706_2_, ItemStack p_215706_3_) {
      this.getBlock().spawnAfterBreak(this, p_215706_1_, p_215706_2_, p_215706_3_);
   }

   public List<ItemStack> getDrops(LootContext.Builder p_215693_1_) {
      return this.getBlock().getDrops(this, p_215693_1_);
   }

   public ActionResultType use(World p_227031_1_, PlayerEntity p_227031_2_, Hand p_227031_3_, BlockRayTraceResult p_227031_4_) {
      return this.getBlock().use(this, p_227031_1_, p_227031_4_.getBlockPos(), p_227031_2_, p_227031_3_, p_227031_4_);
   }

   public void attack(World p_196942_1_, BlockPos p_196942_2_, PlayerEntity p_196942_3_) {
      this.getBlock().attack(this, p_196942_1_, p_196942_2_, p_196942_3_);
   }

   public boolean isSuffocating(IBlockReader p_229980_1_, BlockPos p_229980_2_) {
      return this.getBlock().func_229869_c_(this, p_229980_1_, p_229980_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isViewBlocking(IBlockReader p_215696_1_, BlockPos p_215696_2_) {
      return this.getBlock().func_229870_f_(this, p_215696_1_, p_215696_2_);
   }

   public BlockState updateShape(Direction p_196956_1_, BlockState p_196956_2_, IWorld p_196956_3_, BlockPos p_196956_4_, BlockPos p_196956_5_) {
      return this.getBlock().updateShape(this, p_196956_1_, p_196956_2_, p_196956_3_, p_196956_4_, p_196956_5_);
   }

   public boolean isPathfindable(IBlockReader p_196957_1_, BlockPos p_196957_2_, PathType p_196957_3_) {
      return this.getBlock().isPathfindable(this, p_196957_1_, p_196957_2_, p_196957_3_);
   }

   public boolean canBeReplaced(BlockItemUseContext p_196953_1_) {
      return this.getBlock().canBeReplaced(this, p_196953_1_);
   }

   public boolean canBeReplaced(Fluid p_227032_1_) {
      return this.getBlock().canBeReplaced(this, p_227032_1_);
   }

   public boolean canSurvive(IWorldReader p_196955_1_, BlockPos p_196955_2_) {
      return this.getBlock().canSurvive(this, p_196955_1_, p_196955_2_);
   }

   public boolean hasPostProcess(IBlockReader p_202065_1_, BlockPos p_202065_2_) {
      return this.getBlock().func_201783_b(this, p_202065_1_, p_202065_2_);
   }

   @Nullable
   public INamedContainerProvider getMenuProvider(World p_215699_1_, BlockPos p_215699_2_) {
      return this.getBlock().getMenuProvider(this, p_215699_1_, p_215699_2_);
   }

   public boolean is(Tag<Block> p_203425_1_) {
      return this.getBlock().is(p_203425_1_);
   }

   public IFluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   public boolean isRandomlyTicking() {
      return this.getBlock().isRandomlyTicking(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed(BlockPos p_209533_1_) {
      return this.getBlock().getSeed(this, p_209533_1_);
   }

   public SoundType getSoundType() {
      return this.getBlock().getSoundType(this);
   }

   public void onProjectileHit(World p_215690_1_, BlockState p_215690_2_, BlockRayTraceResult p_215690_3_, Entity p_215690_4_) {
      this.getBlock().onProjectileHit(p_215690_1_, p_215690_2_, p_215690_3_, p_215690_4_);
   }

   public boolean isFaceSturdy(IBlockReader p_224755_1_, BlockPos p_224755_2_, Direction p_224755_3_) {
      return this.cache != null ? this.cache.faceSturdy[p_224755_3_.ordinal()] : Block.func_220056_d(this, p_224755_1_, p_224755_2_, p_224755_3_);
   }

   public boolean func_224756_o(IBlockReader p_224756_1_, BlockPos p_224756_2_) {
      return this.cache != null ? this.cache.isCollisionShapeFullBlock : Block.isShapeFullBlock(this.getCollisionShape(p_224756_1_, p_224756_2_));
   }

   public static <T> Dynamic<T> func_215689_a(DynamicOps<T> p_215689_0_, BlockState p_215689_1_) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_215689_1_.getValues();
      T t;
      if (immutablemap.isEmpty()) {
         t = p_215689_0_.createMap(ImmutableMap.of(p_215689_0_.createString("Name"), p_215689_0_.createString(Registry.BLOCK.getKey(p_215689_1_.getBlock()).toString())));
      } else {
         t = p_215689_0_.createMap(ImmutableMap.of(p_215689_0_.createString("Name"), p_215689_0_.createString(Registry.BLOCK.getKey(p_215689_1_.getBlock()).toString()), p_215689_0_.createString("Properties"), p_215689_0_.createMap(immutablemap.entrySet().stream().map((p_215683_1_) -> {
            return Pair.of(p_215689_0_.createString(p_215683_1_.getKey().getName()), p_215689_0_.createString(IStateHolder.func_215670_b(p_215683_1_.getKey(), p_215683_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic<>(p_215689_0_, t);
   }

   public static <T> BlockState func_215698_a(Dynamic<T> p_215698_0_) {
      Block block = Registry.BLOCK.get(new ResourceLocation(p_215698_0_.getElement("Name").flatMap(p_215698_0_.getOps()::getStringValue).orElse("minecraft:air")));
      Map<String, String> map = p_215698_0_.get("Properties").asMap((p_215701_0_) -> {
         return p_215701_0_.asString("");
      }, (p_215694_0_) -> {
         return p_215694_0_.asString("");
      });
      BlockState blockstate = block.defaultBlockState();
      StateContainer<Block, BlockState> statecontainer = block.getStateDefinition();

      for(Entry<String, String> entry : map.entrySet()) {
         String s = entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            blockstate = IStateHolder.func_215671_a(blockstate, iproperty, s, p_215698_0_.toString(), entry.getValue());
         }
      }

      return blockstate;
   }

   static final class Cache {
      private static final Direction[] DIRECTIONS = Direction.values();
      private final boolean field_222498_b;
      private final boolean solidRender;
      private final boolean propagatesSkylightDown;
      private final int lightBlock;
      private final VoxelShape[] occlusionShapes;
      private final VoxelShape collisionShape;
      private final boolean largeCollisionShape;
      private final boolean[] faceSturdy;
      private final boolean isCollisionShapeFullBlock;

      private Cache(BlockState p_i50627_1_) {
         Block block = p_i50627_1_.getBlock();
         this.field_222498_b = block.func_200124_e(p_i50627_1_);
         this.solidRender = block.func_200012_i(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.propagatesSkylightDown = block.propagatesSkylightDown(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.lightBlock = block.getLightBlock(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         if (!p_i50627_1_.canOcclude()) {
            this.occlusionShapes = null;
         } else {
            this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
            VoxelShape voxelshape = block.getOcclusionShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

            for(Direction direction : DIRECTIONS) {
               this.occlusionShapes[direction.ordinal()] = VoxelShapes.getFaceShape(voxelshape, direction);
            }
         }

         this.collisionShape = block.getCollisionShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, ISelectionContext.empty());
         this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((p_222491_1_) -> {
            return this.collisionShape.min(p_222491_1_) < 0.0D || this.collisionShape.max(p_222491_1_) > 1.0D;
         });
         this.faceSturdy = new boolean[6];

         for(Direction direction1 : DIRECTIONS) {
            this.faceSturdy[direction1.ordinal()] = Block.func_220056_d(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, direction1);
         }

         this.isCollisionShapeFullBlock = Block.isShapeFullBlock(p_i50627_1_.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
      }
   }
}
