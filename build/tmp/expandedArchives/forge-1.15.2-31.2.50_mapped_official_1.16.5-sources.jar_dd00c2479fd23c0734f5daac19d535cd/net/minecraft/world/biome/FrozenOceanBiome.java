package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class FrozenOceanBiome extends Biome {
   protected static final PerlinNoiseGenerator field_205163_aV = new PerlinNoiseGenerator(new SharedSeedRandom(3456L), 2, 0);

   public FrozenOceanBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.RainType.SNOW).biomeCategory(Biome.Category.OCEAN).depth(-1.0F).scale(0.1F).temperature(0.0F).downfall(0.5F).func_205412_a(3750089).func_205413_b(329011).func_205418_a((String)null));
      this.func_226711_a_(Feature.field_204029_o.configured(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)));
      this.func_226711_a_(Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.func_226711_a_(Feature.field_204751_l.configured(new ShipwreckConfig(false)));
      DefaultBiomeFeatures.func_222346_b(this);
      DefaultBiomeFeatures.func_222295_c(this);
      DefaultBiomeFeatures.func_222333_d(this);
      DefaultBiomeFeatures.func_222305_an(this);
      DefaultBiomeFeatures.func_222335_f(this);
      DefaultBiomeFeatures.func_222332_ao(this);
      DefaultBiomeFeatures.func_222326_g(this);
      DefaultBiomeFeatures.func_222288_h(this);
      DefaultBiomeFeatures.func_222282_l(this);
      DefaultBiomeFeatures.func_222296_u(this);
      DefaultBiomeFeatures.func_222342_U(this);
      DefaultBiomeFeatures.func_222348_W(this);
      DefaultBiomeFeatures.func_222315_Z(this);
      DefaultBiomeFeatures.func_222311_aa(this);
      DefaultBiomeFeatures.func_222337_am(this);
      DefaultBiomeFeatures.func_222297_ap(this);
      this.func_201866_a(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SQUID, 1, 1, 4));
      this.func_201866_a(EntityClassification.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SALMON, 15, 1, 5));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.POLAR_BEAR, 1, 1, 2));
      this.func_201866_a(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.DROWNED, 5, 1, 1));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
   }

   public float func_180626_a(BlockPos p_180626_1_) {
      float f = this.func_185353_n();
      double d0 = field_205163_aV.getValue((double)p_180626_1_.getX() * 0.05D, (double)p_180626_1_.getZ() * 0.05D, false) * 7.0D;
      double d1 = BIOME_INFO_NOISE.getValue((double)p_180626_1_.getX() * 0.2D, (double)p_180626_1_.getZ() * 0.2D, false);
      double d2 = d0 + d1;
      if (d2 < 0.3D) {
         double d3 = BIOME_INFO_NOISE.getValue((double)p_180626_1_.getX() * 0.09D, (double)p_180626_1_.getZ() * 0.09D, false);
         if (d3 < 0.8D) {
            f = 0.2F;
         }
      }

      if (p_180626_1_.getY() > 64) {
         float f1 = (float)(TEMPERATURE_NOISE.getValue((double)((float)p_180626_1_.getX() / 8.0F), (double)((float)p_180626_1_.getZ() / 8.0F), false) * 4.0D);
         return f - (f1 + (float)p_180626_1_.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return f;
      }
   }
}