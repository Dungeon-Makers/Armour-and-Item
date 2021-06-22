package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdStructure extends Structure<NoFeatureConfig> {
   private boolean field_75056_f;
   private ChunkPos[] field_75057_g;
   private final List<StructureStart> field_214561_aT = Lists.newArrayList();
   private long field_202387_av;

   public StrongholdStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51428_1_) {
      super(p_i51428_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      if (this.field_202387_av != p_225558_2_.func_202089_c()) {
         this.func_202386_c();
      }

      if (!this.field_75056_f) {
         this.func_202385_a(p_225558_2_);
         this.field_75056_f = true;
      }

      for(ChunkPos chunkpos : this.field_75057_g) {
         if (p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z) {
            return true;
         }
      }

      return false;
   }

   private void func_202386_c() {
      this.field_75056_f = false;
      this.field_75057_g = null;
      this.field_214561_aT.clear();
   }

   public Structure.IStartFactory getStartFactory() {
      return StrongholdStructure.Start::new;
   }

   public String getFeatureName() {
      return "Stronghold";
   }

   public int func_202367_b() {
      return 8;
   }

   @Nullable
   public BlockPos func_211405_a(World p_211405_1_, ChunkGenerator<? extends GenerationSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeSource().canGenerateStructure(this)) {
         return null;
      } else {
         if (this.field_202387_av != p_211405_1_.getSeed()) {
            this.func_202386_c();
         }

         if (!this.field_75056_f) {
            this.func_202385_a(p_211405_2_);
            this.field_75056_f = true;
         }

         BlockPos blockpos = null;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         double d0 = Double.MAX_VALUE;

         for(ChunkPos chunkpos : this.field_75057_g) {
            blockpos$mutable.set((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
            double d1 = blockpos$mutable.distSqr(p_211405_3_);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutable);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutable);
               d0 = d1;
            }
         }

         return blockpos;
      }
   }

   private void func_202385_a(ChunkGenerator<?> p_202385_1_) {
      this.field_202387_av = p_202385_1_.func_202089_c();
      List<Biome> list = Lists.newArrayList();

      for(Biome biome : Registry.field_212624_m) {
         if (biome != null && p_202385_1_.func_202094_a(biome, this)) {
            list.add(biome);
         }
      }

      int i2 = p_202385_1_.func_201496_a_().func_202172_d();
      int j2 = p_202385_1_.func_201496_a_().func_202176_e();
      int i = p_202385_1_.func_201496_a_().func_202175_f();
      this.field_75057_g = new ChunkPos[j2];
      int j = 0;

      for(StructureStart structurestart : this.field_214561_aT) {
         if (j < this.field_75057_g.length) {
            this.field_75057_g[j++] = new ChunkPos(structurestart.getChunkX(), structurestart.getChunkZ());
         }
      }

      Random random = new Random();
      random.setSeed(p_202385_1_.func_202089_c());
      double d1 = random.nextDouble() * Math.PI * 2.0D;
      int k = j;
      if (j < this.field_75057_g.length) {
         int l = 0;
         int i1 = 0;

         for(int j1 = 0; j1 < this.field_75057_g.length; ++j1) {
            double d0 = (double)(4 * i2 + i2 * i1 * 6) + (random.nextDouble() - 0.5D) * (double)i2 * 2.5D;
            int k1 = (int)Math.round(Math.cos(d1) * d0);
            int l1 = (int)Math.round(Math.sin(d1) * d0);
            BlockPos blockpos = p_202385_1_.getBiomeSource().findBiomeHorizontal((k1 << 4) + 8, p_202385_1_.func_222530_f(), (l1 << 4) + 8, 112, list, random);
            if (blockpos != null) {
               k1 = blockpos.getX() >> 4;
               l1 = blockpos.getZ() >> 4;
            }

            if (j1 >= k) {
               this.field_75057_g[j1] = new ChunkPos(k1, l1);
            }

            d1 += (Math.PI * 2D) / (double)i;
            ++l;
            if (l == i) {
               ++i1;
               l = 0;
               i = i + 2 * i / (i1 + 1);
               i = Math.min(i, this.field_75057_g.length - j1);
               d1 += random.nextDouble() * Math.PI * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225818_1_, int p_i225818_2_, int p_i225818_3_, MutableBoundingBox p_i225818_4_, int p_i225818_5_, long p_i225818_6_) {
         super(p_i225818_1_, p_i225818_2_, p_i225818_3_, p_i225818_4_, p_i225818_5_, p_i225818_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         int i = 0;
         long j = p_214625_1_.func_202089_c();

         while(true) {
            this.pieces.clear();
            this.boundingBox = MutableBoundingBox.getUnknownBox();
            this.random.setLargeFeatureSeed(j + (long)(i++), p_214625_3_, p_214625_4_);
            StrongholdPieces.resetPieces();
            StrongholdPieces.Stairs2 strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(this.random, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2);
            this.pieces.add(strongholdpieces$stairs2);
            strongholdpieces$stairs2.addChildren(strongholdpieces$stairs2, this.pieces, this.random);
            List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

            while(!list.isEmpty()) {
               int k = this.random.nextInt(list.size());
               StructurePiece structurepiece = list.remove(k);
               structurepiece.addChildren(strongholdpieces$stairs2, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveBelowSeaLevel(p_214625_1_.func_222530_f(), this.random, 10);
            if (!this.pieces.isEmpty() && strongholdpieces$stairs2.portalRoomPiece != null) {
               break;
            }
         }

         ((StrongholdStructure)this.getFeature()).field_214561_aT.add(this);
      }
   }
}