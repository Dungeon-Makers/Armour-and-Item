package com.yusuf.armouranditem.core.init;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.yusuf.realyusufismailcore.core.init.BlockInitCore;

public class FeatureInit {
    public static void addOres(final BiomeLoadingEvent event) {

        addOre(event, OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                BlockInit.BLACK_DIAMOND_ORE.get().defaultBlockState(), 4, 0, 14, 5);

        addOre(event, OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                BlockInit.PURPLE_DIAMOND_BLOCK.get().defaultBlockState(), 3, 0, 13, 5);

        addOre(event, OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                BlockInitCore.COPPER_ORE.get().defaultBlockState(), 5, 0, 17, 6);

    }

    public static void addOre(final BiomeLoadingEvent event, RuleTest rule, BlockState state, int veinSize,
                              int minHeight, int maxHeight, int amount) {
        event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.configured(new OreFeatureConfig(rule, state, veinSize))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(minHeight, 0, maxHeight)))
                        .squared().count(amount));
    }
}
