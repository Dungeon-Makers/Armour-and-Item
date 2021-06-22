package net.minecraft.world.biome.provider;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProviderSettings implements IBiomeProviderSettings {
   private final long field_226848_a_;
   private final WorldType field_226849_b_;
   private OverworldGenSettings field_205444_b = new OverworldGenSettings();

   public OverworldBiomeProviderSettings(WorldInfo p_i225751_1_) {
      this.field_226848_a_ = p_i225751_1_.func_76063_b();
      this.field_226849_b_ = p_i225751_1_.func_76067_t();
   }

   public OverworldBiomeProviderSettings func_205441_a(OverworldGenSettings p_205441_1_) {
      this.field_205444_b = p_205441_1_;
      return this;
   }

   public long func_226850_a_() {
      return this.field_226848_a_;
   }

   public WorldType func_226851_b_() {
      return this.field_226849_b_;
   }

   public OverworldGenSettings func_205442_b() {
      return this.field_205444_b;
   }
}