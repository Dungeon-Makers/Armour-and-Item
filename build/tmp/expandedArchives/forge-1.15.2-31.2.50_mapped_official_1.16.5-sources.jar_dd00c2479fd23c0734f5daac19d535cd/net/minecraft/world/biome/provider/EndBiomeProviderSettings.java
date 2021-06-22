package net.minecraft.world.biome.provider;

import net.minecraft.world.storage.WorldInfo;

public class EndBiomeProviderSettings implements IBiomeProviderSettings {
   private final long field_205447_a;

   public EndBiomeProviderSettings(WorldInfo p_i225752_1_) {
      this.field_205447_a = p_i225752_1_.func_76063_b();
   }

   public long func_205445_a() {
      return this.field_205447_a;
   }
}