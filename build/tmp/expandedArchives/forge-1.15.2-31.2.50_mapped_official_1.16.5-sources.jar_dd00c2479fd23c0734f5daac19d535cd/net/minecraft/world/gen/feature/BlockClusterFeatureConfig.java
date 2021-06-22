package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockClusterFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider stateProvider;
   public final BlockPlacer blockPlacer;
   public final Set<Block> whitelist;
   public final Set<BlockState> blacklist;
   public final int tries;
   public final int xspread;
   public final int yspread;
   public final int zspread;
   public final boolean canReplace;
   public final boolean project;
   public final boolean needWater;

   private BlockClusterFeatureConfig(BlockStateProvider p_i225836_1_, BlockPlacer p_i225836_2_, Set<Block> p_i225836_3_, Set<BlockState> p_i225836_4_, int p_i225836_5_, int p_i225836_6_, int p_i225836_7_, int p_i225836_8_, boolean p_i225836_9_, boolean p_i225836_10_, boolean p_i225836_11_) {
      this.stateProvider = p_i225836_1_;
      this.blockPlacer = p_i225836_2_;
      this.whitelist = p_i225836_3_;
      this.blacklist = p_i225836_4_;
      this.tries = p_i225836_5_;
      this.xspread = p_i225836_6_;
      this.yspread = p_i225836_7_;
      this.zspread = p_i225836_8_;
      this.canReplace = p_i225836_9_;
      this.project = p_i225836_10_;
      this.needWater = p_i225836_11_;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214634_1_.createString("state_provider"), this.stateProvider.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("block_placer"), this.blockPlacer.func_218175_a(p_214634_1_)).put(p_214634_1_.createString("whitelist"), p_214634_1_.createList(this.whitelist.stream().map((p_227301_1_) -> {
         return BlockState.func_215689_a(p_214634_1_, p_227301_1_.defaultBlockState()).getValue();
      }))).put(p_214634_1_.createString("blacklist"), p_214634_1_.createList(this.blacklist.stream().map((p_227302_1_) -> {
         return BlockState.func_215689_a(p_214634_1_, p_227302_1_).getValue();
      }))).put(p_214634_1_.createString("tries"), p_214634_1_.createInt(this.tries)).put(p_214634_1_.createString("xspread"), p_214634_1_.createInt(this.xspread)).put(p_214634_1_.createString("yspread"), p_214634_1_.createInt(this.yspread)).put(p_214634_1_.createString("zspread"), p_214634_1_.createInt(this.zspread)).put(p_214634_1_.createString("can_replace"), p_214634_1_.createBoolean(this.canReplace)).put(p_214634_1_.createString("project"), p_214634_1_.createBoolean(this.project)).put(p_214634_1_.createString("need_water"), p_214634_1_.createBoolean(this.needWater));
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(builder.build()));
   }

   public static <T> BlockClusterFeatureConfig func_227300_a_(Dynamic<T> p_227300_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_227300_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockPlacerType<?> blockplacertype = Registry.BLOCK_PLACER_TYPES.get(new ResourceLocation(p_227300_0_.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockClusterFeatureConfig(blockstateprovidertype.func_227399_a_(p_227300_0_.get("state_provider").orElseEmptyMap()), blockplacertype.func_227263_a_(p_227300_0_.get("block_placer").orElseEmptyMap()), p_227300_0_.get("whitelist").asList(BlockState::func_215698_a).stream().map(BlockState::getBlock).collect(Collectors.toSet()), Sets.newHashSet(p_227300_0_.get("blacklist").asList(BlockState::func_215698_a)), p_227300_0_.get("tries").asInt(128), p_227300_0_.get("xspread").asInt(7), p_227300_0_.get("yspread").asInt(3), p_227300_0_.get("zspread").asInt(7), p_227300_0_.get("can_replace").asBoolean(false), p_227300_0_.get("project").asBoolean(true), p_227300_0_.get("need_water").asBoolean(false));
   }

   public static class Builder {
      private final BlockStateProvider stateProvider;
      private final BlockPlacer blockPlacer;
      private Set<Block> whitelist = ImmutableSet.of();
      private Set<BlockState> blacklist = ImmutableSet.of();
      private int tries = 64;
      private int xspread = 7;
      private int yspread = 3;
      private int zspread = 7;
      private boolean canReplace;
      private boolean project = true;
      private boolean needWater = false;

      public Builder(BlockStateProvider p_i225838_1_, BlockPlacer p_i225838_2_) {
         this.stateProvider = p_i225838_1_;
         this.blockPlacer = p_i225838_2_;
      }

      public BlockClusterFeatureConfig.Builder whitelist(Set<Block> p_227316_1_) {
         this.whitelist = p_227316_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder blacklist(Set<BlockState> p_227319_1_) {
         this.blacklist = p_227319_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder tries(int p_227315_1_) {
         this.tries = p_227315_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder xspread(int p_227318_1_) {
         this.xspread = p_227318_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder yspread(int p_227321_1_) {
         this.yspread = p_227321_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder zspread(int p_227323_1_) {
         this.zspread = p_227323_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder canReplace() {
         this.canReplace = true;
         return this;
      }

      public BlockClusterFeatureConfig.Builder noProjection() {
         this.project = false;
         return this;
      }

      public BlockClusterFeatureConfig.Builder needWater() {
         this.needWater = true;
         return this;
      }

      public BlockClusterFeatureConfig build() {
         return new BlockClusterFeatureConfig(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.xspread, this.yspread, this.zspread, this.canReplace, this.project, this.needWater);
      }
   }
}