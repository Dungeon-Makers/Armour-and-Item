package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class VoidStartPlatformFeature extends Feature<NoFeatureConfig> {
   private static final BlockPos PLATFORM_ORIGIN = new BlockPos(8, 3, 8);
   private static final ChunkPos PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_ORIGIN);

   public VoidStartPlatformFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51417_1_) {
      super(p_i51417_1_);
   }

   private static int checkerboardDistance(int p_214563_0_, int p_214563_1_, int p_214563_2_, int p_214563_3_) {
      return Math.max(Math.abs(p_214563_0_ - p_214563_2_), Math.abs(p_214563_1_ - p_214563_3_));
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      ChunkPos chunkpos = new ChunkPos(p_212245_4_);
      if (checkerboardDistance(chunkpos.x, chunkpos.z, PLATFORM_ORIGIN_CHUNK.x, PLATFORM_ORIGIN_CHUNK.z) > 1) {
         return true;
      } else {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int i = chunkpos.getMinBlockZ(); i <= chunkpos.getMaxBlockZ(); ++i) {
            for(int j = chunkpos.getMinBlockX(); j <= chunkpos.getMaxBlockX(); ++j) {
               if (checkerboardDistance(PLATFORM_ORIGIN.getX(), PLATFORM_ORIGIN.getZ(), j, i) <= 16) {
                  blockpos$mutable.set(j, PLATFORM_ORIGIN.getY(), i);
                  if (blockpos$mutable.equals(PLATFORM_ORIGIN)) {
                     p_212245_1_.setBlock(blockpos$mutable, Blocks.COBBLESTONE.defaultBlockState(), 2);
                  } else {
                     p_212245_1_.setBlock(blockpos$mutable, Blocks.STONE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}