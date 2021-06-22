package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddBambooForestLayer implements IC1Transformer {
   INSTANCE;

   private static final int field_215732_b = Registry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int field_215733_c = Registry.field_212624_m.getId(Biomes.BAMBOO_JUNGLE);

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      return p_202716_1_.nextRandom(10) == 0 && p_202716_2_ == field_215732_b ? field_215733_c : p_202716_2_;
   }
}