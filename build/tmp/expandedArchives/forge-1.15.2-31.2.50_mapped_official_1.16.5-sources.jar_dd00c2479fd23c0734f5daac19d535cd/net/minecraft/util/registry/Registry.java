package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Attention Modders: This SHOULD NOT be used, you should use ForgeRegistries instead. As it has a cleaner modder facing API.
 * We will be wrapping all of these in our API as necessary for syncing and management.
 */
public abstract class Registry<T> implements IObjectIntIterable<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   public static final MutableRegistry<MutableRegistry<?>> REGISTRY = new SimpleRegistry<>();
   @Deprecated public static final Registry<SoundEvent> SOUND_EVENT = forge("sound_event", SoundEvent.class, () -> {
      return SoundEvents.ITEM_PICKUP;
   });
   @Deprecated public static final DefaultedRegistry<Fluid> FLUID = forgeDefaulted("fluid", Fluid.class, () -> {
      return Fluids.EMPTY;
   });
   @Deprecated public static final Registry<Effect> MOB_EFFECT = forge("mob_effect", Effect.class, () -> {
      return Effects.LUCK;
   });
   @Deprecated public static final DefaultedRegistry<Block> BLOCK = forgeDefaulted("block", Block.class, () -> {
      return Blocks.AIR;
   });
   @Deprecated public static final Registry<Enchantment> ENCHANTMENT = forge("enchantment", Enchantment.class, () -> {
      return Enchantments.BLOCK_FORTUNE;
   });
   @Deprecated public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = forgeDefaulted("entity_type", EntityType.class, () -> {
      return EntityType.PIG;
   });
   @Deprecated public static final DefaultedRegistry<Item> ITEM = forgeDefaulted("item", Item.class, () -> {
      return Items.AIR;
   });
   @Deprecated public static final DefaultedRegistry<Potion> POTION = forgeDefaulted("potion", Potion.class, () -> {
      return Potions.EMPTY;
   });
   @Deprecated public static final Registry<WorldCarver<?>> CARVER = forge("carver", WorldCarver.class, () -> {
      return WorldCarver.CAVE;
   });
   @Deprecated public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = forge("surface_builder", SurfaceBuilder.class, () -> {
      return SurfaceBuilder.DEFAULT;
   });
   @Deprecated public static final Registry<Feature<?>> FEATURE = forge("feature", Feature.class, () -> {
      return Feature.ORE;
   });
   @Deprecated public static final Registry<Placement<?>> DECORATOR = forge("decorator", Placement.class, () -> {
      return Placement.NOPE;
   });
   @Deprecated public static final Registry<Biome> field_212624_m = forge("biome", Biome.class, () -> {
      return Biomes.field_180279_ad;
   });
   @Deprecated public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPES = forge("block_state_provider_type", BlockStateProviderType.class, () -> {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   });
   @Deprecated public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPES = forge("block_placer_type", BlockPlacerType.class, () -> {
      return BlockPlacerType.SIMPLE_BLOCK_PLACER;
   });
   @Deprecated public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = forge("foliage_placer_type", FoliagePlacerType.class, () -> {
      return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
   });
   @Deprecated public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = forge("tree_decorator_type", TreeDecoratorType.class, () -> {
      return TreeDecoratorType.LEAVE_VINE;
   });
   @Deprecated public static final Registry<ParticleType<? extends IParticleData>> PARTICLE_TYPE = forge("particle_type", ParticleType.class, () -> {
      return ParticleTypes.BLOCK;
   });
   @Deprecated public static final Registry<BiomeProviderType<?, ?>> field_212625_n = forge("biome_source_type", BiomeProviderType.class, () -> {
      return BiomeProviderType.field_206859_d;
   });
   @Deprecated public static final Registry<TileEntityType<?>> BLOCK_ENTITY_TYPE = forge("block_entity_type", TileEntityType.class, () -> {
      return TileEntityType.FURNACE;
   });
   @Deprecated public static final Registry<ChunkGeneratorType<?, ?>> field_212627_p = forge("chunk_generator_type", ChunkGeneratorType.class, () -> {
      return ChunkGeneratorType.field_205489_f;
   });
   @Deprecated public static final Registry<DimensionType> field_212622_k = func_222939_a("dimension_type", net.minecraftforge.common.DimensionManager.getRegistry(), () -> {
      return DimensionType.field_223227_a_;
   });
   @Deprecated public static final DefaultedRegistry<PaintingType> MOTIVE = forgeDefaulted("motive", PaintingType.class, () -> {
      return PaintingType.KEBAB;
   });
   public static final Registry<ResourceLocation> CUSTOM_STAT = func_222935_a("custom_stat", () -> {
      return Stats.JUMP;
   });
   @Deprecated public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = forgeDefaulted("chunk_status", ChunkStatus.class, () -> {
      return ChunkStatus.EMPTY;
   });
   @Deprecated public static final Registry<Structure<?>> STRUCTURE_FEATURE = net.minecraftforge.registries.GameData.getStructureFeatures();
   public static final Registry<IStructurePieceType> STRUCTURE_PIECE = func_222935_a("structure_piece", () -> {
      return IStructurePieceType.MINE_SHAFT_ROOM;
   });
   public static final Registry<IRuleTestType> RULE_TEST = func_222935_a("rule_test", () -> {
      return IRuleTestType.ALWAYS_TRUE_TEST;
   });
   public static final Registry<IStructureProcessorType> STRUCTURE_PROCESSOR = func_222935_a("structure_processor", () -> {
      return IStructureProcessorType.BLOCK_IGNORE;
   });
   public static final Registry<IJigsawDeserializer> STRUCTURE_POOL_ELEMENT = func_222935_a("structure_pool_element", () -> {
      return IJigsawDeserializer.EMPTY;
   });
   @Deprecated public static final Registry<ContainerType<?>> MENU = forge("menu", ContainerType.class, () -> {
      return ContainerType.ANVIL;
   });
   public static final Registry<IRecipeType<?>> RECIPE_TYPE = func_222935_a("recipe_type", () -> {
      return IRecipeType.CRAFTING;
   });
   @Deprecated public static final Registry<IRecipeSerializer<?>> RECIPE_SERIALIZER = forge("recipe_serializer", IRecipeSerializer.class, () -> {
      return IRecipeSerializer.SHAPELESS_RECIPE;
   });
   @Deprecated public static final Registry<StatType<?>> STAT_TYPE = forge("stat_type", StatType.class, () -> {
      return Stats.ITEM_USED;
   });
   public static final DefaultedRegistry<IVillagerType> VILLAGER_TYPE = func_222933_a("villager_type", "plains", () -> {
      return IVillagerType.PLAINS;
   });
   @Deprecated public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = forgeDefaulted("villager_profession", VillagerProfession.class, () -> {
      return VillagerProfession.NONE;
   });
   @Deprecated public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = forgeDefaulted("point_of_interest_type", PointOfInterestType.class, () -> {
      return PointOfInterestType.UNEMPLOYED;
   });
   @Deprecated public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = forgeDefaulted("memory_module_type", MemoryModuleType.class, () -> {
      return MemoryModuleType.DUMMY;
   });
   @Deprecated public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = forgeDefaulted("sensor_type", SensorType.class, () -> {
      return SensorType.DUMMY;
   });
   @Deprecated public static final Registry<Schedule> SCHEDULE = forge("schedule", Schedule.class, () -> {
      return Schedule.EMPTY;
   });
   @Deprecated public static final Registry<Activity> ACTIVITY = forge("activity", Activity.class, () -> {
      return Activity.IDLE;
   });

   private static <T> Registry<T> func_222935_a(String p_222935_0_, Supplier<T> p_222935_1_) {
      return func_222939_a(p_222935_0_, new SimpleRegistry<>(), p_222935_1_);
   }

   private static <T> DefaultedRegistry<T> func_222933_a(String p_222933_0_, String p_222933_1_, Supplier<T> p_222933_2_) {
      return func_222939_a(p_222933_0_, new DefaultedRegistry<>(p_222933_1_), p_222933_2_);
   }

   private static <T, R extends MutableRegistry<T>> R func_222939_a(String p_222939_0_, R p_222939_1_, Supplier<T> p_222939_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_222939_0_);
      LOADERS.put(resourcelocation, p_222939_2_);
      return (R)(REGISTRY.register(resourcelocation, p_222939_1_));
   }

   @Nullable
   public abstract ResourceLocation getKey(T p_177774_1_);

   public abstract int getId(@Nullable T p_148757_1_);

   @Nullable
   public abstract T get(@Nullable ResourceLocation p_82594_1_);

   public abstract Optional<T> func_218349_b(@Nullable ResourceLocation p_218349_1_);

   public abstract Set<ResourceLocation> keySet();

   @Nullable
   public abstract T getRandom(Random p_186801_1_);

   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @OnlyIn(Dist.CLIENT)
   public abstract boolean containsKey(ResourceLocation p_212607_1_);

   public static <T> T register(Registry<? super T> p_218325_0_, String p_218325_1_, T p_218325_2_) {
      return register(p_218325_0_, new ResourceLocation(p_218325_1_), p_218325_2_);
   }

   public static <T> T register(Registry<? super T> p_218322_0_, ResourceLocation p_218322_1_, T p_218322_2_) {
      return ((MutableRegistry<T>)p_218322_0_).register(p_218322_1_, p_218322_2_);
   }

   public static <T> T registerMapping(Registry<? super T> p_218343_0_, int p_218343_1_, String p_218343_2_, T p_218343_3_) {
      return ((MutableRegistry<T>)p_218343_0_).registerMapping(p_218343_1_, new ResourceLocation(p_218343_2_), p_218343_3_);
   }

   private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> Registry<T> forge(String name, Class<? super T> cls, Supplier<T> def) {
      return func_222939_a(name, net.minecraftforge.registries.GameData.<T>getWrapper(cls), def);
   }

   private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> DefaultedRegistry<T> forgeDefaulted(String name, Class<? super T> cls, Supplier<T> def) {
      return Registry.<T, DefaultedRegistry<T>>func_222939_a(name, net.minecraftforge.registries.GameData.<T>getWrapperDefaulted(cls), def);
   }

   static {
      LOADERS.entrySet().forEach((p_229397_0_) -> {
         if (p_229397_0_.getValue().get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", p_229397_0_.getKey());
         }

      });
      REGISTRY.forEach((p_229396_0_) -> {
         if (p_229396_0_.isEmpty()) {
            LOGGER.error("Registry '{}' was empty after loading", (Object)REGISTRY.getKey(p_229396_0_));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               throw new IllegalStateException("Registry: '" + REGISTRY.getKey(p_229396_0_) + "' is empty, not allowed, fix me!");
            }
         }

         if (p_229396_0_ instanceof DefaultedRegistry) {
            ResourceLocation resourcelocation = ((DefaultedRegistry)p_229396_0_).getDefaultKey();
            Validate.notNull(p_229396_0_.get(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
         }

      });
   }
}
