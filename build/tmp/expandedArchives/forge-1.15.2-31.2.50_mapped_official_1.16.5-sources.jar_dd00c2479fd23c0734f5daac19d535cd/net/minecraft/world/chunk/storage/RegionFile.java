package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
   private final FileChannel file;
   private final Path externalFileDir;
   private final RegionFileVersion version;
   private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
   private final IntBuffer offsets;
   private final IntBuffer timestamps;
   private final RegionBitmap usedSectors = new RegionBitmap();
   private final Path filePath;

   public RegionFile(File p_i225784_1_, File p_i225784_2_) throws IOException {
      this(p_i225784_1_.toPath(), p_i225784_2_.toPath(), RegionFileVersion.VERSION_DEFLATE);
   }

   public RegionFile(Path p_i225785_1_, Path p_i225785_2_, RegionFileVersion p_i225785_3_) throws IOException {
      this.version = p_i225785_3_;
      this.filePath = p_i225785_1_;
      if (!Files.isDirectory(p_i225785_2_)) {
         throw new IllegalArgumentException("Expected directory, got " + p_i225785_2_.toAbsolutePath());
      } else {
         this.externalFileDir = p_i225785_2_;
         this.offsets = this.header.asIntBuffer();
         ((Buffer)this.offsets).limit(1024);
         ((Buffer)this.header).position(4096);
         this.timestamps = this.header.asIntBuffer();
         this.file = FileChannel.open(p_i225785_1_, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
         this.usedSectors.force(0, 2);
         ((Buffer)this.header).position(0);
         int i = this.file.read(this.header, 0L);
         if (i != -1) {
            if (i != 8192) {
               LOGGER.warn("Region file {} has truncated header: {}", p_i225785_1_, i);
            }

            for(int j = 0; j < 1024; ++j) {
               int k = this.offsets.get(j);
               if (k != 0) {
                  int l = getSectorNumber(k);
                  int i1 = getNumSectors(k);
                  if (i1 == 255)
                      i1 = forgeGetRealLength(j, l);
                  this.usedSectors.force(l, i1);
               }
            }
         }

      }
   }

   private int forgeGetRealLength(int index, int offset) throws IOException {
       int chunkX = index & 31;
       int chunkZ = (index >> 5) & 31;

       ByteBuffer header = ByteBuffer.allocate(5);
       this.file.read(header, offset * 4096);
       ((Buffer)header).flip();

       if (header.remaining() < 5) {
          LOGGER.error("Chunk {},{} in {} header is truncated: expected 5 but read {}", chunkX, chunkZ, this.filePath.getFileName(), header.remaining());
          return 255;
       }

       return (header.getInt() + 4) / 4096 + 1;
   }

   /**
    * In 1.14, Forge added support for large chunks by allowing it to overflow the 255 section limit.
    * Deferring the section size to the 'length' header in front of the chunk data.
    * In 1.15, Mojang solved this issue by adding an external '.mcc' file for large chunks.
    * Here, we attempt to detect and extract these large chunks from Forge's format to Vanilla's
    */
   public RegionFile extractLargeChunks(ChunkPos pos) throws IOException {
       ChunkPos regionBase = new ChunkPos(pos.getRegionX() * 32, pos.getRegionZ() * 32);
       for (int index = 0; index < 1024; index++) {
          int offset = this.offsets.get(index);
          if (getNumSectors(offset) != 255) //If it's not 255, then it's not possible to be a oversized chunk. Move on.
             continue;
          offset = getSectorNumber(offset);

          ChunkPos chunk = new ChunkPos(regionBase.x + (index & 31), regionBase.z + ((index >> 5) & 31));

          ByteBuffer header = ByteBuffer.allocate(5);
          this.file.read(header, offset * 4096);
          ((Buffer)header).flip();

          if (header.remaining() < 5) {
             LOGGER.error("Chunk {} in {} header is truncated: expected 5 but read {}", chunk, this.filePath.getFileName(), header.remaining());
             continue;
          }

          int length = header.getInt();
          byte version = header.get();
          int sectors = (length + 4) / 4096 + 1;
          if (sectors <= 255 || isExternalStreamChunk(version))
             continue; //Not over sized, or already external

          ByteBuffer data = ByteBuffer.allocate(length + 4);
          this.file.read(data, offset * 4096);
          ((Buffer)data).flip();

          if (data.remaining() < length + 4) {
              LOGGER.error("Chunk {} in {} is truncated: expected {} but read {}", chunk, this.filePath.getFileName(), length + 4, data.remaining());
              continue;
          }
          write(chunk, data); //Save the chunk data, it'll be spit out to an external file.
       }
       return this;
   }

   private Path getExternalChunkPath(ChunkPos p_227145_1_) {
      String s = "c." + p_227145_1_.x + "." + p_227145_1_.z + ".mcc";
      return this.externalFileDir.resolve(s);
   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(ChunkPos p_222666_1_) throws IOException {
      int i = this.getOffset(p_222666_1_);
      if (i == 0) {
         return null;
      } else {
         int j = getSectorNumber(i);
         int k = getNumSectors(i);
         int l = k * 4096;
         ByteBuffer bytebuffer = ByteBuffer.allocate(l);
         this.file.read(bytebuffer, (long)(j * 4096));
         ((Buffer)bytebuffer).flip();
         if (bytebuffer.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", p_222666_1_, l, bytebuffer.remaining());
            return null;
         } else {
            int i1 = bytebuffer.getInt();
            byte b0 = bytebuffer.get();
            if (i1 == 0) {
               LOGGER.warn("Chunk {} is allocated, but stream is missing", (Object)p_222666_1_);
               return null;
            } else {
               int j1 = i1 - 1;
               if (isExternalStreamChunk(b0)) {
                  if (j1 != 0) {
                     LOGGER.warn("Chunk has both internal and external streams");
                  }

                  return this.createExternalChunkInputStream(p_222666_1_, getExternalChunkVersion(b0));
               } else if (j1 > bytebuffer.remaining()) {
                  LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", p_222666_1_, j1, bytebuffer.remaining());
                  return null;
               } else if (j1 < 0) {
                  LOGGER.error("Declared size {} of chunk {} is negative", i1, p_222666_1_);
                  return null;
               } else {
                  return this.createChunkInputStream(p_222666_1_, b0, createStream(bytebuffer, j1));
               }
            }
         }
      }
   }

   private static boolean isExternalStreamChunk(byte p_227130_0_) {
      return (p_227130_0_ & 128) != 0;
   }

   private static byte getExternalChunkVersion(byte p_227141_0_) {
      return (byte)(p_227141_0_ & -129);
   }

   @Nullable
   private DataInputStream createChunkInputStream(ChunkPos p_227134_1_, byte p_227134_2_, InputStream p_227134_3_) throws IOException {
      RegionFileVersion regionfileversion = RegionFileVersion.fromId(p_227134_2_);
      if (regionfileversion == null) {
         LOGGER.error("Chunk {} has invalid chunk stream version {}", p_227134_1_, p_227134_2_);
         return null;
      } else {
         return new DataInputStream(new BufferedInputStream(regionfileversion.wrap(p_227134_3_)));
      }
   }

   @Nullable
   private DataInputStream createExternalChunkInputStream(ChunkPos p_227133_1_, byte p_227133_2_) throws IOException {
      Path path = this.getExternalChunkPath(p_227133_1_);
      if (!Files.isRegularFile(path)) {
         LOGGER.error("External chunk path {} is not file", (Object)path);
         return null;
      } else {
         return this.createChunkInputStream(p_227133_1_, p_227133_2_, Files.newInputStream(path));
      }
   }

   private static ByteArrayInputStream createStream(ByteBuffer p_227137_0_, int p_227137_1_) {
      return new ByteArrayInputStream(p_227137_0_.array(), p_227137_0_.position(), p_227137_1_);
   }

   private int packSectorOffset(int p_227132_1_, int p_227132_2_) {
      return p_227132_1_ << 8 | p_227132_2_;
   }

   private static int getNumSectors(int p_227131_0_) {
      return p_227131_0_ & 255;
   }

   private static int getSectorNumber(int p_227142_0_) {
      return p_227142_0_ >> 8;
   }

   private static int sizeToSectors(int p_227144_0_) {
      return (p_227144_0_ + 4096 - 1) / 4096;
   }

   public boolean doesChunkExist(ChunkPos p_222662_1_) {
      int i = this.getOffset(p_222662_1_);
      if (i == 0) {
         return false;
      } else {
         int j = getSectorNumber(i);
         int k = getNumSectors(i);
         ByteBuffer bytebuffer = ByteBuffer.allocate(5);

         try {
            this.file.read(bytebuffer, (long)(j * 4096));
            ((Buffer)bytebuffer).flip();
            if (bytebuffer.remaining() != 5) {
               return false;
            } else {
               int l = bytebuffer.getInt();
               byte b0 = bytebuffer.get();
               if (isExternalStreamChunk(b0)) {
                  if (!RegionFileVersion.isValidVersion(getExternalChunkVersion(b0))) {
                     return false;
                  }

                  if (!Files.isRegularFile(this.getExternalChunkPath(p_222662_1_))) {
                     return false;
                  }
               } else {
                  if (!RegionFileVersion.isValidVersion(b0)) {
                     return false;
                  }

                  if (l == 0) {
                     return false;
                  }

                  int i1 = l - 1;
                  if (i1 < 0 || i1 > 4096 * k) {
                     return false;
                  }
               }

               return true;
            }
         } catch (IOException var9) {
            return false;
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(ChunkPos p_222661_1_) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(this.version.wrap(new RegionFile.ChunkBuffer(p_222661_1_))));
   }

   protected synchronized void write(ChunkPos p_227135_1_, ByteBuffer p_227135_2_) throws IOException {
      int i = getOffsetIndex(p_227135_1_);
      int j = this.offsets.get(i);
      int k = getSectorNumber(j);
      int l = getNumSectors(j);
      if (l == 255) l = forgeGetRealLength(i, k); //Forge: Old Forge fix, get real length, so we can free if needed
      int i1 = p_227135_2_.remaining();
      int j1 = sizeToSectors(i1);
      int k1;
      RegionFile.ICompleteCallback regionfile$icompletecallback;
      if (j1 >= 256) {
         Path path = this.getExternalChunkPath(p_227135_1_);
         LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", p_227135_1_, i1, path);
         j1 = 1;
         k1 = this.usedSectors.allocate(j1);
         regionfile$icompletecallback = this.writeToExternalFile(path, p_227135_2_);
         ByteBuffer bytebuffer = this.createExternalStub();
         this.file.write(bytebuffer, (long)(k1 * 4096));
      } else {
         k1 = this.usedSectors.allocate(j1);
         regionfile$icompletecallback = () -> {
            Files.deleteIfExists(this.getExternalChunkPath(p_227135_1_));
         };
         this.file.write(p_227135_2_, (long)(k1 * 4096));
      }

      int l1 = (int)(Util.getEpochMillis() / 1000L);
      this.offsets.put(i, this.packSectorOffset(k1, j1));
      this.timestamps.put(i, l1);
      this.writeHeader();
      regionfile$icompletecallback.run();
      if (k != 0) {
         this.usedSectors.free(k, l);
      }

   }

   private ByteBuffer createExternalStub() {
      ByteBuffer bytebuffer = ByteBuffer.allocate(5);
      bytebuffer.putInt(1);
      bytebuffer.put((byte)(this.version.getId() | 128));
      ((Buffer)bytebuffer).flip();
      return bytebuffer;
   }

   private RegionFile.ICompleteCallback writeToExternalFile(Path p_227138_1_, ByteBuffer p_227138_2_) throws IOException {
      Path path = Files.createTempFile(this.externalFileDir, "tmp", (String)null);

      try (FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
         ((Buffer)p_227138_2_).position(5);
         filechannel.write(p_227138_2_);
      }

      return () -> {
         Files.move(path, p_227138_1_, StandardCopyOption.REPLACE_EXISTING);
      };
   }

   private void writeHeader() throws IOException {
      ((Buffer)this.header).position(0);
      this.file.write(this.header, 0L);
   }

   private int getOffset(ChunkPos p_222660_1_) {
      return this.offsets.get(getOffsetIndex(p_222660_1_));
   }

   public boolean hasChunk(ChunkPos p_222667_1_) {
      return this.getOffset(p_222667_1_) != 0;
   }

   private static int getOffsetIndex(ChunkPos p_222668_0_) {
      return p_222668_0_.getRegionLocalX() + p_222668_0_.getRegionLocalZ() * 32;
   }

   public void close() throws IOException {
      try {
         this.padToFullSector();
      } finally {
         try {
            this.writeHeader();
         } finally {
            try {
               this.file.force(true);
            } finally {
               this.file.close();
            }
         }
      }

   }

   private void padToFullSector() throws IOException {
      int i = (int)this.file.size();
      int j = sizeToSectors(i) * 4096;
      if (i != j) {
         ByteBuffer bytebuffer = PADDING_BUFFER.duplicate();
         ((Buffer)bytebuffer).position(0);
         this.file.write(bytebuffer, (long)(j - 1));
      }

   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos p_i50620_2_) {
         super(8096);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(RegionFile.this.version.getId());
         this.pos = p_i50620_2_;
      }

      public void close() throws IOException {
         ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);
         bytebuffer.putInt(0, this.count - 5 + 1);
         RegionFile.this.write(this.pos, bytebuffer);
      }
   }

   interface ICompleteCallback {
      void run() throws IOException;
   }
}
