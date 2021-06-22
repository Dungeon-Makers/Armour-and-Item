package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestExecutor {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockPos firstTestNorthWestCorner;
   private final ServerWorld level;
   private final TestCollection testTicker;
   private final List<TestTracker> allTestInfos = Lists.newArrayList();
   private final List<Pair<TestBatch, Collection<TestTracker>>> batches = Lists.newArrayList();
   private TestResultList currentBatchTracker;
   private int currentBatchIndex = 0;
   private BlockPos.Mutable nextTestNorthWestCorner;
   private int field_229475_j_ = 0;

   public TestExecutor(Collection<TestBatch> p_i226066_1_, BlockPos p_i226066_2_, ServerWorld p_i226066_3_, TestCollection p_i226066_4_) {
      this.nextTestNorthWestCorner = new BlockPos.Mutable(p_i226066_2_);
      this.firstTestNorthWestCorner = p_i226066_2_;
      this.level = p_i226066_3_;
      this.testTicker = p_i226066_4_;
      p_i226066_1_.forEach((p_229481_2_) -> {
         Collection<TestTracker> collection = Lists.newArrayList();

         for(TestFunctionInfo testfunctioninfo : p_229481_2_.getTestFunctions()) {
            TestTracker testtracker = new TestTracker(testfunctioninfo, p_i226066_3_);
            collection.add(testtracker);
            this.allTestInfos.add(testtracker);
         }

         this.batches.add(Pair.of(p_229481_2_, collection));
      });
   }

   public List<TestTracker> getTestInfos() {
      return this.allTestInfos;
   }

   public void start() {
      this.runBatch(0);
   }

   private void runBatch(int p_229477_1_) {
      this.currentBatchIndex = p_229477_1_;
      this.currentBatchTracker = new TestResultList();
      if (p_229477_1_ < this.batches.size()) {
         Pair<TestBatch, Collection<TestTracker>> pair = this.batches.get(this.currentBatchIndex);
         TestBatch testbatch = pair.getFirst();
         Collection<TestTracker> collection = pair.getSecond();
         this.createStructuresForBatch(collection);
         testbatch.runBeforeBatchFunction(this.level);
         String s = testbatch.getName();
         LOGGER.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
         collection.forEach((p_229483_1_) -> {
            this.currentBatchTracker.addTestToTrack(p_229483_1_);
            this.currentBatchTracker.func_229580_a_(new ITestCallback() {
               public void testStructureLoaded(TestTracker p_225644_1_) {
               }

               public void testFailed(TestTracker p_225645_1_) {
                  TestExecutor.this.testCompleted(p_225645_1_);
               }
            });
            TestUtils.func_229542_a_(p_229483_1_, this.testTicker);
         });
      }
   }

   private void testCompleted(TestTracker p_229479_1_) {
      if (this.currentBatchTracker.isDone()) {
         this.runBatch(this.currentBatchIndex + 1);
      }

   }

   private void createStructuresForBatch(Collection<TestTracker> p_229480_1_) {
      int i = 0;

      for(TestTracker testtracker : p_229480_1_) {
         BlockPos blockpos = new BlockPos(this.nextTestNorthWestCorner);
         testtracker.setStructureBlockPos(blockpos);
         StructureHelper.func_229602_a_(testtracker.getStructureName(), blockpos, 2, this.level, true);
         BlockPos blockpos1 = testtracker.getStructureSize();
         int j = blockpos1 == null ? 1 : blockpos1.getX();
         int k = blockpos1 == null ? 1 : blockpos1.getZ();
         this.field_229475_j_ = Math.max(this.field_229475_j_, k);
         this.nextTestNorthWestCorner.move(j + 4, 0, 0);
         if (i++ % 8 == 0) {
            this.nextTestNorthWestCorner.move(0, 0, this.field_229475_j_ + 5);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            this.field_229475_j_ = 0;
         }
      }

   }
}