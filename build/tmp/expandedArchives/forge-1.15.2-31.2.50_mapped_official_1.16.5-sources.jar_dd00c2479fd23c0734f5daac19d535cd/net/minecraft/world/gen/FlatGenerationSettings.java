package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenerationSettings extends GenerationSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConfiguredFeature<?, ?> field_202250_m = Feature.field_202329_g.configured(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202251_n = Feature.field_214550_p.configured(new VillageConfig("village/plains/town_centers", 6)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202252_o = Feature.field_202335_m.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202253_p = Feature.field_202334_l.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202254_q = Feature.field_202332_j.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202255_r = Feature.field_202331_i.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202256_s = Feature.field_202333_k.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_204750_v = Feature.field_204751_l.configured(new ShipwreckConfig(false)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202257_t = Feature.field_202336_n.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202258_u = Feature.LAKE.configured(new BlockStateFeatureConfig(Blocks.WATER.defaultBlockState())).decorated(Placement.WATER_LAKE.configured(new ChanceConfig(4)));
   private static final ConfiguredFeature<?, ?> field_202259_v = Feature.LAKE.configured(new BlockStateFeatureConfig(Blocks.LAVA.defaultBlockState())).decorated(Placement.LAVA_LAKE.configured(new ChanceConfig(80)));
   private static final ConfiguredFeature<?, ?> field_202260_w = Feature.field_204292_r.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202261_x = Feature.field_202330_h.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_202262_y = Feature.field_202337_o.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_204028_A = Feature.field_204029_o.configured(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   private static final ConfiguredFeature<?, ?> field_214991_M = Feature.field_214536_b.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));
   public static final Map<ConfiguredFeature<?, ?>, GenerationStage.Decoration> field_202248_k = Util.make(Maps.newHashMap(), (p_209406_0_) -> {
      p_209406_0_.put(field_202250_m, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202251_n, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202252_o, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202253_p, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202254_q, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202255_r, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202256_s, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_204750_v, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_204028_A, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202258_u, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(field_202259_v, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(field_202260_w, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202261_x, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202262_y, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202257_t, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_214991_M, GenerationStage.Decoration.SURFACE_STRUCTURES);
   });
   public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURE_FEATURES = Util.make(Maps.newHashMap(), (p_209404_0_) -> {
      p_209404_0_.put("mineshaft", new ConfiguredFeature[]{field_202250_m});
      p_209404_0_.put("village", new ConfiguredFeature[]{field_202251_n});
      p_209404_0_.put("stronghold", new ConfiguredFeature[]{field_202252_o});
      p_209404_0_.put("biome_1", new ConfiguredFeature[]{field_202253_p, field_202254_q, field_202255_r, field_202256_s, field_204028_A, field_204750_v});
      p_209404_0_.put("oceanmonument", new ConfiguredFeature[]{field_202257_t});
      p_209404_0_.put("lake", new ConfiguredFeature[]{field_202258_u});
      p_209404_0_.put("lava_lake", new ConfiguredFeature[]{field_202259_v});
      p_209404_0_.put("endcity", new ConfiguredFeature[]{field_202260_w});
      p_209404_0_.put("mansion", new ConfiguredFeature[]{field_202261_x});
      p_209404_0_.put("fortress", new ConfiguredFeature[]{field_202262_y});
      p_209404_0_.put("pillager_outpost", new ConfiguredFeature[]{field_214991_M});
   });
   public static final Map<ConfiguredFeature<?, ?>, IFeatureConfig> field_202249_l = Util.make(Maps.newHashMap(), (p_209405_0_) -> {
      p_209405_0_.put(field_202250_m, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
      p_209405_0_.put(field_202251_n, new VillageConfig("village/plains/town_centers", 6));
      p_209405_0_.put(field_202252_o, IFeatureConfig.NONE);
      p_209405_0_.put(field_202253_p, IFeatureConfig.NONE);
      p_209405_0_.put(field_202254_q, IFeatureConfig.NONE);
      p_209405_0_.put(field_202255_r, IFeatureConfig.NONE);
      p_209405_0_.put(field_202256_s, IFeatureConfig.NONE);
      p_209405_0_.put(field_204028_A, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      p_209405_0_.put(field_204750_v, new ShipwreckConfig(false));
      p_209405_0_.put(field_202257_t, IFeatureConfig.NONE);
      p_209405_0_.put(field_202260_w, IFeatureConfig.NONE);
      p_209405_0_.put(field_202261_x, IFeatureConfig.NONE);
      p_209405_0_.put(field_202262_y, IFeatureConfig.NONE);
      p_209405_0_.put(field_214991_M, IFeatureConfig.NONE);
   });
   private final List<FlatLayerInfo> layersInfo = Lists.newArrayList();
   private final Map<String, Map<String, String>> field_82653_b = Maps.newHashMap();
   private Biome biome;
   private final BlockState[] layers = new BlockState[256];
   private boolean voidGen;
   private int field_202246_E;

   @Nullable
   public static Block func_212683_a(String p_212683_0_) {
      try {
         ResourceLocation resourcelocation = new ResourceLocation(p_212683_0_);
         return Registry.BLOCK.func_218349_b(resourcelocation).orElse((Block)null);
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.warn("Invalid blockstate: {}", p_212683_0_, illegalargumentexception);
         return null;
      }
   }

   public Biome getBiome() {
      return this.biome;
   }

   public void func_82647_a(Biome p_82647_1_) {
      this.biome = p_82647_1_;
   }

   public Map<String, Map<String, String>> func_82644_b() {
      return this.field_82653_b;
   }

   public List<FlatLayerInfo> getLayersInfo() {
      return this.layersInfo;
   }

   public void updateLayers() {
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.layersInfo) {
         flatlayerinfo.setStart(i);
         i += flatlayerinfo.getHeight();
      }

      this.field_202246_E = 0;
      this.voidGen = true;
      i = 0;

      for(FlatLayerInfo flatlayerinfo1 : this.layersInfo) {
         for(int j = flatlayerinfo1.getStart(); j < flatlayerinfo1.getStart() + flatlayerinfo1.getHeight(); ++j) {
            BlockState blockstate = flatlayerinfo1.getBlockState();
            if (blockstate.getBlock() != Blocks.AIR) {
               this.voidGen = false;
               this.layers[j] = blockstate;
            }
         }

         if (flatlayerinfo1.getBlockState().getBlock() == Blocks.AIR) {
            i += flatlayerinfo1.getHeight();
         } else {
            this.field_202246_E += flatlayerinfo1.getHeight() + i;
            i = 0;
         }
      }

   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < this.layersInfo.size(); ++i) {
         if (i > 0) {
            stringbuilder.append(",");
         }

         stringbuilder.append(this.layersInfo.get(i));
      }

      stringbuilder.append(";");
      stringbuilder.append((Object)Registry.field_212624_m.getKey(this.biome));
      stringbuilder.append(";");
      if (!this.field_82653_b.isEmpty()) {
         int k = 0;

         for(Entry<String, Map<String, String>> entry : this.field_82653_b.entrySet()) {
            if (k++ > 0) {
               stringbuilder.append(",");
            }

            stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
            Map<String, String> map = entry.getValue();
            if (!map.isEmpty()) {
               stringbuilder.append("(");
               int j = 0;

               for(Entry<String, String> entry1 : map.entrySet()) {
                  if (j++ > 0) {
                     stringbuilder.append(" ");
                  }

                  stringbuilder.append(entry1.getKey());
                  stringbuilder.append("=");
                  stringbuilder.append(entry1.getValue());
               }

               stringbuilder.append(")");
            }
         }
      }

      return stringbuilder.toString();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static FlatLayerInfo func_197526_a(String p_197526_0_, int p_197526_1_) {
      String[] astring = p_197526_0_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = Math.max(Integer.parseInt(astring[0]), 0);
         } catch (NumberFormatException numberformatexception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)numberformatexception.getMessage());
            return null;
         }
      } else {
         i = 1;
      }

      int j = Math.min(p_197526_1_ + i, 256);
      int k = j - p_197526_1_;

      Block block;
      try {
         block = func_212683_a(astring[astring.length - 1]);
      } catch (Exception exception) {
         LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
         return null;
      }

      if (block == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)astring[astring.length - 1]);
         return null;
      } else {
         FlatLayerInfo flatlayerinfo = new FlatLayerInfo(k, block);
         flatlayerinfo.setStart(p_197526_1_);
         return flatlayerinfo;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<FlatLayerInfo> func_197527_b(String p_197527_0_) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] astring = p_197527_0_.split(",");
      int i = 0;

      for(String s : astring) {
         FlatLayerInfo flatlayerinfo = func_197526_a(s, i);
         if (flatlayerinfo == null) {
            return Collections.emptyList();
         }

         list.add(flatlayerinfo);
         i += flatlayerinfo.getHeight();
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Dynamic<T> func_210834_a(DynamicOps<T> p_210834_1_) {
      T t = p_210834_1_.createList(this.layersInfo.stream().map((p_210837_1_) -> {
         return p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("height"), p_210834_1_.createInt(p_210837_1_.getHeight()), p_210834_1_.createString("block"), p_210834_1_.createString(Registry.BLOCK.getKey(p_210837_1_.getBlockState().getBlock()).toString())));
      }));
      T t1 = p_210834_1_.createMap(this.field_82653_b.entrySet().stream().map((p_210833_1_) -> {
         return Pair.of(p_210834_1_.createString(p_210833_1_.getKey().toLowerCase(Locale.ROOT)), p_210834_1_.createMap(p_210833_1_.getValue().entrySet().stream().map((p_210836_1_) -> {
            return Pair.of(p_210834_1_.createString(p_210836_1_.getKey()), p_210834_1_.createString(p_210836_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic<>(p_210834_1_, p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("layers"), t, p_210834_1_.createString("biome"), p_210834_1_.createString(Registry.field_212624_m.getKey(this.biome).toString()), p_210834_1_.createString("structures"), t1)));
   }

   public static FlatGenerationSettings func_210835_a(Dynamic<?> p_210835_0_) {
      FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.field_205489_f.func_205483_a();
      List<Pair<Integer, Block>> list = p_210835_0_.get("layers").asList((p_210838_0_) -> {
         return Pair.of(p_210838_0_.get("height").asInt(1), func_212683_a(p_210838_0_.get("block").asString("")));
      });
      if (list.stream().anyMatch((p_211743_0_) -> {
         return p_211743_0_.getSecond() == null;
      })) {
         return func_82649_e();
      } else {
         List<FlatLayerInfo> list1 = list.stream().map((p_211740_0_) -> {
            return new FlatLayerInfo(p_211740_0_.getFirst(), p_211740_0_.getSecond());
         }).collect(Collectors.toList());
         if (list1.isEmpty()) {
            return func_82649_e();
         } else {
            flatgenerationsettings.getLayersInfo().addAll(list1);
            flatgenerationsettings.updateLayers();
            flatgenerationsettings.func_82647_a(Registry.field_212624_m.get(new ResourceLocation(p_210835_0_.get("biome").asString(""))));
            p_210835_0_.get("structures").flatMap(Dynamic::getMapValues).ifPresent((p_211738_1_) -> {
               p_211738_1_.keySet().forEach((p_211739_1_) -> {
                  p_211739_1_.asString().map((p_211742_1_) -> {
                     return flatgenerationsettings.func_82644_b().put(p_211742_1_, Maps.newHashMap());
                  });
               });
            });
            return flatgenerationsettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static FlatGenerationSettings func_82651_a(String p_82651_0_) {
      Iterator<String> iterator = Splitter.on(';').split(p_82651_0_).iterator();
      if (!iterator.hasNext()) {
         return func_82649_e();
      } else {
         FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.field_205489_f.func_205483_a();
         List<FlatLayerInfo> list = func_197527_b(iterator.next());
         if (list.isEmpty()) {
            return func_82649_e();
         } else {
            flatgenerationsettings.getLayersInfo().addAll(list);
            flatgenerationsettings.updateLayers();
            Biome biome = Biomes.PLAINS;
            if (iterator.hasNext()) {
               try {
                  ResourceLocation resourcelocation = new ResourceLocation(iterator.next());
                  biome = Registry.field_212624_m.func_218349_b(resourcelocation).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + resourcelocation);
                  });
               } catch (Exception exception) {
                  LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
               }
            }

            flatgenerationsettings.func_82647_a(biome);
            if (iterator.hasNext()) {
               String[] astring3 = iterator.next().toLowerCase(Locale.ROOT).split(",");

               for(String s : astring3) {
                  String[] astring = s.split("\\(", 2);
                  if (!astring[0].isEmpty()) {
                     flatgenerationsettings.func_202234_c(astring[0]);
                     if (astring.length > 1 && astring[1].endsWith(")") && astring[1].length() > 1) {
                        String[] astring1 = astring[1].substring(0, astring[1].length() - 1).split(" ");

                        for(String s1 : astring1) {
                           String[] astring2 = s1.split("=", 2);
                           if (astring2.length == 2) {
                              flatgenerationsettings.func_202229_a(astring[0], astring2[0], astring2[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatgenerationsettings.func_82644_b().put("village", Maps.newHashMap());
            }

            return flatgenerationsettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void func_202234_c(String p_202234_1_) {
      Map<String, String> map = Maps.newHashMap();
      this.field_82653_b.put(p_202234_1_, map);
   }

   @OnlyIn(Dist.CLIENT)
   private void func_202229_a(String p_202229_1_, String p_202229_2_, String p_202229_3_) {
      this.field_82653_b.get(p_202229_1_).put(p_202229_2_, p_202229_3_);
      if ("village".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.field_214971_a = MathHelper.getInt(p_202229_3_, this.field_214971_a, 9);
      }

      if ("biome_1".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.field_214978_h = MathHelper.getInt(p_202229_3_, this.field_214978_h, 9);
      }

      if ("stronghold".equals(p_202229_1_)) {
         if ("distance".equals(p_202229_2_)) {
            this.field_214975_e = MathHelper.getInt(p_202229_3_, this.field_214975_e, 1);
         } else if ("count".equals(p_202229_2_)) {
            this.field_214976_f = MathHelper.getInt(p_202229_3_, this.field_214976_f, 1);
         } else if ("spread".equals(p_202229_2_)) {
            this.field_214977_g = MathHelper.getInt(p_202229_3_, this.field_214977_g, 1);
         }
      }

      if ("oceanmonument".equals(p_202229_1_)) {
         if ("separation".equals(p_202229_2_)) {
            this.field_214974_d = MathHelper.getInt(p_202229_3_, this.field_214974_d, 1);
         } else if ("spacing".equals(p_202229_2_)) {
            this.field_214973_c = MathHelper.getInt(p_202229_3_, this.field_214973_c, 1);
         }
      }

      if ("endcity".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.field_214982_l = MathHelper.getInt(p_202229_3_, this.field_214982_l, 1);
      }

      if ("mansion".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.field_214986_p = MathHelper.getInt(p_202229_3_, this.field_214986_p, 1);
      }

   }

   public static FlatGenerationSettings func_82649_e() {
      FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.field_205489_f.func_205483_a();
      flatgenerationsettings.func_82647_a(Biomes.PLAINS);
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgenerationsettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgenerationsettings.updateLayers();
      flatgenerationsettings.func_82644_b().put("village", Maps.newHashMap());
      return flatgenerationsettings;
   }

   public boolean func_202238_o() {
      return this.voidGen;
   }

   public BlockState[] getLayers() {
      return this.layers;
   }

   public void func_214990_a(int p_214990_1_) {
      this.layers[p_214990_1_] = null;
   }
}