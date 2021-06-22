package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructureStart {
   public static final StructureStart INVALID_START = new StructureStart(Feature.field_202329_g, 0, 0, MutableBoundingBox.getUnknownBox(), 0, 0L) {
      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
      }
   };
   private final Structure<?> feature;
   protected final List<StructurePiece> pieces = Lists.newArrayList();
   protected MutableBoundingBox boundingBox;
   private final int chunkX;
   private final int chunkZ;
   private int references;
   protected final SharedSeedRandom random;

   public StructureStart(Structure<?> p_i225876_1_, int p_i225876_2_, int p_i225876_3_, MutableBoundingBox p_i225876_4_, int p_i225876_5_, long p_i225876_6_) {
      this.feature = p_i225876_1_;
      this.chunkX = p_i225876_2_;
      this.chunkZ = p_i225876_3_;
      this.references = p_i225876_5_;
      this.random = new SharedSeedRandom();
      this.random.setLargeFeatureSeed(p_i225876_6_, p_i225876_2_, p_i225876_3_);
      this.boundingBox = p_i225876_4_;
   }

   public abstract void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_);

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public List<StructurePiece> getPieces() {
      return this.pieces;
   }

   public void func_225565_a_(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
      synchronized(this.pieces) {
         Iterator<StructurePiece> iterator = this.pieces.iterator();

         while(iterator.hasNext()) {
            StructurePiece structurepiece = iterator.next();
            if (structurepiece.getBoundingBox().intersects(p_225565_4_) && !structurepiece.func_225577_a_(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_)) {
               iterator.remove();
            }
         }

         this.calculateBoundingBox();
      }
   }

   protected void calculateBoundingBox() {
      this.boundingBox = MutableBoundingBox.getUnknownBox();

      for(StructurePiece structurepiece : this.pieces) {
         this.boundingBox.expand(structurepiece.getBoundingBox());
      }

   }

   public CompoundNBT createTag(int p_143021_1_, int p_143021_2_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (this.isValid()) {
         if (Registry.STRUCTURE_FEATURE.getKey(this.getFeature()) == null) { // FORGE: This is just a more friendly error instead of the 'Null String' below
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\": \"" + this.getFeature() + "\" missing ID Mapping, Modder see MapGenStructureIO");
         }
         compoundnbt.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
         compoundnbt.putInt("ChunkX", p_143021_1_);
         compoundnbt.putInt("ChunkZ", p_143021_2_);
         compoundnbt.putInt("references", this.references);
         compoundnbt.put("BB", this.boundingBox.createTag());
         ListNBT lvt_4_1_ = new ListNBT();
         synchronized(this.pieces) {
            for(StructurePiece structurepiece : this.pieces) {
               lvt_4_1_.add(structurepiece.createTag());
            }
         }

         compoundnbt.put("Children", lvt_4_1_);
         return compoundnbt;
      } else {
         compoundnbt.putString("id", "INVALID");
         return compoundnbt;
      }
   }

   protected void moveBelowSeaLevel(int p_214628_1_, Random p_214628_2_, int p_214628_3_) {
      int i = p_214628_1_ - p_214628_3_;
      int j = this.boundingBox.getYSpan() + 1;
      if (j < i) {
         j += p_214628_2_.nextInt(i - j);
      }

      int k = j - this.boundingBox.y1;
      this.boundingBox.move(0, k, 0);

      for(StructurePiece structurepiece : this.pieces) {
         structurepiece.move(0, k, 0);
      }

   }

   protected void moveInsideHeights(Random p_214626_1_, int p_214626_2_, int p_214626_3_) {
      int i = p_214626_3_ - p_214626_2_ + 1 - this.boundingBox.getYSpan();
      int j;
      if (i > 1) {
         j = p_214626_2_ + p_214626_1_.nextInt(i);
      } else {
         j = p_214626_2_;
      }

      int k = j - this.boundingBox.y0;
      this.boundingBox.move(0, k, 0);

      for(StructurePiece structurepiece : this.pieces) {
         structurepiece.move(0, k, 0);
      }

   }

   public boolean isValid() {
      return !this.pieces.isEmpty();
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public BlockPos getLocatePos() {
      return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
   }

   public boolean canBeReferenced() {
      return this.references < this.getMaxReferences();
   }

   public void addReference() {
      ++this.references;
   }

   public int getReferences() {
      return this.references;
   }

   protected int getMaxReferences() {
      return 1;
   }

   public Structure<?> getFeature() {
      return this.feature;
   }
}
