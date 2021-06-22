package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockplacer.ColumnBlockPlacer;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.AxisRotatingBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.PlainFlowerBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.BigMushroomFeatureConfig;
import net.minecraft.world.gen.feature.BlockBlobConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateProvidingFeatureConfig;
import net.minecraft.world.gen.feature.BlockWithContextConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.MultipleWithChanceRandomFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.gen.feature.SeaGrassConfig;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.TwoFeatureChoiceConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.PineFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.HeightWithChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.CocoaTreeDecorator;
import net.minecraft.world.gen.treedecorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;

public class DefaultBiomeFeatures {
   private static final BlockState field_226769_ad_ = Blocks.GRASS.defaultBlockState();
   private static final BlockState field_226770_ae_ = Blocks.FERN.defaultBlockState();
   private static final BlockState field_226771_af_ = Blocks.PODZOL.defaultBlockState();
   private static final BlockState field_226772_ag_ = Blocks.OAK_LOG.defaultBlockState();
   private static final BlockState field_226773_ah_ = Blocks.OAK_LEAVES.defaultBlockState();
   private static final BlockState field_226774_ai_ = Blocks.JUNGLE_LOG.defaultBlockState();
   private static final BlockState field_226775_aj_ = Blocks.JUNGLE_LEAVES.defaultBlockState();
   private static final BlockState field_226776_ak_ = Blocks.SPRUCE_LOG.defaultBlockState();
   private static final BlockState field_226777_al_ = Blocks.SPRUCE_LEAVES.defaultBlockState();
   private static final BlockState field_226778_am_ = Blocks.ACACIA_LOG.defaultBlockState();
   private static final BlockState field_226779_an_ = Blocks.ACACIA_LEAVES.defaultBlockState();
   private static final BlockState field_226780_ao_ = Blocks.BIRCH_LOG.defaultBlockState();
   private static final BlockState field_226781_ap_ = Blocks.BIRCH_LEAVES.defaultBlockState();
   private static final BlockState field_226782_aq_ = Blocks.DARK_OAK_LOG.defaultBlockState();
   private static final BlockState field_226783_ar_ = Blocks.DARK_OAK_LEAVES.defaultBlockState();
   private static final BlockState field_226784_as_ = Blocks.WATER.defaultBlockState();
   private static final BlockState field_226785_at_ = Blocks.LAVA.defaultBlockState();
   private static final BlockState field_226786_au_ = Blocks.DIRT.defaultBlockState();
   private static final BlockState field_226787_av_ = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState field_226788_aw_ = Blocks.GRANITE.defaultBlockState();
   private static final BlockState field_226789_ax_ = Blocks.DIORITE.defaultBlockState();
   private static final BlockState field_226790_ay_ = Blocks.ANDESITE.defaultBlockState();
   private static final BlockState field_226791_az_ = Blocks.COAL_ORE.defaultBlockState();
   private static final BlockState field_226740_aA_ = Blocks.IRON_ORE.defaultBlockState();
   private static final BlockState field_226741_aB_ = Blocks.GOLD_ORE.defaultBlockState();
   private static final BlockState field_226742_aC_ = Blocks.REDSTONE_ORE.defaultBlockState();
   private static final BlockState field_226743_aD_ = Blocks.DIAMOND_ORE.defaultBlockState();
   private static final BlockState field_226744_aE_ = Blocks.LAPIS_ORE.defaultBlockState();
   private static final BlockState field_226745_aF_ = Blocks.STONE.defaultBlockState();
   private static final BlockState field_226746_aG_ = Blocks.EMERALD_ORE.defaultBlockState();
   private static final BlockState field_226747_aH_ = Blocks.INFESTED_STONE.defaultBlockState();
   private static final BlockState field_226748_aI_ = Blocks.SAND.defaultBlockState();
   private static final BlockState field_226749_aJ_ = Blocks.CLAY.defaultBlockState();
   private static final BlockState field_226750_aK_ = Blocks.GRASS_BLOCK.defaultBlockState();
   private static final BlockState field_226751_aL_ = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
   private static final BlockState field_226752_aM_ = Blocks.LARGE_FERN.defaultBlockState();
   private static final BlockState field_226753_aN_ = Blocks.TALL_GRASS.defaultBlockState();
   private static final BlockState field_226754_aO_ = Blocks.LILAC.defaultBlockState();
   private static final BlockState field_226755_aP_ = Blocks.ROSE_BUSH.defaultBlockState();
   private static final BlockState field_226756_aQ_ = Blocks.PEONY.defaultBlockState();
   private static final BlockState field_226757_aR_ = Blocks.BROWN_MUSHROOM.defaultBlockState();
   private static final BlockState field_226758_aS_ = Blocks.RED_MUSHROOM.defaultBlockState();
   private static final BlockState field_226759_aT_ = Blocks.SEAGRASS.defaultBlockState();
   private static final BlockState field_226760_aU_ = Blocks.PACKED_ICE.defaultBlockState();
   private static final BlockState field_226761_aV_ = Blocks.BLUE_ICE.defaultBlockState();
   private static final BlockState field_226762_aW_ = Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
   private static final BlockState field_226763_aX_ = Blocks.BLUE_ORCHID.defaultBlockState();
   private static final BlockState field_226764_aY_ = Blocks.POPPY.defaultBlockState();
   private static final BlockState field_226765_aZ_ = Blocks.DANDELION.defaultBlockState();
   private static final BlockState field_226793_ba_ = Blocks.DEAD_BUSH.defaultBlockState();
   private static final BlockState field_226794_bb_ = Blocks.MELON.defaultBlockState();
   private static final BlockState field_226795_bc_ = Blocks.PUMPKIN.defaultBlockState();
   private static final BlockState field_226796_bd_ = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3));
   private static final BlockState field_226797_be_ = Blocks.FIRE.defaultBlockState();
   private static final BlockState field_226798_bf_ = Blocks.NETHERRACK.defaultBlockState();
   private static final BlockState field_226799_bg_ = Blocks.LILY_PAD.defaultBlockState();
   private static final BlockState field_226800_bh_ = Blocks.SNOW.defaultBlockState();
   private static final BlockState field_226801_bi_ = Blocks.JACK_O_LANTERN.defaultBlockState();
   private static final BlockState field_226802_bj_ = Blocks.SUNFLOWER.defaultBlockState();
   private static final BlockState field_226803_bk_ = Blocks.CACTUS.defaultBlockState();
   private static final BlockState field_226804_bl_ = Blocks.SUGAR_CANE.defaultBlockState();
   private static final BlockState field_226805_bm_ = Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
   private static final BlockState field_226806_bn_ = Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(true)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
   private static final BlockState field_226807_bo_ = Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));
   public static final TreeFeatureConfig field_226739_a_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_226792_b_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226774_ai_), new SimpleBlockStateProvider(field_226775_aj_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(8).func_227360_i_(3).func_227353_a_(ImmutableList.of(new CocoaTreeDecorator(0.2F), new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator())).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING).build();
   public static final TreeFeatureConfig field_226808_c_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226774_ai_), new SimpleBlockStateProvider(field_226775_aj_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(8).func_227360_i_(3).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING).build();
   public static final TreeFeatureConfig field_226809_d_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226776_ak_), new SimpleBlockStateProvider(field_226777_al_), new PineFoliagePlacer(1, 0))).func_225569_d_(7).func_227354_b_(4).func_227358_g_(1).func_227360_i_(3).func_227361_j_(1).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING).build();
   public static final TreeFeatureConfig field_226810_e_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226776_ak_), new SimpleBlockStateProvider(field_226777_al_), new SpruceFoliagePlacer(2, 1))).func_225569_d_(6).func_227354_b_(3).func_227356_e_(1).func_227357_f_(1).func_227359_h_(2).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING).build();
   public static final TreeFeatureConfig field_226811_f_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226778_am_), new SimpleBlockStateProvider(field_226779_an_), new AcaciaFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227355_c_(2).func_227356_e_(0).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.ACACIA_SAPLING).build();
   public static final TreeFeatureConfig field_226812_g_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226780_ao_), new SimpleBlockStateProvider(field_226781_ap_), new BlobFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227360_i_(3).func_227352_a_().setSapling((net.minecraftforge.common.IPlantable)Blocks.BIRCH_SAPLING).build();
   public static final TreeFeatureConfig field_230129_h_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226780_ao_), new SimpleBlockStateProvider(field_226781_ap_), new BlobFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.002F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.BIRCH_SAPLING).build();
   public static final TreeFeatureConfig field_230130_i_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226780_ao_), new SimpleBlockStateProvider(field_226781_ap_), new BlobFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227355_c_(6).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.002F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.BIRCH_SAPLING).build();
   public static final TreeFeatureConfig field_226814_i_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(3, 0))).func_225569_d_(5).func_227354_b_(3).func_227360_i_(3).func_227362_k_(1).func_227353_a_(ImmutableList.of(new LeaveVineTreeDecorator())).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_226815_j_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(0, 0))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_226816_k_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.05F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_230131_m_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(0, 0))).func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.002F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_226817_l_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(0, 0))).func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.05F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_230132_o_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.002F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_230133_p_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(2, 0))).func_225569_d_(4).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.02F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_230134_q_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226772_ag_), new SimpleBlockStateProvider(field_226773_ah_), new BlobFoliagePlacer(0, 0))).func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.02F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING).build();
   public static final TreeFeatureConfig field_230135_r_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226780_ao_), new SimpleBlockStateProvider(field_226781_ap_), new BlobFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.02F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.BIRCH_SAPLING).build();
   public static final TreeFeatureConfig field_230136_s_ = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226780_ao_), new SimpleBlockStateProvider(field_226781_ap_), new BlobFoliagePlacer(2, 0))).func_225569_d_(5).func_227354_b_(2).func_227360_i_(3).func_227352_a_().func_227353_a_(ImmutableList.of(new BeehiveTreeDecorator(0.05F))).setSapling((net.minecraftforge.common.IPlantable)Blocks.BIRCH_SAPLING).build();
   public static final BaseTreeFeatureConfig field_226821_p_ = (new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226774_ai_), new SimpleBlockStateProvider(field_226773_ah_))).func_225569_d_(4).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING).build();
   public static final HugeTreeFeatureConfig field_226822_q_ = (new HugeTreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226782_aq_), new SimpleBlockStateProvider(field_226783_ar_))).func_225569_d_(6).setSapling((net.minecraftforge.common.IPlantable)Blocks.DARK_OAK_SAPLING).build();
   public static final HugeTreeFeatureConfig field_226823_r_ = (new HugeTreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226776_ak_), new SimpleBlockStateProvider(field_226777_al_))).func_225569_d_(13).func_227283_b_(15).func_227284_c_(13).func_227282_a_(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(field_226771_af_)))).setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING).build();
   public static final HugeTreeFeatureConfig field_226824_s_ = (new HugeTreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226776_ak_), new SimpleBlockStateProvider(field_226777_al_))).func_225569_d_(13).func_227283_b_(15).func_227284_c_(3).func_227282_a_(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(field_226771_af_)))).setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING).build();
   public static final HugeTreeFeatureConfig field_226825_t_ = (new HugeTreeFeatureConfig.Builder(new SimpleBlockStateProvider(field_226774_ai_), new SimpleBlockStateProvider(field_226775_aj_))).func_225569_d_(10).func_227283_b_(20).func_227282_a_(ImmutableList.of(new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator())).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING).build();
   public static final BlockClusterFeatureConfig field_226826_u_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226769_ad_), new SimpleBlockPlacer())).tries(32).build();
   public static final BlockClusterFeatureConfig field_226827_v_ = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(field_226769_ad_, 1).add(field_226770_ae_, 4), new SimpleBlockPlacer())).tries(32).build();
   public static final BlockClusterFeatureConfig field_226828_w_ = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(field_226769_ad_, 3).add(field_226770_ae_, 1), new SimpleBlockPlacer())).blacklist(ImmutableSet.of(field_226771_af_)).tries(32).build();
   public static final BlockClusterFeatureConfig field_226829_x_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226762_aW_), new SimpleBlockPlacer())).tries(64).build();
   public static final BlockClusterFeatureConfig field_226830_y_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226763_aX_), new SimpleBlockPlacer())).tries(64).build();
   public static final BlockClusterFeatureConfig field_226831_z_ = (new BlockClusterFeatureConfig.Builder((new WeightedBlockStateProvider()).add(field_226764_aY_, 2).add(field_226765_aZ_, 1), new SimpleBlockPlacer())).tries(64).build();
   public static final BlockClusterFeatureConfig field_226713_A_ = (new BlockClusterFeatureConfig.Builder(new PlainFlowerBlockStateProvider(), new SimpleBlockPlacer())).tries(64).build();
   public static final BlockClusterFeatureConfig field_226714_B_ = (new BlockClusterFeatureConfig.Builder(new ForestFlowerBlockStateProvider(), new SimpleBlockPlacer())).tries(64).build();
   public static final BlockClusterFeatureConfig field_226715_C_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226793_ba_), new SimpleBlockPlacer())).tries(4).build();
   public static final BlockClusterFeatureConfig field_226716_D_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226794_bb_), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(field_226750_aK_.getBlock())).canReplace().noProjection().build();
   public static final BlockClusterFeatureConfig field_226717_E_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226795_bc_), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(field_226750_aK_.getBlock())).noProjection().build();
   public static final BlockClusterFeatureConfig field_226718_F_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226796_bd_), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(field_226750_aK_.getBlock())).noProjection().build();
   public static final BlockClusterFeatureConfig field_226719_G_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226797_be_), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(field_226798_bf_.getBlock())).noProjection().build();
   public static final BlockClusterFeatureConfig field_226720_H_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226799_bg_), new SimpleBlockPlacer())).tries(10).build();
   public static final BlockClusterFeatureConfig field_226721_I_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226758_aS_), new SimpleBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226722_J_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226757_aR_), new SimpleBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226723_K_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226754_aO_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226724_L_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226755_aP_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226725_M_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226756_aQ_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226726_N_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226802_bj_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226727_O_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226753_aN_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226728_P_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226752_aM_), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
   public static final BlockClusterFeatureConfig field_226729_Q_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226803_bk_), new ColumnBlockPlacer(1, 2))).tries(10).noProjection().build();
   public static final BlockClusterFeatureConfig field_226730_R_ = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(field_226804_bl_), new ColumnBlockPlacer(2, 2))).tries(20).xspread(4).yspread(0).zspread(4).noProjection().needWater().build();
   public static final BlockStateProvidingFeatureConfig field_226731_S_ = new BlockStateProvidingFeatureConfig(new AxisRotatingBlockStateProvider(Blocks.HAY_BLOCK));
   public static final BlockStateProvidingFeatureConfig field_226732_T_ = new BlockStateProvidingFeatureConfig(new SimpleBlockStateProvider(field_226800_bh_));
   public static final BlockStateProvidingFeatureConfig field_226733_U_ = new BlockStateProvidingFeatureConfig(new SimpleBlockStateProvider(field_226794_bb_));
   public static final BlockStateProvidingFeatureConfig field_226734_V_ = new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(field_226795_bc_, 19).add(field_226801_bi_, 1));
   public static final BlockStateProvidingFeatureConfig field_226735_W_ = new BlockStateProvidingFeatureConfig((new WeightedBlockStateProvider()).add(field_226761_aV_, 1).add(field_226760_aU_, 5));
   public static final LiquidsConfig field_226736_X_ = new LiquidsConfig(Fluids.WATER.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
   public static final LiquidsConfig field_226737_Y_ = new LiquidsConfig(Fluids.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
   public static final LiquidsConfig field_226738_Z_ = new LiquidsConfig(Fluids.LAVA.defaultFluidState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
   public static final LiquidsConfig field_226766_aa_ = new LiquidsConfig(Fluids.LAVA.defaultFluidState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
   public static final BigMushroomFeatureConfig field_226767_ab_ = new BigMushroomFeatureConfig(new SimpleBlockStateProvider(field_226805_bm_), new SimpleBlockStateProvider(field_226807_bo_), 2);
   public static final BigMushroomFeatureConfig field_226768_ac_ = new BigMushroomFeatureConfig(new SimpleBlockStateProvider(field_226806_bn_), new SimpleBlockStateProvider(field_226807_bo_), 3);

   public static void func_222300_a(Biome p_222300_0_) {
      p_222300_0_.func_203609_a(GenerationStage.Carving.AIR, Biome.func_203606_a(WorldCarver.CAVE, new ProbabilityConfig(0.14285715F)));
      p_222300_0_.func_203609_a(GenerationStage.Carving.AIR, Biome.func_203606_a(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
   }

   public static void func_222346_b(Biome p_222346_0_) {
      p_222346_0_.func_203609_a(GenerationStage.Carving.AIR, Biome.func_203606_a(WorldCarver.CAVE, new ProbabilityConfig(0.06666667F)));
      p_222346_0_.func_203609_a(GenerationStage.Carving.AIR, Biome.func_203606_a(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
      p_222346_0_.func_203609_a(GenerationStage.Carving.LIQUID, Biome.func_203606_a(WorldCarver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
      p_222346_0_.func_203609_a(GenerationStage.Carving.LIQUID, Biome.func_203606_a(WorldCarver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
   }

   public static void func_222295_c(Biome p_222295_0_) {
      p_222295_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Feature.field_202329_g.configured(new MineshaftConfig((double)0.004F, MineshaftStructure.Type.NORMAL)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_214536_b.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Feature.field_202335_m.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202334_l.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202332_j.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202331_i.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202333_k.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_204751_l.configured(new ShipwreckConfig(false)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202336_n.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_202330_h.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_204029_o.configured(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Feature.field_214549_o.configured(new BuriedTreasureConfig(0.01F)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      p_222295_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_214550_p.configured(new VillageConfig("village/plains/town_centers", 6)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
   }

   public static void func_222333_d(Biome p_222333_0_) {
      p_222333_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateFeatureConfig(field_226784_as_)).decorated(Placement.WATER_LAKE.configured(new ChanceConfig(4))));
      p_222333_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateFeatureConfig(field_226785_at_)).decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(80))));
   }

   public static void func_222301_e(Biome p_222301_0_) {
      p_222301_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateFeatureConfig(field_226785_at_)).decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(80))));
   }

   public static void func_222335_f(Biome p_222335_0_) {
      p_222335_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Feature.MONSTER_ROOM.configured(IFeatureConfig.NONE).decorated(Placement.field_215007_F.configured(new ChanceConfig(8))));
   }

   public static void func_222326_g(Biome p_222326_0_) {
      p_222326_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226786_au_, 33)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(10, 0, 0, 256))));
      p_222326_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226787_av_, 33)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(8, 0, 0, 256))));
      p_222326_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226788_aw_, 33)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(10, 0, 0, 80))));
      p_222326_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226789_ax_, 33)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(10, 0, 0, 80))));
      p_222326_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226790_ay_, 33)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(10, 0, 0, 80))));
   }

   public static void func_222288_h(Biome p_222288_0_) {
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226791_az_, 17)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(20, 0, 0, 128))));
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226740_aA_, 9)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(20, 0, 0, 64))));
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226741_aB_, 9)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(2, 0, 0, 32))));
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226742_aC_, 8)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(8, 0, 0, 16))));
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226743_aD_, 8)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(1, 0, 0, 16))));
      p_222288_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226744_aE_, 7)).decorated(Placement.field_215035_u.configured(new DepthAverageConfig(1, 16, 16))));
   }

   public static void func_222328_i(Biome p_222328_0_) {
      p_222328_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226741_aB_, 9)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(20, 32, 32, 80))));
   }

   public static void func_222291_j(Biome p_222291_0_) {
      p_222291_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.EMERALD_ORE.configured(new ReplaceBlockConfig(field_226745_aF_, field_226746_aG_)).decorated(Placement.EMERALD_ORE.configured(IPlacementConfig.NONE)));
   }

   public static void func_222322_k(Biome p_222322_0_) {
      p_222322_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, field_226747_aH_, 9)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(7, 0, 0, 64))));
   }

   public static void func_222282_l(Biome p_222282_0_) {
      p_222282_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(field_226748_aI_, 7, 2, Lists.newArrayList(field_226786_au_, field_226750_aK_))).decorated(Placement.field_215016_b.configured(new FrequencyConfig(3))));
      p_222282_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(field_226749_aJ_, 4, 1, Lists.newArrayList(field_226786_au_, field_226749_aJ_))).decorated(Placement.field_215016_b.configured(new FrequencyConfig(1))));
      p_222282_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(field_226787_av_, 6, 2, Lists.newArrayList(field_226786_au_, field_226750_aK_))).decorated(Placement.field_215016_b.configured(new FrequencyConfig(1))));
   }

   public static void func_222318_m(Biome p_222318_0_) {
      p_222318_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(field_226749_aJ_, 4, 1, Lists.newArrayList(field_226786_au_, field_226749_aJ_))).decorated(Placement.field_215016_b.configured(new FrequencyConfig(1))));
   }

   public static void func_222313_n(Biome p_222313_0_) {
      p_222313_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.FOREST_ROCK.configured(new BlockBlobConfig(field_226751_aL_, 0)).decorated(Placement.field_215040_z.configured(new FrequencyConfig(3))));
   }

   public static void func_222345_o(Biome p_222345_0_) {
      p_222345_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226728_P_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(7))));
   }

   public static void func_222307_p(Biome p_222307_0_) {
      p_222307_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226718_F_).decorated(Placement.field_215024_j.configured(new ChanceConfig(12))));
   }

   public static void func_222341_q(Biome p_222341_0_) {
      p_222341_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226718_F_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
   }

   public static void func_222289_r(Biome p_222289_0_) {
      p_222289_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.BAMBOO.configured(new ProbabilityConfig(0.0F)).decorated(Placement.field_215018_d.configured(new FrequencyConfig(16))));
   }

   public static void func_222325_s(Biome p_222325_0_) {
      p_222325_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.BAMBOO.configured(new ProbabilityConfig(0.2F)).decorated(Placement.field_215038_x.configured(new TopSolidWithNoiseConfig(160, 80.0D, 0.3D, Heightmap.Type.WORLD_SURFACE_WG))));
      p_222325_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202339_q.configured(field_226815_j_).weighted(0.05F), Feature.field_202342_t.configured(field_226821_p_).weighted(0.15F), Feature.field_202302_B.configured(field_226825_t_).weighted(0.7F)), Feature.RANDOM_PATCH.configured(field_226828_w_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(30, 0.1F, 1))));
   }

   public static void func_222293_t(Biome p_222293_0_) {
      p_222293_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(field_226809_d_).weighted(0.33333334F)), Feature.field_202301_A.configured(field_226810_e_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222296_u(Biome p_222296_0_) {
      p_222296_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202339_q.configured(field_226815_j_).weighted(0.1F)), Feature.field_202301_A.configured(field_226739_a_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   }

   public static void func_222330_v(Biome p_222330_0_) {
      p_222330_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202301_A.configured(field_230129_h_).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222302_w(Biome p_222302_0_) {
      p_222302_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(field_230129_h_).weighted(0.2F), Feature.field_202339_q.configured(field_230131_m_).weighted(0.1F)), Feature.field_202301_A.configured(field_230132_o_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222336_x(Biome p_222336_0_) {
      p_222336_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(field_230130_i_).weighted(0.5F)), Feature.field_202301_A.configured(field_230129_h_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222310_y(Biome p_222310_0_) {
      p_222310_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_227246_s_.configured(field_226811_f_).weighted(0.8F)), Feature.field_202301_A.configured(field_226739_a_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(1, 0.1F, 1))));
   }

   public static void func_222347_z(Biome p_222347_0_) {
      p_222347_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_227246_s_.configured(field_226811_f_).weighted(0.8F)), Feature.field_202301_A.configured(field_226739_a_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
   }

   public static void func_222343_A(Biome p_222343_0_) {
      p_222343_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(field_226810_e_).weighted(0.666F), Feature.field_202339_q.configured(field_226815_j_).weighted(0.1F)), Feature.field_202301_A.configured(field_226739_a_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   }

   public static void func_222304_B(Biome p_222304_0_) {
      p_222304_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(field_226810_e_).weighted(0.666F), Feature.field_202339_q.configured(field_226815_j_).weighted(0.1F)), Feature.field_202301_A.configured(field_226739_a_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(3, 0.1F, 1))));
   }

   public static void func_222323_C(Biome p_222323_0_) {
      p_222323_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202339_q.configured(field_226815_j_).weighted(0.1F), Feature.field_202342_t.configured(field_226821_p_).weighted(0.5F), Feature.field_202302_B.configured(field_226825_t_).weighted(0.33333334F)), Feature.field_202301_A.configured(field_226792_b_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(50, 0.1F, 1))));
   }

   public static void func_222290_D(Biome p_222290_0_) {
      p_222290_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202339_q.configured(field_226815_j_).weighted(0.1F), Feature.field_202342_t.configured(field_226821_p_).weighted(0.5F)), Feature.field_202301_A.configured(field_226792_b_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
   }

   public static void func_222327_E(Biome p_222327_0_) {
      p_222327_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202301_A.configured(field_226739_a_).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(5, 0.1F, 1))));
   }

   public static void func_222284_F(Biome p_222284_0_) {
      p_222284_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202301_A.configured(field_226810_e_).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(0, 0.1F, 1))));
   }

   public static void func_222316_G(Biome p_222316_0_) {
      p_222316_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202304_D.configured(field_226823_r_).weighted(0.33333334F), Feature.field_202301_A.configured(field_226809_d_).weighted(0.33333334F)), Feature.field_202301_A.configured(field_226810_e_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222285_H(Biome p_222285_0_) {
      p_222285_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202304_D.configured(field_226823_r_).weighted(0.025641026F), Feature.field_202304_D.configured(field_226824_s_).weighted(0.30769232F), Feature.field_202301_A.configured(field_226809_d_).weighted(0.33333334F)), Feature.field_202301_A.configured(field_226810_e_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
   }

   public static void func_222321_I(Biome p_222321_0_) {
      p_222321_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226828_w_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(25))));
   }

   public static void func_222344_J(Biome p_222344_0_) {
      p_222344_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226727_O_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(7))));
   }

   public static void func_222314_K(Biome p_222314_0_) {
      p_222314_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(5))));
   }

   public static void func_222339_L(Biome p_222339_0_) {
      p_222339_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(20))));
   }

   public static void func_222308_M(Biome p_222308_0_) {
      p_222308_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
      p_222308_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226715_C_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(20))));
   }

   public static void func_222338_N(Biome p_222338_0_) {
      p_222338_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202291_ak.configured(new MultipleWithChanceRandomFeatureConfig(ImmutableList.of(Feature.RANDOM_PATCH.configured(field_226723_K_), Feature.RANDOM_PATCH.configured(field_226724_L_), Feature.RANDOM_PATCH.configured(field_226725_M_), Feature.FLOWER.configured(field_226829_x_)), 0)).decorated(Placement.field_215017_c.configured(new FrequencyConfig(5))));
   }

   public static void func_222298_O(Biome p_222298_0_) {
      p_222298_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(2))));
   }

   public static void func_222331_P(Biome p_222331_0_) {
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202301_A.configured(field_226814_i_).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(2, 0.1F, 1))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(field_226830_y_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(1))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(5))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226715_C_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226720_H_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(4))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226722_J_).decorated(Placement.field_215033_s.configured(new HeightWithChanceConfig(8, 0.25F))));
      p_222331_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226721_I_).decorated(Placement.field_215034_t.configured(new HeightWithChanceConfig(8, 0.125F))));
   }

   public static void func_222294_Q(Biome p_222294_0_) {
      p_222294_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_BOOLEAN_SELECTOR.configured(new TwoFeatureChoiceConfig(Feature.HUGE_RED_MUSHROOM.configured(field_226767_ab_), Feature.HUGE_BROWN_MUSHROOM.configured(field_226768_ac_))).decorated(Placement.field_215015_a.configured(new FrequencyConfig(1))));
      p_222294_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226722_J_).decorated(Placement.field_215033_s.configured(new HeightWithChanceConfig(1, 0.25F))));
      p_222294_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226721_I_).decorated(Placement.field_215034_t.configured(new HeightWithChanceConfig(1, 0.125F))));
   }

   public static void func_222299_R(Biome p_222299_0_) {
      p_222299_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202339_q.configured(field_226817_l_).weighted(0.33333334F)), Feature.field_202301_A.configured(field_226816_k_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));
      p_222299_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(field_226713_A_).decorated(Placement.field_215020_f.configured(new NoiseDependant(-0.8D, 15, 4))));
      p_222299_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215021_g.configured(new NoiseDependant(-0.8D, 5, 10))));
   }

   public static void func_222334_S(Biome p_222334_0_) {
      p_222334_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226715_C_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(2))));
   }

   public static void func_222303_T(Biome p_222303_0_) {
      p_222303_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226827_v_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(7))));
      p_222303_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226715_C_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
      p_222303_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226722_J_).decorated(Placement.field_215033_s.configured(new HeightWithChanceConfig(3, 0.25F))));
      p_222303_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226721_I_).decorated(Placement.field_215034_t.configured(new HeightWithChanceConfig(3, 0.125F))));
   }

   public static void func_222342_U(Biome p_222342_0_) {
      p_222342_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(field_226831_z_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(2))));
   }

   public static void func_222306_V(Biome p_222306_0_) {
      p_222306_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(field_226831_z_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(4))));
   }

   public static void func_222348_W(Biome p_222348_0_) {
      p_222348_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226826_u_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
   }

   public static void func_222319_X(Biome p_222319_0_) {
      p_222319_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226827_v_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
      p_222319_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226722_J_).decorated(Placement.field_215033_s.configured(new HeightWithChanceConfig(1, 0.25F))));
      p_222319_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226721_I_).decorated(Placement.field_215034_t.configured(new HeightWithChanceConfig(1, 0.125F))));
   }

   public static void func_222283_Y(Biome p_222283_0_) {
      p_222283_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226727_O_).decorated(Placement.field_215020_f.configured(new NoiseDependant(-0.8D, 0, 7))));
   }

   public static void func_222315_Z(Biome p_222315_0_) {
      p_222315_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226722_J_).decorated(Placement.field_215024_j.configured(new ChanceConfig(4))));
      p_222315_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226721_I_).decorated(Placement.field_215024_j.configured(new ChanceConfig(8))));
   }

   public static void func_222311_aa(Biome p_222311_0_) {
      p_222311_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226730_R_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(10))));
      p_222311_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226717_E_).decorated(Placement.field_215024_j.configured(new ChanceConfig(32))));
   }

   public static void func_222286_ab(Biome p_222286_0_) {
      p_222286_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226730_R_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(13))));
      p_222286_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226717_E_).decorated(Placement.field_215024_j.configured(new ChanceConfig(32))));
      p_222286_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226729_Q_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(5))));
   }

   public static void func_222324_ac(Biome p_222324_0_) {
      p_222324_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226716_D_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(1))));
      p_222324_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.VINES.configured(IFeatureConfig.NONE).decorated(Placement.field_215019_e.configured(new FrequencyConfig(50))));
   }

   public static void func_222292_ad(Biome p_222292_0_) {
      p_222292_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226730_R_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(60))));
      p_222292_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226717_E_).decorated(Placement.field_215024_j.configured(new ChanceConfig(32))));
      p_222292_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226729_Q_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(10))));
   }

   public static void func_222329_ae(Biome p_222329_0_) {
      p_222329_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226730_R_).decorated(Placement.field_215018_d.configured(new FrequencyConfig(20))));
      p_222329_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(field_226717_E_).decorated(Placement.field_215024_j.configured(new ChanceConfig(32))));
   }

   public static void func_222281_af(Biome p_222281_0_) {
      p_222281_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.DESERT_WELL.configured(IFeatureConfig.NONE).decorated(Placement.field_215023_i.configured(new ChanceConfig(1000))));
      p_222281_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.FOSSIL.configured(IFeatureConfig.NONE).decorated(Placement.field_215025_k.configured(new ChanceConfig(64))));
   }

   public static void func_222317_ag(Biome p_222317_0_) {
      p_222317_0_.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.FOSSIL.configured(IFeatureConfig.NONE).decorated(Placement.field_215025_k.configured(new ChanceConfig(64))));
   }

   public static void func_222287_ah(Biome p_222287_0_) {
      p_222287_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.KELP.configured(IFeatureConfig.NONE).decorated(Placement.field_215038_x.configured(new TopSolidWithNoiseConfig(120, 80.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));
   }

   public static void func_222320_ai(Biome p_222320_0_) {
      p_222320_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(field_226759_aT_, new BlockState[]{field_226745_aF_}, new BlockState[]{field_226784_as_}, new BlockState[]{field_226784_as_})).decorated(Placement.CARVING_MASK.configured(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.1F))));
   }

   public static void func_222309_aj(Biome p_222309_0_) {
      p_222309_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.configured(new SeaGrassConfig(80, 0.3D)).decorated(Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE)));
   }

   public static void func_222340_ak(Biome p_222340_0_) {
      p_222340_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.configured(new SeaGrassConfig(80, 0.8D)).decorated(Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE)));
   }

   public static void func_222312_al(Biome p_222312_0_) {
      p_222312_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.KELP.configured(IFeatureConfig.NONE).decorated(Placement.field_215038_x.configured(new TopSolidWithNoiseConfig(80, 80.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));
   }

   public static void func_222337_am(Biome p_222337_0_) {
      p_222337_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SPRING.configured(field_226736_X_).decorated(Placement.field_215029_o.configured(new CountRangeConfig(50, 8, 8, 256))));
      p_222337_0_.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SPRING.configured(field_226737_Y_).decorated(Placement.field_215030_p.configured(new CountRangeConfig(20, 8, 16, 256))));
   }

   public static void func_222305_an(Biome p_222305_0_) {
      p_222305_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.ICEBERG.configured(new BlockStateFeatureConfig(field_226760_aU_)).decorated(Placement.ICEBERG.configured(new ChanceConfig(16))));
      p_222305_0_.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.ICEBERG.configured(new BlockStateFeatureConfig(field_226761_aV_)).decorated(Placement.ICEBERG.configured(new ChanceConfig(200))));
   }

   public static void func_222332_ao(Biome p_222332_0_) {
      p_222332_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.BLUE_ICE.configured(IFeatureConfig.NONE).decorated(Placement.field_215031_q.configured(new CountRangeConfig(20, 30, 32, 64))));
   }

   public static void func_222297_ap(Biome p_222297_0_) {
      p_222297_0_.func_203611_a(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.FREEZE_TOP_LAYER.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
   }

   public static void func_225489_aq(Biome p_225489_0_) {
      p_225489_0_.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.field_204292_r.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
   }
}
