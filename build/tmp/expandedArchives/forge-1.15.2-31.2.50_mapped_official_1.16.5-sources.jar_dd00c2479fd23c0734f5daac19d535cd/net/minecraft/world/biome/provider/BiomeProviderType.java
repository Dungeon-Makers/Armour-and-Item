package net.minecraft.world.biome.provider;

import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> extends net.minecraftforge.registries.ForgeRegistryEntry<BiomeProviderType<?, ?>> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> field_205460_b = func_226841_a_("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> field_205461_c = func_226841_a_("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> field_206859_d = func_226841_a_("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> field_205463_e = func_226841_a_("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final Function<C, T> field_205465_g;
   private final Function<WorldInfo, C> field_205466_h;

   private static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> func_226841_a_(String p_226841_0_, Function<C, T> p_226841_1_, Function<WorldInfo, C> p_226841_2_) {
      return Registry.register(Registry.field_212625_n, p_226841_0_, new BiomeProviderType<>(p_226841_1_, p_226841_2_));
   }

   public BiomeProviderType(Function<C, T> p_i225746_1_, Function<WorldInfo, C> p_i225746_2_) {
      this.field_205465_g = p_i225746_1_;
      this.field_205466_h = p_i225746_2_;
   }

   public T func_205457_a(C p_205457_1_) {
      return (T)(this.field_205465_g.apply(p_205457_1_));
   }

   public C func_226840_a_(WorldInfo p_226840_1_) {
      return (C)(this.field_205466_h.apply(p_226840_1_));
   }
}
