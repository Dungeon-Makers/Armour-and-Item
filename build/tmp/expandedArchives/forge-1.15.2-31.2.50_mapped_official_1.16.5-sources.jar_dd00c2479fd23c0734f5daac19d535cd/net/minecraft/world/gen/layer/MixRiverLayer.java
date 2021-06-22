package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   private static final int field_202720_c = Registry.field_212624_m.getId(Biomes.FROZEN_RIVER);
   private static final int field_202721_d = Registry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int field_202722_e = Registry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int field_202723_f = Registry.field_212624_m.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int field_202725_h = Registry.field_212624_m.getId(Biomes.RIVER);

   public int applyPixel(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.get(this.getParentX(p_215723_4_), this.getParentY(p_215723_5_));
      int j = p_215723_3_.get(this.getParentX(p_215723_4_), this.getParentY(p_215723_5_));
      if (LayerUtil.isOcean(i)) {
         return i;
      } else if (j == field_202725_h) {
         return Registry.field_212624_m.getId(Registry.field_212624_m.byId(i).getRiver());
      } else {
         return i;
      }
   }
}
