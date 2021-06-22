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

public class AtSurfaceRandomCount extends Placement<FrequencyConfig> {
   public AtSurfaceRandomCount(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51370_1_) {
      super(p_i51370_1_);
   }

   public Stream<BlockPos> func_212848_a_(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, FrequencyConfig p_212848_4_, BlockPos p_212848_5_) {
      int i = p_212848_3_.nextInt(p_212848_4_.field_202476_a);
      return IntStream.range(0, i).mapToObj((p_227447_3_) -> {
         int j = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int k = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int l = p_212848_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, j, k);
         return new BlockPos(j, l, k);
      });
   }
}