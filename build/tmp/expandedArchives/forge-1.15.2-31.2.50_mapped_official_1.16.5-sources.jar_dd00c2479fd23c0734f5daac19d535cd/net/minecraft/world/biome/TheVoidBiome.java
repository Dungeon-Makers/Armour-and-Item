package net.minecraft.world.biome;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class TheVoidBiome extends Biome {
   public TheVoidBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.NOPE, SurfaceBuilder.CONFIG_STONE).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_203611_a(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.VOID_START_PLATFORM.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
   }
}