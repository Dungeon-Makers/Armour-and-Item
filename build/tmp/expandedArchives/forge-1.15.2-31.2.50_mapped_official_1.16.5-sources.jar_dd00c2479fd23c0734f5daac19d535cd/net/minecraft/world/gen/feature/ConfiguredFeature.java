package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends IFeatureConfig, F extends Feature<FC>> {
   public static final Logger LOGGER = LogManager.getLogger();
   public final F feature;
   public final FC config;

   public ConfiguredFeature(F p_i49900_1_, FC p_i49900_2_) {
      this.feature = p_i49900_1_;
      this.config = p_i49900_2_;
   }

   public ConfiguredFeature(F p_i49901_1_, Dynamic<?> p_i49901_2_) {
      this(p_i49901_1_, (FC)p_i49901_1_.func_214470_a(p_i49901_2_));
   }

   public ConfiguredFeature<?, ?> decorated(ConfiguredPlacement<?> p_227228_1_) {
      Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowersFeature ? Feature.field_214484_aL : Feature.DECORATED;
      return feature.configured(new DecoratedFeatureConfig(this, p_227228_1_));
   }

   public ConfiguredRandomFeatureList<FC> weighted(float p_227227_1_) {
      return new ConfiguredRandomFeatureList<>(this, p_227227_1_);
   }

   public <T> Dynamic<T> func_222735_a(DynamicOps<T> p_222735_1_) {
      return new Dynamic<>(p_222735_1_, p_222735_1_.createMap(ImmutableMap.of(p_222735_1_.createString("name"), p_222735_1_.createString(Registry.FEATURE.getKey(this.feature).toString()), p_222735_1_.createString("config"), this.config.func_214634_a(p_222735_1_).getValue())));
   }

   public boolean func_222734_a(IWorld p_222734_1_, ChunkGenerator<? extends GenerationSettings> p_222734_2_, Random p_222734_3_, BlockPos p_222734_4_) {
      return this.feature.func_212245_a(p_222734_1_, p_222734_2_, p_222734_3_, p_222734_4_, this.config);
   }

   public static <T> ConfiguredFeature<?, ?> func_222736_a(Dynamic<T> p_222736_0_) {
      String s = p_222736_0_.get("name").asString("");
      Feature<? extends IFeatureConfig> feature = Registry.FEATURE.get(new ResourceLocation(s));

      try {
         return new ConfiguredFeature<>(feature, p_222736_0_.get("config").orElseEmptyMap());
      } catch (RuntimeException var4) {
         LOGGER.warn("Error while deserializing {}", (Object)s);
         return new ConfiguredFeature<>(Feature.NO_OP, NoFeatureConfig.NONE);
      }
   }
}