package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ShoreLayer implements ICastleTransformer {
   INSTANCE;

   private static final int field_202768_b = Registry.field_212624_m.getId(Biomes.BEACH);
   private static final int field_202769_c = Registry.field_212624_m.getId(Biomes.SNOWY_BEACH);
   private static final int field_202771_e = Registry.field_212624_m.getId(Biomes.DESERT);
   private static final int field_202772_f = Registry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int field_202773_g = Registry.field_212624_m.getId(Biomes.WOODED_MOUNTAINS);
   private static final int field_202774_h = Registry.field_212624_m.getId(Biomes.FOREST);
   private static final int field_202775_i = Registry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int field_202776_j = Registry.field_212624_m.getId(Biomes.JUNGLE_EDGE);
   private static final int field_202777_k = Registry.field_212624_m.getId(Biomes.JUNGLE_HILLS);
   private static final int field_202778_l = Registry.field_212624_m.getId(Biomes.BADLANDS);
   private static final int field_202779_m = Registry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int field_202780_n = Registry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int field_202781_o = Registry.field_212624_m.getId(Biomes.ERODED_BADLANDS);
   private static final int field_202782_p = Registry.field_212624_m.getId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
   private static final int field_202783_q = Registry.field_212624_m.getId(Biomes.MODIFIED_BADLANDS_PLATEAU);
   private static final int field_202784_r = Registry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int field_202785_s = Registry.field_212624_m.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int field_202787_u = Registry.field_212624_m.getId(Biomes.RIVER);
   private static final int field_202788_v = Registry.field_212624_m.getId(Biomes.MOUNTAIN_EDGE);
   private static final int field_202789_w = Registry.field_212624_m.getId(Biomes.STONE_SHORE);
   private static final int field_202790_x = Registry.field_212624_m.getId(Biomes.SWAMP);
   private static final int field_202791_y = Registry.field_212624_m.getId(Biomes.TAIGA);

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      Biome biome = Registry.field_212624_m.byId(p_202748_6_);
      if (p_202748_6_ == field_202784_r) {
         if (LayerUtil.isShallowOcean(p_202748_2_) || LayerUtil.isShallowOcean(p_202748_3_) || LayerUtil.isShallowOcean(p_202748_4_) || LayerUtil.isShallowOcean(p_202748_5_)) {
            return field_202785_s;
         }
      } else if (biome != null && biome.getBiomeCategory() == Biome.Category.JUNGLE) {
         if (!isJungleCompatible(p_202748_2_) || !isJungleCompatible(p_202748_3_) || !isJungleCompatible(p_202748_4_) || !isJungleCompatible(p_202748_5_)) {
            return field_202776_j;
         }

         if (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_)) {
            return field_202768_b;
         }
      } else if (p_202748_6_ != field_202772_f && p_202748_6_ != field_202773_g && p_202748_6_ != field_202788_v) {
         if (biome != null && biome.getPrecipitation() == Biome.RainType.SNOW) {
            if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return field_202769_c;
            }
         } else if (p_202748_6_ != field_202778_l && p_202748_6_ != field_202779_m) {
            if (!LayerUtil.isOcean(p_202748_6_) && p_202748_6_ != field_202787_u && p_202748_6_ != field_202790_x && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return field_202768_b;
            }
         } else if (!LayerUtil.isOcean(p_202748_2_) && !LayerUtil.isOcean(p_202748_3_) && !LayerUtil.isOcean(p_202748_4_) && !LayerUtil.isOcean(p_202748_5_) && (!this.isMesa(p_202748_2_) || !this.isMesa(p_202748_3_) || !this.isMesa(p_202748_4_) || !this.isMesa(p_202748_5_))) {
            return field_202771_e;
         }
      } else if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
         return field_202789_w;
      }

      return p_202748_6_;
   }

   private static boolean isJungleCompatible(int p_151631_0_) {
      if (Registry.field_212624_m.byId(p_151631_0_) != null && Registry.field_212624_m.byId(p_151631_0_).getBiomeCategory() == Biome.Category.JUNGLE) {
         return true;
      } else {
         return p_151631_0_ == field_202776_j || p_151631_0_ == field_202775_i || p_151631_0_ == field_202777_k || p_151631_0_ == field_202774_h || p_151631_0_ == field_202791_y || LayerUtil.isOcean(p_151631_0_);
      }
   }

   private boolean isMesa(int p_151633_1_) {
      return p_151633_1_ == field_202778_l || p_151633_1_ == field_202779_m || p_151633_1_ == field_202780_n || p_151633_1_ == field_202781_o || p_151633_1_ == field_202782_p || p_151633_1_ == field_202783_q;
   }
}