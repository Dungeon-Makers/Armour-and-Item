package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.MultipleWithChanceRandomFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public final class FlowerForestBiome extends Biome {
   public FlowerForestBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.FOREST).depth(0.1F).scale(0.4F).temperature(0.7F).downfall(0.8F).func_205412_a(4159204).func_205413_b(329011).func_205418_a("forest"));
      this.func_226711_a_(Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      this.func_226711_a_(Feature.field_202335_m.configured(IFeatureConfig.NONE));
      DefaultBiomeFeatures.func_222300_a(this);
      DefaultBiomeFeatures.func_222295_c(this);
      DefaultBiomeFeatures.func_222333_d(this);
      DefaultBiomeFeatures.func_222335_f(this);
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.field_202291_ak.configured(new MultipleWithChanceRandomFeatureConfig(ImmutableList.of(Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226723_K_), Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226724_L_), Feature.RANDOM_PATCH.configured(DefaultBiomeFeatures.field_226725_M_), Feature.FLOWER.configured(DefaultBiomeFeatures.field_226829_x_)), 2)).decorated(Placement.field_215017_c.configured(new FrequencyConfig(5))));
      DefaultBiomeFeatures.func_222326_g(this);
      DefaultBiomeFeatures.func_222288_h(this);
      DefaultBiomeFeatures.func_222282_l(this);
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_202301_A.configured(DefaultBiomeFeatures.field_230135_r_).weighted(0.2F), Feature.field_202339_q.configured(DefaultBiomeFeatures.field_230134_q_).weighted(0.1F)), Feature.field_202301_A.configured(DefaultBiomeFeatures.field_230133_p_))).decorated(Placement.field_215027_m.configured(new AtSurfaceWithExtraConfig(6, 0.1F, 1))));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(DefaultBiomeFeatures.field_226714_B_).decorated(Placement.field_215017_c.configured(new FrequencyConfig(100))));
      DefaultBiomeFeatures.func_222348_W(this);
      DefaultBiomeFeatures.func_222315_Z(this);
      DefaultBiomeFeatures.func_222311_aa(this);
      DefaultBiomeFeatures.func_222337_am(this);
      DefaultBiomeFeatures.func_222297_ap(this);
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.RABBIT, 4, 2, 3));
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