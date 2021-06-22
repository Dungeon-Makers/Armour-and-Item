package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class MushroomFieldShoreBiome extends Biome {
   public MushroomFieldShoreBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_MYCELIUM).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.MUSHROOM).depth(0.0F).scale(0.025F).temperature(0.9F).downfall(1.0F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_226711_a_(Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.func_226711_a_(Feature.field_202335_m.configured(IFeatureConfig.NONE));
      DefaultBiomeFeatures.func_222300_a(this);
      DefaultBiomeFeatures.func_222295_c(this);
      DefaultBiomeFeatures.func_222333_d(this);
      DefaultBiomeFeatures.func_222335_f(this);
      DefaultBiomeFeatures.func_222326_g(this);
      DefaultBiomeFeatures.func_222288_h(this);
      DefaultBiomeFeatures.func_222282_l(this);
      DefaultBiomeFeatures.func_222294_Q(this);
      DefaultBiomeFeatures.func_222315_Z(this);
      DefaultBiomeFeatures.func_222311_aa(this);
      DefaultBiomeFeatures.func_222337_am(this);
      DefaultBiomeFeatures.func_222297_ap(this);
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.MOOSHROOM, 8, 4, 8));
      this.func_201866_a(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
   }
}