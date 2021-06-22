package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class TreeFeatureConfig extends BaseTreeFeatureConfig {
   public final FoliagePlacer field_227327_a_;
   public final int field_227328_b_;
   public final int field_227329_c_;
   public final int field_227330_d_;
   public final int field_227331_f_;
   public final int field_227332_g_;
   public final int field_227333_h_;
   public final int field_227334_i_;
   public final int field_227335_j_;
   public final int field_227336_k_;
   public final boolean field_227337_l_;

   protected TreeFeatureConfig(BlockStateProvider p_i225839_1_, BlockStateProvider p_i225839_2_, FoliagePlacer p_i225839_3_, List<TreeDecorator> p_i225839_4_, int p_i225839_5_, int p_i225839_6_, int p_i225839_7_, int p_i225839_8_, int p_i225839_9_, int p_i225839_10_, int p_i225839_11_, int p_i225839_12_, int p_i225839_13_, int p_i225839_14_, boolean p_i225839_15_) {
      super(p_i225839_1_, p_i225839_2_, p_i225839_4_, p_i225839_5_);
      this.field_227327_a_ = p_i225839_3_;
      this.field_227328_b_ = p_i225839_6_;
      this.field_227329_c_ = p_i225839_7_;
      this.field_227330_d_ = p_i225839_8_;
      this.field_227331_f_ = p_i225839_9_;
      this.field_227332_g_ = p_i225839_10_;
      this.field_227333_h_ = p_i225839_11_;
      this.field_227334_i_ = p_i225839_12_;
      this.field_227335_j_ = p_i225839_13_;
      this.field_227336_k_ = p_i225839_14_;
      this.field_227337_l_ = p_i225839_15_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("foliage_placer"), this.field_227327_a_.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("height_rand_a"), p_214634_1_.createInt(this.field_227328_b_)).put(p_214634_1_.createString("height_rand_b"), p_214634_1_.createInt(this.field_227329_c_)).put(p_214634_1_.createString("trunk_height"), p_214634_1_.createInt(this.field_227330_d_)).put(p_214634_1_.createString("trunk_height_random"), p_214634_1_.createInt(this.field_227331_f_)).put(p_214634_1_.createString("trunk_top_offset"), p_214634_1_.createInt(this.field_227332_g_)).put(p_214634_1_.createString("trunk_top_offset_random"), p_214634_1_.createInt(this.field_227333_h_)).put(p_214634_1_.createString("foliage_height"), p_214634_1_.createInt(this.field_227334_i_)).put(p_214634_1_.createString("foliage_height_random"), p_214634_1_.createInt(this.field_227335_j_)).put(p_214634_1_.createString("max_water_depth"), p_214634_1_.createInt(this.field_227336_k_)).put(p_214634_1_.createString("ignore_vines"), p_214634_1_.createBoolean(this.field_227337_l_));
      Dynamic<T> dynamic = new Dynamic<>(p_214634_1_, p_214634_1_.createMap(builder.build()));
      return dynamic.merge(super.func_214634_a(p_214634_1_));
   }

   @Override
   protected TreeFeatureConfig setSapling(net.minecraftforge.common.IPlantable value) {
      super.setSapling(value);
      return this;
   }

   public static <T> TreeFeatureConfig func_227338_a_(Dynamic<T> p_227338_0_) {
      BaseTreeFeatureConfig basetreefeatureconfig = BaseTreeFeatureConfig.func_227376_b_(p_227338_0_);
      FoliagePlacerType<?> foliageplacertype = Registry.FOLIAGE_PLACER_TYPES.get(new ResourceLocation(p_227338_0_.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new TreeFeatureConfig(basetreefeatureconfig.trunkProvider, basetreefeatureconfig.leavesProvider, foliageplacertype.func_227391_a_(p_227338_0_.get("foliage_placer").orElseEmptyMap()), basetreefeatureconfig.decorators, basetreefeatureconfig.field_227371_p_, p_227338_0_.get("height_rand_a").asInt(0), p_227338_0_.get("height_rand_b").asInt(0), p_227338_0_.get("trunk_height").asInt(-1), p_227338_0_.get("trunk_height_random").asInt(0), p_227338_0_.get("trunk_top_offset").asInt(0), p_227338_0_.get("trunk_top_offset_random").asInt(0), p_227338_0_.get("foliage_height").asInt(-1), p_227338_0_.get("foliage_height_random").asInt(0), p_227338_0_.get("max_water_depth").asInt(0), p_227338_0_.get("ignore_vines").asBoolean(false));
   }

   public static <T> TreeFeatureConfig deserializeJungle(Dynamic<T> data) {
      return func_227338_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.JUNGLE_SAPLING);
   }

   public static <T> TreeFeatureConfig deserializeAcacia(Dynamic<T> data) {
      return func_227338_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.ACACIA_SAPLING);
   }

   public static class Builder extends BaseTreeFeatureConfig.Builder {
      private final FoliagePlacer field_227339_c_;
      private List<TreeDecorator> field_227340_d_ = ImmutableList.of();
      private int field_227341_e_;
      private int field_227342_f_;
      private int field_227343_g_;
      private int field_227344_h_ = -1;
      private int field_227345_i_;
      private int field_227346_j_;
      private int field_227347_k_;
      private int field_227348_l_ = -1;
      private int field_227349_m_;
      private int field_227350_n_;
      private boolean field_227351_o_;

      public Builder(BlockStateProvider p_i225840_1_, BlockStateProvider p_i225840_2_, FoliagePlacer p_i225840_3_) {
         super(p_i225840_1_, p_i225840_2_);
         this.field_227339_c_ = p_i225840_3_;
      }

      public TreeFeatureConfig.Builder func_227353_a_(List<TreeDecorator> p_227353_1_) {
         this.field_227340_d_ = p_227353_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_225569_d_(int p_225569_1_) {
         this.field_227341_e_ = p_225569_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227354_b_(int p_227354_1_) {
         this.field_227342_f_ = p_227354_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227355_c_(int p_227355_1_) {
         this.field_227343_g_ = p_227355_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227356_e_(int p_227356_1_) {
         this.field_227344_h_ = p_227356_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227357_f_(int p_227357_1_) {
         this.field_227345_i_ = p_227357_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227358_g_(int p_227358_1_) {
         this.field_227346_j_ = p_227358_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227359_h_(int p_227359_1_) {
         this.field_227347_k_ = p_227359_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227360_i_(int p_227360_1_) {
         this.field_227348_l_ = p_227360_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227361_j_(int p_227361_1_) {
         this.field_227349_m_ = p_227361_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227362_k_(int p_227362_1_) {
         this.field_227350_n_ = p_227362_1_;
         return this;
      }

      public TreeFeatureConfig.Builder func_227352_a_() {
         this.field_227351_o_ = true;
         return this;
      }

      @Override
      public TreeFeatureConfig.Builder setSapling(net.minecraftforge.common.IPlantable value) {
         super.setSapling(value);
         return this;
      }

      public TreeFeatureConfig build() {
         return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_227339_c_, this.field_227340_d_, this.field_227341_e_, this.field_227342_f_, this.field_227343_g_, this.field_227344_h_, this.field_227345_i_, this.field_227346_j_, this.field_227347_k_, this.field_227348_l_, this.field_227349_m_, this.field_227350_n_, this.field_227351_o_).setSapling(this.sapling);
      }
   }
}
