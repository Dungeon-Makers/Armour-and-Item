package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum EdgeBiomeLayer implements ICastleTransformer {
   INSTANCE;

   private static final int field_202752_b = Registry.field_212624_m.getId(Biomes.DESERT);
   private static final int field_202753_c = Registry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int field_202754_d = Registry.field_212624_m.getId(Biomes.WOODED_MOUNTAINS);
   private static final int field_202755_e = Registry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int field_202756_f = Registry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int field_215731_g = Registry.field_212624_m.getId(Biomes.BAMBOO_JUNGLE);
   private static final int field_202757_g = Registry.field_212624_m.getId(Biomes.JUNGLE_EDGE);
   private static final int field_202758_h = Registry.field_212624_m.getId(Biomes.BADLANDS);
   private static final int field_202759_i = Registry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int field_202760_j = Registry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int field_202761_k = Registry.field_212624_m.getId(Biomes.PLAINS);
   private static final int field_202762_l = Registry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int field_202763_m = Registry.field_212624_m.getId(Biomes.MOUNTAIN_EDGE);
   private static final int field_202764_n = Registry.field_212624_m.getId(Biomes.SWAMP);
   private static final int field_202765_o = Registry.field_212624_m.getId(Biomes.TAIGA);
   private static final int field_202766_p = Registry.field_212624_m.getId(Biomes.SNOWY_TAIGA);

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      int[] aint = new int[1];
      if (!this.func_202751_a(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, field_202753_c, field_202763_m) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, field_202760_j, field_202758_h) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, field_202759_i, field_202758_h) && !this.checkEdgeStrict(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, field_202762_l, field_202765_o)) {
         if (p_202748_6_ != field_202752_b || p_202748_2_ != field_202755_e && p_202748_3_ != field_202755_e && p_202748_5_ != field_202755_e && p_202748_4_ != field_202755_e) {
            if (p_202748_6_ == field_202764_n) {
               if (p_202748_2_ == field_202752_b || p_202748_3_ == field_202752_b || p_202748_5_ == field_202752_b || p_202748_4_ == field_202752_b || p_202748_2_ == field_202766_p || p_202748_3_ == field_202766_p || p_202748_5_ == field_202766_p || p_202748_4_ == field_202766_p || p_202748_2_ == field_202755_e || p_202748_3_ == field_202755_e || p_202748_5_ == field_202755_e || p_202748_4_ == field_202755_e) {
                  return field_202761_k;
               }

               if (p_202748_2_ == field_202756_f || p_202748_4_ == field_202756_f || p_202748_3_ == field_202756_f || p_202748_5_ == field_202756_f || p_202748_2_ == field_215731_g || p_202748_4_ == field_215731_g || p_202748_3_ == field_215731_g || p_202748_5_ == field_215731_g) {
                  return field_202757_g;
               }
            }

            return p_202748_6_;
         } else {
            return field_202754_d;
         }
      } else {
         return aint[0];
      }
   }

   private boolean func_202751_a(int[] p_202751_1_, int p_202751_2_, int p_202751_3_, int p_202751_4_, int p_202751_5_, int p_202751_6_, int p_202751_7_, int p_202751_8_) {
      if (!LayerUtil.isSame(p_202751_6_, p_202751_7_)) {
         return false;
      } else {
         if (this.func_151634_b(p_202751_2_, p_202751_7_) && this.func_151634_b(p_202751_3_, p_202751_7_) && this.func_151634_b(p_202751_5_, p_202751_7_) && this.func_151634_b(p_202751_4_, p_202751_7_)) {
            p_202751_1_[0] = p_202751_6_;
         } else {
            p_202751_1_[0] = p_202751_8_;
         }

         return true;
      }
   }

   private boolean checkEdgeStrict(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_) {
      if (p_151635_6_ != p_151635_7_) {
         return false;
      } else {
         if (LayerUtil.isSame(p_151635_2_, p_151635_7_) && LayerUtil.isSame(p_151635_3_, p_151635_7_) && LayerUtil.isSame(p_151635_5_, p_151635_7_) && LayerUtil.isSame(p_151635_4_, p_151635_7_)) {
            p_151635_1_[0] = p_151635_6_;
         } else {
            p_151635_1_[0] = p_151635_8_;
         }

         return true;
      }
   }

   private boolean func_151634_b(int p_151634_1_, int p_151634_2_) {
      if (LayerUtil.isSame(p_151634_1_, p_151634_2_)) {
         return true;
      } else {
         Biome biome = Registry.field_212624_m.byId(p_151634_1_);
         Biome biome1 = Registry.field_212624_m.byId(p_151634_2_);
         if (biome != null && biome1 != null) {
            Biome.TempCategory biome$tempcategory = biome.func_150561_m();
            Biome.TempCategory biome$tempcategory1 = biome1.func_150561_m();
            return biome$tempcategory == biome$tempcategory1 || biome$tempcategory == Biome.TempCategory.MEDIUM || biome$tempcategory1 == Biome.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}