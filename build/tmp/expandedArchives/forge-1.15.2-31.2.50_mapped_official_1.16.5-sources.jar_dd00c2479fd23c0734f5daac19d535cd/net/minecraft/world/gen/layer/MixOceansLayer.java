package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixOceansLayer implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   public int applyPixel(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.get(this.getParentX(p_215723_4_), this.getParentY(p_215723_5_));
      int j = p_215723_3_.get(this.getParentX(p_215723_4_), this.getParentY(p_215723_5_));
      if (!LayerUtil.isOcean(i)) {
         return i;
      } else {
         int k = 8;
         int l = 4;

         for(int i1 = -8; i1 <= 8; i1 += 4) {
            for(int j1 = -8; j1 <= 8; j1 += 4) {
               int k1 = p_215723_2_.get(this.getParentX(p_215723_4_ + i1), this.getParentY(p_215723_5_ + j1));
               if (!LayerUtil.isOcean(k1)) {
                  if (j == LayerUtil.field_203632_a) {
                     return LayerUtil.field_203633_b;
                  }

                  if (j == LayerUtil.field_202831_b) {
                     return LayerUtil.field_203634_d;
                  }
               }
            }
         }

         if (i == LayerUtil.field_202830_a) {
            if (j == LayerUtil.field_203633_b) {
               return LayerUtil.field_203636_g;
            }

            if (j == LayerUtil.field_202832_c) {
               return LayerUtil.field_202830_a;
            }

            if (j == LayerUtil.field_203634_d) {
               return LayerUtil.field_203637_i;
            }

            if (j == LayerUtil.field_202831_b) {
               return LayerUtil.field_203638_j;
            }
         }

         return j;
      }
   }
}