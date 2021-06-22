package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootTables;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public BonusChestFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49911_1_) {
      super(p_i49911_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      ChunkPos chunkpos = new ChunkPos(p_212245_4_);
      List<Integer> list = IntStream.rangeClosed(chunkpos.getMinBlockX(), chunkpos.getMaxBlockX()).boxed().collect(Collectors.toList());
      Collections.shuffle(list, p_212245_3_);
      List<Integer> list1 = IntStream.rangeClosed(chunkpos.getMinBlockZ(), chunkpos.getMaxBlockZ()).boxed().collect(Collectors.toList());
      Collections.shuffle(list1, p_212245_3_);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Integer integer : list) {
         for(Integer integer1 : list1) {
            blockpos$mutable.set(integer, 0, integer1);
            BlockPos blockpos = p_212245_1_.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable);
            if (p_212245_1_.isEmptyBlock(blockpos) || p_212245_1_.getBlockState(blockpos).getCollisionShape(p_212245_1_, blockpos).isEmpty()) {
               p_212245_1_.setBlock(blockpos, Blocks.CHEST.defaultBlockState(), 2);
               LockableLootTileEntity.setLootTable(p_212245_1_, p_212245_3_, blockpos, LootTables.SPAWN_BONUS_CHEST);
               BlockState blockstate = Blocks.TORCH.defaultBlockState();

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos blockpos1 = blockpos.relative(direction);
                  if (blockstate.canSurvive(p_212245_1_, blockpos1)) {
                     p_212245_1_.setBlock(blockpos1, blockstate, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}