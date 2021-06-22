package net.minecraft.world.gen;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public abstract class ChunkGenerator<C extends GenerationSettings> {
   protected final IWorld field_222540_a;
   protected final long field_222541_b;
   protected final BiomeProvider biomeSource;
   protected final C settings;

   public ChunkGenerator(IWorld p_i49954_1_, BiomeProvider p_i49954_2_, C p_i49954_3_) {
      this.field_222540_a = p_i49954_1_;
      this.field_222541_b = p_i49954_1_.getSeed();
      this.biomeSource = p_i49954_2_;
      this.settings = p_i49954_3_;
   }

   public void func_222539_a(IChunk p_222539_1_) {
      ChunkPos chunkpos = p_222539_1_.getPos();
      ((ChunkPrimer)p_222539_1_).setBiomes(new BiomeContainer(chunkpos, this.biomeSource));
   }

   protected Biome func_225552_a_(BiomeManager p_225552_1_, BlockPos p_225552_2_) {
      return p_225552_1_.getBiome(p_225552_2_);
   }

   public void func_225550_a_(BiomeManager p_225550_1_, IChunk p_225550_2_, GenerationStage.Carving p_225550_3_) {
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      int i = 8;
      ChunkPos chunkpos = p_225550_2_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      Biome biome = this.func_225552_a_(p_225550_1_, chunkpos.getWorldPosition());
      BitSet bitset = p_225550_2_.getCarvingMask(p_225550_3_);

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<ConfiguredCarver<?>> list = biome.func_203603_a(p_225550_3_);
            ListIterator<ConfiguredCarver<?>> listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int j1 = listiterator.nextIndex();
               ConfiguredCarver<?> configuredcarver = listiterator.next();
               sharedseedrandom.setLargeFeatureSeed(this.field_222541_b + (long)j1, l, i1);
               if (configuredcarver.isStartChunk(sharedseedrandom, l, i1)) {
                  configuredcarver.carve(p_225550_2_, (p_227059_2_) -> {
                     return this.func_225552_a_(p_225550_1_, p_227059_2_);
                  }, sharedseedrandom, this.func_222530_f(), l, i1, j, k, bitset);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos func_211403_a(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      Structure<?> structure = Feature.field_202300_at.get(p_211403_2_.toLowerCase(Locale.ROOT));
      return structure != null ? structure.func_211405_a(p_211403_1_, this, p_211403_3_, p_211403_4_, p_211403_5_) : null;
   }

   public void func_202092_b(WorldGenRegion p_202092_1_) {
      int i = p_202092_1_.getCenterX();
      int j = p_202092_1_.getCenterZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      Biome biome = this.func_225552_a_(p_202092_1_.getBiomeManager(), blockpos.offset(8, 8, 8));
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setDecorationSeed(p_202092_1_.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         try {
            biome.func_203608_a(generationstage$decoration, this, p_202092_1_, i1, sharedseedrandom, blockpos);
         } catch (Exception exception) {
            CrashReport crashreport = CrashReport.forThrowable(exception, "Biome decoration");
            crashreport.addCategory("Generation").setDetail("CenterX", i).setDetail("CenterZ", j).setDetail("Step", generationstage$decoration).setDetail("Seed", i1).setDetail("Biome", Registry.field_212624_m.getKey(biome));
            throw new ReportedException(crashreport);
         }
      }

   }

   public abstract void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_);

   public void func_202093_c(WorldGenRegion p_202093_1_) {
   }

   public C func_201496_a_() {
      return this.settings;
   }

   public abstract int getSpawnHeight();

   public void func_203222_a(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
   }

   public boolean func_202094_a(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return p_202094_1_.func_201858_a(p_202094_2_);
   }

   @Nullable
   public <C extends IFeatureConfig> C func_202087_b(Biome p_202087_1_, Structure<C> p_202087_2_) {
      return p_202087_1_.func_201857_b(p_202087_2_);
   }

   public BiomeProvider getBiomeSource() {
      return this.biomeSource;
   }

   public long func_202089_c() {
      return this.field_222541_b;
   }

   public int func_207511_e() {
      return 256;
   }

   public List<Biome.SpawnListEntry> func_177458_a(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      return this.field_222540_a.getBiome(p_177458_2_).func_76747_a(p_177458_1_);
   }

   public void func_227058_a_(BiomeManager p_227058_1_, IChunk p_227058_2_, ChunkGenerator<?> p_227058_3_, TemplateManager p_227058_4_) {
      for(Structure<?> structure : Feature.field_202300_at.values()) {
         if (p_227058_3_.getBiomeSource().canGenerateStructure(structure)) {
            StructureStart structurestart = p_227058_2_.func_201585_a(structure.getFeatureName());
            int i = structurestart != null ? structurestart.getReferences() : 0;
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
            ChunkPos chunkpos = p_227058_2_.getPos();
            StructureStart structurestart1 = StructureStart.INVALID_START;
            Biome biome = p_227058_1_.getBiome(new BlockPos(chunkpos.getMinBlockX() + 9, 0, chunkpos.getMinBlockZ() + 9));
            if (structure.func_225558_a_(p_227058_1_, p_227058_3_, sharedseedrandom, chunkpos.x, chunkpos.z, biome)) {
               StructureStart structurestart2 = structure.getStartFactory().create(structure, chunkpos.x, chunkpos.z, MutableBoundingBox.getUnknownBox(), i, p_227058_3_.func_202089_c());
               structurestart2.func_214625_a(this, p_227058_4_, chunkpos.x, chunkpos.z, biome);
               structurestart1 = structurestart2.isValid() ? structurestart2 : StructureStart.INVALID_START;
            }

            p_227058_2_.func_201584_a(structure.getFeatureName(), structurestart1);
         }
      }

   }

   public void func_222528_a(IWorld p_222528_1_, IChunk p_222528_2_) {
      int i = 8;
      int j = p_222528_2_.getPos().x;
      int k = p_222528_2_.getPos().z;
      int l = j << 4;
      int i1 = k << 4;

      for(int j1 = j - 8; j1 <= j + 8; ++j1) {
         for(int k1 = k - 8; k1 <= k + 8; ++k1) {
            long l1 = ChunkPos.asLong(j1, k1);

            for(Entry<String, StructureStart> entry : p_222528_1_.getChunk(j1, k1).getAllStarts().entrySet()) {
               StructureStart structurestart = entry.getValue();
               if (structurestart != StructureStart.INVALID_START && structurestart.getBoundingBox().intersects(l, i1, l + 15, i1 + 15)) {
                  p_222528_2_.func_201583_a(entry.getKey(), l1);
                  DebugPacketSender.sendStructurePacket(p_222528_1_, structurestart);
               }
            }
         }
      }

   }

   public abstract void func_222537_b(IWorld p_222537_1_, IChunk p_222537_2_);

   public int func_222530_f() {
      return field_222540_a.func_201675_m().getSeaLevel();
   }

   public abstract int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_);

   public int getFirstFreeHeight(int p_222532_1_, int p_222532_2_, Heightmap.Type p_222532_3_) {
      return this.getBaseHeight(p_222532_1_, p_222532_2_, p_222532_3_);
   }

   public int getFirstOccupiedHeight(int p_222531_1_, int p_222531_2_, Heightmap.Type p_222531_3_) {
      return this.getBaseHeight(p_222531_1_, p_222531_2_, p_222531_3_) - 1;
   }
}
