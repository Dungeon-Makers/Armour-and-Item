package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class SwampHillsBiome extends Biome {
   protected SwampHillsBiome() {
      super((new Biome.Builder()).func_222351_a(SurfaceBuilder.SWAMP, SurfaceBuilder.CONFIG_GRASS).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.SWAMP).depth(-0.1F).scale(0.3F).temperature(0.8F).downfall(0.9F).func_205412_a(6388580).func_205413_b(2302743).func_205418_a("swamp"));
      this.func_226711_a_(Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
      DefaultBiomeFeatures.func_222300_a(this);
      DefaultBiomeFeatures.func_222295_c(this);
      DefaultBiomeFeatures.func_222333_d(this);
      DefaultBiomeFeatures.func_222335_f(this);
      DefaultBiomeFeatures.func_222326_g(this);
      DefaultBiomeFeatures.func_222288_h(this);
      DefaultBiomeFeatures.func_222318_m(this);
      DefaultBiomeFeatures.func_222331_P(this);
      DefaultBiomeFeatures.func_222315_Z(this);
      DefaultBiomeFeatures.func_222329_ae(this);
      DefaultBiomeFeatures.func_222337_am(this);
      DefaultBiomeFeatures.func_222317_ag(this);
      DefaultBiomeFeatures.func_222297_ap(this);
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
      this.func_201866_a(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
      this.func_201866_a(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
      this.func_201866_a(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 1, 1, 1));
   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(double p_225528_1_, double p_225528_3_) {
      double d0 = BIOME_INFO_NOISE.getValue(p_225528_1_ * 0.0225D, p_225528_3_ * 0.0225D, false);
      return d0 < -0.1D ? 5011004 : 6975545;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor() {
      return 6975545;
   }
}