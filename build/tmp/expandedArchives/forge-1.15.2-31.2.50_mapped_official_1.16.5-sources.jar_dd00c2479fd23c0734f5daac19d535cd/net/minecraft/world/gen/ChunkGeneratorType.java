package net.minecraft.world.gen;

import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkGeneratorType<C extends GenerationSettings, T extends ChunkGenerator<C>> extends net.minecraftforge.registries.ForgeRegistryEntry<ChunkGeneratorType<?, ?>> implements IChunkGeneratorFactory<C, T> {
   public static final ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> field_206911_b = func_212676_a("surface", OverworldChunkGenerator::new, OverworldGenSettings::new, true);
   public static final ChunkGeneratorType<NetherGenSettings, NetherChunkGenerator> field_206912_c = func_212676_a("caves", NetherChunkGenerator::new, NetherGenSettings::new, true);
   public static final ChunkGeneratorType<EndGenerationSettings, EndChunkGenerator> field_206913_d = func_212676_a("floating_islands", EndChunkGenerator::new, EndGenerationSettings::new, true);
   public static final ChunkGeneratorType<DebugGenerationSettings, DebugChunkGenerator> field_205488_e = func_212676_a("debug", DebugChunkGenerator::new, DebugGenerationSettings::new, false);
   public static final ChunkGeneratorType<FlatGenerationSettings, FlatChunkGenerator> field_205489_f = func_212676_a("flat", FlatChunkGenerator::new, FlatGenerationSettings::new, false);
   private final IChunkGeneratorFactory<C, T> field_205491_h;
   private final boolean field_205492_i;
   private final Supplier<C> field_205493_j;

   private static <C extends GenerationSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> func_212676_a(String p_212676_0_, IChunkGeneratorFactory<C, T> p_212676_1_, Supplier<C> p_212676_2_, boolean p_212676_3_) {
      return Registry.register(Registry.field_212627_p, p_212676_0_, new ChunkGeneratorType<>(p_212676_1_, p_212676_3_, p_212676_2_));
   }

   public ChunkGeneratorType(IChunkGeneratorFactory<C, T> p_i49953_1_, boolean p_i49953_2_, Supplier<C> p_i49953_3_) {
      this.field_205491_h = p_i49953_1_;
      this.field_205492_i = p_i49953_2_;
      this.field_205493_j = p_i49953_3_;
   }

   public T create(World p_create_1_, BiomeProvider p_create_2_, C p_create_3_) {
      return this.field_205491_h.create(p_create_1_, p_create_2_, p_create_3_);
   }

   public C func_205483_a() {
      return (C)(this.field_205493_j.get());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_205481_b() {
      return this.field_205492_i;
   }
}
