package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PhantomSpawner;

public class FlatChunkGenerator extends ChunkGenerator<FlatGenerationSettings> {
   private final Biome field_202103_f;
   private final PhantomSpawner field_203229_i = new PhantomSpawner();
   private final CatSpawner field_222544_g = new CatSpawner();

   public FlatChunkGenerator(IWorld p_i48958_1_, BiomeProvider p_i48958_2_, FlatGenerationSettings p_i48958_3_) {
      super(p_i48958_1_, p_i48958_2_, p_i48958_3_);
      this.field_202103_f = this.func_202099_e();
   }

   private Biome func_202099_e() {
      Biome biome = this.settings.getBiome();
      FlatChunkGenerator.WrapperBiome flatchunkgenerator$wrapperbiome = new FlatChunkGenerator.WrapperBiome(biome.func_205401_q(), biome.getPrecipitation(), biome.getBiomeCategory(), biome.getDepth(), biome.getScale(), biome.func_185353_n(), biome.getDownfall(), biome.getWaterColor(), biome.getWaterFogColor(), biome.func_205402_s());
      Map<String, Map<String, String>> map = this.settings.func_82644_b();

      for(String s : map.keySet()) {
         ConfiguredFeature<?, ?>[] configuredfeature = FlatGenerationSettings.STRUCTURE_FEATURES.get(s);
         if (configuredfeature != null) {
            for(ConfiguredFeature<?, ?> configuredfeature1 : configuredfeature) {
               flatchunkgenerator$wrapperbiome.func_203611_a(FlatGenerationSettings.field_202248_k.get(configuredfeature1), configuredfeature1);
               ConfiguredFeature<?, ?> configuredfeature2 = ((DecoratedFeatureConfig)configuredfeature1.config).feature;
               if (configuredfeature2.feature instanceof Structure) {
                  Structure<IFeatureConfig> structure = (Structure)configuredfeature2.feature;
                  IFeatureConfig ifeatureconfig = biome.func_201857_b(structure);
                  IFeatureConfig ifeatureconfig1 = ifeatureconfig != null ? ifeatureconfig : FlatGenerationSettings.field_202249_l.get(configuredfeature1);
                  flatchunkgenerator$wrapperbiome.func_226711_a_(structure.configured(ifeatureconfig1));
               }
            }
         }
      }

      boolean flag = (!this.settings.func_202238_o() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
      if (flag) {
         List<GenerationStage.Decoration> list = Lists.newArrayList();
         list.add(GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         list.add(GenerationStage.Decoration.SURFACE_STRUCTURES);

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            if (!list.contains(generationstage$decoration)) {
               for(ConfiguredFeature<?, ?> configuredfeature3 : biome.func_203607_a(generationstage$decoration)) {
                  flatchunkgenerator$wrapperbiome.func_203611_a(generationstage$decoration, configuredfeature3);
               }
            }
         }
      }

      BlockState[] ablockstate = this.settings.getLayers();

      for(int i = 0; i < ablockstate.length; ++i) {
         BlockState blockstate = ablockstate[i];
         if (blockstate != null && !Heightmap.Type.MOTION_BLOCKING.isOpaque().test(blockstate)) {
            this.settings.func_214990_a(i);
            flatchunkgenerator$wrapperbiome.func_203611_a(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new FillLayerConfig(i, blockstate)).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
         }
      }

      return flatchunkgenerator$wrapperbiome;
   }

   public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
   }

   public int getSpawnHeight() {
      IChunk ichunk = this.field_222540_a.getChunk(0, 0);
      return ichunk.getHeight(Heightmap.Type.MOTION_BLOCKING, 8, 8);
   }

   protected Biome func_225552_a_(BiomeManager p_225552_1_, BlockPos p_225552_2_) {
      return this.field_202103_f;
   }

   public void func_222537_b(IWorld p_222537_1_, IChunk p_222537_2_) {
      BlockState[] ablockstate = this.settings.getLayers();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Heightmap heightmap = p_222537_2_.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = p_222537_2_.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

      for(int i = 0; i < ablockstate.length; ++i) {
         BlockState blockstate = ablockstate[i];
         if (blockstate != null) {
            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  p_222537_2_.setBlockState(blockpos$mutable.set(j, i, k), blockstate, false);
                  heightmap.update(j, i, k, blockstate);
                  heightmap1.update(j, i, k, blockstate);
               }
            }
         }
      }

   }

   public int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      BlockState[] ablockstate = this.settings.getLayers();

      for(int i = ablockstate.length - 1; i >= 0; --i) {
         BlockState blockstate = ablockstate[i];
         if (blockstate != null && p_222529_3_.isOpaque().test(blockstate)) {
            return i + 1;
         }
      }

      return 0;
   }

   public void func_203222_a(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      this.field_203229_i.func_203232_a(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_222544_g.func_221124_a(p_203222_1_, p_203222_2_, p_203222_3_);
   }

   public boolean func_202094_a(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return this.field_202103_f.func_201858_a(p_202094_2_);
   }

   @Nullable
   public <C extends IFeatureConfig> C func_202087_b(Biome p_202087_1_, Structure<C> p_202087_2_) {
      return this.field_202103_f.func_201857_b(p_202087_2_);
   }

   @Nullable
   public BlockPos func_211403_a(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      return !this.settings.func_82644_b().keySet().contains(p_211403_2_.toLowerCase(Locale.ROOT)) ? null : super.func_211403_a(p_211403_1_, p_211403_2_, p_211403_3_, p_211403_4_, p_211403_5_);
   }

   class WrapperBiome extends Biome {
      protected WrapperBiome(ConfiguredSurfaceBuilder<?> p_i51092_2_, Biome.RainType p_i51092_3_, Biome.Category p_i51092_4_, float p_i51092_5_, float p_i51092_6_, float p_i51092_7_, float p_i51092_8_, int p_i51092_9_, int p_i51092_10_, @Nullable String p_i51092_11_) {
         super((new Biome.Builder()).func_205416_a(p_i51092_2_).precipitation(p_i51092_3_).biomeCategory(p_i51092_4_).depth(p_i51092_5_).scale(p_i51092_6_).temperature(p_i51092_7_).downfall(p_i51092_8_).func_205412_a(p_i51092_9_).func_205413_b(p_i51092_10_).func_205418_a(p_i51092_11_));
      }
   }
}