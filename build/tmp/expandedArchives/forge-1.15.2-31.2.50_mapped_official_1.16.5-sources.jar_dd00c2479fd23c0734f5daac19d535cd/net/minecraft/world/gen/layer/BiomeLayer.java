package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class BiomeLayer implements IC0Transformer {
   private static final int field_202727_a = Registry.field_212624_m.getId(Biomes.BIRCH_FOREST);
   private static final int field_202728_b = Registry.field_212624_m.getId(Biomes.DESERT);
   private static final int field_202729_c = Registry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int field_202730_d = Registry.field_212624_m.getId(Biomes.FOREST);
   private static final int field_202731_e = Registry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int field_202732_f = Registry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int field_202733_g = Registry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int field_202734_h = Registry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int field_202735_i = Registry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int field_202736_j = Registry.field_212624_m.getId(Biomes.PLAINS);
   private static final int field_202737_k = Registry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int field_202738_l = Registry.field_212624_m.getId(Biomes.DARK_FOREST);
   private static final int field_202739_m = Registry.field_212624_m.getId(Biomes.SAVANNA);
   private static final int field_202740_n = Registry.field_212624_m.getId(Biomes.SWAMP);
   private static final int field_202741_o = Registry.field_212624_m.getId(Biomes.TAIGA);
   private static final int field_202742_p = Registry.field_212624_m.getId(Biomes.SNOWY_TAIGA);
   private final int field_227472_v_;
   @SuppressWarnings("unchecked")
   private java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry>[] biomes = new java.util.ArrayList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];

   public BiomeLayer(WorldType p_i225882_1_, int p_i225882_2_) {
     for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values()) {
         com.google.common.collect.ImmutableList<net.minecraftforge.common.BiomeManager.BiomeEntry> biomesToAdd = net.minecraftforge.common.BiomeManager.getBiomes(type);
         int idx = type.ordinal();

         if (biomes[idx] == null) biomes[idx] = new java.util.ArrayList<net.minecraftforge.common.BiomeManager.BiomeEntry>();
         if (biomesToAdd != null) biomes[idx].addAll(biomesToAdd);
      }

      int desertIdx = net.minecraftforge.common.BiomeManager.BiomeType.DESERT.ordinal();

      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 30));
      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SAVANNA, 20));
      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));

      if (p_i225882_1_ == WorldType.field_77136_e) {
         biomes[desertIdx].clear();
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.FOREST, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SWAMP, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.TAIGA, 10));
         this.field_227472_v_ = -1;
      } else {
         this.field_227472_v_ = p_i225882_2_;
      }

   }

   public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
      if (this.field_227472_v_ >= 0) {
         return this.field_227472_v_;
      } else {
         int i = (p_202726_2_ & 3840) >> 8;
         p_202726_2_ = p_202726_2_ & -3841;
         if (!LayerUtil.isOcean(p_202726_2_) && p_202726_2_ != field_202735_i) {
            switch(p_202726_2_) {
            case 1:
               if (i > 0) {
                  return p_202726_1_.nextRandom(3) == 0 ? field_202733_g : field_202734_h;
               }

               return Registry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.DESERT, p_202726_1_).biome);
            case 2:
               if (i > 0) {
                  return field_202732_f;
               }

               return Registry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.WARM, p_202726_1_).biome);
            case 3:
               if (i > 0) {
                  return field_202737_k;
               }

               return Registry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.COOL, p_202726_1_).biome);
            case 4:
               return Registry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.ICY, p_202726_1_).biome);
            default:
               return field_202735_i;
            }
         } else {
            return p_202726_2_;
         }
      }
   }

   protected net.minecraftforge.common.BiomeManager.BiomeEntry getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType type, INoiseRandom context) {
      java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
      int totalWeight = net.minecraft.util.WeightedRandom.getTotalWeight(biomeList);
      int weight = net.minecraftforge.common.BiomeManager.isTypeListModded(type)?context.nextRandom(totalWeight):context.nextRandom(totalWeight / 10) * 10;
      return (net.minecraftforge.common.BiomeManager.BiomeEntry)net.minecraft.util.WeightedRandom.getWeightedItem(biomeList, weight);
   }
}
