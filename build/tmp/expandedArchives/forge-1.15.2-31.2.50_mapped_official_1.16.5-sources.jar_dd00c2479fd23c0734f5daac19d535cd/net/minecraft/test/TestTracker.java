package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TestTracker {
   private final TestFunctionInfo testFunction;
   private BlockPos structureBlockPos;
   private final ServerWorld level;
   private final Collection<ITestCallback> listeners = Lists.newArrayList();
   private final int timeoutTicks;
   private final Collection<TestList> sequences = Lists.newCopyOnWriteArrayList();
   private Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap<>();
   private long startTick;
   private long tickCount;
   private boolean started = false;
   private final Stopwatch timer = Stopwatch.createUnstarted();
   private boolean done = false;
   @Nullable
   private Throwable error;

   public TestTracker(TestFunctionInfo p_i226070_1_, ServerWorld p_i226070_2_) {
      this.testFunction = p_i226070_1_;
      this.level = p_i226070_2_;
      this.timeoutTicks = p_i226070_1_.getMaxTicks();
   }

   public TestTracker(TestFunctionInfo p_i226069_1_, BlockPos p_i226069_2_, ServerWorld p_i226069_3_) {
      this(p_i226069_1_, p_i226069_3_);
      this.setStructureBlockPos(p_i226069_2_);
   }

   void setStructureBlockPos(BlockPos p_229503_1_) {
      this.structureBlockPos = p_229503_1_;
   }

   void startExecution() {
      this.startTick = this.level.getGameTime() + 1L + this.testFunction.getSetupTicks();
      this.timer.start();
   }

   public void tick() {
      if (!this.isDone()) {
         this.tickCount = this.level.getGameTime() - this.startTick;
         if (this.tickCount >= 0L) {
            if (this.tickCount == 0L) {
               this.startTest();
            }

            ObjectIterator<Entry<Runnable>> objectiterator = this.runAtTickTimeMap.object2LongEntrySet().iterator();

            while(objectiterator.hasNext()) {
               Entry<Runnable> entry = objectiterator.next();
               if (entry.getLongValue() <= this.tickCount) {
                  try {
                     entry.getKey().run();
                  } catch (Exception exception) {
                     this.fail(exception);
                  }

                  objectiterator.remove();
               }
            }

            if (this.tickCount > (long)this.timeoutTicks) {
               if (this.sequences.isEmpty()) {
                  this.fail(new TestTimeoutException("Didn't succeed or fail within " + this.testFunction.getMaxTicks() + " ticks"));
               } else {
                  this.sequences.forEach((p_229509_1_) -> {
                     p_229509_1_.tickAndFailIfNotComplete(this.tickCount);
                  });
                  if (this.error == null) {
                     this.fail(new TestTimeoutException("No sequences finished"));
                  }
               }
            } else {
               this.sequences.forEach((p_229505_1_) -> {
                  p_229505_1_.tickAndContinue(this.tickCount);
               });
            }

         }
      }
   }

   private void startTest() {
      if (this.started) {
         throw new IllegalStateException("Test already started");
      } else {
         this.started = true;

         try {
            this.testFunction.run(new TestTrackerHolder(this));
         } catch (Exception exception) {
            this.fail(exception);
         }

      }
   }

   public String getTestName() {
      return this.testFunction.getTestName();
   }

   public BlockPos getStructureBlockPos() {
      return this.structureBlockPos;
   }

   @Nullable
   public BlockPos getStructureSize() {
      StructureBlockTileEntity structureblocktileentity = this.getStructureBlockEntity();
      return structureblocktileentity == null ? null : structureblocktileentity.getStructureSize();
   }

   @Nullable
   private StructureBlockTileEntity getStructureBlockEntity() {
      return (StructureBlockTileEntity)this.level.getBlockEntity(this.structureBlockPos);
   }

   public ServerWorld getLevel() {
      return this.level;
   }

   public boolean hasSucceeded() {
      return this.done && this.error == null;
   }

   public boolean hasFailed() {
      return this.error != null;
   }

   public boolean hasStarted() {
      return this.started;
   }

   public boolean isDone() {
      return this.done;
   }

   private void finish() {
      if (!this.done) {
         this.done = true;
         this.timer.stop();
      }

   }

   public void fail(Throwable p_229506_1_) {
      this.finish();
      this.error = p_229506_1_;
      this.listeners.forEach((p_229511_1_) -> {
         p_229511_1_.testFailed(this);
      });
   }

   @Nullable
   public Throwable getError() {
      return this.error;
   }

   public String toString() {
      return this.getTestName();
   }

   public void addListener(ITestCallback p_229504_1_) {
      this.listeners.add(p_229504_1_);
   }

   public void func_229502_a_(int p_229502_1_) {
      StructureBlockTileEntity structureblocktileentity = StructureHelper.func_229602_a_(this.testFunction.getStructureName(), this.structureBlockPos, p_229502_1_, this.level, false);
      structureblocktileentity.setStructureName(this.getTestName());
      StructureHelper.func_229600_a_(this.structureBlockPos.offset(1, 0, -1), this.level);
      this.listeners.forEach((p_229508_1_) -> {
         p_229508_1_.testStructureLoaded(this);
      });
   }

   public boolean isRequired() {
      return this.testFunction.isRequired();
   }

   public boolean isOptional() {
      return !this.testFunction.isRequired();
   }

   public String getStructureName() {
      return this.testFunction.getStructureName();
   }
}