package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OreGen {
  public static final List<PlacedFeature> OVERWORLD_ORES = new ArrayList<>();

  public static final ConfiguredFeature<?, ?> TEST_CF =
      Feature.ORE.configured(new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), // Blocks
                                                                                                    // which
                                                                                                    // the
                                                                                                    // ore
                                                                                                    // can
                                                                                                    // replace
          BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE.get().defaultBlockState(), // The ore
          10, // Size of vein
          0.0f // Unsure atm
      ));


  public static final PlacedFeature TEST_PF = TEST_CF.placed(CountPlacement.of(10), // Attempts per
                                                                                    // chunk
      InSquarePlacement.spread(), // Causes the randomness in the ore veins
      HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(50), // Min height for ore to spawn
                                                                   // (worldMinHeight + height)
          VerticalAnchor.belowTop(50)), // Max height for it to spawn (worldMaxHeight - height)
      BiomeFilter.biome() // Allows for ore to spawn correctly
  );


  @Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
  public static class ForgeEvents {

    @SubscribeEvent
    public static void biomeLoading(@NotNull BiomeLoadingEvent event) {
      final var features =
          event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

      OreGen.OVERWORLD_ORES.forEach(ore -> features.add(() -> ore));
    }
  }

  // These methods are to be called in the main class
  public static void registerPlaced() {
    Registry<PlacedFeature> registry = BuiltinRegistries.PLACED_FEATURE;

    OVERWORLD_ORES.add(Registry.register(registry,
        new ResourceLocation(ArmourAndItem.MOD_ID, "deepslate_black_diamond_ore"), OreGen.TEST_PF));
  }

  public static void registerConfigured() {
    Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;

    Registry.register(registry,
        new ResourceLocation(ArmourAndItem.MOD_ID, "deepslate_black_diamond_ore"), OreGen.TEST_CF);
  }
}
