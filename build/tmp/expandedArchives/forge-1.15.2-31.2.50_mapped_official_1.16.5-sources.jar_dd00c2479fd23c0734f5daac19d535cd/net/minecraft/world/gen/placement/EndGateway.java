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

public class EndGateway extends Placement<NoPlacementConfig> {
   public EndGateway(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51373_1_) {
      super(p_i51373_1_);
   }

   public Stream<BlockPos> func_212848_a_(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos p_212848_5_) {
      if (p_212848_3_.nextInt(700) == 0) {
         int i = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int j = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int k = p_212848_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, i, j);
         if (k > 0) {
            int l = k + 3 + p_212848_3_.nextInt(7);
            return Stream.of(new BlockPos(i, l, j));
         }
      }

      return Stream.empty();
   }
}