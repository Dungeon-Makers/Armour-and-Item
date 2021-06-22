package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum OceanLayer implements IAreaTransformer0 {
   INSTANCE;

   public int applyPixel(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_) {
      ImprovedNoiseGenerator improvednoisegenerator = p_215735_1_.getBiomeNoise();
      double d0 = improvednoisegenerator.noise((double)p_215735_2_ / 8.0D, (double)p_215735_3_ / 8.0D, 0.0D, 0.0D, 0.0D);
      if (d0 > 0.4D) {
         return LayerUtil.field_203632_a;
      } else if (d0 > 0.2D) {
         return LayerUtil.field_203633_b;
      } else if (d0 < -0.4D) {
         return LayerUtil.field_202831_b;
      } else {
         return d0 < -0.2D ? LayerUtil.field_203634_d : LayerUtil.field_202832_c;
      }
   }
}