package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum HillsLayer implements IAreaTransformer2, IDimOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final int field_202796_c = Registry.field_212624_m.getId(Biomes.BIRCH_FOREST);
   private static final int field_202797_d = Registry.field_212624_m.getId(Biomes.BIRCH_FOREST_HILLS);
   private static final int field_202799_f = Registry.field_212624_m.getId(Biomes.DESERT);
   private static final int field_202800_g = Registry.field_212624_m.getId(Biomes.DESERT_HILLS);
   private static final int field_202801_h = Registry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int field_202802_i = Registry.field_212624_m.getId(Biomes.WOODED_MOUNTAINS);
   private static final int field_202803_j = Registry.field_212624_m.getId(Biomes.FOREST);
   private static final int field_202804_k = Registry.field_212624_m.getId(Biomes.WOODED_HILLS);
   private static final int field_202805_l = Registry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int field_202806_m = Registry.field_212624_m.getId(Biomes.SNOWY_MOUNTAINS);
   private static final int field_202807_n = Registry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int field_202808_o = Registry.field_212624_m.getId(Biomes.JUNGLE_HILLS);
   private static final int field_215729_o = Registry.field_212624_m.getId(Biomes.BAMBOO_JUNGLE);
   private static final int field_215730_p = Registry.field_212624_m.getId(Biomes.BAMBOO_JUNGLE_HILLS);
   private static final int field_202809_p = Registry.field_212624_m.getId(Biomes.BADLANDS);
   private static final int field_202810_q = Registry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int field_202812_s = Registry.field_212624_m.getId(Biomes.PLAINS);
   private static final int field_202813_t = Registry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int field_202814_u = Registry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
   private static final int field_202815_v = Registry.field_212624_m.getId(Biomes.DARK_FOREST);
   private static final int field_202816_w = Registry.field_212624_m.getId(Biomes.SAVANNA);
   private static final int field_202817_x = Registry.field_212624_m.getId(Biomes.SAVANNA_PLATEAU);
   private static final int field_202818_y = Registry.field_212624_m.getId(Biomes.TAIGA);
   private static final int field_202819_z = Registry.field_212624_m.getId(Biomes.SNOWY_TAIGA);
   private static final int field_202794_A = Registry.field_212624_m.getId(Biomes.SNOWY_TAIGA_HILLS);
   private static final int field_202795_B = Registry.field_212624_m.getId(Biomes.TAIGA_HILLS);

   public int applyPixel(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.get(this.getParentX(p_215723_4_ + 1), this.getParentY(p_215723_5_ + 1));
      int j = p_215723_3_.get(this.getParentX(p_215723_4_ + 1), this.getParentY(p_215723_5_ + 1));
      if (i > 255) {
         LOGGER.debug("old! {}", (int)i);
      }

      int k = (j - 2) % 29;
      if (!LayerUtil.isShallowOcean(i) && j >= 2 && k == 1) {
         Biome biome = Registry.field_212624_m.byId(i);
         if (biome == null || !biome.func_185363_b()) {
            Biome biome2 = Biome.func_185356_b(biome);
            return biome2 == null ? i : Registry.field_212624_m.getId(biome2);
         }
      }

      if (p_215723_1_.nextRandom(3) == 0 || k == 0) {
         int l = i;
         Biome biome = Registry.field_212624_m.byId(i);
         Biome hill = biome == null ? null : biome.getHill(p_215723_1_);
         if (hill != null) l = Registry.field_212624_m.getId(hill);
         else if (i == field_202799_f) {
            l = field_202800_g;
         } else if (i == field_202803_j) {
            l = field_202804_k;
         } else if (i == field_202796_c) {
            l = field_202797_d;
         } else if (i == field_202815_v) {
            l = field_202812_s;
         } else if (i == field_202818_y) {
            l = field_202795_B;
         } else if (i == field_202813_t) {
            l = field_202814_u;
         } else if (i == field_202819_z) {
            l = field_202794_A;
         } else if (i == field_202812_s) {
            l = p_215723_1_.nextRandom(3) == 0 ? field_202804_k : field_202803_j;
         } else if (i == field_202805_l) {
            l = field_202806_m;
         } else if (i == field_202807_n) {
            l = field_202808_o;
         } else if (i == field_215729_o) {
            l = field_215730_p;
         } else if (i == LayerUtil.field_202832_c) {
            l = LayerUtil.field_202830_a;
         } else if (i == LayerUtil.field_203633_b) {
            l = LayerUtil.field_203636_g;
         } else if (i == LayerUtil.field_203634_d) {
            l = LayerUtil.field_203637_i;
         } else if (i == LayerUtil.field_202831_b) {
            l = LayerUtil.field_203638_j;
         } else if (i == field_202801_h) {
            l = field_202802_i;
         } else if (i == field_202816_w) {
            l = field_202817_x;
         } else if (LayerUtil.isSame(i, field_202810_q)) {
            l = field_202809_p;
         } else if ((i == LayerUtil.field_202830_a || i == LayerUtil.field_203636_g || i == LayerUtil.field_203637_i || i == LayerUtil.field_203638_j) && p_215723_1_.nextRandom(3) == 0) {
            l = p_215723_1_.nextRandom(2) == 0 ? field_202812_s : field_202803_j;
         }

         if (k == 0 && l != i) {
            Biome biome1 = Biome.func_185356_b(Registry.field_212624_m.byId(l));
            l = biome1 == null ? i : Registry.field_212624_m.getId(biome1);
         }

         if (l != i) {
            int i1 = 0;
            if (LayerUtil.isSame(p_215723_2_.get(this.getParentX(p_215723_4_ + 1), this.getParentY(p_215723_5_ + 0)), i)) {
               ++i1;
            }

            if (LayerUtil.isSame(p_215723_2_.get(this.getParentX(p_215723_4_ + 2), this.getParentY(p_215723_5_ + 1)), i)) {
               ++i1;
            }

            if (LayerUtil.isSame(p_215723_2_.get(this.getParentX(p_215723_4_ + 0), this.getParentY(p_215723_5_ + 1)), i)) {
               ++i1;
            }

            if (LayerUtil.isSame(p_215723_2_.get(this.getParentX(p_215723_4_ + 1), this.getParentY(p_215723_5_ + 2)), i)) {
               ++i1;
            }

            if (i1 >= 3) {
               return l;
            }
         }
      }

      return i;
   }
}
