package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.feature.structure.DesertPyramidStructure;
import net.minecraft.world.gen.feature.structure.EndCityStructure;
import net.minecraft.world.gen.feature.structure.FortressStructure;
import net.minecraft.world.gen.feature.structure.IglooStructure;
import net.minecraft.world.gen.feature.structure.JunglePyramidStructure;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.PillagerOutpostStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;
import net.minecraft.world.gen.feature.structure.StrongholdStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.structure.WoodlandMansionStructure;
import net.minecraft.world.gen.placement.CountConfig;

public abstract class Feature<FC extends IFeatureConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Feature<?>> {
   public static final Structure<NoFeatureConfig> field_214536_b = register("pillager_outpost", new PillagerOutpostStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<MineshaftConfig> field_202329_g = register("mineshaft", new MineshaftStructure(MineshaftConfig::func_214638_a));
   public static final Structure<NoFeatureConfig> field_202330_h = register("woodland_mansion", new WoodlandMansionStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_202331_i = register("jungle_temple", new JunglePyramidStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_202332_j = register("desert_pyramid", new DesertPyramidStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_202333_k = register("igloo", new IglooStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<ShipwreckConfig> field_204751_l = register("shipwreck", new ShipwreckStructure(ShipwreckConfig::func_214658_a));
   public static final SwampHutStructure field_202334_l = register("swamp_hut", new SwampHutStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_202335_m = register("stronghold", new StrongholdStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_202336_n = register("ocean_monument", new OceanMonumentStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<OceanRuinConfig> field_204029_o = register("ocean_ruin", new OceanRuinStructure(OceanRuinConfig::func_214640_a));
   public static final Structure<NoFeatureConfig> field_202337_o = register("nether_bridge", new FortressStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<NoFeatureConfig> field_204292_r = register("end_city", new EndCityStructure(NoFeatureConfig::func_214639_a));
   public static final Structure<BuriedTreasureConfig> field_214549_o = register("buried_treasure", new BuriedTreasureStructure(BuriedTreasureConfig::func_214684_a));
   public static final Structure<VillageConfig> field_214550_p = register("village", new VillageStructure(VillageConfig::func_214679_a));
   public static final Feature<NoFeatureConfig> NO_OP = register("no_op", new NoOpFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<TreeFeatureConfig> field_202301_A = register("normal_tree", new TreeFeature(TreeFeatureConfig::func_227338_a_));
   public static final Feature<TreeFeatureConfig> field_227246_s_ = register("acacia_tree", new AcaciaFeature(TreeFeatureConfig::deserializeAcacia));
   public static final Feature<TreeFeatureConfig> field_202339_q = register("fancy_tree", new FancyTreeFeature(TreeFeatureConfig::func_227338_a_));
   public static final Feature<BaseTreeFeatureConfig> field_202342_t = register("jungle_ground_bush", new ShrubFeature(BaseTreeFeatureConfig::deserializeJungle));
   public static final Feature<HugeTreeFeatureConfig> field_214551_w = register("dark_oak_tree", new DarkOakTreeFeature(HugeTreeFeatureConfig::deserializeDarkOak));
   public static final Feature<HugeTreeFeatureConfig> field_202302_B = register("mega_jungle_tree", new MegaJungleFeature(HugeTreeFeatureConfig::deserializeJungle));
   public static final Feature<HugeTreeFeatureConfig> field_202304_D = register("mega_spruce_tree", new MegaPineTree(HugeTreeFeatureConfig::deserializeSpruce));
   public static final FlowersFeature<BlockClusterFeatureConfig> FLOWER = register("flower", new DefaultFlowersFeature(BlockClusterFeatureConfig::func_227300_a_));
   public static final Feature<BlockClusterFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(BlockClusterFeatureConfig::func_227300_a_));
   public static final Feature<BlockStateProvidingFeatureConfig> BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockStateProvidingFeatureConfig::func_227269_a_));
   public static final Feature<LiquidsConfig> SPRING = register("spring_feature", new SpringFeature(LiquidsConfig::func_214677_a));
   public static final Feature<NoFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<ReplaceBlockConfig> EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfig::func_214657_a));
   public static final Feature<NoFeatureConfig> VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellsFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> FOSSIL = register("fossil", new FossilsFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<BigMushroomFeatureConfig> HUGE_RED_MUSHROOM = register("huge_red_mushroom", new BigRedMushroomFeature(BigMushroomFeatureConfig::func_222853_a));
   public static final Feature<BigMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new BigBrownMushroomFeature(BigMushroomFeatureConfig::func_222853_a));
   public static final Feature<NoFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new IceAndSnowFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> VINES = register("vines", new VinesFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonsFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<BlockStateFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(BlockStateFeatureConfig::func_227271_a_));
   public static final Feature<BlockBlobConfig> FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockBlobConfig::func_214682_a));
   public static final Feature<SphereReplaceConfig> DISK = register("disk", new SphereReplaceFeature(SphereReplaceConfig::func_214691_a));
   public static final Feature<FeatureRadiusConfig> ICE_PATCH = register("ice_patch", new IcePathFeature(FeatureRadiusConfig::func_214706_a));
   public static final Feature<BlockStateFeatureConfig> LAKE = register("lake", new LakesFeature(BlockStateFeatureConfig::func_227271_a_));
   public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig::func_214641_a));
   public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::func_214673_a));
   public static final Feature<NoFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<EndGatewayConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfig::func_214697_a));
   public static final Feature<SeaGrassConfig> SEAGRASS = register("seagrass", new SeaGrassFeature(SeaGrassConfig::func_214659_a));
   public static final Feature<NoFeatureConfig> KELP = register("kelp", new KelpFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<NoFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<CountConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountConfig::func_214687_a));
   public static final Feature<BlockWithContextConfig> SIMPLE_BLOCK = register("simple_block", new BlockWithContextFeature(BlockWithContextConfig::func_214663_a));
   public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig::func_214645_a));
   public static final Feature<FillLayerConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerConfig::func_214635_a));
   public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(NoFeatureConfig::func_214639_a));
   public static final Feature<MultipleWithChanceRandomFeatureConfig> field_202291_ak = register("random_random_selector", new MultipleRandomFeature(MultipleWithChanceRandomFeatureConfig::func_214653_a));
   public static final Feature<MultipleRandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new MultipleWithChanceRandomFeature(MultipleRandomFeatureConfig::func_214648_a));
   public static final Feature<SingleRandomFeature> SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SingleRandomFeatureConfig(SingleRandomFeature::func_214664_a));
   public static final Feature<TwoFeatureChoiceConfig> RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new TwoFeatureChoiceFeature(TwoFeatureChoiceConfig::func_227287_a_));
   public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig::func_214688_a));
   public static final Feature<DecoratedFeatureConfig> field_214484_aL = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::func_214688_a));
   public static final BiMap<String, Structure<?>> field_202300_at = Util.make(net.minecraftforge.registries.GameData.getStructureMap(), (p_205170_0_) -> {
      if (true) return; // Forge: This is now a slave map to the feature registry, leave this code here to reduce patch size
      p_205170_0_.put("Pillager_Outpost".toLowerCase(Locale.ROOT), field_214536_b);
      p_205170_0_.put("Mineshaft".toLowerCase(Locale.ROOT), field_202329_g);
      p_205170_0_.put("Mansion".toLowerCase(Locale.ROOT), field_202330_h);
      p_205170_0_.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), field_202331_i);
      p_205170_0_.put("Desert_Pyramid".toLowerCase(Locale.ROOT), field_202332_j);
      p_205170_0_.put("Igloo".toLowerCase(Locale.ROOT), field_202333_k);
      p_205170_0_.put("Shipwreck".toLowerCase(Locale.ROOT), field_204751_l);
      p_205170_0_.put("Swamp_Hut".toLowerCase(Locale.ROOT), field_202334_l);
      p_205170_0_.put("Stronghold".toLowerCase(Locale.ROOT), field_202335_m);
      p_205170_0_.put("Monument".toLowerCase(Locale.ROOT), field_202336_n);
      p_205170_0_.put("Ocean_Ruin".toLowerCase(Locale.ROOT), field_204029_o);
      p_205170_0_.put("Fortress".toLowerCase(Locale.ROOT), field_202337_o);
      p_205170_0_.put("EndCity".toLowerCase(Locale.ROOT), field_204292_r);
      p_205170_0_.put("Buried_Treasure".toLowerCase(Locale.ROOT), field_214549_o);
      p_205170_0_.put("Village".toLowerCase(Locale.ROOT), field_214550_p);
   });
   public static final List<Structure<?>> field_214488_aQ = ImmutableList.of(field_214536_b, field_214550_p);
   private final Function<Dynamic<?>, ? extends FC> field_214535_a;

   private static <C extends IFeatureConfig, F extends Feature<C>> F register(String p_214468_0_, F p_214468_1_) {
      return (F)(Registry.<Feature<?>>register(Registry.FEATURE, p_214468_0_, p_214468_1_));
   }

   public Feature(Function<Dynamic<?>, ? extends FC> p_i49878_1_) {
      this.field_214535_a = p_i49878_1_;
   }

   public ConfiguredFeature<FC, ?> configured(FC p_225566_1_) {
      return new ConfiguredFeature<>(this, p_225566_1_);
   }

   public FC func_214470_a(Dynamic<?> p_214470_1_) {
      return (FC)(this.field_214535_a.apply(p_214470_1_));
   }

   protected void func_202278_a(IWorldWriter p_202278_1_, BlockPos p_202278_2_, BlockState p_202278_3_) {
      p_202278_1_.setBlock(p_202278_2_, p_202278_3_, 3);
   }

   public abstract boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, FC p_212245_5_);

   public List<Biome.SpawnListEntry> getSpecialEnemies() {
      return Collections.emptyList();
   }

   public List<Biome.SpawnListEntry> getSpecialAnimals() {
      return Collections.emptyList();
   }

   protected static boolean isStone(Block p_227249_0_) {
      return net.minecraftforge.common.Tags.Blocks.STONE.func_199685_a_(p_227249_0_);
   }

   protected static boolean isDirt(Block p_227250_0_) {
      return net.minecraftforge.common.Tags.Blocks.DIRT.func_199685_a_(p_227250_0_);
   }
}
