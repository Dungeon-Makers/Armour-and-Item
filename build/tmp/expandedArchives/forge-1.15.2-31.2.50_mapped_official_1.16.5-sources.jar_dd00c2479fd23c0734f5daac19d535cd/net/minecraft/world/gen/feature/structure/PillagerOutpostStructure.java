package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class PillagerOutpostStructure extends ScatteredStructure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> OUTPOST_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.PILLAGER, 1, 1, 1));

   public PillagerOutpostStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51470_1_) {
      super(p_i51470_1_);
   }

   public String getFeatureName() {
      return "Pillager_Outpost";
   }

   public int func_202367_b() {
      return 3;
   }

   public List<Biome.SpawnListEntry> getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos chunkpos = this.func_211744_a(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      if (p_225558_4_ == chunkpos.x && p_225558_5_ == chunkpos.z) {
         int i = p_225558_4_ >> 4;
         int j = p_225558_5_ >> 4;
         p_225558_3_.setSeed((long)(i ^ j << 4) ^ p_225558_2_.func_202089_c());
         p_225558_3_.nextInt();
         if (p_225558_3_.nextInt(5) != 0) {
            return false;
         }

         if (p_225558_2_.func_202094_a(p_225558_6_, this)) {
            for(int k = p_225558_4_ - 10; k <= p_225558_4_ + 10; ++k) {
               for(int l = p_225558_5_ - 10; l <= p_225558_5_ + 10; ++l) {
                  if (Feature.field_214550_p.func_225558_a_(p_225558_1_, p_225558_2_, p_225558_3_, k, l, p_225558_1_.getBiome(new BlockPos((k << 4) + 9, 0, (l << 4) + 9)))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   public Structure.IStartFactory getStartFactory() {
      return PillagerOutpostStructure.Start::new;
   }

   protected int func_202382_c() {
      return 165745296;
   }

   public static class Start extends MarginedStructureStart {
      public Start(Structure<?> p_i225815_1_, int p_i225815_2_, int p_i225815_3_, MutableBoundingBox p_i225815_4_, int p_i225815_5_, long p_i225815_6_) {
         super(p_i225815_1_, p_i225815_2_, p_i225815_3_, p_i225815_4_, p_i225815_5_, p_i225815_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         BlockPos blockpos = new BlockPos(p_214625_3_ * 16, 90, p_214625_4_ * 16);
         PillagerOutpostPieces.func_215139_a(p_214625_1_, p_214625_2_, blockpos, this.pieces, this.random);
         this.calculateBoundingBox();
      }
   }
}