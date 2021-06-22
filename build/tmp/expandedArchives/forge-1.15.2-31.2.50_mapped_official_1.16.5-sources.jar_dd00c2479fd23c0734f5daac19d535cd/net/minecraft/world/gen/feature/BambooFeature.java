package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class BambooFeature extends Feature<ProbabilityConfig> {
   private static final BlockState BAMBOO_TRUNK = Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.AGE, Integer.valueOf(1)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE).setValue(BambooBlock.STAGE, Integer.valueOf(0));
   private static final BlockState BAMBOO_FINAL_LARGE = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE).setValue(BambooBlock.STAGE, Integer.valueOf(1));
   private static final BlockState BAMBOO_TOP_LARGE = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
   private static final BlockState BAMBOO_TOP_SMALL = BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);

   public BambooFeature(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49919_1_) {
      super(p_i49919_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, ProbabilityConfig p_212245_5_) {
      int i = 0;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_212245_4_);
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable(p_212245_4_);
      if (p_212245_1_.isEmptyBlock(blockpos$mutable)) {
         if (Blocks.BAMBOO.defaultBlockState().canSurvive(p_212245_1_, blockpos$mutable)) {
            int j = p_212245_3_.nextInt(12) + 5;
            if (p_212245_3_.nextFloat() < p_212245_5_.probability) {
               int k = p_212245_3_.nextInt(4) + 1;

               for(int l = p_212245_4_.getX() - k; l <= p_212245_4_.getX() + k; ++l) {
                  for(int i1 = p_212245_4_.getZ() - k; i1 <= p_212245_4_.getZ() + k; ++i1) {
                     int j1 = l - p_212245_4_.getX();
                     int k1 = i1 - p_212245_4_.getZ();
                     if (j1 * j1 + k1 * k1 <= k * k) {
                        blockpos$mutable1.set(l, p_212245_1_.getHeight(Heightmap.Type.WORLD_SURFACE, l, i1) - 1, i1);
                        if (isDirt(p_212245_1_.getBlockState(blockpos$mutable1).getBlock())) {
                           p_212245_1_.setBlock(blockpos$mutable1, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            for(int l1 = 0; l1 < j && p_212245_1_.isEmptyBlock(blockpos$mutable); ++l1) {
               p_212245_1_.setBlock(blockpos$mutable, BAMBOO_TRUNK, 2);
               blockpos$mutable.move(Direction.UP, 1);
            }

            if (blockpos$mutable.getY() - p_212245_4_.getY() >= 3) {
               p_212245_1_.setBlock(blockpos$mutable, BAMBOO_FINAL_LARGE, 2);
               p_212245_1_.setBlock(blockpos$mutable.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
               p_212245_1_.setBlock(blockpos$mutable.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
            }
         }

         ++i;
      }

      return i > 0;
   }
}