package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ChunkPrimer read(ServerWorld p_222656_0_, TemplateManager p_222656_1_, PointOfInterestManager p_222656_2_, ChunkPos p_222656_3_, CompoundNBT p_222656_4_) {
      ChunkGenerator<?> chunkgenerator = p_222656_0_.getChunkSource().getGenerator();
      BiomeProvider biomeprovider = chunkgenerator.getBiomeSource();
      CompoundNBT compoundnbt = p_222656_4_.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (!Objects.equals(p_222656_3_, chunkpos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", p_222656_3_, p_222656_3_, chunkpos);
      }

      BiomeContainer biomecontainer = new BiomeContainer(p_222656_3_, biomeprovider, compoundnbt.contains("Biomes", 11) ? compoundnbt.getIntArray("Biomes") : null);
      UpgradeData upgradedata = compoundnbt.contains("UpgradeData", 10) ? new UpgradeData(compoundnbt.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist = new ChunkPrimerTickList<>((p_222652_0_) -> {
         return p_222652_0_ == null || p_222652_0_.defaultBlockState().isAir();
      }, p_222656_3_, compoundnbt.getList("ToBeTicked", 9));
      ChunkPrimerTickList<Fluid> chunkprimerticklist1 = new ChunkPrimerTickList<>((p_222646_0_) -> {
         return p_222646_0_ == null || p_222646_0_ == Fluids.EMPTY;
      }, p_222656_3_, compoundnbt.getList("LiquidsToBeTicked", 9));
      boolean flag = compoundnbt.getBoolean("isLightOn");
      ListNBT listnbt = compoundnbt.getList("Sections", 10);
      int i = 16;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag1 = p_222656_0_.func_201675_m().func_191066_m();
      AbstractChunkProvider abstractchunkprovider = p_222656_0_.getChunkSource();
      WorldLightManager worldlightmanager = abstractchunkprovider.getLightEngine();
      if (flag) {
         worldlightmanager.retainData(p_222656_3_, true);
      }

      for(int j = 0; j < listnbt.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt.getCompound(j);
         int k = compoundnbt1.getByte("Y");
         if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
            ChunkSection chunksection = new ChunkSection(k << 4);
            chunksection.getStates().read(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
            chunksection.recalcBlockCounts();
            if (!chunksection.isEmpty()) {
               achunksection[k] = chunksection;
            }

            p_222656_2_.checkConsistencyWithBlocks(p_222656_3_, chunksection);
         }

         if (flag) {
            if (compoundnbt1.contains("BlockLight", 7)) {
               worldlightmanager.queueSectionData(LightType.BLOCK, SectionPos.of(p_222656_3_, k), new NibbleArray(compoundnbt1.getByteArray("BlockLight")));
            }

            if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
               worldlightmanager.queueSectionData(LightType.SKY, SectionPos.of(p_222656_3_, k), new NibbleArray(compoundnbt1.getByteArray("SkyLight")));
            }
         }
      }

      long k1 = compoundnbt.getLong("InhabitedTime");
      ChunkStatus.Type chunkstatus$type = getChunkTypeFromTag(p_222656_4_);
      IChunk ichunk;
      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         ITickList<Block> iticklist;
         if (compoundnbt.contains("TileTicks", 9)) {
            iticklist = SerializableTickList.create(compoundnbt.getList("TileTicks", 10), Registry.BLOCK::getKey, Registry.BLOCK::get);
         } else {
            iticklist = chunkprimerticklist;
         }

         ITickList<Fluid> iticklist1;
         if (compoundnbt.contains("LiquidTicks", 9)) {
            iticklist1 = SerializableTickList.create(compoundnbt.getList("LiquidTicks", 10), Registry.FLUID::getKey, Registry.FLUID::get);
         } else {
            iticklist1 = chunkprimerticklist1;
         }

         ichunk = new Chunk(p_222656_0_.getLevel(), p_222656_3_, biomecontainer, upgradedata, iticklist, iticklist1, k1, achunksection, (p_222648_1_) -> {
            postLoadChunk(compoundnbt, p_222648_1_);
         });
         if (compoundnbt.contains("ForgeCaps")) ((Chunk)ichunk).readCapsFromNBT(compoundnbt.getCompound("ForgeCaps"));
      } else {
         ChunkPrimer chunkprimer = new ChunkPrimer(p_222656_3_, upgradedata, achunksection, chunkprimerticklist, chunkprimerticklist1);
         chunkprimer.setBiomes(biomecontainer);
         ichunk = chunkprimer;
         chunkprimer.setInhabitedTime(k1);
         chunkprimer.setStatus(ChunkStatus.byName(compoundnbt.getString("Status")));
         if (chunkprimer.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
            chunkprimer.setLightEngine(worldlightmanager);
         }

         if (!flag && chunkprimer.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            for(BlockPos blockpos : BlockPos.betweenClosed(p_222656_3_.getMinBlockX(), 0, p_222656_3_.getMinBlockZ(), p_222656_3_.getMaxBlockX(), 255, p_222656_3_.getMaxBlockZ())) {
               if (ichunk.getBlockState(blockpos).getLightValue(ichunk, blockpos) != 0) {
                  chunkprimer.addLight(blockpos);
               }
            }
         }
      }

      ichunk.setLightCorrect(flag);
      CompoundNBT compoundnbt3 = compoundnbt.getCompound("Heightmaps");
      EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);

      for(Heightmap.Type heightmap$type : ichunk.getStatus().heightmapsAfter()) {
         String s = heightmap$type.getSerializationKey();
         if (compoundnbt3.contains(s, 12)) {
            ichunk.setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
         } else {
            enumset.add(heightmap$type);
         }
      }

      Heightmap.primeHeightmaps(ichunk, enumset);
      CompoundNBT compoundnbt4 = compoundnbt.getCompound("Structures");
      ichunk.setAllStarts(func_227076_a_(chunkgenerator, p_222656_1_, compoundnbt4));
      ichunk.setAllReferences(unpackStructureReferences(p_222656_3_, compoundnbt4));
      if (compoundnbt.getBoolean("shouldSave")) {
         ichunk.setUnsaved(true);
      }

      ListNBT listnbt3 = compoundnbt.getList("PostProcessing", 9);

      for(int l1 = 0; l1 < listnbt3.size(); ++l1) {
         ListNBT listnbt1 = listnbt3.getList(l1);

         for(int l = 0; l < listnbt1.size(); ++l) {
            ichunk.addPackedPostProcess(listnbt1.getShort(l), l1);
         }
      }

      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compoundnbt, chunkstatus$type));
         return new ChunkPrimerWrapper((Chunk)ichunk);
      } else {
         ChunkPrimer chunkprimer1 = (ChunkPrimer)ichunk;
         ListNBT listnbt4 = compoundnbt.getList("Entities", 10);

         for(int i2 = 0; i2 < listnbt4.size(); ++i2) {
            chunkprimer1.addEntity(listnbt4.getCompound(i2));
         }

         ListNBT listnbt5 = compoundnbt.getList("TileEntities", 10);

         for(int i1 = 0; i1 < listnbt5.size(); ++i1) {
            CompoundNBT compoundnbt2 = listnbt5.getCompound(i1);
            ichunk.setBlockEntityNbt(compoundnbt2);
         }

         ListNBT listnbt6 = compoundnbt.getList("Lights", 9);

         for(int j2 = 0; j2 < listnbt6.size(); ++j2) {
            ListNBT listnbt2 = listnbt6.getList(j2);

            for(int j1 = 0; j1 < listnbt2.size(); ++j1) {
               chunkprimer1.addLight(listnbt2.getShort(j1), j2);
            }
         }

         CompoundNBT compoundnbt5 = compoundnbt.getCompound("CarvingMasks");

         for(String s1 : compoundnbt5.getAllKeys()) {
            GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s1);
            chunkprimer1.setCarvingMask(generationstage$carving, BitSet.valueOf(compoundnbt5.getByteArray(s1)));
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compoundnbt, chunkstatus$type));

         return chunkprimer1;
      }
   }

   public static CompoundNBT write(ServerWorld p_222645_0_, IChunk p_222645_1_) {
      ChunkPos chunkpos = p_222645_1_.getPos();
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      compoundnbt.put("Level", compoundnbt1);
      compoundnbt1.putInt("xPos", chunkpos.x);
      compoundnbt1.putInt("zPos", chunkpos.z);
      compoundnbt1.putLong("LastUpdate", p_222645_0_.getGameTime());
      compoundnbt1.putLong("InhabitedTime", p_222645_1_.getInhabitedTime());
      compoundnbt1.putString("Status", p_222645_1_.getStatus().getName());
      UpgradeData upgradedata = p_222645_1_.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compoundnbt1.put("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = p_222645_1_.getSections();
      ListNBT listnbt = new ListNBT();
      WorldLightManager worldlightmanager = p_222645_0_.getChunkSource().getLightEngine();
      boolean flag = p_222645_1_.isLightCorrect();

      for(int i = -1; i < 17; ++i) {
         int j = i;
         ChunkSection chunksection = Arrays.stream(achunksection).filter((p_222657_1_) -> {
            return p_222657_1_ != null && p_222657_1_.bottomBlockY() >> 4 == j;
         }).findFirst().orElse(Chunk.EMPTY_SECTION);
         NibbleArray nibblearray = worldlightmanager.getLayerListener(LightType.BLOCK).getDataLayerData(SectionPos.of(chunkpos, j));
         NibbleArray nibblearray1 = worldlightmanager.getLayerListener(LightType.SKY).getDataLayerData(SectionPos.of(chunkpos, j));
         if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Y", (byte)(j & 255));
            if (chunksection != Chunk.EMPTY_SECTION) {
               chunksection.getStates().write(compoundnbt2, "Palette", "BlockStates");
            }

            if (nibblearray != null && !nibblearray.isEmpty()) {
               compoundnbt2.putByteArray("BlockLight", nibblearray.getData());
            }

            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               compoundnbt2.putByteArray("SkyLight", nibblearray1.getData());
            }

            listnbt.add(compoundnbt2);
         }
      }

      compoundnbt1.put("Sections", listnbt);
      if (flag) {
         compoundnbt1.putBoolean("isLightOn", true);
      }

      BiomeContainer biomecontainer = p_222645_1_.getBiomes();
      if (biomecontainer != null) {
         compoundnbt1.putIntArray("Biomes", biomecontainer.writeBiomes());
      }

      ListNBT listnbt1 = new ListNBT();

      for(BlockPos blockpos : p_222645_1_.getBlockEntitiesPos()) {
         CompoundNBT compoundnbt4 = p_222645_1_.getBlockEntityNbtForSaving(blockpos);
         if (compoundnbt4 != null) {
            listnbt1.add(compoundnbt4);
         }
      }

      compoundnbt1.put("TileEntities", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      if (p_222645_1_.getStatus().getChunkType() == ChunkStatus.Type.LEVELCHUNK) {
         Chunk chunk = (Chunk)p_222645_1_;
         chunk.setLastSaveHadEntities(false);

         for(int k = 0; k < chunk.getEntitySections().length; ++k) {
            for(Entity entity : chunk.getEntitySections()[k]) {
               CompoundNBT compoundnbt3 = new CompoundNBT();
               try {
               if (entity.save(compoundnbt3)) {
                  chunk.setLastSaveHadEntities(true);
                  listnbt2.add(compoundnbt3);
               }
               } catch (Exception e) {
                  LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
               }
            }
         }
         try {
             final CompoundNBT capTag = chunk.writeCapsToNBT();
             if (capTag != null) compoundnbt1.put("ForgeCaps", capTag);
         } catch (Exception exception) {
             LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
         }
      } else {
         ChunkPrimer chunkprimer = (ChunkPrimer)p_222645_1_;
         listnbt2.addAll(chunkprimer.getEntities());
         compoundnbt1.put("Lights", packOffsets(chunkprimer.getPackedLights()));
         CompoundNBT compoundnbt5 = new CompoundNBT();

         for(GenerationStage.Carving generationstage$carving : GenerationStage.Carving.values()) {
            compoundnbt5.putByteArray(generationstage$carving.toString(), p_222645_1_.getCarvingMask(generationstage$carving).toByteArray());
         }

         compoundnbt1.put("CarvingMasks", compoundnbt5);
      }

      compoundnbt1.put("Entities", listnbt2);
      ITickList<Block> iticklist = p_222645_1_.getBlockTicks();
      if (iticklist instanceof ChunkPrimerTickList) {
         compoundnbt1.put("ToBeTicked", ((ChunkPrimerTickList)iticklist).save());
      } else if (iticklist instanceof SerializableTickList) {
         compoundnbt1.put("TileTicks", ((SerializableTickList)iticklist).func_219498_a(p_222645_0_.getGameTime()));
      } else {
         compoundnbt1.put("TileTicks", p_222645_0_.getBlockTicks().save(chunkpos));
      }

      ITickList<Fluid> iticklist1 = p_222645_1_.getLiquidTicks();
      if (iticklist1 instanceof ChunkPrimerTickList) {
         compoundnbt1.put("LiquidsToBeTicked", ((ChunkPrimerTickList)iticklist1).save());
      } else if (iticklist1 instanceof SerializableTickList) {
         compoundnbt1.put("LiquidTicks", ((SerializableTickList)iticklist1).func_219498_a(p_222645_0_.getGameTime()));
      } else {
         compoundnbt1.put("LiquidTicks", p_222645_0_.getLiquidTicks().save(chunkpos));
      }

      compoundnbt1.put("PostProcessing", packOffsets(p_222645_1_.getPostProcessing()));
      CompoundNBT compoundnbt6 = new CompoundNBT();

      for(Entry<Heightmap.Type, Heightmap> entry : p_222645_1_.getHeightmaps()) {
         if (p_222645_1_.getStatus().heightmapsAfter().contains(entry.getKey())) {
            compoundnbt6.put(entry.getKey().getSerializationKey(), new LongArrayNBT(entry.getValue().getRawData()));
         }
      }

      compoundnbt1.put("Heightmaps", compoundnbt6);
      compoundnbt1.put("Structures", packStructureData(chunkpos, p_222645_1_.getAllStarts(), p_222645_1_.getAllReferences()));
      return compoundnbt;
   }

   public static ChunkStatus.Type getChunkTypeFromTag(@Nullable CompoundNBT p_222651_0_) {
      if (p_222651_0_ != null) {
         ChunkStatus chunkstatus = ChunkStatus.byName(p_222651_0_.getCompound("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getChunkType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   private static void postLoadChunk(CompoundNBT p_222650_0_, Chunk p_222650_1_) {
      ListNBT listnbt = p_222650_0_.getList("Entities", 10);
      World world = p_222650_1_.getLevel();

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         EntityType.loadEntityRecursive(compoundnbt, world, (p_222655_1_) -> {
            p_222650_1_.addEntity(p_222655_1_);
            return p_222655_1_;
         });
         p_222650_1_.setLastSaveHadEntities(true);
      }

      ListNBT listnbt1 = p_222650_0_.getList("TileEntities", 10);

      for(int j = 0; j < listnbt1.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt1.getCompound(j);
         boolean flag = compoundnbt1.getBoolean("keepPacked");
         if (flag) {
            p_222650_1_.setBlockEntityNbt(compoundnbt1);
         } else {
            TileEntity tileentity = TileEntity.func_203403_c(compoundnbt1);
            if (tileentity != null) {
               p_222650_1_.addBlockEntity(tileentity);
            }
         }
      }

   }

   private static CompoundNBT packStructureData(ChunkPos p_222649_0_, Map<String, StructureStart> p_222649_1_, Map<String, LongSet> p_222649_2_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(Entry<String, StructureStart> entry : p_222649_1_.entrySet()) {
         compoundnbt1.put(entry.getKey(), entry.getValue().createTag(p_222649_0_.x, p_222649_0_.z));
      }

      compoundnbt.put("Starts", compoundnbt1);
      CompoundNBT compoundnbt2 = new CompoundNBT();

      for(Entry<String, LongSet> entry1 : p_222649_2_.entrySet()) {
         compoundnbt2.put(entry1.getKey(), new LongArrayNBT(entry1.getValue()));
      }

      compoundnbt.put("References", compoundnbt2);
      return compoundnbt;
   }

   private static Map<String, StructureStart> func_227076_a_(ChunkGenerator<?> p_227076_0_, TemplateManager p_227076_1_, CompoundNBT p_227076_2_) {
      Map<String, StructureStart> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227076_2_.getCompound("Starts");

      for(String s : compoundnbt.getAllKeys()) {
         map.put(s, Structures.func_227456_a_(p_227076_0_, p_227076_1_, compoundnbt.getCompound(s)));
      }

      return map;
   }

   private static Map<String, LongSet> unpackStructureReferences(ChunkPos p_227075_0_, CompoundNBT p_227075_1_) {
      Map<String, LongSet> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227075_1_.getCompound("References");

      for(String s : compoundnbt.getAllKeys()) {
         map.put(s, new LongOpenHashSet(Arrays.stream(compoundnbt.getLongArray(s)).filter((p_227074_2_) -> {
            ChunkPos chunkpos = new ChunkPos(p_227074_2_);
            if (chunkpos.getChessboardDistance(p_227075_0_) > 8) {
               LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", s, chunkpos, p_227075_0_);
               return false;
            } else {
               return true;
            }
         }).toArray()));
      }

      return map;
   }

   public static ListNBT packOffsets(ShortList[] p_222647_0_) {
      ListNBT listnbt = new ListNBT();

      for(ShortList shortlist : p_222647_0_) {
         ListNBT listnbt1 = new ListNBT();
         if (shortlist != null) {
            for(Short oshort : shortlist) {
               listnbt1.add(ShortNBT.valueOf(oshort));
            }
         }

         listnbt.add(listnbt1);
      }

      return listnbt;
   }
}
