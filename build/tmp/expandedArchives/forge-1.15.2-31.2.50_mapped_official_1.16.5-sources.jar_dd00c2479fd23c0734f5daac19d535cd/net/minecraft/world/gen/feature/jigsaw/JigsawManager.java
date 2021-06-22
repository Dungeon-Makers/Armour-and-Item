package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final JigsawPatternRegistry field_214891_a = new JigsawPatternRegistry();

   public static void func_214889_a(ResourceLocation p_214889_0_, int p_214889_1_, JigsawManager.IPieceFactory p_214889_2_, ChunkGenerator<?> p_214889_3_, TemplateManager p_214889_4_, BlockPos p_214889_5_, List<StructurePiece> p_214889_6_, Random p_214889_7_) {
      Structures.func_215140_a();
      new JigsawManager.Assembler(p_214889_0_, p_214889_1_, p_214889_2_, p_214889_3_, p_214889_4_, p_214889_5_, p_214889_6_, p_214889_7_);
   }

   static {
      field_214891_a.func_214932_a(JigsawPattern.field_214949_a);
   }

   static final class Assembler {
      private final int maxDepth;
      private final JigsawManager.IPieceFactory factory;
      private final ChunkGenerator<?> chunkGenerator;
      private final TemplateManager structureManager;
      private final List<StructurePiece> pieces;
      private final Random random;
      private final Deque<JigsawManager.Entry> placing = Queues.newArrayDeque();

      public Assembler(ResourceLocation p_i50691_1_, int p_i50691_2_, JigsawManager.IPieceFactory p_i50691_3_, ChunkGenerator<?> p_i50691_4_, TemplateManager p_i50691_5_, BlockPos p_i50691_6_, List<StructurePiece> p_i50691_7_, Random p_i50691_8_) {
         this.maxDepth = p_i50691_2_;
         this.factory = p_i50691_3_;
         this.chunkGenerator = p_i50691_4_;
         this.structureManager = p_i50691_5_;
         this.pieces = p_i50691_7_;
         this.random = p_i50691_8_;
         Rotation rotation = Rotation.getRandom(p_i50691_8_);
         JigsawPattern jigsawpattern = JigsawManager.field_214891_a.func_214933_a(p_i50691_1_);
         JigsawPiece jigsawpiece = jigsawpattern.getRandomTemplate(p_i50691_8_);
         AbstractVillagePiece abstractvillagepiece = p_i50691_3_.create(p_i50691_5_, jigsawpiece, p_i50691_6_, jigsawpiece.getGroundLevelDelta(), rotation, jigsawpiece.getBoundingBox(p_i50691_5_, p_i50691_6_, rotation));
         MutableBoundingBox mutableboundingbox = abstractvillagepiece.getBoundingBox();
         int i = (mutableboundingbox.x1 + mutableboundingbox.x0) / 2;
         int j = (mutableboundingbox.z1 + mutableboundingbox.z0) / 2;
         int k = p_i50691_4_.getFirstFreeHeight(i, j, Heightmap.Type.WORLD_SURFACE_WG);
         abstractvillagepiece.move(0, k - (mutableboundingbox.y0 + abstractvillagepiece.getGroundLevelDelta()), 0);
         p_i50691_7_.add(abstractvillagepiece);
         if (p_i50691_2_ > 0) {
            int l = 80;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
            this.placing.addLast(new JigsawManager.Entry(abstractvillagepiece, new AtomicReference<>(VoxelShapes.join(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox)), IBooleanFunction.ONLY_FIRST)), k + 80, 0));

            while(!this.placing.isEmpty()) {
               JigsawManager.Entry jigsawmanager$entry = this.placing.removeFirst();
               this.func_214881_a(jigsawmanager$entry.piece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth);
            }

         }
      }

      private void func_214881_a(AbstractVillagePiece p_214881_1_, AtomicReference<VoxelShape> p_214881_2_, int p_214881_3_, int p_214881_4_) {
         JigsawPiece jigsawpiece = p_214881_1_.getElement();
         BlockPos blockpos = p_214881_1_.getPosition();
         Rotation rotation = p_214881_1_.getRotation();
         JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = jigsawpiece.getProjection();
         boolean flag = jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID;
         AtomicReference<VoxelShape> atomicreference = new AtomicReference<>();
         MutableBoundingBox mutableboundingbox = p_214881_1_.getBoundingBox();
         int i = mutableboundingbox.y0;

         label123:
         for(Template.BlockInfo template$blockinfo : jigsawpiece.getShuffledJigsawBlocks(this.structureManager, blockpos, rotation, this.random)) {
            Direction direction = template$blockinfo.state.getValue(JigsawBlock.FACING);
            BlockPos blockpos1 = template$blockinfo.pos;
            BlockPos blockpos2 = blockpos1.relative(direction);
            int j = blockpos1.getY() - i;
            int k = -1;
            JigsawPattern jigsawpattern = JigsawManager.field_214891_a.func_214933_a(new ResourceLocation(template$blockinfo.nbt.getString("target_pool")));
            JigsawPattern jigsawpattern1 = JigsawManager.field_214891_a.func_214933_a(jigsawpattern.getFallback());
            if (jigsawpattern != JigsawPattern.field_214950_b && (jigsawpattern.size() != 0 || jigsawpattern == JigsawPattern.field_214949_a)) {
               boolean flag1 = mutableboundingbox.isInside(blockpos2);
               AtomicReference<VoxelShape> atomicreference1;
               int l;
               if (flag1) {
                  atomicreference1 = atomicreference;
                  l = i;
                  if (atomicreference.get() == null) {
                     atomicreference.set(VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox)));
                  }
               } else {
                  atomicreference1 = p_214881_2_;
                  l = p_214881_3_;
               }

               List<JigsawPiece> list = Lists.newArrayList();
               if (p_214881_4_ != this.maxDepth) {
                  list.addAll(jigsawpattern.getShuffledTemplates(this.random));
               }

               list.addAll(jigsawpattern1.getShuffledTemplates(this.random));

               for(JigsawPiece jigsawpiece1 : list) {
                  if (jigsawpiece1 == EmptyJigsawPiece.INSTANCE) {
                     break;
                  }

                  for(Rotation rotation1 : Rotation.getShuffled(this.random)) {
                     List<Template.BlockInfo> list1 = jigsawpiece1.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, rotation1, this.random);
                     MutableBoundingBox mutableboundingbox1 = jigsawpiece1.getBoundingBox(this.structureManager, BlockPos.ZERO, rotation1);
                     int i1;
                     if (mutableboundingbox1.getYSpan() > 16) {
                        i1 = 0;
                     } else {
                        i1 = list1.stream().mapToInt((p_214880_2_) -> {
                           if (!mutableboundingbox1.isInside(p_214880_2_.pos.relative(p_214880_2_.state.getValue(JigsawBlock.FACING)))) {
                              return 0;
                           } else {
                              ResourceLocation resourcelocation = new ResourceLocation(p_214880_2_.nbt.getString("target_pool"));
                              JigsawPattern jigsawpattern2 = JigsawManager.field_214891_a.func_214933_a(resourcelocation);
                              JigsawPattern jigsawpattern3 = JigsawManager.field_214891_a.func_214933_a(jigsawpattern2.getFallback());
                              return Math.max(jigsawpattern2.getMaxSize(this.structureManager), jigsawpattern3.getMaxSize(this.structureManager));
                           }
                        }).max().orElse(0);
                     }

                     for(Template.BlockInfo template$blockinfo1 : list1) {
                        if (JigsawBlock.canAttach(template$blockinfo, template$blockinfo1)) {
                           BlockPos blockpos3 = template$blockinfo1.pos;
                           BlockPos blockpos4 = new BlockPos(blockpos2.getX() - blockpos3.getX(), blockpos2.getY() - blockpos3.getY(), blockpos2.getZ() - blockpos3.getZ());
                           MutableBoundingBox mutableboundingbox2 = jigsawpiece1.getBoundingBox(this.structureManager, blockpos4, rotation1);
                           int j1 = mutableboundingbox2.y0;
                           JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour1 = jigsawpiece1.getProjection();
                           boolean flag2 = jigsawpattern$placementbehaviour1 == JigsawPattern.PlacementBehaviour.RIGID;
                           int k1 = blockpos3.getY();
                           int l1 = j - k1 + template$blockinfo.state.getValue(JigsawBlock.FACING).getStepY();
                           int i2;
                           if (flag && flag2) {
                              i2 = i + l1;
                           } else {
                              if (k == -1) {
                                 k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                              }

                              i2 = k - k1;
                           }

                           int j2 = i2 - j1;
                           MutableBoundingBox mutableboundingbox3 = mutableboundingbox2.moved(0, j2, 0);
                           BlockPos blockpos5 = blockpos4.offset(0, j2, 0);
                           if (i1 > 0) {
                              int k2 = Math.max(i1 + 1, mutableboundingbox3.y1 - mutableboundingbox3.y0);
                              mutableboundingbox3.y1 = mutableboundingbox3.y0 + k2;
                           }

                           if (!VoxelShapes.joinIsNotEmpty(atomicreference1.get(), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox3).deflate(0.25D)), IBooleanFunction.ONLY_SECOND)) {
                              atomicreference1.set(VoxelShapes.joinUnoptimized(atomicreference1.get(), VoxelShapes.create(AxisAlignedBB.of(mutableboundingbox3)), IBooleanFunction.ONLY_FIRST));
                              int j3 = p_214881_1_.getGroundLevelDelta();
                              int l2;
                              if (flag2) {
                                 l2 = j3 - l1;
                              } else {
                                 l2 = jigsawpiece1.getGroundLevelDelta();
                              }

                              AbstractVillagePiece abstractvillagepiece = this.factory.create(this.structureManager, jigsawpiece1, blockpos5, l2, rotation1, mutableboundingbox3);
                              int i3;
                              if (flag) {
                                 i3 = i + j;
                              } else if (flag2) {
                                 i3 = i2 + k1;
                              } else {
                                 if (k == -1) {
                                    k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                 }

                                 i3 = k + l1 / 2;
                              }

                              p_214881_1_.addJunction(new JigsawJunction(blockpos2.getX(), i3 - j + j3, blockpos2.getZ(), l1, jigsawpattern$placementbehaviour1));
                              abstractvillagepiece.addJunction(new JigsawJunction(blockpos1.getX(), i3 - k1 + l2, blockpos1.getZ(), -l1, jigsawpattern$placementbehaviour));
                              this.pieces.add(abstractvillagepiece);
                              if (p_214881_4_ + 1 <= this.maxDepth) {
                                 this.placing.addLast(new JigsawManager.Entry(abstractvillagepiece, atomicreference1, l, p_214881_4_ + 1));
                              }
                              continue label123;
                           }
                        }
                     }
                  }
               }
            } else {
               JigsawManager.LOGGER.warn("Empty or none existent pool: {}", (Object)template$blockinfo.nbt.getString("target_pool"));
            }
         }

      }
   }

   static final class Entry {
      private final AbstractVillagePiece piece;
      private final AtomicReference<VoxelShape> free;
      private final int boundsTop;
      private final int depth;

      private Entry(AbstractVillagePiece p_i50692_1_, AtomicReference<VoxelShape> p_i50692_2_, int p_i50692_3_, int p_i50692_4_) {
         this.piece = p_i50692_1_;
         this.free = p_i50692_2_;
         this.boundsTop = p_i50692_3_;
         this.depth = p_i50692_4_;
      }
   }

   public interface IPieceFactory {
      AbstractVillagePiece create(TemplateManager p_create_1_, JigsawPiece p_create_2_, BlockPos p_create_3_, int p_create_4_, Rotation p_create_5_, MutableBoundingBox p_create_6_);
   }
}