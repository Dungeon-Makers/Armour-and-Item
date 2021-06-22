package net.minecraft.world.gen.blockstateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class PlainFlowerBlockStateProvider extends BlockStateProvider {
   private static final BlockState[] LOW_NOISE_FLOWERS = new BlockState[]{Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState()};
   private static final BlockState[] HIGH_NOISE_FLOWERS = new BlockState[]{Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState()};

   public PlainFlowerBlockStateProvider() {
      super(BlockStateProviderType.PLAIN_FLOWER_PROVIDER);
   }

   public <T> PlainFlowerBlockStateProvider(Dynamic<T> p_i225857_1_) {
      this();
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      double d0 = Biome.BIOME_INFO_NOISE.getValue((double)p_225574_2_.getX() / 200.0D, (double)p_225574_2_.getZ() / 200.0D, false);
      if (d0 < -0.8D) {
         return LOW_NOISE_FLOWERS[p_225574_1_.nextInt(LOW_NOISE_FLOWERS.length)];
      } else {
         return p_225574_1_.nextInt(3) > 0 ? HIGH_NOISE_FLOWERS[p_225574_1_.nextInt(HIGH_NOISE_FLOWERS.length)] : Blocks.DANDELION.defaultBlockState();
      }
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.field_227393_a_).toString()));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}