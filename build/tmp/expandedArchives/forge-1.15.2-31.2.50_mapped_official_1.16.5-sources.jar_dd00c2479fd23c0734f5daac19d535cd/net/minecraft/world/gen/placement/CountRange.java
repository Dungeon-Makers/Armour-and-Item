package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountRange extends SimplePlacement<CountRangeConfig> {
   public CountRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51357_1_) {
      super(p_i51357_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, CountRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      return IntStream.range(0, p_212852_2_.field_202469_a).mapToObj((p_227453_3_) -> {
         int i = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int j = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int k = p_212852_1_.nextInt(p_212852_2_.field_202472_d - p_212852_2_.field_202471_c) + p_212852_2_.field_202470_b;
         return new BlockPos(i, k, j);
      });
   }
}