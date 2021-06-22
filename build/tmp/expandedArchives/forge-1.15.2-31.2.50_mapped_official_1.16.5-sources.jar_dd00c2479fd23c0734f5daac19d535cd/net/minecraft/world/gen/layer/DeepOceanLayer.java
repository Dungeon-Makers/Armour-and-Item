package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum DeepOceanLayer implements ICastleTransformer {
   INSTANCE;

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      if (LayerUtil.isShallowOcean(p_202748_6_)) {
         int i = 0;
         if (LayerUtil.isShallowOcean(p_202748_2_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_3_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_5_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_4_)) {
            ++i;
         }

         if (i > 3) {
            if (p_202748_6_ == LayerUtil.field_203632_a) {
               return LayerUtil.field_203635_f;
            }

            if (p_202748_6_ == LayerUtil.field_203633_b) {
               return LayerUtil.field_203636_g;
            }

            if (p_202748_6_ == LayerUtil.field_202832_c) {
               return LayerUtil.field_202830_a;
            }

            if (p_202748_6_ == LayerUtil.field_203634_d) {
               return LayerUtil.field_203637_i;
            }

            if (p_202748_6_ == LayerUtil.field_202831_b) {
               return LayerUtil.field_203638_j;
            }

            return LayerUtil.field_202830_a;
         }
      }

      return p_202748_6_;
   }
}