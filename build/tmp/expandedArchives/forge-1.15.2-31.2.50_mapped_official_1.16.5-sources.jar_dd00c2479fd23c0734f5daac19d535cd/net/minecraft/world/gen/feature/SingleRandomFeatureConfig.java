package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SingleRandomFeatureConfig extends Feature<SingleRandomFeature> {
   public SingleRandomFeatureConfig(Function<Dynamic<?>, ? extends SingleRandomFeature> p_i51436_1_) {
      super(p_i51436_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SingleRandomFeature p_212245_5_) {
      int i = p_212245_3_.nextInt(p_212245_5_.features.size());
      ConfiguredFeature<?, ?> configuredfeature = p_212245_5_.features.get(i);
      return configuredfeature.func_222734_a(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }
}