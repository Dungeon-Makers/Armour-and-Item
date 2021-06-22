package net.minecraft.world.gen.layer;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(IAreaFactory<LazyArea> p_i48639_1_) {
      this.area = p_i48639_1_.make();
   }

   private Biome func_215739_a(int p_215739_1_) {
      Biome biome = Registry.field_212624_m.byId(p_215739_1_);
      if (biome == null) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Unknown biome id: " + p_215739_1_));
         } else {
            LOGGER.warn("Unknown biome id: ", (int)p_215739_1_);
            return Biomes.field_180279_ad;
         }
      } else {
         return biome;
      }
   }

   public Biome func_215738_a(int p_215738_1_, int p_215738_2_) {
      return this.func_215739_a(this.area.get(p_215738_1_, p_215738_2_));
   }
}