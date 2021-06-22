package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class ForestFlowerBlockStateProvider extends BlockStateProvider {
   private static final BlockState[] FLOWERS = new BlockState[]{Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()};

   public ForestFlowerBlockStateProvider() {
      super(BlockStateProviderType.FOREST_FLOWER_PROVIDER);
   }

   public <T> ForestFlowerBlockStateProvider(Dynamic<T> p_i225856_1_) {
      this();
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      double d0 = MathHelper.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)p_225574_2_.getX() / 48.0D, (double)p_225574_2_.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
      return FLOWERS[(int)(d0 * (double)FLOWERS.length)];
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.field_227393_a_).toString()));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}