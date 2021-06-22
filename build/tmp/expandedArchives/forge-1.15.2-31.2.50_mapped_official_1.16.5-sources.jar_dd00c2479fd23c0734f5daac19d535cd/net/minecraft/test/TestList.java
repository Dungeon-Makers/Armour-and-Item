package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TestList {
   private final TestTracker parent;
   private final List<TestTickResult> events;
   private long lastTick;

   private TestList(TestTracker p_TestList_1_, List<TestTickResult> p_TestList_2_) {
      this.parent = p_TestList_1_;
      this.events = p_TestList_2_;
   }

   public void tickAndContinue(long p_229567_1_) {
      try {
         this.tick(p_229567_1_);
      } catch (Exception var4) {
         ;
      }

   }

   public void tickAndFailIfNotComplete(long p_229568_1_) {
      try {
         this.tick(p_229568_1_);
      } catch (Exception exception) {
         this.parent.fail(exception);
      }

   }

   private void tick(long p_229569_1_) {
      Iterator<TestTickResult> iterator = this.events.iterator();

      while(iterator.hasNext()) {
         TestTickResult testtickresult = iterator.next();
         testtickresult.assertion.run();
         iterator.remove();
         long i = p_229569_1_ - this.lastTick;
         long j = this.lastTick;
         this.lastTick = p_229569_1_;
         if (testtickresult.expectedDelay != null && testtickresult.expectedDelay != i) {
            this.parent.fail(new TestRuntimeException("Succeeded in invalid tick: expected " + (j + testtickresult.expectedDelay) + ", but current tick is " + p_229569_1_));
            break;
         }
      }

   }
}