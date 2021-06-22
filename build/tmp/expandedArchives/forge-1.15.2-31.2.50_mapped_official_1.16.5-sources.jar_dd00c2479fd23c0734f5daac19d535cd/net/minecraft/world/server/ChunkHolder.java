package net.minecraft.world.server;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkHolder {
   public static final Either<IChunk, ChunkHolder.IChunkLoadingError> UNLOADED_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   public static final CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   public static final Either<Chunk, ChunkHolder.IChunkLoadingError> UNLOADED_LEVEL_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   private static final CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
   private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
   private static final ChunkHolder.LocationType[] FULL_CHUNK_STATUSES = ChunkHolder.LocationType.values();
   private final AtomicReferenceArray<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> futures = new AtomicReferenceArray<>(CHUNK_STATUSES.size());
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private CompletableFuture<IChunk> chunkToSave = CompletableFuture.completedFuture((IChunk)null);
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   private final ChunkPos pos;
   private short[] field_219320_o = new short[64];
   private int field_219321_p;
   private int field_219322_q;
   private int field_219323_r;
   private int blockChangedLightSectionFilter;
   private int skyChangedLightSectionFilter;
   private final WorldLightManager lightEngine;
   private final ChunkHolder.IListener onLevelChange;
   private final ChunkHolder.IPlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;

   public ChunkHolder(ChunkPos p_i50716_1_, int p_i50716_2_, WorldLightManager p_i50716_3_, ChunkHolder.IListener p_i50716_4_, ChunkHolder.IPlayerProvider p_i50716_5_) {
      this.pos = p_i50716_1_;
      this.lightEngine = p_i50716_3_;
      this.onLevelChange = p_i50716_4_;
      this.playerProvider = p_i50716_5_;
      this.oldTicketLevel = ChunkManager.MAX_CHUNK_DISTANCE + 1;
      this.ticketLevel = this.oldTicketLevel;
      this.queueLevel = this.oldTicketLevel;
      this.setTicketLevel(p_i50716_2_);
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> getFutureIfPresentUnchecked(ChunkStatus p_219301_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures.get(p_219301_1_.getIndex());
      return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> getFutureIfPresent(ChunkStatus p_225410_1_) {
      return getStatus(this.ticketLevel).isOrAfter(p_225410_1_) ? this.getFutureIfPresentUnchecked(p_225410_1_) : UNLOADED_CHUNK_FUTURE;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getTickingChunkFuture() {
      return this.tickingChunkFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getEntityTickingChunkFuture() {
      return this.entityTickingChunkFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getFullChunkFuture() {
      return this.fullChunkFuture;
   }

   @Nullable
   public Chunk getTickingChunk() {
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.getTickingChunkFuture();
      Either<Chunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
      return either == null ? null : either.left().orElse((Chunk)null);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ChunkStatus getLastAvailableStatus() {
      for(int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
         if (completablefuture.getNow(UNLOADED_CHUNK).left().isPresent()) {
            return chunkstatus;
         }
      }

      return null;
   }

   @Nullable
   public IChunk getLastAvailable() {
      for(int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
         if (!completablefuture.isCompletedExceptionally()) {
            Optional<IChunk> optional = completablefuture.getNow(UNLOADED_CHUNK).left();
            if (optional.isPresent()) {
               return optional.get();
            }
         }
      }

      return null;
   }

   public CompletableFuture<IChunk> getChunkToSave() {
      return this.chunkToSave;
   }

   public void func_219279_a(int p_219279_1_, int p_219279_2_, int p_219279_3_) {
      Chunk chunk = this.getTickingChunk();
      if (chunk != null) {
         this.field_219322_q |= 1 << (p_219279_2_ >> 4);
         { //Forge; Cache everything, so always run
            short short1 = (short)(p_219279_1_ << 12 | p_219279_3_ << 8 | p_219279_2_);

            for(int i = 0; i < this.field_219321_p; ++i) {
               if (this.field_219320_o[i] == short1) {
                  return;
               }
            }

            if (this.field_219321_p == this.field_219320_o.length)
               this.field_219320_o = java.util.Arrays.copyOf(this.field_219320_o, this.field_219320_o.length << 1);
            this.field_219320_o[this.field_219321_p++] = short1;
         }

      }
   }

   public void sectionLightChanged(LightType p_219280_1_, int p_219280_2_) {
      Chunk chunk = this.getTickingChunk();
      if (chunk != null) {
         chunk.setUnsaved(true);
         if (p_219280_1_ == LightType.SKY) {
            this.skyChangedLightSectionFilter |= 1 << p_219280_2_ - -1;
         } else {
            this.blockChangedLightSectionFilter |= 1 << p_219280_2_ - -1;
         }

      }
   }

   public void broadcastChanges(Chunk p_219274_1_) {
      if (this.field_219321_p != 0 || this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
         World world = p_219274_1_.getLevel();
         if (this.field_219321_p >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.field_219323_r = -1;
         }

         if (this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
            this.broadcast(new SUpdateLightPacket(p_219274_1_.getPos(), this.lightEngine, this.skyChangedLightSectionFilter & ~this.field_219323_r, this.blockChangedLightSectionFilter & ~this.field_219323_r), true);
            int i = this.skyChangedLightSectionFilter & this.field_219323_r;
            int j = this.blockChangedLightSectionFilter & this.field_219323_r;
            if (i != 0 || j != 0) {
               this.broadcast(new SUpdateLightPacket(p_219274_1_.getPos(), this.lightEngine, i, j), false);
            }

            this.skyChangedLightSectionFilter = 0;
            this.blockChangedLightSectionFilter = 0;
            this.field_219323_r &= ~(this.skyChangedLightSectionFilter & this.blockChangedLightSectionFilter);
         }

         if (this.field_219321_p == 1) {
            int l = (this.field_219320_o[0] >> 12 & 15) + this.pos.x * 16;
            int j1 = this.field_219320_o[0] & 255;
            int k = (this.field_219320_o[0] >> 8 & 15) + this.pos.z * 16;
            BlockPos blockpos = new BlockPos(l, j1, k);
            this.broadcast(new SChangeBlockPacket(world, blockpos), false);
            if (world.getBlockState(blockpos).hasTileEntity()) {
               this.broadcastBlockEntity(world, blockpos);
            }
         } else if (this.field_219321_p >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.broadcast(new SChunkDataPacket(p_219274_1_, this.field_219322_q), false);
         } else if (this.field_219321_p != 0) {
            this.broadcast(new SMultiBlockChangePacket(this.field_219321_p, this.field_219320_o, p_219274_1_), false);
            for(int i1 = 0; i1 < this.field_219321_p; ++i1) {
               int k1 = (this.field_219320_o[i1] >> 12 & 15) + this.pos.x * 16;
               int l1 = this.field_219320_o[i1] & 255;
               int i2 = (this.field_219320_o[i1] >> 8 & 15) + this.pos.z * 16;
               BlockPos blockpos1 = new BlockPos(k1, l1, i2);
               if (world.getBlockState(blockpos1).hasTileEntity()) {
                  this.broadcastBlockEntity(world, blockpos1);
               }
            }
         }

         this.field_219321_p = 0;
         this.field_219322_q = 0;
      }
   }

   private void broadcastBlockEntity(World p_219305_1_, BlockPos p_219305_2_) {
      TileEntity tileentity = p_219305_1_.getBlockEntity(p_219305_2_);
      if (tileentity != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = tileentity.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.broadcast(supdatetileentitypacket, false);
         }
      }

   }

   private void broadcast(IPacket<?> p_219293_1_, boolean p_219293_2_) {
      this.playerProvider.getPlayers(this.pos, p_219293_2_).forEach((p_219304_1_) -> {
         p_219304_1_.connection.send(p_219293_1_);
      });
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> getOrScheduleFuture(ChunkStatus p_219276_1_, ChunkManager p_219276_2_) {
      int i = p_219276_1_.getIndex();
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures.get(i);
      if (completablefuture != null) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<IChunk, ChunkHolder.IChunkLoadingError>)null);
         if (either == null || either.left().isPresent()) {
            return completablefuture;
         }
      }

      if (getStatus(this.ticketLevel).isOrAfter(p_219276_1_)) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_219276_2_.schedule(this, p_219276_1_);
         this.updateChunkToSave(completablefuture1);
         this.futures.set(i, completablefuture1);
         return completablefuture1;
      } else {
         return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
      }
   }

   private void updateChunkToSave(CompletableFuture<? extends Either<? extends IChunk, ChunkHolder.IChunkLoadingError>> p_219284_1_) {
      this.chunkToSave = this.chunkToSave.thenCombine(p_219284_1_, (p_219295_0_, p_219295_1_) -> {
         return p_219295_1_.map((p_219283_0_) -> {
            return p_219283_0_;
         }, (p_219288_1_) -> {
            return p_219295_0_;
         });
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkHolder.LocationType getFullStatus() {
      return getFullChunkStatus(this.ticketLevel);
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   public int getQueueLevel() {
      return this.queueLevel;
   }

   private void setQueueLevel(int p_219275_1_) {
      this.queueLevel = p_219275_1_;
   }

   public void setTicketLevel(int p_219292_1_) {
      this.ticketLevel = p_219292_1_;
   }

   protected void updateFutures(ChunkManager p_219291_1_) {
      ChunkStatus chunkstatus = getStatus(this.oldTicketLevel);
      ChunkStatus chunkstatus1 = getStatus(this.ticketLevel);
      boolean flag = this.oldTicketLevel <= ChunkManager.MAX_CHUNK_DISTANCE;
      boolean flag1 = this.ticketLevel <= ChunkManager.MAX_CHUNK_DISTANCE;
      ChunkHolder.LocationType chunkholder$locationtype = getFullChunkStatus(this.oldTicketLevel);
      ChunkHolder.LocationType chunkholder$locationtype1 = getFullChunkStatus(this.ticketLevel);
      if (flag) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = Either.right(new ChunkHolder.IChunkLoadingError() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
            }
         });

         for(int i = flag1 ? chunkstatus1.getIndex() + 1 : 0; i <= chunkstatus.getIndex(); ++i) {
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures.get(i);
            if (completablefuture != null) {
               completablefuture.complete(either);
            } else {
               this.futures.set(i, CompletableFuture.completedFuture(either));
            }
         }
      }

      boolean flag5 = chunkholder$locationtype.isOrAfter(ChunkHolder.LocationType.BORDER);
      boolean flag6 = chunkholder$locationtype1.isOrAfter(ChunkHolder.LocationType.BORDER);
      this.wasAccessibleSinceLastSave |= flag6;
      if (!flag5 && flag6) {
         this.fullChunkFuture = p_219291_1_.unpackTicks(this);
         this.updateChunkToSave(this.fullChunkFuture);
      }

      if (flag5 && !flag6) {
         CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = this.fullChunkFuture;
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
         this.updateChunkToSave(completablefuture1.thenApply((p_222982_1_) -> {
            return p_222982_1_.ifLeft(p_219291_1_::packTicks);
         }));
      }

      boolean flag7 = chunkholder$locationtype.isOrAfter(ChunkHolder.LocationType.TICKING);
      boolean flag2 = chunkholder$locationtype1.isOrAfter(ChunkHolder.LocationType.TICKING);
      if (!flag7 && flag2) {
         this.tickingChunkFuture = p_219291_1_.postProcess(this);
         this.updateChunkToSave(this.tickingChunkFuture);
      }

      if (flag7 && !flag2) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean flag3 = chunkholder$locationtype.isOrAfter(ChunkHolder.LocationType.ENTITY_TICKING);
      boolean flag4 = chunkholder$locationtype1.isOrAfter(ChunkHolder.LocationType.ENTITY_TICKING);
      if (!flag3 && flag4) {
         if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
         }

         this.entityTickingChunkFuture = p_219291_1_.getEntityTickingRangeFuture(this.pos);
         this.updateChunkToSave(this.entityTickingChunkFuture);
      }

      if (flag3 && !flag4) {
         this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
      this.oldTicketLevel = this.ticketLevel;
   }

   public static ChunkStatus getStatus(int p_219278_0_) {
      return p_219278_0_ < 33 ? ChunkStatus.FULL : ChunkStatus.getStatus(p_219278_0_ - 33);
   }

   public static ChunkHolder.LocationType getFullChunkStatus(int p_219286_0_) {
      return FULL_CHUNK_STATUSES[MathHelper.clamp(33 - p_219286_0_ + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
   }

   public boolean wasAccessibleSinceLastSave() {
      return this.wasAccessibleSinceLastSave;
   }

   public void refreshAccessibility() {
      this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(ChunkHolder.LocationType.BORDER);
   }

   public void replaceProtoChunk(ChunkPrimerWrapper p_219294_1_) {
      for(int i = 0; i < this.futures.length(); ++i) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.futures.get(i);
         if (completablefuture != null) {
            Optional<IChunk> optional = completablefuture.getNow(UNLOADED_CHUNK).left();
            if (optional.isPresent() && optional.get() instanceof ChunkPrimer) {
               this.futures.set(i, CompletableFuture.completedFuture(Either.left(p_219294_1_)));
            }
         }
      }

      this.updateChunkToSave(CompletableFuture.completedFuture(Either.left(p_219294_1_.getWrapped())));
   }

   public interface IChunkLoadingError {
      ChunkHolder.IChunkLoadingError UNLOADED = new ChunkHolder.IChunkLoadingError() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public interface IListener {
      void onLevelChange(ChunkPos p_219066_1_, IntSupplier p_219066_2_, int p_219066_3_, IntConsumer p_219066_4_);
   }

   public interface IPlayerProvider {
      Stream<ServerPlayerEntity> getPlayers(ChunkPos p_219097_1_, boolean p_219097_2_);
   }

   public static enum LocationType {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isOrAfter(ChunkHolder.LocationType p_219065_1_) {
         return this.ordinal() >= p_219065_1_.ordinal();
      }
   }
}
