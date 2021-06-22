package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class JungleTree extends BigTree {
   @Nullable
   protected ConfiguredFeature<TreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      return (new TreeFeature(TreeFeatureConfig::deserializeJungle)).configured(DefaultBiomeFeatures.field_226808_c_);
   }

   @Nullable
   protected ConfiguredFeature<HugeTreeFeatureConfig, ?> getConfiguredMegaFeature(Random p_225547_1_) {
      return Feature.field_202302_B.configured(DefaultBiomeFeatures.field_226825_t_);
   }
}
