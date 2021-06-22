package net.minecraft.world.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionSectionCache<R extends IDynamicSerializable> implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IOWorker worker;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap<>();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final BiFunction<Runnable, Dynamic<?>, R> field_219123_e;
   private final Function<Runnable, R> factory;
   private final DataFixer fixerUpper;
   private final DefaultTypeReferences type;

   public RegionSectionCache(File p_i49937_1_, BiFunction<Runnable, Dynamic<?>, R> p_i49937_2_, Function<Runnable, R> p_i49937_3_, DataFixer p_i49937_4_, DefaultTypeReferences p_i49937_5_) {
      this.field_219123_e = p_i49937_2_;
      this.factory = p_i49937_3_;
      this.fixerUpper = p_i49937_4_;
      this.type = p_i49937_5_;
      this.worker = new IOWorker(new RegionFileCache(p_i49937_1_), p_i49937_1_.getName());
   }

   protected void tick(BooleanSupplier p_219115_1_) {
      while(!this.dirty.isEmpty() && p_219115_1_.getAsBoolean()) {
         ChunkPos chunkpos = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(chunkpos);
      }

   }

   @Nullable
   protected Optional<R> get(long p_219106_1_) {
      return this.storage.get(p_219106_1_);
   }

   protected Optional<R> getOrLoad(long p_219113_1_) {
      SectionPos sectionpos = SectionPos.of(p_219113_1_);
      if (this.outsideStoredRange(sectionpos)) {
         return Optional.empty();
      } else {
         Optional<R> optional = this.get(p_219113_1_);
         if (optional != null) {
            return optional;
         } else {
            this.readColumn(sectionpos.chunk());
            optional = this.get(p_219113_1_);
            if (optional == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return optional;
            }
         }
      }
   }

   protected boolean outsideStoredRange(SectionPos p_219114_1_) {
      return World.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(p_219114_1_.y()));
   }

   protected R func_219110_e(long p_219110_1_) {
      Optional<R> optional = this.getOrLoad(p_219110_1_);
      if (optional.isPresent()) {
         return (R)(optional.get());
      } else {
         R r = this.factory.apply(() -> {
            this.setDirty(p_219110_1_);
         });
         this.storage.put(p_219110_1_, Optional.of(r));
         return r;
      }
   }

   private void readColumn(ChunkPos p_219107_1_) {
      this.func_219119_a(p_219107_1_, NBTDynamicOps.INSTANCE, this.tryRead(p_219107_1_));
   }

   @Nullable
   private CompoundNBT tryRead(ChunkPos p_223138_1_) {
      try {
         return this.worker.load(p_223138_1_);
      } catch (IOException ioexception) {
         LOGGER.error("Error reading chunk {} data from disk", p_223138_1_, ioexception);
         return null;
      }
   }

   private <T> void func_219119_a(ChunkPos p_219119_1_, DynamicOps<T> p_219119_2_, @Nullable T p_219119_3_) {
      if (p_219119_3_ == null) {
         for(int i = 0; i < 16; ++i) {
            this.storage.put(SectionPos.of(p_219119_1_, i).asLong(), Optional.empty());
         }
      } else {
         Dynamic<T> dynamic1 = new Dynamic<>(p_219119_2_, p_219119_3_);
         int j = func_219103_a(dynamic1);
         int k = SharedConstants.getCurrentVersion().getWorldVersion();
         boolean flag = j != k;
         Dynamic<T> dynamic = this.fixerUpper.update(this.type.getType(), dynamic1, j, k);
         OptionalDynamic<T> optionaldynamic = dynamic.get("Sections");

         for(int l = 0; l < 16; ++l) {
            long i1 = SectionPos.of(p_219119_1_, l).asLong();
            Optional<R> optional = optionaldynamic.get(Integer.toString(l)).get().map((p_219105_3_) -> {
               return (R)(this.field_219123_e.apply(() -> {
                  this.setDirty(i1);
               }, p_219105_3_));
            });
            this.storage.put(i1, optional);
            optional.ifPresent((p_219118_4_) -> {
               this.onSectionLoad(i1);
               if (flag) {
                  this.setDirty(i1);
               }

            });
         }
      }

   }

   private void writeColumn(ChunkPos p_219117_1_) {
      Dynamic<INBT> dynamic = this.func_219108_a(p_219117_1_, NBTDynamicOps.INSTANCE);
      INBT inbt = dynamic.getValue();
      if (inbt instanceof CompoundNBT) {
         this.worker.store(p_219117_1_, (CompoundNBT)inbt);
      } else {
         LOGGER.error("Expected compound tag, got {}", (Object)inbt);
      }

   }

   private <T> Dynamic<T> func_219108_a(ChunkPos p_219108_1_, DynamicOps<T> p_219108_2_) {
      Map<T, T> map = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         long j = SectionPos.of(p_219108_1_, i).asLong();
         this.dirty.remove(j);
         Optional<R> optional = this.storage.get(j);
         if (optional != null && optional.isPresent()) {
            map.put(p_219108_2_.createString(Integer.toString(i)), ((IDynamicSerializable)optional.get()).func_218175_a(p_219108_2_));
         }
      }

      return new Dynamic<>(p_219108_2_, p_219108_2_.createMap(ImmutableMap.of(p_219108_2_.createString("Sections"), p_219108_2_.createMap(map), p_219108_2_.createString("DataVersion"), p_219108_2_.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
   }

   protected void onSectionLoad(long p_219111_1_) {
   }

   protected void setDirty(long p_219116_1_) {
      Optional<R> optional = this.storage.get(p_219116_1_);
      if (optional != null && optional.isPresent()) {
         this.dirty.add(p_219116_1_);
      } else {
         LOGGER.warn("No data for position: {}", (Object)SectionPos.of(p_219116_1_));
      }
   }

   private static int func_219103_a(Dynamic<?> p_219103_0_) {
      return p_219103_0_.get("DataVersion").asNumber().orElse(1945).intValue();
   }

   public void flush(ChunkPos p_219112_1_) {
      if (!this.dirty.isEmpty()) {
         for(int i = 0; i < 16; ++i) {
            long j = SectionPos.of(p_219112_1_, i).asLong();
            if (this.dirty.contains(j)) {
               this.writeColumn(p_219112_1_);
               return;
            }
         }
      }

   }

   public void close() throws IOException {
      this.worker.close();
   }
}