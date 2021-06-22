package net.minecraft.world.gen;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IWorldGenerationBaseReader {
   boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_);

   BlockPos getHeightmapPos(Heightmap.Type p_205770_1_, BlockPos p_205770_2_);

   default int getMaxHeight() {
      return this instanceof net.minecraft.world.IWorld ? ((net.minecraft.world.IWorld)this).getLevel().func_201675_m().getHeight() : 256;
   }
}
