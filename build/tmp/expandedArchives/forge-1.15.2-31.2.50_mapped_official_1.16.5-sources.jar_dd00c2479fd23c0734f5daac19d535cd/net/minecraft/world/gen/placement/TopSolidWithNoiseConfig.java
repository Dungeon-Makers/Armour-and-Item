package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.Heightmap;

public class TopSolidWithNoiseConfig implements IPlacementConfig {
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;
   public final Heightmap.Type field_214728_d;

   public TopSolidWithNoiseConfig(int p_i51376_1_, double p_i51376_2_, double p_i51376_4_, Heightmap.Type p_i51376_6_) {
      this.noiseToCountRatio = p_i51376_1_;
      this.noiseFactor = p_i51376_2_;
      this.noiseOffset = p_i51376_4_;
      this.field_214728_d = p_i51376_6_;
   }

   public <T> Dynamic<T> func_214719_a(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("noise_to_count_ratio"), p_214719_1_.createInt(this.noiseToCountRatio), p_214719_1_.createString("noise_factor"), p_214719_1_.createDouble(this.noiseFactor), p_214719_1_.createString("noise_offset"), p_214719_1_.createDouble(this.noiseOffset), p_214719_1_.createString("heightmap"), p_214719_1_.createString(this.field_214728_d.getSerializationKey()))));
   }

   public static TopSolidWithNoiseConfig func_214726_a(Dynamic<?> p_214726_0_) {
      int i = p_214726_0_.get("noise_to_count_ratio").asInt(10);
      double d0 = p_214726_0_.get("noise_factor").asDouble(80.0D);
      double d1 = p_214726_0_.get("noise_offset").asDouble(0.0D);
      Heightmap.Type heightmap$type = Heightmap.Type.getFromKey(p_214726_0_.get("heightmap").asString("OCEAN_FLOOR_WG"));
      return new TopSolidWithNoiseConfig(i, d0, d1, heightmap$type);
   }
}