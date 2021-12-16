package io.github.dungeonmakers.armouranditem.core.world;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OreGen {
  private OreGen() {}

  protected static final List<PlacedFeature> OVERWORLD_ORES = new ArrayList<>();
  protected static final List<PlacedFeature> END_ORES = new ArrayList<>();
  protected static final List<PlacedFeature> NETHER_ORES = new ArrayList<>();

  public static final RuleTest END_TEST = new BlockMatchTest(Blocks.END_STONE);

  public static void registerOres() {

    final ConfiguredFeature<?, ?> blackDiamondOre =
        FeatureUtils
            .register("black_diamond_ore",
                Feature.ORE
                    .configured(new OreConfiguration(
                        List.of(
                            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES,
                                BlockInit.BLACK_DIAMOND_BLOCK.get().defaultBlockState()),
                            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
                                BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE.get().defaultBlockState())),
                        8)));

    final PlacedFeature placedBlackDiamondOre = PlacementUtils.register("black_diamond_ore",
        blackDiamondOre.placed(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(20)),
            InSquarePlacement.spread(), CountPlacement.of(100)));
    OVERWORLD_ORES.add(placedBlackDiamondOre);
  }

  @Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
  public static class ForgeBusSubscriber {
    private ForgeBusSubscriber() {}

    @SubscribeEvent
    public static void biomeLoading(BiomeLoadingEvent event) {
      final List<Supplier<PlacedFeature>> features =
          event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

      switch (event.getCategory()) {
        case NETHER -> OreGen.NETHER_ORES.forEach(ore -> features.add(() -> ore));
        case THEEND -> OreGen.END_ORES.forEach(ore -> features.add(() -> ore));
        default -> OreGen.OVERWORLD_ORES.forEach(ore -> features.add(() -> ore));
      }
    }
  }
}
