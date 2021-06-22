package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class Placement<DC extends IPlacementConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Placement<?>> {
   public static final Placement<NoPlacementConfig> NOPE = register("nope", new Passthrough(NoPlacementConfig::func_214735_a));
   public static final Placement<FrequencyConfig> field_215015_a = register("count_heightmap", new AtSurface(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> field_215016_b = register("count_top_solid", new TopSolid(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> field_215017_c = register("count_heightmap_32", new SurfacePlus32(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> field_215018_d = register("count_heightmap_double", new TwiceSurface(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> field_215019_e = register("count_height_64", new AtHeight64(FrequencyConfig::func_214721_a));
   public static final Placement<NoiseDependant> field_215020_f = register("noise_heightmap_32", new SurfacePlus32WithNoise(NoiseDependant::func_214734_a));
   public static final Placement<NoiseDependant> field_215021_g = register("noise_heightmap_double", new TwiceSurfaceWithNoise(NoiseDependant::func_214734_a));
   public static final Placement<ChanceConfig> field_215023_i = register("chance_heightmap", new AtSurfaceWithChance(ChanceConfig::func_214722_a));
   public static final Placement<ChanceConfig> field_215024_j = register("chance_heightmap_double", new TwiceSurfaceWithChance(ChanceConfig::func_214722_a));
   public static final Placement<ChanceConfig> field_215025_k = register("chance_passthrough", new WithChance(ChanceConfig::func_214722_a));
   public static final Placement<ChanceConfig> field_215026_l = register("chance_top_solid_heightmap", new TopSolidWithChance(ChanceConfig::func_214722_a));
   public static final Placement<AtSurfaceWithExtraConfig> field_215027_m = register("count_extra_heightmap", new AtSurfaceWithExtra(AtSurfaceWithExtraConfig::func_214723_a));
   public static final Placement<CountRangeConfig> field_215028_n = register("count_range", new CountRange(CountRangeConfig::func_214733_a));
   public static final Placement<CountRangeConfig> field_215029_o = register("count_biased_range", new HeightBiasedRange(CountRangeConfig::func_214733_a));
   public static final Placement<CountRangeConfig> field_215030_p = register("count_very_biased_range", new HeightVeryBiasedRange(CountRangeConfig::func_214733_a));
   public static final Placement<CountRangeConfig> field_215031_q = register("random_count_range", new RandomCountWithRange(CountRangeConfig::func_214733_a));
   public static final Placement<ChanceRangeConfig> field_215032_r = register("chance_range", new ChanceRange(ChanceRangeConfig::func_214732_a));
   public static final Placement<HeightWithChanceConfig> field_215033_s = register("count_chance_heightmap", new AtSurfaceWithChanceMultiple(HeightWithChanceConfig::func_214724_a));
   public static final Placement<HeightWithChanceConfig> field_215034_t = register("count_chance_heightmap_double", new TwiceSurfaceWithChanceMultiple(HeightWithChanceConfig::func_214724_a));
   public static final Placement<DepthAverageConfig> field_215035_u = register("count_depth_average", new DepthAverage(DepthAverageConfig::func_214729_a));
   public static final Placement<NoPlacementConfig> TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", new TopSolidOnce(NoPlacementConfig::func_214735_a));
   public static final Placement<TopSolidRangeConfig> field_215037_w = register("top_solid_heightmap_range", new TopSolidRange(TopSolidRangeConfig::func_214725_a));
   public static final Placement<TopSolidWithNoiseConfig> field_215038_x = register("top_solid_heightmap_noise_biased", new TopSolidWithNoise(TopSolidWithNoiseConfig::func_214726_a));
   public static final Placement<CaveEdgeConfig> CARVING_MASK = register("carving_mask", new CaveEdge(CaveEdgeConfig::func_214720_a));
   public static final Placement<FrequencyConfig> field_215040_z = register("forest_rock", new AtSurfaceRandomCount(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> field_215002_A = register("hell_fire", new NetherFire(FrequencyConfig::func_214721_a));
   public static final Placement<FrequencyConfig> MAGMA = register("magma", new NetherMagma(FrequencyConfig::func_214721_a));
   public static final Placement<NoPlacementConfig> EMERALD_ORE = register("emerald_ore", new Height4To32(NoPlacementConfig::func_214735_a));
   public static final Placement<ChanceConfig> LAVA_LAKE = register("lava_lake", new LakeLava(ChanceConfig::func_214722_a));
   public static final Placement<ChanceConfig> WATER_LAKE = register("water_lake", new LakeWater(ChanceConfig::func_214722_a));
   public static final Placement<ChanceConfig> field_215007_F = register("dungeons", new DungeonRoom(ChanceConfig::func_214722_a));
   public static final Placement<NoPlacementConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreePlacement(NoPlacementConfig::func_214735_a));
   public static final Placement<ChanceConfig> ICEBERG = register("iceberg", new IcebergPlacement(ChanceConfig::func_214722_a));
   public static final Placement<FrequencyConfig> field_215010_I = register("light_gem_chance", new NetherGlowstone(FrequencyConfig::func_214721_a));
   public static final Placement<NoPlacementConfig> END_ISLAND = register("end_island", new EndIsland(NoPlacementConfig::func_214735_a));
   public static final Placement<NoPlacementConfig> field_215012_K = register("chorus_plant", new ChorusPlant(NoPlacementConfig::func_214735_a));
   public static final Placement<NoPlacementConfig> END_GATEWAY = register("end_gateway", new EndGateway(NoPlacementConfig::func_214735_a));
   private final Function<Dynamic<?>, ? extends DC> field_215014_M;

   private static <T extends IPlacementConfig, G extends Placement<T>> G register(String p_214999_0_, G p_214999_1_) {
      return (G)(Registry.<Placement<?>>register(Registry.DECORATOR, p_214999_0_, p_214999_1_));
   }

   public Placement(Function<Dynamic<?>, ? extends DC> p_i51371_1_) {
      this.field_215014_M = p_i51371_1_;
   }

   public DC func_215001_a(Dynamic<?> p_215001_1_) {
      return (DC)(this.field_215014_M.apply(p_215001_1_));
   }

   public ConfiguredPlacement<DC> configured(DC p_227446_1_) {
      return new ConfiguredPlacement<>(this, p_227446_1_);
   }

   protected <FC extends IFeatureConfig, F extends Feature<FC>> boolean func_214998_a(IWorld p_214998_1_, ChunkGenerator<? extends GenerationSettings> p_214998_2_, Random p_214998_3_, BlockPos p_214998_4_, DC p_214998_5_, ConfiguredFeature<FC, F> p_214998_6_) {
      AtomicBoolean atomicboolean = new AtomicBoolean(false);
      this.func_212848_a_(p_214998_1_, p_214998_2_, p_214998_3_, p_214998_5_, p_214998_4_).forEach((p_215000_5_) -> {
         boolean flag = p_214998_6_.func_222734_a(p_214998_1_, p_214998_2_, p_214998_3_, p_215000_5_);
         atomicboolean.set(atomicboolean.get() || flag);
      });
      return atomicboolean.get();
   }

   public abstract Stream<BlockPos> func_212848_a_(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, DC p_212848_4_, BlockPos p_212848_5_);

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   }
}
