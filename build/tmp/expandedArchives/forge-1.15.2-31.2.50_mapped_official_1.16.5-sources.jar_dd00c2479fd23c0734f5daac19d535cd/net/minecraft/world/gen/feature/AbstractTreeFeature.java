package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.template.Template;

public abstract class AbstractTreeFeature<T extends BaseTreeFeatureConfig> extends Feature<T> {
   public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> p_i225797_1_) {
      super(p_i225797_1_);
   }

   protected static boolean func_214587_a(IWorldGenerationBaseReader p_214587_0_, BlockPos p_214587_1_) {
      if (p_214587_0_ instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
         return p_214587_0_.isStateAtPosition(p_214587_1_, state -> state.canBeReplacedByLogs((net.minecraft.world.IWorldReader)p_214587_0_, p_214587_1_));
      return p_214587_0_.isStateAtPosition(p_214587_1_, (p_214573_0_) -> {
         Block block = p_214573_0_.getBlock();
         return p_214573_0_.isAir() || p_214573_0_.is(BlockTags.LEAVES) || isDirt(block) || block.is(BlockTags.LOGS) || block.is(BlockTags.SAPLINGS) || block == Blocks.VINE;
      });
   }

   public static boolean func_214574_b(IWorldGenerationBaseReader p_214574_0_, BlockPos p_214574_1_) {
      if (p_214574_0_ instanceof net.minecraft.world.IBlockReader) // FORGE: Redirect to state method when possible
        return p_214574_0_.isStateAtPosition(p_214574_1_, state -> state.isAir((net.minecraft.world.IBlockReader)p_214574_0_, p_214574_1_));
      return p_214574_0_.isStateAtPosition(p_214574_1_, BlockState::isAir);
   }

   protected static boolean func_214578_c(IWorldGenerationBaseReader p_214578_0_, BlockPos p_214578_1_) {
      return p_214578_0_.isStateAtPosition(p_214578_1_, (p_214590_0_) -> {
         Block block = p_214590_0_.getBlock();
         return isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
      });
   }

   protected static boolean func_227222_d_(IWorldGenerationBaseReader p_227222_0_, BlockPos p_227222_1_) {
      return p_227222_0_.isStateAtPosition(p_227222_1_, (p_227224_0_) -> {
         return p_227224_0_.getBlock() == Blocks.VINE;
      });
   }

   public static boolean func_214571_e(IWorldGenerationBaseReader p_214571_0_, BlockPos p_214571_1_) {
      return p_214571_0_.isStateAtPosition(p_214571_1_, (p_214583_0_) -> {
         return p_214583_0_.getBlock() == Blocks.WATER;
      });
   }

   public static boolean func_214572_g(IWorldGenerationBaseReader p_214572_0_, BlockPos p_214572_1_) {
      if (p_214572_0_ instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
         return p_214572_0_.isStateAtPosition(p_214572_1_, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader)p_214572_0_, p_214572_1_));
      return p_214572_0_.isStateAtPosition(p_214572_1_, (p_227223_0_) -> {
         return p_227223_0_.isAir() || p_227223_0_.is(BlockTags.LEAVES);
      });
   }

   @Deprecated //Forge: moved to isSoil
   public static boolean func_214589_h(IWorldGenerationBaseReader p_214589_0_, BlockPos p_214589_1_) {
      return p_214589_0_.isStateAtPosition(p_214589_1_, (p_227221_0_) -> {
         return isDirt(p_227221_0_.getBlock());
      });
   }

   protected static boolean isSoil(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return func_214589_h(reader, pos);
      return reader.isStateAtPosition(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   @Deprecated //Forge: moved to isSoilOrFarm
   protected static boolean func_214585_i(IWorldGenerationBaseReader p_214585_0_, BlockPos p_214585_1_) {
      return p_214585_0_.isStateAtPosition(p_214585_1_, (p_227220_0_) -> {
         Block block = p_227220_0_.getBlock();
         return isDirt(block) || block == Blocks.FARMLAND;
      });
   }

   protected static boolean isSoilOrFarm(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return func_214585_i(reader, pos);
      return reader.isStateAtPosition(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   public static boolean func_214576_j(IWorldGenerationBaseReader p_214576_0_, BlockPos p_214576_1_) {
      return p_214576_0_.isStateAtPosition(p_214576_1_, (p_227218_0_) -> {
         Material material = p_227218_0_.getMaterial();
         return material == Material.REPLACEABLE_PLANT;
      });
   }

   @Deprecated //Forge: moved to setDirtAt
   protected void func_214584_a(IWorldGenerationReader p_214584_1_, BlockPos p_214584_2_) {
      if (!func_214578_c(p_214584_1_, p_214584_2_)) {
         this.func_202278_a(p_214584_1_, p_214584_2_, Blocks.DIRT.defaultBlockState());
      }

   }

   protected boolean func_227216_a_(IWorldGenerationReader p_227216_1_, Random p_227216_2_, BlockPos p_227216_3_, Set<BlockPos> p_227216_4_, MutableBoundingBox p_227216_5_, BaseTreeFeatureConfig p_227216_6_) {
      if (!func_214572_g(p_227216_1_, p_227216_3_) && !func_214576_j(p_227216_1_, p_227216_3_) && !func_214571_e(p_227216_1_, p_227216_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227216_1_, p_227216_3_, p_227216_6_.trunkProvider.getState(p_227216_2_, p_227216_3_), p_227216_5_);
         p_227216_4_.add(p_227216_3_.immutable());
         return true;
      }
   }

   protected boolean func_227219_b_(IWorldGenerationReader p_227219_1_, Random p_227219_2_, BlockPos p_227219_3_, Set<BlockPos> p_227219_4_, MutableBoundingBox p_227219_5_, BaseTreeFeatureConfig p_227219_6_) {
      if (!func_214572_g(p_227219_1_, p_227219_3_) && !func_214576_j(p_227219_1_, p_227219_3_) && !func_214571_e(p_227219_1_, p_227219_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227219_1_, p_227219_3_, p_227219_6_.leavesProvider.getState(p_227219_2_, p_227219_3_), p_227219_5_);
         p_227219_4_.add(p_227219_3_.immutable());
         return true;
      }
   }

   protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
      if (!(reader instanceof IWorld)) {
         func_214584_a(reader, pos);
         return;
      }
      ((IWorld)reader).getBlockState(pos).onPlantGrow((IWorld)reader, pos, origin);
   }
   protected void func_202278_a(IWorldWriter p_202278_1_, BlockPos p_202278_2_, BlockState p_202278_3_) {
      this.func_208521_b(p_202278_1_, p_202278_2_, p_202278_3_);
   }

   protected final void func_227217_a_(IWorldWriter p_227217_1_, BlockPos p_227217_2_, BlockState p_227217_3_, MutableBoundingBox p_227217_4_) {
      this.func_208521_b(p_227217_1_, p_227217_2_, p_227217_3_);
      p_227217_4_.expand(new MutableBoundingBox(p_227217_2_, p_227217_2_));
   }

   private void func_208521_b(IWorldWriter p_208521_1_, BlockPos p_208521_2_, BlockState p_208521_3_) {
      p_208521_1_.setBlock(p_208521_2_, p_208521_3_, 19);
   }

   public final boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, T p_212245_5_) {
      Set<BlockPos> set = Sets.newHashSet();
      Set<BlockPos> set1 = Sets.newHashSet();
      Set<BlockPos> set2 = Sets.newHashSet();
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getUnknownBox();
      boolean flag = this.doPlace(p_212245_1_, p_212245_3_, p_212245_4_, set, set1, mutableboundingbox, p_212245_5_);
      if (mutableboundingbox.x0 <= mutableboundingbox.x1 && flag && !set.isEmpty()) {
         if (!p_212245_5_.decorators.isEmpty()) {
            List<BlockPos> list = Lists.newArrayList(set);
            List<BlockPos> list1 = Lists.newArrayList(set1);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list1.sort(Comparator.comparingInt(Vec3i::getY));
            p_212245_5_.decorators.forEach((p_227215_6_) -> {
               p_227215_6_.place(p_212245_1_, p_212245_3_, list, list1, set2, mutableboundingbox);
            });
         }

         VoxelShapePart voxelshapepart = this.func_227214_a_(p_212245_1_, mutableboundingbox, set, set2);
         Template.updateShapeAtEdge(p_212245_1_, 3, voxelshapepart, mutableboundingbox.x0, mutableboundingbox.y0, mutableboundingbox.z0);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShapePart func_227214_a_(IWorld p_227214_1_, MutableBoundingBox p_227214_2_, Set<BlockPos> p_227214_3_, Set<BlockPos> p_227214_4_) {
      List<Set<BlockPos>> list = Lists.newArrayList();
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(p_227214_2_.getXSpan(), p_227214_2_.getYSpan(), p_227214_2_.getZSpan());
      int i = 6;

      for(int j = 0; j < 6; ++j) {
         list.add(Sets.newHashSet());
      }

      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
         for(BlockPos blockpos : Lists.newArrayList(p_227214_4_)) {
            if (p_227214_2_.isInside(blockpos)) {
               voxelshapepart.setFull(blockpos.getX() - p_227214_2_.x0, blockpos.getY() - p_227214_2_.y0, blockpos.getZ() - p_227214_2_.z0, true, true);
            }
         }

         for(BlockPos blockpos1 : Lists.newArrayList(p_227214_3_)) {
            if (p_227214_2_.isInside(blockpos1)) {
               voxelshapepart.setFull(blockpos1.getX() - p_227214_2_.x0, blockpos1.getY() - p_227214_2_.y0, blockpos1.getZ() - p_227214_2_.z0, true, true);
            }

            for(Direction direction : Direction.values()) {
               blockpos$pooledmutable.set(blockpos1).move(direction);
               if (!p_227214_3_.contains(blockpos$pooledmutable)) {
                  BlockState blockstate = p_227214_1_.getBlockState(blockpos$pooledmutable);
                  if (blockstate.func_196959_b(BlockStateProperties.DISTANCE)) {
                     list.get(0).add(blockpos$pooledmutable.immutable());
                     this.func_208521_b(p_227214_1_, blockpos$pooledmutable, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(1)));
                     if (p_227214_2_.isInside(blockpos$pooledmutable)) {
                        voxelshapepart.setFull(blockpos$pooledmutable.getX() - p_227214_2_.x0, blockpos$pooledmutable.getY() - p_227214_2_.y0, blockpos$pooledmutable.getZ() - p_227214_2_.z0, true, true);
                     }
                  }
               }
            }
         }

         for(int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for(BlockPos blockpos2 : set) {
               if (p_227214_2_.isInside(blockpos2)) {
                  voxelshapepart.setFull(blockpos2.getX() - p_227214_2_.x0, blockpos2.getY() - p_227214_2_.y0, blockpos2.getZ() - p_227214_2_.z0, true, true);
               }

               for(Direction direction1 : Direction.values()) {
                  blockpos$pooledmutable.set(blockpos2).move(direction1);
                  if (!set.contains(blockpos$pooledmutable) && !set1.contains(blockpos$pooledmutable)) {
                     BlockState blockstate1 = p_227214_1_.getBlockState(blockpos$pooledmutable);
                     if (blockstate1.func_196959_b(BlockStateProperties.DISTANCE)) {
                        int k = blockstate1.getValue(BlockStateProperties.DISTANCE);
                        if (k > l + 1) {
                           BlockState blockstate2 = blockstate1.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(l + 1));
                           this.func_208521_b(p_227214_1_, blockpos$pooledmutable, blockstate2);
                           if (p_227214_2_.isInside(blockpos$pooledmutable)) {
                              voxelshapepart.setFull(blockpos$pooledmutable.getX() - p_227214_2_.x0, blockpos$pooledmutable.getY() - p_227214_2_.y0, blockpos$pooledmutable.getZ() - p_227214_2_.z0, true, true);
                           }

                           set1.add(blockpos$pooledmutable.immutable());
                        }
                     }
                  }
               }
            }
         }
      }

      return voxelshapepart;
   }

   protected abstract boolean doPlace(IWorldGenerationReader p_225557_1_, Random p_225557_2_, BlockPos p_225557_3_, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox p_225557_6_, T p_225557_7_);
}
