package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class SpruceTree extends BigTree {
   @Nullable
   protected ConfiguredFeature<TreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      return Feature.field_202301_A.configured(DefaultBiomeFeatures.field_226810_e_);
   }

   @Nullable
   protected ConfiguredFeature<HugeTreeFeatureConfig, ?> getConfiguredMegaFeature(Random p_225547_1_) {
      return Feature.field_202304_D.configured(p_225547_1_.nextBoolean() ? DefaultBiomeFeatures.field_226823_r_ : DefaultBiomeFeatures.field_226824_s_);
   }
}