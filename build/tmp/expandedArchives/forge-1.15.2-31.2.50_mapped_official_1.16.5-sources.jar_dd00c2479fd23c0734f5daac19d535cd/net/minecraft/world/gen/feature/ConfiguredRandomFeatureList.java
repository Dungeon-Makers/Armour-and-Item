package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ConfiguredRandomFeatureList<FC extends IFeatureConfig> {
   public final ConfiguredFeature<FC, ?> feature;
   public final float chance;

   public ConfiguredRandomFeatureList(ConfiguredFeature<FC, ?> p_i225822_1_, float p_i225822_2_) {
      this.feature = p_i225822_1_;
      this.chance = p_i225822_2_;
   }

   public <T> Dynamic<T> func_214841_a(DynamicOps<T> p_214841_1_) {
      return new Dynamic<>(p_214841_1_, p_214841_1_.createMap(ImmutableMap.of(p_214841_1_.createString("name"), p_214841_1_.createString(Registry.FEATURE.getKey(this.feature.feature).toString()), p_214841_1_.createString("config"), this.feature.config.func_214634_a(p_214841_1_).getValue(), p_214841_1_.createString("chance"), p_214841_1_.createFloat(this.chance))));
   }

   public boolean func_214839_a(IWorld p_214839_1_, ChunkGenerator<? extends GenerationSettings> p_214839_2_, Random p_214839_3_, BlockPos p_214839_4_) {
      return this.feature.func_222734_a(p_214839_1_, p_214839_2_, p_214839_3_, p_214839_4_);
   }

   public static <T> ConfiguredRandomFeatureList<?> func_214840_a(Dynamic<T> p_214840_0_) {
      return ConfiguredFeature.func_222736_a(p_214840_0_).weighted(p_214840_0_.get("chance").asFloat(0.0F));
   }
}