package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TopSolidRange extends Placement<TopSolidRangeConfig> {
   public TopSolidRange(Function<Dynamic<?>, ? extends TopSolidRangeConfig> p_i51359_1_) {
      super(p_i51359_1_);
   }

   public Stream<BlockPos> func_212848_a_(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, TopSolidRangeConfig p_212848_4_, BlockPos p_212848_5_) {
      int i = p_212848_3_.nextInt(p_212848_4_.field_204633_b - p_212848_4_.field_204632_a) + p_212848_4_.field_204632_a;
      return IntStream.range(0, i).mapToObj((p_227452_3_) -> {
         int j = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int k = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int l = p_212848_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, j, k);
         return new BlockPos(j, l, k);
      });
   }
}