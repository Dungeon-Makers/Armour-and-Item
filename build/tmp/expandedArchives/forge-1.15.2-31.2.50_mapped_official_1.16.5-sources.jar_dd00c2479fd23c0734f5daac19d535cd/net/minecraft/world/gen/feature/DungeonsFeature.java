package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.storage.loot.LootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonsFeature extends Feature<NoFeatureConfig> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityType<?>[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public DungeonsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51477_1_) {
      super(p_i51477_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = 3;
      int j = p_212245_3_.nextInt(2) + 2;
      int k = -j - 1;
      int l = j + 1;
      int i1 = -1;
      int j1 = 4;
      int k1 = p_212245_3_.nextInt(2) + 2;
      int l1 = -k1 - 1;
      int i2 = k1 + 1;
      int j2 = 0;

      for(int k2 = k; k2 <= l; ++k2) {
         for(int l2 = -1; l2 <= 4; ++l2) {
            for(int i3 = l1; i3 <= i2; ++i3) {
               BlockPos blockpos = p_212245_4_.offset(k2, l2, i3);
               Material material = p_212245_1_.getBlockState(blockpos).getMaterial();
               boolean flag = material.isSolid();
               if (l2 == -1 && !flag) {
                  return false;
               }

               if (l2 == 4 && !flag) {
                  return false;
               }

               if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && p_212245_1_.isEmptyBlock(blockpos) && p_212245_1_.isEmptyBlock(blockpos.above())) {
                  ++j2;
               }
            }
         }
      }

      if (j2 >= 1 && j2 <= 5) {
         for(int k3 = k; k3 <= l; ++k3) {
            for(int i4 = 3; i4 >= -1; --i4) {
               for(int k4 = l1; k4 <= i2; ++k4) {
                  BlockPos blockpos1 = p_212245_4_.offset(k3, i4, k4);
                  if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                     if (p_212245_1_.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                        p_212245_1_.setBlock(blockpos1, AIR, 2);
                     }
                  } else if (blockpos1.getY() >= 0 && !p_212245_1_.getBlockState(blockpos1.below()).getMaterial().isSolid()) {
                     p_212245_1_.setBlock(blockpos1, AIR, 2);
                  } else if (p_212245_1_.getBlockState(blockpos1).getMaterial().isSolid() && p_212245_1_.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                     if (i4 == -1 && p_212245_3_.nextInt(4) != 0) {
                        p_212245_1_.setBlock(blockpos1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                     } else {
                        p_212245_1_.setBlock(blockpos1, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         for(int l3 = 0; l3 < 2; ++l3) {
            for(int j4 = 0; j4 < 3; ++j4) {
               int l4 = p_212245_4_.getX() + p_212245_3_.nextInt(j * 2 + 1) - j;
               int i5 = p_212245_4_.getY();
               int j5 = p_212245_4_.getZ() + p_212245_3_.nextInt(k1 * 2 + 1) - k1;
               BlockPos blockpos2 = new BlockPos(l4, i5, j5);
               if (p_212245_1_.isEmptyBlock(blockpos2)) {
                  int j3 = 0;

                  for(Direction direction : Direction.Plane.HORIZONTAL) {
                     if (p_212245_1_.getBlockState(blockpos2.relative(direction)).getMaterial().isSolid()) {
                        ++j3;
                     }
                  }

                  if (j3 == 1) {
                     p_212245_1_.setBlock(blockpos2, StructurePiece.reorient(p_212245_1_, blockpos2, Blocks.CHEST.defaultBlockState()), 2);
                     LockableLootTileEntity.setLootTable(p_212245_1_, p_212245_3_, blockpos2, LootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         p_212245_1_.setBlock(p_212245_4_, Blocks.SPAWNER.defaultBlockState(), 2);
         TileEntity tileentity = p_212245_1_.getBlockEntity(p_212245_4_);
         if (tileentity instanceof MobSpawnerTileEntity) {
            ((MobSpawnerTileEntity)tileentity).getSpawner().setEntityId(this.randomEntityId(p_212245_3_));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", p_212245_4_.getX(), p_212245_4_.getY(), p_212245_4_.getZ());
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType<?> randomEntityId(Random p_201043_1_) {
      return net.minecraftforge.common.DungeonHooks.getRandomDungeonMob(p_201043_1_);
   }
}
