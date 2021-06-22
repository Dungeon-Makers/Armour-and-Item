package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoaderUtil {
   public static ChunkLoaderUtil.AnvilConverterData load(CompoundNBT p_76691_0_) {
      int i = p_76691_0_.getInt("xPos");
      int j = p_76691_0_.getInt("zPos");
      ChunkLoaderUtil.AnvilConverterData chunkloaderutil$anvilconverterdata = new ChunkLoaderUtil.AnvilConverterData(i, j);
      chunkloaderutil$anvilconverterdata.blocks = p_76691_0_.getByteArray("Blocks");
      chunkloaderutil$anvilconverterdata.data = new NibbleArrayReader(p_76691_0_.getByteArray("Data"), 7);
      chunkloaderutil$anvilconverterdata.skyLight = new NibbleArrayReader(p_76691_0_.getByteArray("SkyLight"), 7);
      chunkloaderutil$anvilconverterdata.blockLight = new NibbleArrayReader(p_76691_0_.getByteArray("BlockLight"), 7);
      chunkloaderutil$anvilconverterdata.heightmap = p_76691_0_.getByteArray("HeightMap");
      chunkloaderutil$anvilconverterdata.terrainPopulated = p_76691_0_.getBoolean("TerrainPopulated");
      chunkloaderutil$anvilconverterdata.entities = p_76691_0_.getList("Entities", 10);
      chunkloaderutil$anvilconverterdata.blockEntities = p_76691_0_.getList("TileEntities", 10);
      chunkloaderutil$anvilconverterdata.blockTicks = p_76691_0_.getList("TileTicks", 10);

      try {
         chunkloaderutil$anvilconverterdata.lastUpdated = p_76691_0_.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         chunkloaderutil$anvilconverterdata.lastUpdated = (long)p_76691_0_.getInt("LastUpdate");
      }

      return chunkloaderutil$anvilconverterdata;
   }

   public static void func_76690_a(ChunkLoaderUtil.AnvilConverterData p_76690_0_, CompoundNBT p_76690_1_, BiomeProvider p_76690_2_) {
      p_76690_1_.putInt("xPos", p_76690_0_.x);
      p_76690_1_.putInt("zPos", p_76690_0_.z);
      p_76690_1_.putLong("LastUpdate", p_76690_0_.lastUpdated);
      int[] aint = new int[p_76690_0_.heightmap.length];

      for(int i = 0; i < p_76690_0_.heightmap.length; ++i) {
         aint[i] = p_76690_0_.heightmap[i];
      }

      p_76690_1_.putIntArray("HeightMap", aint);
      p_76690_1_.putBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
      ListNBT listnbt = new ListNBT();

      for(int j = 0; j < 8; ++j) {
         boolean flag = true;

         for(int k = 0; k < 16 && flag; ++k) {
            for(int l = 0; l < 16 && flag; ++l) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  int j1 = k << 11 | i1 << 7 | l + (j << 4);
                  int k1 = p_76690_0_.blocks[j1];
                  if (k1 != 0) {
                     flag = false;
                     break;
                  }
               }
            }
         }

         if (!flag) {
            byte[] abyte = new byte[4096];
            NibbleArray nibblearray = new NibbleArray();
            NibbleArray nibblearray1 = new NibbleArray();
            NibbleArray nibblearray2 = new NibbleArray();

            for(int l2 = 0; l2 < 16; ++l2) {
               for(int l1 = 0; l1 < 16; ++l1) {
                  for(int i2 = 0; i2 < 16; ++i2) {
                     int j2 = l2 << 11 | i2 << 7 | l1 + (j << 4);
                     int k2 = p_76690_0_.blocks[j2];
                     abyte[l1 << 8 | i2 << 4 | l2] = (byte)(k2 & 255);
                     nibblearray.set(l2, l1, i2, p_76690_0_.data.get(l2, l1 + (j << 4), i2));
                     nibblearray1.set(l2, l1, i2, p_76690_0_.skyLight.get(l2, l1 + (j << 4), i2));
                     nibblearray2.set(l2, l1, i2, p_76690_0_.blockLight.get(l2, l1 + (j << 4), i2));
                  }
               }
            }

            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Y", (byte)(j & 255));
            compoundnbt.putByteArray("Blocks", abyte);
            compoundnbt.putByteArray("Data", nibblearray.getData());
            compoundnbt.putByteArray("SkyLight", nibblearray1.getData());
            compoundnbt.putByteArray("BlockLight", nibblearray2.getData());
            listnbt.add(compoundnbt);
         }
      }

      p_76690_1_.put("Sections", listnbt);
      p_76690_1_.putIntArray("Biomes", (new BiomeContainer(new ChunkPos(p_76690_0_.x, p_76690_0_.z), p_76690_2_)).writeBiomes());
      p_76690_1_.put("Entities", p_76690_0_.entities);
      p_76690_1_.put("TileEntities", p_76690_0_.blockEntities);
      if (p_76690_0_.blockTicks != null) {
         p_76690_1_.put("TileTicks", p_76690_0_.blockTicks);
      }

      p_76690_1_.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public ListNBT entities;
      public ListNBT blockEntities;
      public ListNBT blockTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int p_i1999_1_, int p_i1999_2_) {
         this.x = p_i1999_1_;
         this.z = p_i1999_2_;
      }
   }
}