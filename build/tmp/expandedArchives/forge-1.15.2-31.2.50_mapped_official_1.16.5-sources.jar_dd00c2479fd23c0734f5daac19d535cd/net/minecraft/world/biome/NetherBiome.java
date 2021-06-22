package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class NetherBiome extends Biome {
   protected NetherBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.NETHER, SurfaceBuilder.CONFIG_HELL).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_226711_a_(Feature.field_202337_o.configured(IFeatureConfig.NONE));
      this.func_203609_a(GenerationStage.Carving.AIR, func_203606_a(WorldCarver.field_222710_b, new ProbabilityConfig(0.2F)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SPRING.configured(DefaultBiomeFeatures.field_226737_Y_).decorated(Placement.field_215030_p.configured(new CountRangeConfig(20, 8, 16, 256))));
      DefaultBiomeFeatures.func_222315_Z(this);
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.field_202337_o.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.SPRING.configured(DefaultBiomeFeatures.field_226738_Z_).decorated(Placement.field_215028_n.configured(new CountRangeConfig(8, 4, 8, 128))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226719_G_).decorated(Placement.field_215002_A.configured(new FrequencyConfig(10))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.configured(IFeatureConfig.NONE).decorated(Placement.field_215010_I.configured(new FrequencyConfig(10))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.GLOWSTONE_BLOB.configured(IFeatureConfig.NONE).decorated(Placement.field_215028_n.configured(new CountRangeConfig(10, 0, 0, 128))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226722_J_).decorated(Placement.field_215032_r.configured(new ChanceRangeConfig(0.5F, 0, 0, 128))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226721_I_).decorated(Placement.field_215032_r.configured(new ChanceRangeConfig(0.5F, 0, 0, 128))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14)).decorated(Placement.field_215028_n.configured(new CountRangeConfig(16, 10, 20, 128))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Blocks.MAGMA_BLOCK.defaultBlockState(), 33)).decorated(Placement.MAGMA.configured(new FrequencyConfig(4))));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.SPRING.configured(DefaultBiomeFeatures.field_226766_aa_).decorated(Placement.field_215028_n.configured(new CountRangeConfig(16, 10, 20, 128))));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.GHAST, 50, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.field_200785_Y, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 1, 4, 4));
   }
}