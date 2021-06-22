package net.minecraft.world.biome;

import java.util.Collections;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public abstract class Biomes {
   public static final Biome OCEAN = func_222369_a(0, "ocean", new OceanBiome());
   @net.minecraftforge.registries.ObjectHolder("minecraft:ocean")
   public static final Biome field_180279_ad = OCEAN;
   public static final Biome PLAINS = func_222369_a(1, "plains", new PlainsBiome());
   public static final Biome DESERT = func_222369_a(2, "desert", new DesertBiome());
   public static final Biome MOUNTAINS = func_222369_a(3, "mountains", new MountainsBiome());
   public static final Biome FOREST = func_222369_a(4, "forest", new ForestBiome());
   public static final Biome TAIGA = func_222369_a(5, "taiga", new TaigaBiome());
   public static final Biome SWAMP = func_222369_a(6, "swamp", new SwampBiome());
   public static final Biome RIVER = func_222369_a(7, "river", new RiverBiome());
   public static final Biome field_76778_j = func_222369_a(8, "nether", new NetherBiome());
   public static final Biome THE_END = func_222369_a(9, "the_end", new TheEndBiome());
   public static final Biome FROZEN_OCEAN = func_222369_a(10, "frozen_ocean", new FrozenOceanBiome());
   public static final Biome FROZEN_RIVER = func_222369_a(11, "frozen_river", new FrozenRiverBiome());
   public static final Biome SNOWY_TUNDRA = func_222369_a(12, "snowy_tundra", new SnowyTundraBiome());
   public static final Biome SNOWY_MOUNTAINS = func_222369_a(13, "snowy_mountains", new SnowyMountainsBiome());
   public static final Biome MUSHROOM_FIELDS = func_222369_a(14, "mushroom_fields", new MushroomFieldsBiome());
   public static final Biome MUSHROOM_FIELD_SHORE = func_222369_a(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
   public static final Biome BEACH = func_222369_a(16, "beach", new BeachBiome());
   public static final Biome DESERT_HILLS = func_222369_a(17, "desert_hills", new DesertHillsBiome());
   public static final Biome WOODED_HILLS = func_222369_a(18, "wooded_hills", new WoodedHillsBiome());
   public static final Biome TAIGA_HILLS = func_222369_a(19, "taiga_hills", new TaigaHillsBiome());
   public static final Biome MOUNTAIN_EDGE = func_222369_a(20, "mountain_edge", new MountainEdgeBiome());
   public static final Biome JUNGLE = func_222369_a(21, "jungle", new JungleBiome());
   public static final Biome JUNGLE_HILLS = func_222369_a(22, "jungle_hills", new JungleHillsBiome());
   public static final Biome JUNGLE_EDGE = func_222369_a(23, "jungle_edge", new JungleEdgeBiome());
   public static final Biome DEEP_OCEAN = func_222369_a(24, "deep_ocean", new DeepOceanBiome());
   public static final Biome STONE_SHORE = func_222369_a(25, "stone_shore", new StoneShoreBiome());
   public static final Biome SNOWY_BEACH = func_222369_a(26, "snowy_beach", new SnowyBeachBiome());
   public static final Biome BIRCH_FOREST = func_222369_a(27, "birch_forest", new BirchForestBiome());
   public static final Biome BIRCH_FOREST_HILLS = func_222369_a(28, "birch_forest_hills", new BirchForestHillsBiome());
   public static final Biome DARK_FOREST = func_222369_a(29, "dark_forest", new DarkForestBiome());
   public static final Biome SNOWY_TAIGA = func_222369_a(30, "snowy_taiga", new SnowyTaigaBiome());
   public static final Biome SNOWY_TAIGA_HILLS = func_222369_a(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
   public static final Biome GIANT_TREE_TAIGA = func_222369_a(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
   public static final Biome GIANT_TREE_TAIGA_HILLS = func_222369_a(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
   public static final Biome WOODED_MOUNTAINS = func_222369_a(34, "wooded_mountains", new WoodedMountainsBiome());
   public static final Biome SAVANNA = func_222369_a(35, "savanna", new SavannaBiome());
   public static final Biome SAVANNA_PLATEAU = func_222369_a(36, "savanna_plateau", new SavannaPlateauBiome());
   public static final Biome BADLANDS = func_222369_a(37, "badlands", new BadlandsBiome());
   public static final Biome WOODED_BADLANDS_PLATEAU = func_222369_a(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
   public static final Biome BADLANDS_PLATEAU = func_222369_a(39, "badlands_plateau", new BadlandsPlateauBiome());
   public static final Biome SMALL_END_ISLANDS = func_222369_a(40, "small_end_islands", new SmallEndIslandsBiome());
   public static final Biome END_MIDLANDS = func_222369_a(41, "end_midlands", new EndMidlandsBiome());
   public static final Biome END_HIGHLANDS = func_222369_a(42, "end_highlands", new EndHighlandsBiome());
   public static final Biome END_BARRENS = func_222369_a(43, "end_barrens", new EndBarrensBiome());
   public static final Biome WARM_OCEAN = func_222369_a(44, "warm_ocean", new WarmOceanBiome());
   public static final Biome LUKEWARM_OCEAN = func_222369_a(45, "lukewarm_ocean", new LukewarmOceanBiome());
   public static final Biome COLD_OCEAN = func_222369_a(46, "cold_ocean", new ColdOceanBiome());
   public static final Biome DEEP_WARM_OCEAN = func_222369_a(47, "deep_warm_ocean", new DeepWarmOceanBiome());
   public static final Biome DEEP_LUKEWARM_OCEAN = func_222369_a(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
   public static final Biome DEEP_COLD_OCEAN = func_222369_a(49, "deep_cold_ocean", new DeepColdOceanBiome());
   public static final Biome DEEP_FROZEN_OCEAN = func_222369_a(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
   public static final Biome THE_VOID = func_222369_a(127, "the_void", new TheVoidBiome());
   public static final Biome SUNFLOWER_PLAINS = func_222369_a(129, "sunflower_plains", new SunflowerPlainsBiome());
   public static final Biome DESERT_LAKES = func_222369_a(130, "desert_lakes", new DesertLakesBiome());
   public static final Biome GRAVELLY_MOUNTAINS = func_222369_a(131, "gravelly_mountains", new GravellyMountainsBiome());
   public static final Biome FLOWER_FOREST = func_222369_a(132, "flower_forest", new FlowerForestBiome());
   public static final Biome TAIGA_MOUNTAINS = func_222369_a(133, "taiga_mountains", new TaigaMountainsBiome());
   public static final Biome SWAMP_HILLS = func_222369_a(134, "swamp_hills", new SwampHillsBiome());
   public static final Biome ICE_SPIKES = func_222369_a(140, "ice_spikes", new IceSpikesBiome());
   public static final Biome MODIFIED_JUNGLE = func_222369_a(149, "modified_jungle", new ModifiedJungleBiome());
   public static final Biome MODIFIED_JUNGLE_EDGE = func_222369_a(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
   public static final Biome TALL_BIRCH_FOREST = func_222369_a(155, "tall_birch_forest", new TallBirchForestBiome());
   public static final Biome TALL_BIRCH_HILLS = func_222369_a(156, "tall_birch_hills", new TallBirchHillsBiome());
   public static final Biome DARK_FOREST_HILLS = func_222369_a(157, "dark_forest_hills", new DarkForestHillsBiome());
   public static final Biome SNOWY_TAIGA_MOUNTAINS = func_222369_a(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
   public static final Biome GIANT_SPRUCE_TAIGA = func_222369_a(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
   public static final Biome GIANT_SPRUCE_TAIGA_HILLS = func_222369_a(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
   public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = func_222369_a(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
   public static final Biome SHATTERED_SAVANNA = func_222369_a(163, "shattered_savanna", new ShatteredSavannaBiome());
   public static final Biome SHATTERED_SAVANNA_PLATEAU = func_222369_a(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
   public static final Biome ERODED_BADLANDS = func_222369_a(165, "eroded_badlands", new ErodedBadlandsBiome());
   public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = func_222369_a(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
   public static final Biome MODIFIED_BADLANDS_PLATEAU = func_222369_a(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
   public static final Biome BAMBOO_JUNGLE = func_222369_a(168, "bamboo_jungle", new BambooJungleBiome());
   public static final Biome BAMBOO_JUNGLE_HILLS = func_222369_a(169, "bamboo_jungle_hills", new BambooJungleHillsBiome());

   private static Biome func_222369_a(int p_222369_0_, String p_222369_1_, Biome p_222369_2_) {
      Registry.registerMapping(Registry.field_212624_m, p_222369_0_, p_222369_1_, p_222369_2_);
      if (p_222369_2_.func_185363_b()) {
         Biome.field_185373_j.addMapping(p_222369_2_, Registry.field_212624_m.getId(Registry.field_212624_m.get(new ResourceLocation(p_222369_2_.field_185364_H))));
      }

      return p_222369_2_;
   }

   static {
      Collections.addAll(Biome.field_201870_ab, OCEAN, PLAINS, DESERT, MOUNTAINS, FOREST, TAIGA, SWAMP, RIVER, FROZEN_RIVER, SNOWY_TUNDRA, SNOWY_MOUNTAINS, MUSHROOM_FIELDS, MUSHROOM_FIELD_SHORE, BEACH, DESERT_HILLS, WOODED_HILLS, TAIGA_HILLS, JUNGLE, JUNGLE_HILLS, JUNGLE_EDGE, DEEP_OCEAN, STONE_SHORE, SNOWY_BEACH, BIRCH_FOREST, BIRCH_FOREST_HILLS, DARK_FOREST, SNOWY_TAIGA, SNOWY_TAIGA_HILLS, GIANT_TREE_TAIGA, GIANT_TREE_TAIGA_HILLS, WOODED_MOUNTAINS, SAVANNA, SAVANNA_PLATEAU, BADLANDS, WOODED_BADLANDS_PLATEAU, BADLANDS_PLATEAU);
   }
}
