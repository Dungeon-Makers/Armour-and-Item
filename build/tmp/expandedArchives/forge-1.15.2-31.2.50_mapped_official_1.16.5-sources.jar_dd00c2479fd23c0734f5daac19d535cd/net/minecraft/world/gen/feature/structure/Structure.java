package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();

   public Structure(Function<Dynamic<?>, ? extends C> p_i51427_1_) {
      super(p_i51427_1_);
   }

   public ConfiguredFeature<C, ? extends Structure<C>> configured(C p_225566_1_) {
      return new ConfiguredFeature<>(this, p_225566_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, C p_212245_5_) {
      if (!p_212245_1_.getLevelData().func_76089_r()) {
         return false;
      } else {
         int i = p_212245_4_.getX() >> 4;
         int j = p_212245_4_.getZ() >> 4;
         int k = i << 4;
         int l = j << 4;
         boolean flag = false;

         for(Long olong : p_212245_1_.getChunk(i, j).func_201578_b(this.getFeatureName())) {
            ChunkPos chunkpos = new ChunkPos(olong);
            StructureStart structurestart = p_212245_1_.getChunk(chunkpos.x, chunkpos.z).func_201585_a(this.getFeatureName());
            if (structurestart != null && structurestart != StructureStart.INVALID_START) {
               structurestart.func_225565_a_(p_212245_1_, p_212245_2_, p_212245_3_, new MutableBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
               flag = true;
            }
         }

         return flag;
      }
   }

   protected StructureStart func_202364_a(IWorld p_202364_1_, BlockPos p_202364_2_, boolean p_202364_3_) {
      label35:
      for(StructureStart structurestart : this.func_202371_a(p_202364_1_, p_202364_2_.getX() >> 4, p_202364_2_.getZ() >> 4)) {
         if (structurestart.isValid() && structurestart.getBoundingBox().isInside(p_202364_2_)) {
            if (!p_202364_3_) {
               return structurestart;
            }

            Iterator iterator = structurestart.getPieces().iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  continue label35;
               }

               StructurePiece structurepiece = (StructurePiece)iterator.next();
               if (structurepiece.getBoundingBox().isInside(p_202364_2_)) {
                  break;
               }
            }

            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public boolean func_175796_a(IWorld p_175796_1_, BlockPos p_175796_2_) {
      return this.func_202364_a(p_175796_1_, p_175796_2_, false).isValid();
   }

   public boolean func_202366_b(IWorld p_202366_1_, BlockPos p_202366_2_) {
      return this.func_202364_a(p_202366_1_, p_202366_2_, true).isValid();
   }

   @Nullable
   public BlockPos func_211405_a(World p_211405_1_, ChunkGenerator<? extends GenerationSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeSource().canGenerateStructure(this)) {
         return null;
      } else {
         int i = p_211405_3_.getX() >> 4;
         int j = p_211405_3_.getZ() >> 4;
         int k = 0;

         for(SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); k <= p_211405_4_; ++k) {
            for(int l = -k; l <= k; ++l) {
               boolean flag = l == -k || l == k;

               for(int i1 = -k; i1 <= k; ++i1) {
                  boolean flag1 = i1 == -k || i1 == k;
                  if (flag || flag1) {
                     ChunkPos chunkpos = this.func_211744_a(p_211405_2_, sharedseedrandom, i, j, l, i1);
                     StructureStart structurestart = p_211405_1_.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS).func_201585_a(this.getFeatureName());
                     if (structurestart != null && structurestart.isValid()) {
                        if (p_211405_5_ && structurestart.canBeReferenced()) {
                           structurestart.addReference();
                           return structurestart.getLocatePos();
                        }

                        if (!p_211405_5_) {
                           return structurestart.getLocatePos();
                        }
                     }

                     if (k == 0) {
                        break;
                     }
                  }
               }

               if (k == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> func_202371_a(IWorld p_202371_1_, int p_202371_2_, int p_202371_3_) {
      List<StructureStart> list = Lists.newArrayList();
      IChunk ichunk = p_202371_1_.getChunk(p_202371_2_, p_202371_3_, ChunkStatus.STRUCTURE_REFERENCES);
      LongIterator longiterator = ichunk.func_201578_b(this.getFeatureName()).iterator();

      while(longiterator.hasNext()) {
         long i = longiterator.nextLong();
         IStructureReader istructurereader = p_202371_1_.getChunk(ChunkPos.getX(i), ChunkPos.getZ(i), ChunkStatus.STRUCTURE_STARTS);
         StructureStart structurestart = istructurereader.func_201585_a(this.getFeatureName());
         if (structurestart != null) {
            list.add(structurestart);
         }
      }

      return list;
   }

   protected ChunkPos func_211744_a(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      return new ChunkPos(p_211744_3_ + p_211744_5_, p_211744_4_ + p_211744_6_);
   }

   public abstract boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_);

   public abstract Structure.IStartFactory getStartFactory();

   public abstract String getFeatureName();

   public abstract int func_202367_b();

   public interface IStartFactory {
      StructureStart create(Structure<?> p_create_1_, int p_create_2_, int p_create_3_, MutableBoundingBox p_create_4_, int p_create_5_, long p_create_6_);
   }
}