package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome extends net.minecraftforge.registries.ForgeRegistryEntry<Biome> {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Set<Biome> field_201870_ab = Sets.newHashSet();
   public static final ObjectIntIdentityMap<Biome> field_185373_j = new ObjectIntIdentityMap<>();
   protected static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(1234L), 0, 0);
   public static final PerlinNoiseGenerator BIOME_INFO_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(2345L), 0, 0);
   @Nullable
   protected String field_205405_aL;
   protected final float depth;
   protected final float scale;
   protected final float field_76750_F;
   protected final float field_76751_G;
   protected final int field_76759_H;
   protected final int field_204275_aE;
   private final int field_229978_u_;
   @Nullable
   protected final String field_185364_H;
   protected final ConfiguredSurfaceBuilder<?> field_201875_ar;
   protected final Biome.Category biomeCategory;
   protected final Biome.RainType field_201878_av;
   protected final Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> field_201871_ag = Maps.newHashMap();
   protected final Map<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>> field_201872_ah = Maps.newHashMap();
   protected final List<ConfiguredFeature<?, ?>> field_201873_ai = Lists.newArrayList();
   protected final Map<Structure<?>, IFeatureConfig> field_201874_aj = Maps.newHashMap();
   private final Map<EntityClassification, List<Biome.SpawnListEntry>> field_201880_ax = Maps.newHashMap();
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
      return Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });
   });

   @Nullable
   public static Biome func_185356_b(Biome p_185356_0_) {
      return field_185373_j.byId(Registry.field_212624_m.getId(p_185356_0_));
   }

   public static <C extends ICarverConfig> ConfiguredCarver<C> func_203606_a(WorldCarver<C> p_203606_0_, C p_203606_1_) {
      return new ConfiguredCarver<>(p_203606_0_, p_203606_1_);
   }

   protected Biome(Biome.Builder p_i48975_1_) {
      if (p_i48975_1_.field_205422_a != null && p_i48975_1_.precipitation != null && p_i48975_1_.biomeCategory != null && p_i48975_1_.depth != null && p_i48975_1_.scale != null && p_i48975_1_.temperature != null && p_i48975_1_.downfall != null && p_i48975_1_.field_205429_h != null && p_i48975_1_.field_205430_i != null) {
         this.field_201875_ar = p_i48975_1_.field_205422_a;
         this.field_201878_av = p_i48975_1_.precipitation;
         this.biomeCategory = p_i48975_1_.biomeCategory;
         this.depth = p_i48975_1_.depth;
         this.scale = p_i48975_1_.scale;
         this.field_76750_F = p_i48975_1_.temperature;
         this.field_76751_G = p_i48975_1_.downfall;
         this.field_76759_H = p_i48975_1_.field_205429_h;
         this.field_204275_aE = p_i48975_1_.field_205430_i;
         this.field_229978_u_ = this.func_229979_u_();
         this.field_185364_H = p_i48975_1_.field_205431_j;

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            this.field_201872_ah.put(generationstage$decoration, Lists.newArrayList());
         }

         for(EntityClassification entityclassification : EntityClassification.values()) {
            this.field_201880_ax.put(entityclassification, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + p_i48975_1_);
      }
   }

   public boolean func_185363_b() {
      return this.field_185364_H != null;
   }

   private int func_229979_u_() {
      float f = this.field_76750_F;
      f = f / 3.0F;
      f = MathHelper.clamp(f, -1.0F, 1.0F);
      return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColor() {
      return this.field_229978_u_;
   }

   protected void func_201866_a(EntityClassification p_201866_1_, Biome.SpawnListEntry p_201866_2_) {
      this.field_201880_ax.computeIfAbsent(p_201866_1_, k -> Lists.newArrayList()).add(p_201866_2_);
   }

   public List<Biome.SpawnListEntry> func_76747_a(EntityClassification p_76747_1_) {
      return this.field_201880_ax.computeIfAbsent(p_76747_1_, k -> Lists.newArrayList());
   }

   public Biome.RainType getPrecipitation() {
      return this.field_201878_av;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   public float func_76741_f() {
      return 0.1F;
   }

   public float func_180626_a(BlockPos p_180626_1_) {
      if (p_180626_1_.getY() > 64) {
         float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)p_180626_1_.getX() / 8.0F), (double)((float)p_180626_1_.getZ() / 8.0F), false) * 4.0D);
         return this.func_185353_n() - (f + (float)p_180626_1_.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.func_185353_n();
      }
   }

   public final float getTemperature(BlockPos p_225486_1_) {
      long i = p_225486_1_.asLong();
      Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = this.temperatureCache.get();
      float f = long2floatlinkedopenhashmap.get(i);
      if (!Float.isNaN(f)) {
         return f;
      } else {
         float f1 = this.func_180626_a(p_225486_1_);
         if (long2floatlinkedopenhashmap.size() == 1024) {
            long2floatlinkedopenhashmap.removeFirstFloat();
         }

         long2floatlinkedopenhashmap.put(i, f1);
         return f1;
      }
   }

   public boolean shouldFreeze(IWorldReader p_201848_1_, BlockPos p_201848_2_) {
      return this.shouldFreeze(p_201848_1_, p_201848_2_, true);
   }

   public boolean shouldFreeze(IWorldReader p_201854_1_, BlockPos p_201854_2_, boolean p_201854_3_) {
      if (this.getTemperature(p_201854_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201854_2_.getY() >= 0 && p_201854_2_.getY() < p_201854_1_.func_201675_m().getHeight() && p_201854_1_.getBrightness(LightType.BLOCK, p_201854_2_) < 10) {
            BlockState blockstate = p_201854_1_.getBlockState(p_201854_2_);
            IFluidState ifluidstate = p_201854_1_.getFluidState(p_201854_2_);
            if (ifluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
               if (!p_201854_3_) {
                  return true;
               }

               boolean flag = p_201854_1_.isWaterAt(p_201854_2_.west()) && p_201854_1_.isWaterAt(p_201854_2_.east()) && p_201854_1_.isWaterAt(p_201854_2_.north()) && p_201854_1_.isWaterAt(p_201854_2_.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldSnow(IWorldReader p_201850_1_, BlockPos p_201850_2_) {
      if (this.getTemperature(p_201850_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201850_2_.getY() >= 0 && p_201850_2_.getY() < 256 && p_201850_1_.getBrightness(LightType.BLOCK, p_201850_2_) < 10) {
            BlockState blockstate = p_201850_1_.getBlockState(p_201850_2_);
            if (blockstate.isAir(p_201850_1_, p_201850_2_) && Blocks.SNOW.defaultBlockState().canSurvive(p_201850_1_, p_201850_2_)) {
               return true;
            }
         }

         return false;
      }
   }

   public void func_203611_a(GenerationStage.Decoration p_203611_1_, ConfiguredFeature<?, ?> p_203611_2_) {
      if (p_203611_2_.feature == Feature.field_214484_aL) {
         this.field_201873_ai.add(p_203611_2_);
      }

      this.field_201872_ah.get(p_203611_1_).add(p_203611_2_);
   }

   public <C extends ICarverConfig> void func_203609_a(GenerationStage.Carving p_203609_1_, ConfiguredCarver<C> p_203609_2_) {
      this.field_201871_ag.computeIfAbsent(p_203609_1_, (p_203604_0_) -> {
         return Lists.newArrayList();
      }).add(p_203609_2_);
   }

   public List<ConfiguredCarver<?>> func_203603_a(GenerationStage.Carving p_203603_1_) {
      return this.field_201871_ag.computeIfAbsent(p_203603_1_, (p_203610_0_) -> {
         return Lists.newArrayList();
      });
   }

   public <C extends IFeatureConfig> void func_226711_a_(ConfiguredFeature<C, ? extends Structure<C>> p_226711_1_) {
      this.field_201874_aj.put(p_226711_1_.feature, p_226711_1_.config);
   }

   public <C extends IFeatureConfig> boolean func_201858_a(Structure<C> p_201858_1_) {
      return this.field_201874_aj.containsKey(p_201858_1_);
   }

   @Nullable
   public <C extends IFeatureConfig> C func_201857_b(Structure<C> p_201857_1_) {
      return (C)(this.field_201874_aj.get(p_201857_1_));
   }

   public List<ConfiguredFeature<?, ?>> func_201853_g() {
      return this.field_201873_ai;
   }

   public List<ConfiguredFeature<?, ?>> func_203607_a(GenerationStage.Decoration p_203607_1_) {
      return this.field_201872_ah.get(p_203607_1_);
   }

   public void func_203608_a(GenerationStage.Decoration p_203608_1_, ChunkGenerator<? extends GenerationSettings> p_203608_2_, IWorld p_203608_3_, long p_203608_4_, SharedSeedRandom p_203608_6_, BlockPos p_203608_7_) {
      int i = 0;

      for(ConfiguredFeature<?, ?> configuredfeature : this.field_201872_ah.get(p_203608_1_)) {
         p_203608_6_.setFeatureSeed(p_203608_4_, i, p_203608_1_.ordinal());

         try {
            configuredfeature.func_222734_a(p_203608_3_, p_203608_2_, p_203608_6_, p_203608_7_);
         } catch (Exception exception) {
            CrashReport crashreport = CrashReport.forThrowable(exception, "Feature placement");
            crashreport.addCategory("Feature").setDetail("Id", Registry.FEATURE.getKey(configuredfeature.feature)).setDetail("Description", () -> {
               return configuredfeature.feature.toString();
            });
            throw new ReportedException(crashreport);
         }

         ++i;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(double p_225528_1_, double p_225528_3_) {
      double d0 = (double)MathHelper.clamp(this.func_185353_n(), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColors.get(d0, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor() {
      double d0 = (double)MathHelper.clamp(this.func_185353_n(), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColors.get(d0, d1);
   }

   public void buildSurfaceAt(Random p_206854_1_, IChunk p_206854_2_, int p_206854_3_, int p_206854_4_, int p_206854_5_, double p_206854_6_, BlockState p_206854_8_, BlockState p_206854_9_, int p_206854_10_, long p_206854_11_) {
      this.field_201875_ar.initNoise(p_206854_11_);
      this.field_201875_ar.apply(p_206854_1_, p_206854_2_, this, p_206854_3_, p_206854_4_, p_206854_5_, p_206854_6_, p_206854_8_, p_206854_9_, p_206854_10_, p_206854_11_);
   }

   public Biome.TempCategory func_150561_m() {
      if (this.biomeCategory == Biome.Category.OCEAN) {
         return Biome.TempCategory.OCEAN;
      } else if ((double)this.func_185353_n() < 0.2D) {
         return Biome.TempCategory.COLD;
      } else {
         return (double)this.func_185353_n() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
      }
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.field_76751_G;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_205403_k() {
      return new TranslationTextComponent(this.func_210773_k());
   }

   public String func_210773_k() {
      if (this.field_205405_aL == null) {
         this.field_205405_aL = Util.makeDescriptionId("biome", Registry.field_212624_m.getKey(this));
      }

      return this.field_205405_aL;
   }

   public final float getScale() {
      return this.scale;
   }

   public final float func_185353_n() {
      return this.field_76750_F;
   }

   public final int getWaterColor() {
      return this.field_76759_H;
   }

   public final int getWaterFogColor() {
      return this.field_204275_aE;
   }

   public final Biome.Category getBiomeCategory() {
      return this.biomeCategory;
   }

   public ConfiguredSurfaceBuilder<?> func_205401_q() {
      return this.field_201875_ar;
   }

   public ISurfaceBuilderConfig func_203944_q() {
      return this.field_201875_ar.config();
   }

   @Nullable
   public String func_205402_s() {
      return this.field_185364_H;
   }

   public Biome getRiver() {
      if (this == Biomes.SNOWY_TUNDRA) return Biomes.FROZEN_RIVER;
      if (this == Biomes.MUSHROOM_FIELDS || this == Biomes.MUSHROOM_FIELD_SHORE) return Biomes.MUSHROOM_FIELD_SHORE;
      return Biomes.RIVER;
   }

   @Nullable
   public Biome getHill(net.minecraft.world.gen.INoiseRandom rand) {
      return null;
   }

   public static class Builder {
      @Nullable
      private ConfiguredSurfaceBuilder<?> field_205422_a;
      @Nullable
      private Biome.RainType precipitation;
      @Nullable
      private Biome.Category biomeCategory;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer field_205429_h;
      @Nullable
      private Integer field_205430_i;
      @Nullable
      private String field_205431_j;

      public <SC extends ISurfaceBuilderConfig> Biome.Builder func_222351_a(SurfaceBuilder<SC> p_222351_1_, SC p_222351_2_) {
         this.field_205422_a = new ConfiguredSurfaceBuilder<>(p_222351_1_, p_222351_2_);
         return this;
      }

      public Biome.Builder func_205416_a(ConfiguredSurfaceBuilder<?> p_205416_1_) {
         this.field_205422_a = p_205416_1_;
         return this;
      }

      public Biome.Builder precipitation(Biome.RainType p_205415_1_) {
         this.precipitation = p_205415_1_;
         return this;
      }

      public Biome.Builder biomeCategory(Biome.Category p_205419_1_) {
         this.biomeCategory = p_205419_1_;
         return this;
      }

      public Biome.Builder depth(float p_205421_1_) {
         this.depth = p_205421_1_;
         return this;
      }

      public Biome.Builder scale(float p_205420_1_) {
         this.scale = p_205420_1_;
         return this;
      }

      public Biome.Builder temperature(float p_205414_1_) {
         this.temperature = p_205414_1_;
         return this;
      }

      public Biome.Builder downfall(float p_205417_1_) {
         this.downfall = p_205417_1_;
         return this;
      }

      public Biome.Builder func_205412_a(int p_205412_1_) {
         this.field_205429_h = p_205412_1_;
         return this;
      }

      public Biome.Builder func_205413_b(int p_205413_1_) {
         this.field_205430_i = p_205413_1_;
         return this;
      }

      public Biome.Builder func_205418_a(@Nullable String p_205418_1_) {
         this.field_205431_j = p_205418_1_;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.field_205422_a + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.field_205429_h + ",\nwaterFogColor=" + this.field_205430_i + ",\nparent='" + this.field_205431_j + '\'' + "\n" + '}';
      }
   }

   public static enum Category {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether");

      private static final Map<String, Biome.Category> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.Category::getName, (p_222353_0_) -> {
         return p_222353_0_;
      }));
      private final String name;

      private Category(String p_i50595_3_) {
         this.name = p_i50595_3_;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum RainType {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      private static final Map<String, Biome.RainType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.RainType::getName, (p_222360_0_) -> {
         return p_222360_0_;
      }));
      private final String name;

      private RainType(String p_i50593_3_) {
         this.name = p_i50593_3_;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public final EntityType<?> field_200702_b;
      public final int field_76301_c;
      public final int field_76299_d;

      public SpawnListEntry(EntityType<?> p_i48588_1_, int p_i48588_2_, int p_i48588_3_, int p_i48588_4_) {
         super(p_i48588_2_);
         this.field_200702_b = p_i48588_1_;
         this.field_76301_c = p_i48588_3_;
         this.field_76299_d = p_i48588_4_;
      }

      public String toString() {
         return EntityType.getKey(this.field_200702_b) + "*(" + this.field_76301_c + "-" + this.field_76299_d + "):" + this.weight;
      }
   }

   public static class FlowerEntry extends WeightedRandom.Item {
      private final BlockState state;
      public FlowerEntry(BlockState state, int weight) {
         super(weight);
         this.state = state;
      }

      public BlockState getState() {
         return state;
      }
   }

   public static enum TempCategory {
      OCEAN("ocean"),
      COLD("cold"),
      MEDIUM("medium"),
      WARM("warm");

      private static final Map<String, Biome.TempCategory> field_222358_e = Arrays.stream(values()).collect(Collectors.toMap(Biome.TempCategory::func_222357_a, (p_222356_0_) -> {
         return p_222356_0_;
      }));
      private final String field_222359_f;

      private TempCategory(String p_i50594_3_) {
         this.field_222359_f = p_i50594_3_;
      }

      public String func_222357_a() {
         return this.field_222359_f;
      }
   }
}
