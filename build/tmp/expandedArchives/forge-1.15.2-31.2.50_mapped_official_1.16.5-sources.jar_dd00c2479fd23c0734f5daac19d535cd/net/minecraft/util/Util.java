package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ExecutorService BACKGROUND_EXECUTOR = func_215078_k();
   public static LongSupplier timeSource = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getPropertyName(IProperty<T> p_200269_0_, Object p_200269_1_) {
      return p_200269_0_.getName((T)(p_200269_1_));
   }

   public static String makeDescriptionId(String p_200697_0_, @Nullable ResourceLocation p_200697_1_) {
      return p_200697_1_ == null ? p_200697_0_ + ".unregistered_sadface" : p_200697_0_ + '.' + p_200697_1_.getNamespace() + '.' + p_200697_1_.getPath().replace('/', '.');
   }

   public static long getMillis() {
      return getNanos() / 1000000L;
   }

   public static long getNanos() {
      return timeSource.getAsLong();
   }

   public static long getEpochMillis() {
      return Instant.now().toEpochMilli();
   }

   private static ExecutorService func_215078_k() {
      int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      ExecutorService executorservice;
      if (i <= 0) {
         executorservice = MoreExecutors.newDirectExecutorService();
      } else {
         executorservice = new ForkJoinPool(i, (p_215073_0_) -> {
            ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_215073_0_) {
               protected void onTermination(Throwable p_onTermination_1_) {
                  if (p_onTermination_1_ != null) {
                     Util.LOGGER.warn("{} died", this.getName(), p_onTermination_1_);
                  } else {
                     Util.LOGGER.debug("{} shutdown", (Object)this.getName());
                  }

                  super.onTermination(p_onTermination_1_);
               }
            };
            forkjoinworkerthread.setName("Server-Worker-" + WORKER_COUNT.getAndIncrement());
            return forkjoinworkerthread;
         }, (p_215086_0_, p_215086_1_) -> {
            pauseInIde(p_215086_1_);
            if (p_215086_1_ instanceof CompletionException) {
               p_215086_1_ = p_215086_1_.getCause();
            }

            if (p_215086_1_ instanceof ReportedException) {
               Bootstrap.realStdoutPrintln(((ReportedException)p_215086_1_).getReport().getFriendlyReport());
               System.exit(-1);
            }

            LOGGER.error(String.format("Caught exception in thread %s", p_215086_0_), p_215086_1_);
         }, true);
      }

      return executorservice;
   }

   public static Executor backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static void func_215082_f() {
      BACKGROUND_EXECUTOR.shutdown();

      boolean flag;
      try {
         flag = BACKGROUND_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         flag = false;
      }

      if (!flag) {
         BACKGROUND_EXECUTOR.shutdownNow();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static <T> CompletableFuture<T> failedFuture(Throwable p_215087_0_) {
      CompletableFuture<T> completablefuture = new CompletableFuture<>();
      completablefuture.completeExceptionally(p_215087_0_);
      return completablefuture;
   }

   @OnlyIn(Dist.CLIENT)
   public static void throwAsRuntime(Throwable p_229756_0_) {
      throw p_229756_0_ instanceof RuntimeException ? (RuntimeException)p_229756_0_ : new RuntimeException(p_229756_0_);
   }

   public static Util.OS getPlatform() {
      String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (s.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (s.contains("mac")) {
         return Util.OS.OSX;
      } else if (s.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return s.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getVmArguments() {
      RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
      return runtimemxbean.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static <T> T lastOf(List<T> p_223378_0_) {
      return p_223378_0_.get(p_223378_0_.size() - 1);
   }

   public static <T> T findNextInIterable(Iterable<T> p_195647_0_, @Nullable T p_195647_1_) {
      Iterator<T> iterator = p_195647_0_.iterator();
      T t = iterator.next();
      if (p_195647_1_ != null) {
         T t1 = t;

         while(t1 != p_195647_1_) {
            if (iterator.hasNext()) {
               t1 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return t;
   }

   public static <T> T findPreviousInIterable(Iterable<T> p_195648_0_, @Nullable T p_195648_1_) {
      Iterator<T> iterator = p_195648_0_.iterator();

      T t;
      T t1;
      for(t = null; iterator.hasNext(); t = t1) {
         t1 = iterator.next();
         if (t1 == p_195648_1_) {
            if (t == null) {
               t = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : p_195648_1_);
            }
            break;
         }
      }

      return t;
   }

   public static <T> T make(Supplier<T> p_199748_0_) {
      return p_199748_0_.get();
   }

   public static <T> T make(T p_200696_0_, Consumer<T> p_200696_1_) {
      p_200696_1_.accept(p_200696_0_);
      return p_200696_0_;
   }

   public static <K> Strategy<K> identityStrategy() {
      return (Strategy<K>) Util.IdentityStrategy.INSTANCE;
   }

   public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<? extends V>> p_215079_0_) {
      List<V> list = Lists.newArrayListWithCapacity(p_215079_0_.size());
      CompletableFuture<?>[] completablefuture = new CompletableFuture[p_215079_0_.size()];
      CompletableFuture<Void> completablefuture1 = new CompletableFuture<>();
      p_215079_0_.forEach((p_215083_3_) -> {
         int i = list.size();
         list.add((V)null);
         completablefuture[i] = p_215083_3_.whenComplete((p_215085_3_, p_215085_4_) -> {
            if (p_215085_4_ != null) {
               completablefuture1.completeExceptionally(p_215085_4_);
            } else {
               list.set(i, p_215085_3_);
            }

         });
      });
      return CompletableFuture.allOf(completablefuture).applyToEither(completablefuture1, (p_215089_1_) -> {
         return list;
      });
   }

   public static <T> Stream<T> toStream(Optional<? extends T> p_215081_0_) {
      return DataFixUtils.orElseGet(p_215081_0_.map(Stream::of), Stream::empty);
   }

   public static <T> Optional<T> ifElse(Optional<T> p_215077_0_, Consumer<T> p_215077_1_, Runnable p_215077_2_) {
      if (p_215077_0_.isPresent()) {
         p_215077_1_.accept(p_215077_0_.get());
      } else {
         p_215077_2_.run();
      }

      return p_215077_0_;
   }

   public static Runnable name(Runnable p_215075_0_, Supplier<String> p_215075_1_) {
      return p_215075_0_;
   }

   public static Optional<UUID> func_215074_a(String p_215074_0_, Dynamic<?> p_215074_1_) {
      return p_215074_1_.get(p_215074_0_ + "Most").asNumber().flatMap((p_215076_2_) -> {
         return p_215074_1_.get(p_215074_0_ + "Least").asNumber().map((p_215080_1_) -> {
            return new UUID(p_215076_2_.longValue(), p_215080_1_.longValue());
         });
      });
   }

   public static <T> Dynamic<T> func_215084_a(String p_215084_0_, UUID p_215084_1_, Dynamic<T> p_215084_2_) {
      return p_215084_2_.set(p_215084_0_ + "Most", p_215084_2_.createLong(p_215084_1_.getMostSignificantBits())).set(p_215084_0_ + "Least", p_215084_2_.createLong(p_215084_1_.getLeastSignificantBits()));
   }

   public static <T extends Throwable> T pauseInIde(T p_229757_0_) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", p_229757_0_);

         while(true) {
            try {
               Thread.sleep(1000L);
               LOGGER.error("paused");
            } catch (InterruptedException var2) {
               return p_229757_0_;
            }
         }
      } else {
         return p_229757_0_;
      }
   }

   public static String describeError(Throwable p_229758_0_) {
      if (p_229758_0_.getCause() != null) {
         return describeError(p_229758_0_.getCause());
      } else {
         return p_229758_0_.getMessage() != null ? p_229758_0_.getMessage() : p_229758_0_.toString();
      }
   }

   static enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      public int hashCode(Object p_hashCode_1_) {
         return System.identityHashCode(p_hashCode_1_);
      }

      public boolean equals(Object p_equals_1_, Object p_equals_2_) {
         return p_equals_1_ == p_equals_2_;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenUrlArguments(URL p_195643_1_) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", p_195643_1_.toString()};
         }
      },
      OSX {
         @OnlyIn(Dist.CLIENT)
         protected String[] getOpenUrlArguments(URL p_195643_1_) {
            return new String[]{"open", p_195643_1_.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      @OnlyIn(Dist.CLIENT)
      public void openUrl(URL p_195639_1_) {
         try {
            Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> {
               return Runtime.getRuntime().exec(this.getOpenUrlArguments(p_195639_1_));
            }));

            for(String s : IOUtils.readLines(process.getErrorStream())) {
               Util.LOGGER.error(s);
            }

            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException | PrivilegedActionException privilegedactionexception) {
            Util.LOGGER.error("Couldn't open url '{}'", p_195639_1_, privilegedactionexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openUri(URI p_195642_1_) {
         try {
            this.openUrl(p_195642_1_.toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195642_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      public void openFile(File p_195641_1_) {
         try {
            this.openUrl(p_195641_1_.toURI().toURL());
         } catch (MalformedURLException malformedurlexception) {
            Util.LOGGER.error("Couldn't open file '{}'", p_195641_1_, malformedurlexception);
         }

      }

      @OnlyIn(Dist.CLIENT)
      protected String[] getOpenUrlArguments(URL p_195643_1_) {
         String s = p_195643_1_.toString();
         if ("file".equals(p_195643_1_.getProtocol())) {
            s = s.replace("file:", "file://");
         }

         return new String[]{"xdg-open", s};
      }

      @OnlyIn(Dist.CLIENT)
      public void openUri(String p_195640_1_) {
         try {
            this.openUrl((new URI(p_195640_1_)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
            Util.LOGGER.error("Couldn't open uri '{}'", p_195640_1_, urisyntaxexception);
         }

      }
   }
}