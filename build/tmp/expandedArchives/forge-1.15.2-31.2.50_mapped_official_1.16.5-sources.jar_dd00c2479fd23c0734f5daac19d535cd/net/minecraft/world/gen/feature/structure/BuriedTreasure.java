package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class BuriedTreasure {
   public static class Piece extends StructurePiece {
      public Piece(BlockPos p_i48882_1_) {
         super(IStructurePieceType.BURIED_TREASURE_PIECE, 0);
         this.boundingBox = new MutableBoundingBox(p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ(), p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ());
      }

      public Piece(TemplateManager p_i50677_1_, CompoundNBT p_i50677_2_) {
         super(IStructurePieceType.BURIED_TREASURE_PIECE, p_i50677_2_);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         int i = p_225577_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.boundingBox.x0, i, this.boundingBox.z0);

         while(blockpos$mutable.getY() > 0) {
            BlockState blockstate = p_225577_1_.getBlockState(blockpos$mutable);
            BlockState blockstate1 = p_225577_1_.getBlockState(blockpos$mutable.below());
            if (blockstate1 == Blocks.SANDSTONE.defaultBlockState() || blockstate1 == Blocks.STONE.defaultBlockState() || blockstate1 == Blocks.ANDESITE.defaultBlockState() || blockstate1 == Blocks.GRANITE.defaultBlockState() || blockstate1 == Blocks.DIORITE.defaultBlockState()) {
               BlockState blockstate2 = !blockstate.isAir() && !this.isLiquid(blockstate) ? blockstate : Blocks.SAND.defaultBlockState();

               for(Direction direction : Direction.values()) {
                  BlockPos blockpos = blockpos$mutable.relative(direction);
                  BlockState blockstate3 = p_225577_1_.getBlockState(blockpos);
                  if (blockstate3.isAir() || this.isLiquid(blockstate3)) {
                     BlockPos blockpos1 = blockpos.below();
                     BlockState blockstate4 = p_225577_1_.getBlockState(blockpos1);
                     if ((blockstate4.isAir() || this.isLiquid(blockstate4)) && direction != Direction.UP) {
                        p_225577_1_.setBlock(blockpos, blockstate1, 3);
                     } else {
                        p_225577_1_.setBlock(blockpos, blockstate2, 3);
                     }
                  }
               }

               this.boundingBox = new MutableBoundingBox(blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ(), blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ());
               return this.createChest(p_225577_1_, p_225577_4_, p_225577_3_, blockpos$mutable, LootTables.BURIED_TREASURE, (BlockState)null);
            }

            blockpos$mutable.move(0, -1, 0);
         }

         return false;
      }

      private boolean isLiquid(BlockState p_204295_1_) {
         return p_204295_1_ == Blocks.WATER.defaultBlockState() || p_204295_1_ == Blocks.LAVA.defaultBlockState();
      }
   }
}