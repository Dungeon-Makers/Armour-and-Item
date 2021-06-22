package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class DarkOakTree extends BigTree {
   @Nullable
   protected ConfiguredFeature<TreeFeatureConfig, ?> getConfiguredFeature(Random p_225546_1_, boolean p_225546_2_) {
      return null;
   }

   @Nullable
   protected ConfiguredFeature<HugeTreeFeatureConfig, ?> getConfiguredMegaFeature(Random p_225547_1_) {
      return Feature.field_214551_w.configured(DefaultBiomeFeatures.field_226822_q_);
   }
}