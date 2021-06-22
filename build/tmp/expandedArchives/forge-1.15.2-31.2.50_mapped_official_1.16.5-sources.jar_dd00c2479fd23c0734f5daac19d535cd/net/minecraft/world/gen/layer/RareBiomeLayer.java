package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum RareBiomeLayer implements IC1Transformer {
   INSTANCE;

   private static final int field_202717_b = Registry.field_212624_m.getId(Biomes.PLAINS);
   private static final int field_202718_c = Registry.field_212624_m.getId(Biomes.SUNFLOWER_PLAINS);

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      return p_202716_1_.nextRandom(57) == 0 && p_202716_2_ == field_202717_b ? field_202718_c : p_202716_2_;
   }
}