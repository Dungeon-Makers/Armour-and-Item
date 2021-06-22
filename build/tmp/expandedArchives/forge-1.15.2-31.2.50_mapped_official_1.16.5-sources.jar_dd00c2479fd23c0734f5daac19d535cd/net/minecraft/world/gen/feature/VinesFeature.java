package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class VinesFeature extends Feature<NoFeatureConfig> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51418_1_) {
      super(p_i51418_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_212245_4_);

      for(int i = p_212245_4_.getY(); i < p_212245_1_.getLevel().func_201675_m().getHeight(); ++i) {
         blockpos$mutable.set(p_212245_4_);
         blockpos$mutable.move(p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), 0, p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4));
         blockpos$mutable.setY(i);
         if (p_212245_1_.isEmptyBlock(blockpos$mutable)) {
            for(Direction direction : DIRECTIONS) {
               if (direction != Direction.DOWN && VineBlock.isAcceptableNeighbour(p_212245_1_, blockpos$mutable, direction)) {
                  p_212245_1_.setBlock(blockpos$mutable, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), Boolean.valueOf(true)), 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}
