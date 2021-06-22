package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndPodiumFeature extends Feature<NoFeatureConfig> {
   public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
   private final boolean active;

   public EndPodiumFeature(boolean p_i46666_1_) {
      super(NoFeatureConfig::func_214639_a);
      this.active = p_i46666_1_;
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(p_212245_4_.getX() - 4, p_212245_4_.getY() - 1, p_212245_4_.getZ() - 4), new BlockPos(p_212245_4_.getX() + 4, p_212245_4_.getY() + 32, p_212245_4_.getZ() + 4))) {
         boolean flag = blockpos.closerThan(p_212245_4_, 2.5D);
         if (flag || blockpos.closerThan(p_212245_4_, 3.5D)) {
            if (blockpos.getY() < p_212245_4_.getY()) {
               if (flag) {
                  this.func_202278_a(p_212245_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
               } else if (blockpos.getY() < p_212245_4_.getY()) {
                  this.func_202278_a(p_212245_1_, blockpos, Blocks.END_STONE.defaultBlockState());
               }
            } else if (blockpos.getY() > p_212245_4_.getY()) {
               this.func_202278_a(p_212245_1_, blockpos, Blocks.AIR.defaultBlockState());
            } else if (!flag) {
               this.func_202278_a(p_212245_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
            } else if (this.active) {
               this.func_202278_a(p_212245_1_, new BlockPos(blockpos), Blocks.END_PORTAL.defaultBlockState());
            } else {
               this.func_202278_a(p_212245_1_, new BlockPos(blockpos), Blocks.AIR.defaultBlockState());
            }
         }
      }

      for(int i = 0; i < 4; ++i) {
         this.func_202278_a(p_212245_1_, p_212245_4_.above(i), Blocks.BEDROCK.defaultBlockState());
      }

      BlockPos blockpos1 = p_212245_4_.above(2);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         this.func_202278_a(p_212245_1_, blockpos1.relative(direction), Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction));
      }

      return true;
   }
}