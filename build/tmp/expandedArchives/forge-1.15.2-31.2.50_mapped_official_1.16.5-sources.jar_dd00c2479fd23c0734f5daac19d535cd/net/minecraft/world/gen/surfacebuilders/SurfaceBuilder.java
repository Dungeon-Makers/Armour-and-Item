package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public abstract class SurfaceBuilder<C extends ISurfaceBuilderConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<SurfaceBuilder<?>> {
   public static final BlockState field_215409_f = Blocks.AIR.defaultBlockState();
   public static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
   public static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
   public static final BlockState PODZOL = Blocks.PODZOL.defaultBlockState();
   public static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   public static final BlockState STONE = Blocks.STONE.defaultBlockState();
   public static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
   public static final BlockState SAND = Blocks.SAND.defaultBlockState();
   public static final BlockState RED_SAND = Blocks.RED_SAND.defaultBlockState();
   public static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   public static final BlockState MYCELIUM = Blocks.MYCELIUM.defaultBlockState();
   public static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
   public static final BlockState ENDSTONE = Blocks.END_STONE.defaultBlockState();
   public static final SurfaceBuilderConfig field_215422_s = new SurfaceBuilderConfig(field_215409_f, field_215409_f, field_215409_f);
   public static final SurfaceBuilderConfig CONFIG_PODZOL = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_GRAVEL = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_GRASS = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig field_215426_w = new SurfaceBuilderConfig(DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_STONE = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_COARSE_DIRT = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_DESERT = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_OCEAN_SAND = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
   public static final SurfaceBuilderConfig CONFIG_FULL_SAND = new SurfaceBuilderConfig(SAND, SAND, SAND);
   public static final SurfaceBuilderConfig CONFIG_BADLANDS = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_MYCELIUM = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_HELL = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
   public static final SurfaceBuilderConfig CONFIG_THEEND = new SurfaceBuilderConfig(ENDSTONE, ENDSTONE, ENDSTONE);
   public static final SurfaceBuilder<SurfaceBuilderConfig> DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavannaSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   public static final SurfaceBuilder<SurfaceBuilderConfig> NOPE = register("nope", new NoopSurfaceBuilder(SurfaceBuilderConfig::func_215455_a));
   private final Function<Dynamic<?>, ? extends C> field_215408_a;

   private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String p_215389_0_, F p_215389_1_) {
      return (F)(Registry.<SurfaceBuilder<?>>register(Registry.SURFACE_BUILDER, p_215389_0_, p_215389_1_));
   }

   public SurfaceBuilder(Function<Dynamic<?>, ? extends C> p_i51305_1_) {
      this.field_215408_a = p_i51305_1_;
   }

   public abstract void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, C p_205610_14_);

   public void initNoise(long p_205548_1_) {
   }
}
