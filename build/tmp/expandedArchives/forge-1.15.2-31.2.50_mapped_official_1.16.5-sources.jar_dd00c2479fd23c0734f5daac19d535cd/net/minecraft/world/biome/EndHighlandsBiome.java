package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndHighlandsBiome extends Biome {
   public EndHighlandsBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_THEEND).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_226711_a_(Feature.field_204292_r.configured(IFeatureConfig.NONE));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.END_GATEWAY.configured(EndGatewayConfig.knownExit(EndDimension.field_209958_g, true)).decorated(Placement.END_GATEWAY.configured(IPlacementConfig.NONE)));
      DefaultBiomeFeatures.func_225489_aq(this);
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.CHORUS_PLANT.configured(IFeatureConfig.NONE).decorated(Placement.field_215012_K.configured(IPlacementConfig.NONE)));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 4, 4));
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColor() {
      return 0;
   }
}