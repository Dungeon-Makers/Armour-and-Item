package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TopSolidOnce extends Placement<NoPlacementConfig> {
   public TopSolidOnce(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51361_1_) {
      super(p_i51361_1_);
   }

   public Stream<BlockPos> func_212848_a_(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos p_212848_5_) {
      int i = p_212848_3_.nextInt(16) + p_212848_5_.getX();
      int j = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
      int k = p_212848_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
      return Stream.of(new BlockPos(i, k, j));
   }
}