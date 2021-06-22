package net.minecraft.world.gen.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   protected static final int field_203632_a = Registry.field_212624_m.getId(Biomes.WARM_OCEAN);
   protected static final int field_203633_b = Registry.field_212624_m.getId(Biomes.LUKEWARM_OCEAN);
   protected static final int field_202832_c = Registry.field_212624_m.getId(Biomes.OCEAN);
   protected static final int field_203634_d = Registry.field_212624_m.getId(Biomes.COLD_OCEAN);
   protected static final int field_202831_b = Registry.field_212624_m.getId(Biomes.FROZEN_OCEAN);
   protected static final int field_203635_f = Registry.field_212624_m.getId(Biomes.DEEP_WARM_OCEAN);
   protected static final int field_203636_g = Registry.field_212624_m.getId(Biomes.DEEP_LUKEWARM_OCEAN);
   protected static final int field_202830_a = Registry.field_212624_m.getId(Biomes.DEEP_OCEAN);
   protected static final int field_203637_i = Registry.field_212624_m.getId(Biomes.DEEP_COLD_OCEAN);
   protected static final int field_203638_j = Registry.field_212624_m.getId(Biomes.DEEP_FROZEN_OCEAN);

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> zoom(long p_202829_0_, IAreaTransformer1 p_202829_2_, IAreaFactory<T> p_202829_3_, int p_202829_4_, LongFunction<C> p_202829_5_) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < p_202829_4_; ++i) {
         iareafactory = p_202829_2_.run(p_202829_5_.apply(p_202829_0_ + (long)i), iareafactory);
      }

      return iareafactory;
   }

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_227475_a_(WorldType p_227475_0_, OverworldGenSettings p_227475_1_, LongFunction<C> p_227475_2_) {
      IAreaFactory<T> iareafactory = IslandLayer.INSTANCE.run(p_227475_2_.apply(1L));
      iareafactory = ZoomLayer.FUZZY.run(p_227475_2_.apply(2000L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(1L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_227475_2_.apply(2001L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(50L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(70L), iareafactory);
      iareafactory = RemoveTooMuchOceanLayer.INSTANCE.run(p_227475_2_.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.run(p_227475_2_.apply(2L));
      iareafactory1 = zoom(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_227475_2_);
      iareafactory = AddSnowLayer.INSTANCE.run(p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(3L), iareafactory);
      iareafactory = EdgeLayer.CoolWarm.INSTANCE.run(p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.HeatIce.INSTANCE.run(p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.Special.INSTANCE.run(p_227475_2_.apply(3L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_227475_2_.apply(2002L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_227475_2_.apply(2003L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_227475_2_.apply(4L), iareafactory);
      iareafactory = AddMushroomIslandLayer.INSTANCE.run(p_227475_2_.apply(5L), iareafactory);
      iareafactory = DeepOceanLayer.INSTANCE.run(p_227475_2_.apply(4L), iareafactory);
      iareafactory = zoom(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      int i = p_227475_0_ == WorldType.field_77135_d ? 6 : p_227475_1_.func_202200_j();
      i = getModdedBiomeSize(p_227475_0_, i);
      int j = p_227475_1_.func_202198_k();
      IAreaFactory<T> lvt_7_1_ = zoom(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      lvt_7_1_ = StartRiverLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = p_227475_0_.getBiomeLayer(iareafactory, p_227475_1_, p_227475_2_);
      IAreaFactory<T> lvt_9_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_8_1_ = HillsLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_, lvt_9_1_);
      lvt_7_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_7_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_7_1_, j, p_227475_2_);
      lvt_7_1_ = RiverLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1L), lvt_7_1_);
      lvt_7_1_ = SmoothLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_7_1_);
      lvt_8_1_ = RareBiomeLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1001L), lvt_8_1_);

      for(int k = 0; k < i; ++k) {
         lvt_8_1_ = ZoomLayer.NORMAL.run((IExtendedNoiseRandom)p_227475_2_.apply((long)(1000 + k)), lvt_8_1_);
         if (k == 0) {
            lvt_8_1_ = AddIslandLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(3L), lvt_8_1_);
         }

         if (k == 1 || i == 1) {
            lvt_8_1_ = ShoreLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
         }
      }

      lvt_8_1_ = SmoothLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
      lvt_8_1_ = MixRiverLayer.INSTANCE.run((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_8_1_, lvt_7_1_);
      lvt_8_1_ = MixOceansLayer.INSTANCE.run(p_227475_2_.apply(100L), lvt_8_1_, iareafactory1);
      return lvt_8_1_;
   }

   public static Layer func_227474_a_(long p_227474_0_, WorldType p_227474_2_, OverworldGenSettings p_227474_3_) {
      int i = 25;
      IAreaFactory<LazyArea> iareafactory = func_227475_a_(p_227474_2_, p_227474_3_, (p_227473_2_) -> {
         return new LazyAreaLayerContext(25, p_227474_0_, p_227473_2_);
      });
      return new Layer(iareafactory);
   }

   public static boolean isSame(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         Biome biome = Registry.field_212624_m.byId(p_202826_0_);
         Biome biome1 = Registry.field_212624_m.byId(p_202826_1_);
         if (biome != null && biome1 != null) {
            if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
               if (biome.getBiomeCategory() != Biome.Category.NONE && biome1.getBiomeCategory() != Biome.Category.NONE && biome.getBiomeCategory() == biome1.getBiomeCategory()) {
                  return true;
               } else {
                  return biome == biome1;
               }
            } else {
               return biome1 == Biomes.WOODED_BADLANDS_PLATEAU || biome1 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   /* ======================================== FORGE START =====================================*/
   public static int getModdedBiomeSize(WorldType worldType, int original)
   {
       net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize event = new net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize(worldType, original);
       net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
       return event.getNewSize();
   }
   /* ========================================= FORGE END ======================================*/

   protected static boolean isOcean(int p_202827_0_) {
      return p_202827_0_ == field_203632_a || p_202827_0_ == field_203633_b || p_202827_0_ == field_202832_c || p_202827_0_ == field_203634_d || p_202827_0_ == field_202831_b || p_202827_0_ == field_203635_f || p_202827_0_ == field_203636_g || p_202827_0_ == field_202830_a || p_202827_0_ == field_203637_i || p_202827_0_ == field_203638_j;
   }

   protected static boolean isShallowOcean(int p_203631_0_) {
      return p_203631_0_ == field_203632_a || p_203631_0_ == field_203633_b || p_203631_0_ == field_202832_c || p_203631_0_ == field_203634_d || p_203631_0_ == field_202831_b;
   }
}
