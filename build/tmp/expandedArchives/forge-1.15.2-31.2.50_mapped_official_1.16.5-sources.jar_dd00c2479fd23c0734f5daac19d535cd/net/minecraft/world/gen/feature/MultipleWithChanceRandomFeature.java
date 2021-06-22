package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class MultipleWithChanceRandomFeature extends Feature<MultipleRandomFeatureConfig> {
   public MultipleWithChanceRandomFeature(Function<Dynamic<?>, ? extends MultipleRandomFeatureConfig> p_i51447_1_) {
      super(p_i51447_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, MultipleRandomFeatureConfig p_212245_5_) {
      for(ConfiguredRandomFeatureList<?> configuredrandomfeaturelist : p_212245_5_.features) {
         if (p_212245_3_.nextFloat() < configuredrandomfeaturelist.chance) {
            return configuredrandomfeaturelist.func_214839_a(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
         }
      }

      return p_212245_5_.defaultFeature.func_222734_a(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }
}