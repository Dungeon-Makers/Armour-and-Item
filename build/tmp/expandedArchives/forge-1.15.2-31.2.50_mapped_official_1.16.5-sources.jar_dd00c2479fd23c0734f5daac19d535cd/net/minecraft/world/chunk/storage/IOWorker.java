package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Thread field_227081_b_;
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final Queue<Runnable> field_227083_d_ = Queues.newConcurrentLinkedQueue();
   private final RegionFileCache storage;
   private final Map<ChunkPos, IOWorker.Entry> pendingWrites = Maps.newLinkedHashMap();
   private boolean field_227086_g_ = true;
   private CompletableFuture<Void> field_227087_h_ = new CompletableFuture<>();

   IOWorker(RegionFileCache p_i225782_1_, String p_i225782_2_) {
      this.storage = p_i225782_1_;
      this.field_227081_b_ = new Thread(this::func_227107_d_);
      this.field_227081_b_.setName(p_i225782_2_ + " IO worker");
      this.field_227081_b_.start();
   }

   public CompletableFuture<Void> store(ChunkPos p_227093_1_, CompoundNBT p_227093_2_) {
      return this.func_227099_a_((p_227094_3_) -> {
         return () -> {
            IOWorker.Entry ioworker$entry = this.pendingWrites.computeIfAbsent(p_227093_1_, (p_227101_0_) -> {
               return new IOWorker.Entry();
            });
            ioworker$entry.data = p_227093_2_;
            ioworker$entry.result.whenComplete((p_227098_1_, p_227098_2_) -> {
               if (p_227098_2_ != null) {
                  p_227094_3_.completeExceptionally(p_227098_2_);
               } else {
                  p_227094_3_.complete(null);
               }

            });
         };
      });
   }

   @Nullable
   public CompoundNBT load(ChunkPos p_227090_1_) throws IOException {
      CompletableFuture<CompoundNBT> completablefuture = this.func_227099_a_((p_227092_2_) -> {
         return () -> {
            IOWorker.Entry ioworker$entry = this.pendingWrites.get(p_227090_1_);
            if (ioworker$entry != null) {
               p_227092_2_.complete(ioworker$entry.data);
            } else {
               try {
                  CompoundNBT compoundnbt = this.storage.read(p_227090_1_);
                  p_227092_2_.complete(compoundnbt);
               } catch (Exception exception) {
                  LOGGER.warn("Failed to read chunk {}", p_227090_1_, exception);
                  p_227092_2_.completeExceptionally(exception);
               }
            }

         };
      });

      try {
         return completablefuture.join();
      } catch (CompletionException completionexception) {
         if (completionexception.getCause() instanceof IOException) {
            throw (IOException)completionexception.getCause();
         } else {
            throw completionexception;
         }
      }
   }

   private CompletableFuture<Void> func_227100_b_() {
      return this.func_227099_a_((p_227106_1_) -> {
         return () -> {
            this.field_227086_g_ = false;
            this.field_227087_h_ = p_227106_1_;
         };
      });
   }

   public CompletableFuture<Void> synchronize() {
      return this.func_227099_a_((p_227096_1_) -> {
         return () -> {
            CompletableFuture<?> completablefuture = CompletableFuture.allOf(this.pendingWrites.values().stream().map((p_227095_0_) -> {
               return p_227095_0_.result;
            }).toArray((p_227089_0_) -> {
               return new CompletableFuture[p_227089_0_];
            }));
            completablefuture.whenComplete((p_227097_1_, p_227097_2_) -> {
               p_227096_1_.complete(null);
            });
         };
      });
   }

   private <T> CompletableFuture<T> func_227099_a_(Function<CompletableFuture<T>, Runnable> p_227099_1_) {
      CompletableFuture<T> completablefuture = new CompletableFuture<>();
      this.field_227083_d_.add(p_227099_1_.apply(completablefuture));
      LockSupport.unpark(this.field_227081_b_);
      return completablefuture;
   }

   private void func_227105_c_() {
      LockSupport.park("waiting for tasks");
   }

   private void func_227107_d_() {
      try {
         while(this.field_227086_g_) {
            boolean flag = this.func_227112_h_();
            boolean flag1 = this.func_227109_e_();
            if (!flag && !flag1) {
               this.func_227105_c_();
            }
         }

         this.func_227112_h_();
         this.func_227110_f_();
      } finally {
         this.func_227111_g_();
      }

   }

   private boolean func_227109_e_() {
      Iterator<Map.Entry<ChunkPos, IOWorker.Entry>> iterator = this.pendingWrites.entrySet().iterator();
      if (!iterator.hasNext()) {
         return false;
      } else {
         Map.Entry<ChunkPos, IOWorker.Entry> entry = iterator.next();
         iterator.remove();
         this.runStore(entry.getKey(), entry.getValue());
         return true;
      }
   }

   private void func_227110_f_() {
      this.pendingWrites.forEach(this::runStore);
      this.pendingWrites.clear();
   }

   private void runStore(ChunkPos p_227091_1_, IOWorker.Entry p_227091_2_) {
      try {
         this.storage.write(p_227091_1_, p_227091_2_.data);
         p_227091_2_.result.complete((Void)null);
      } catch (Exception exception) {
         LOGGER.error("Failed to store chunk {}", p_227091_1_, exception);
         p_227091_2_.result.completeExceptionally(exception);
      }

   }

   private void func_227111_g_() {
      try {
         this.storage.close();
         this.field_227087_h_.complete((Void)null);
      } catch (Exception exception) {
         LOGGER.error("Failed to close storage", (Throwable)exception);
         this.field_227087_h_.completeExceptionally(exception);
      }

   }

   private boolean func_227112_h_() {
      boolean flag = false;

      Runnable runnable;
      while((runnable = this.field_227083_d_.poll()) != null) {
         flag = true;
         runnable.run();
      }

      return flag;
   }

   public void close() throws IOException {
      if (this.shutdownRequested.compareAndSet(false, true)) {
         try {
            this.func_227100_b_().join();
         } catch (CompletionException completionexception) {
            if (completionexception.getCause() instanceof IOException) {
               throw (IOException)completionexception.getCause();
            } else {
               throw completionexception;
            }
         }
      }
   }

   static class Entry {
      private CompoundNBT data;
      private final CompletableFuture<Void> result = new CompletableFuture<>();

      private Entry() {
      }
   }
}