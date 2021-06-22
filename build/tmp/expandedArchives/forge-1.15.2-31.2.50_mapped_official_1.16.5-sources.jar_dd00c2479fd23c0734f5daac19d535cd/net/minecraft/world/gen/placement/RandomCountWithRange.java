package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RandomCountWithRange extends SimplePlacement<CountRangeConfig> {
   public RandomCountWithRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51353_1_) {
      super(p_i51353_1_);
   }

   public Stream<BlockPos> place(Random p_212852_1_, CountRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_1_.nextInt(Math.max(p_212852_2_.field_202469_a, 1));
      return IntStream.range(0, i).mapToObj((p_227455_3_) -> {
         int j = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int k = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int l = p_212852_1_.nextInt(p_212852_2_.field_202472_d - p_212852_2_.field_202471_c) + p_212852_2_.field_202470_b;
         return new BlockPos(j, l, k);
      });
   }
}