package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class TheEndBiome extends Biome {
   public TheEndBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_THEEND).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, Feature.END_SPIKE.configured(new EndSpikeFeatureConfig(false, ImmutableList.of(), (BlockPos)null)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
      DefaultBiomeFeatures.func_225489_aq(this);
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 4, 4));
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColor() {
      return 0;
   }
}