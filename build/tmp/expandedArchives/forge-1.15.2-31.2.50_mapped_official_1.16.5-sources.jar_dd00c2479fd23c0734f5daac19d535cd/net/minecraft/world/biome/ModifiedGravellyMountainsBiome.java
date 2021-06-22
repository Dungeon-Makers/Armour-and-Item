package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class ModifiedGravellyMountainsBiome extends Biome {
   protected ModifiedGravellyMountainsBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.GRAVELLY_MOUNTAIN, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.EXTREME_HILLS).depth(1.0F).scale(0.5F).temperature(0.2F).downfall(0.3F).func_205412_a(4159204).func_205413_b(329011).func_205418_a("wooded_mountains"));
      this.func_226711_a_(Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.func_226711_a_(Feature.field_202335_m.configured(IFeatureConfig.NONE));
      DefaultBiomeFeatures.func_222300_a(this);
      DefaultBiomeFeatures.func_222295_c(this);
      DefaultBiomeFeatures.func_222333_d(this);
      DefaultBiomeFeatures.func_222335_f(this);
      DefaultBiomeFeatures.func_222326_g(this);
      DefaultBiomeFeatures.func_222288_h(this);
      DefaultBiomeFeatures.func_222282_l(this);
      DefaultBiomeFeatures.func_222343_A(this);
      DefaultBiomeFeatures.func_222342_U(this);
      DefaultBiomeFeatures.func_222348_W(this);
      DefaultBiomeFeatures.func_222315_Z(this);
      DefaultBiomeFeatures.func_222311_aa(this);
      DefaultBiomeFeatures.func_222337_am(this);
      DefaultBiomeFeatures.func_222291_j(this);
      DefaultBiomeFeatures.func_222322_k(this);
      DefaultBiomeFeatures.func_222297_ap(this);
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.LLAMA, 5, 4, 6));
      this.func_201866_a(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
   }
}