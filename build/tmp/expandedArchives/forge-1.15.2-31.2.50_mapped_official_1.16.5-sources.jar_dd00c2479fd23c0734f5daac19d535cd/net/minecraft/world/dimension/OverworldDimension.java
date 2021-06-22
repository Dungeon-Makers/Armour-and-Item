package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DebugGenerationSettings;
import net.minecraft.world.gen.EndChunkGenerator;
import net.minecraft.world.gen.EndGenerationSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.NetherChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OverworldDimension extends Dimension {
   public OverworldDimension(World p_i49933_1_, DimensionType p_i49933_2_) {
      super(p_i49933_1_, p_i49933_2_, 0.0F);
   }

   public ChunkGenerator<? extends GenerationSettings> func_186060_c() {
      WorldType worldtype = this.field_76579_a.getLevelData().func_76067_t();
      ChunkGeneratorType<FlatGenerationSettings, FlatChunkGenerator> chunkgeneratortype = ChunkGeneratorType.field_205489_f;
      ChunkGeneratorType<DebugGenerationSettings, DebugChunkGenerator> chunkgeneratortype1 = ChunkGeneratorType.field_205488_e;
      ChunkGeneratorType<NetherGenSettings, NetherChunkGenerator> chunkgeneratortype2 = ChunkGeneratorType.field_206912_c;
      ChunkGeneratorType<EndGenerationSettings, EndChunkGenerator> chunkgeneratortype3 = ChunkGeneratorType.field_206913_d;
      ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkgeneratortype4 = ChunkGeneratorType.field_206911_b;
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.field_205461_c;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.field_206859_d;
      BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> biomeprovidertype2 = BiomeProviderType.field_205460_b;
      if (worldtype == WorldType.field_77138_c) {
         FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings.func_210835_a(new Dynamic<>(NBTDynamicOps.INSTANCE, this.field_76579_a.getLevelData().func_211027_A()));
         SingleBiomeProviderSettings singlebiomeprovidersettings1 = biomeprovidertype.func_226840_a_(this.field_76579_a.getLevelData()).func_205436_a(flatgenerationsettings.getBiome());
         return chunkgeneratortype.create(this.field_76579_a, biomeprovidertype.func_205457_a(singlebiomeprovidersettings1), flatgenerationsettings);
      } else if (worldtype == WorldType.field_180272_g) {
         SingleBiomeProviderSettings singlebiomeprovidersettings = biomeprovidertype.func_226840_a_(this.field_76579_a.getLevelData()).func_205436_a(Biomes.PLAINS);
         return chunkgeneratortype1.create(this.field_76579_a, biomeprovidertype.func_205457_a(singlebiomeprovidersettings), chunkgeneratortype1.func_205483_a());
      } else if (worldtype != WorldType.field_205394_h) {
         OverworldGenSettings overworldgensettings = chunkgeneratortype4.func_205483_a();
         OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeprovidertype1.func_226840_a_(this.field_76579_a.getLevelData()).func_205441_a(overworldgensettings);
         return chunkgeneratortype4.create(this.field_76579_a, biomeprovidertype1.func_205457_a(overworldbiomeprovidersettings), overworldgensettings);
      } else {
         BiomeProvider biomeprovider = null;
         JsonElement jsonelement = Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.field_76579_a.getLevelData().func_211027_A());
         JsonObject jsonobject = jsonelement.getAsJsonObject();
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source");
         if (jsonobject1 != null && jsonobject1.has("type") && jsonobject1.has("options")) {
            BiomeProviderType<?, ?> biomeprovidertype3 = Registry.field_212625_n.get(new ResourceLocation(jsonobject1.getAsJsonPrimitive("type").getAsString()));
            JsonObject jsonobject2 = jsonobject1.getAsJsonObject("options");
            Biome[] abiome = new Biome[]{Biomes.OCEAN};
            if (jsonobject2.has("biomes")) {
               JsonArray jsonarray = jsonobject2.getAsJsonArray("biomes");
               abiome = jsonarray.size() > 0 ? new Biome[jsonarray.size()] : new Biome[]{Biomes.OCEAN};

               for(int i = 0; i < jsonarray.size(); ++i) {
                  abiome[i] = Registry.field_212624_m.func_218349_b(new ResourceLocation(jsonarray.get(i).getAsString())).orElse(Biomes.OCEAN);
               }
            }

            if (BiomeProviderType.field_205461_c == biomeprovidertype3) {
               SingleBiomeProviderSettings singlebiomeprovidersettings2 = biomeprovidertype.func_226840_a_(this.field_76579_a.getLevelData()).func_205436_a(abiome[0]);
               biomeprovider = biomeprovidertype.func_205457_a(singlebiomeprovidersettings2);
            }

            if (BiomeProviderType.field_205460_b == biomeprovidertype3) {
               int j = jsonobject2.has("size") ? jsonobject2.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeProviderSettings checkerboardbiomeprovidersettings = biomeprovidertype2.func_226840_a_(this.field_76579_a.getLevelData()).func_206860_a(abiome).func_206861_a(j);
               biomeprovider = biomeprovidertype2.func_205457_a(checkerboardbiomeprovidersettings);
            }

            if (BiomeProviderType.field_206859_d == biomeprovidertype3) {
               OverworldBiomeProviderSettings overworldbiomeprovidersettings1 = biomeprovidertype1.func_226840_a_(this.field_76579_a.getLevelData());
               biomeprovider = biomeprovidertype1.func_205457_a(overworldbiomeprovidersettings1);
            }
         }

         if (biomeprovider == null) {
            biomeprovider = biomeprovidertype.func_205457_a(biomeprovidertype.func_226840_a_(this.field_76579_a.getLevelData()).func_205436_a(Biomes.OCEAN));
         }

         BlockState blockstate = Blocks.STONE.defaultBlockState();
         BlockState blockstate1 = Blocks.WATER.defaultBlockState();
         JsonObject jsonobject3 = jsonobject.getAsJsonObject("chunk_generator");
         if (jsonobject3 != null && jsonobject3.has("options")) {
            JsonObject jsonobject4 = jsonobject3.getAsJsonObject("options");
            if (jsonobject4.has("default_block")) {
               String s = jsonobject4.getAsJsonPrimitive("default_block").getAsString();
               blockstate = Registry.BLOCK.get(new ResourceLocation(s)).defaultBlockState();
            }

            if (jsonobject4.has("default_fluid")) {
               String s1 = jsonobject4.getAsJsonPrimitive("default_fluid").getAsString();
               blockstate1 = Registry.BLOCK.get(new ResourceLocation(s1)).defaultBlockState();
            }
         }

         if (jsonobject3 != null && jsonobject3.has("type")) {
            ChunkGeneratorType<?, ?> chunkgeneratortype5 = Registry.field_212627_p.get(new ResourceLocation(jsonobject3.getAsJsonPrimitive("type").getAsString()));
            if (ChunkGeneratorType.field_206912_c == chunkgeneratortype5) {
               NetherGenSettings nethergensettings = chunkgeneratortype2.func_205483_a();
               nethergensettings.func_214969_a(blockstate);
               nethergensettings.func_214970_b(blockstate1);
               return chunkgeneratortype2.create(this.field_76579_a, biomeprovider, nethergensettings);
            }

            if (ChunkGeneratorType.field_206913_d == chunkgeneratortype5) {
               EndGenerationSettings endgenerationsettings = chunkgeneratortype3.func_205483_a();
               endgenerationsettings.func_205538_a(new BlockPos(0, 64, 0));
               endgenerationsettings.func_214969_a(blockstate);
               endgenerationsettings.func_214970_b(blockstate1);
               return chunkgeneratortype3.create(this.field_76579_a, biomeprovider, endgenerationsettings);
            }
         }

         OverworldGenSettings overworldgensettings1 = chunkgeneratortype4.func_205483_a();
         overworldgensettings1.func_214969_a(blockstate);
         overworldgensettings1.func_214970_b(blockstate1);
         return chunkgeneratortype4.create(this.field_76579_a, biomeprovider, overworldgensettings1);
      }
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos p_206920_1_, boolean p_206920_2_) {
      for(int i = p_206920_1_.getMinBlockX(); i <= p_206920_1_.getMaxBlockX(); ++i) {
         for(int j = p_206920_1_.getMinBlockZ(); j <= p_206920_1_.getMaxBlockZ(); ++j) {
            BlockPos blockpos = this.func_206921_a(i, j, p_206920_2_);
            if (blockpos != null) {
               return blockpos;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos func_206921_a(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_206921_1_, 0, p_206921_2_);
      Biome biome = this.field_76579_a.getBiome(blockpos$mutable);
      BlockState blockstate = biome.func_203944_q().getTopMaterial();
      if (p_206921_3_ && !blockstate.getBlock().is(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         Chunk chunk = this.field_76579_a.getChunk(p_206921_1_ >> 4, p_206921_2_ >> 4);
         int i = chunk.getHeight(Heightmap.Type.MOTION_BLOCKING, p_206921_1_ & 15, p_206921_2_ & 15);
         if (i < 0) {
            return null;
         } else if (chunk.getHeight(Heightmap.Type.WORLD_SURFACE, p_206921_1_ & 15, p_206921_2_ & 15) > chunk.getHeight(Heightmap.Type.OCEAN_FLOOR, p_206921_1_ & 15, p_206921_2_ & 15)) {
            return null;
         } else {
            for(int j = i + 1; j >= 0; --j) {
               blockpos$mutable.set(p_206921_1_, j, p_206921_2_);
               BlockState blockstate1 = this.field_76579_a.getBlockState(blockpos$mutable);
               if (!blockstate1.getFluidState().isEmpty()) {
                  break;
               }

               if (blockstate1.equals(blockstate)) {
                  return blockpos$mutable.above().immutable();
               }
            }

            return null;
         }
      }
   }

   public float func_76563_a(long p_76563_1_, float p_76563_3_) {
      double d0 = MathHelper.frac((double)p_76563_1_ / 24000.0D - 0.25D);
      double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
      return (float)(d0 * 2.0D + d1) / 3.0F;
   }

   public boolean func_76569_d() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d func_76562_b(float p_76562_1_, float p_76562_2_) {
      float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.7529412F;
      float f2 = 0.84705883F;
      float f3 = 1.0F;
      f1 = f1 * (f * 0.94F + 0.06F);
      f2 = f2 * (f * 0.94F + 0.06F);
      f3 = f3 * (f * 0.91F + 0.09F);
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   public boolean func_76567_e() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_76568_b(int p_76568_1_, int p_76568_2_) {
      return false;
   }
}
