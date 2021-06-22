package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class BaseTreeFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider leavesProvider;
   public final List<TreeDecorator> decorators;
   public final int field_227371_p_;
   public transient boolean fromSapling;
   protected net.minecraftforge.common.IPlantable sapling = (net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.OAK_SAPLING;

   protected BaseTreeFeatureConfig(BlockStateProvider p_i225842_1_, BlockStateProvider p_i225842_2_, List<TreeDecorator> p_i225842_3_, int p_i225842_4_) {
      this.trunkProvider = p_i225842_1_;
      this.leavesProvider = p_i225842_2_;
      this.decorators = p_i225842_3_;
      this.field_227371_p_ = p_i225842_4_;
   }

   public void setFromSapling() {
      this.fromSapling = true;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("trunk_provider"), this.trunkProvider.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("leaves_provider"), this.leavesProvider.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("decorators"), p_214634_1_.createList(this.decorators.stream().map((p_227375_1_) -> {
         return p_227375_1_.func_218175_a(p_214634_1_);
      }))).put(p_214634_1_.createString("base_height"), p_214634_1_.createInt(this.field_227371_p_));
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(builder.build()));
   }

   protected BaseTreeFeatureConfig setSapling(net.minecraftforge.common.IPlantable value) {
      this.sapling = value;
      return this;
   }

   public net.minecraftforge.common.IPlantable getSapling() {
       return this.sapling;
   }

   public static <T> BaseTreeFeatureConfig func_227376_b_(Dynamic<T> p_227376_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_227376_0_.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType<?> blockstateprovidertype1 = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_227376_0_.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BaseTreeFeatureConfig(blockstateprovidertype.func_227399_a_(p_227376_0_.get("trunk_provider").orElseEmptyMap()), blockstateprovidertype1.func_227399_a_(p_227376_0_.get("leaves_provider").orElseEmptyMap()), p_227376_0_.get("decorators").asList((p_227374_0_) -> {
         return Registry.TREE_DECORATOR_TYPES.get(new ResourceLocation(p_227374_0_.get("type").asString().orElseThrow(RuntimeException::new))).func_227431_a_(p_227374_0_);
      }), p_227376_0_.get("base_height").asInt(0));
   }

   public static <T> BaseTreeFeatureConfig deserializeJungle(Dynamic<T> data) {
      return func_227376_b_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.JUNGLE_SAPLING);
   }

   public static class Builder {
      public final BlockStateProvider trunkProvider;
      public final BlockStateProvider leavesProvider;
      private List<TreeDecorator> decorators = Lists.newArrayList();
      private int field_227380_d_ = 0;
      protected net.minecraftforge.common.IPlantable sapling = (net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.OAK_SAPLING;

      public Builder(BlockStateProvider p_i225843_1_, BlockStateProvider p_i225843_2_) {
         this.trunkProvider = p_i225843_1_;
         this.leavesProvider = p_i225843_2_;
      }

      public BaseTreeFeatureConfig.Builder func_225569_d_(int p_225569_1_) {
         this.field_227380_d_ = p_225569_1_;
         return this;
      }

      public BaseTreeFeatureConfig.Builder setSapling(net.minecraftforge.common.IPlantable value) {
         this.sapling = value;
         return this;
      }

      public BaseTreeFeatureConfig build() {
         return new BaseTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.decorators, this.field_227380_d_).setSapling(sapling);
      }
   }
}
