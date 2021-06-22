package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;

public class TestResultList {
   private final Collection<TestTracker> tests = Lists.newArrayList();
   @Nullable
   private ITestCallback field_229577_b_;

   public TestResultList() {
   }

   public TestResultList(Collection<TestTracker> p_i226072_1_) {
      this.tests.addAll(p_i226072_1_);
   }

   public void addTestToTrack(TestTracker p_229579_1_) {
      this.tests.add(p_229579_1_);
      if (this.field_229577_b_ != null) {
         p_229579_1_.addListener(this.field_229577_b_);
      }

   }

   public void func_229580_a_(ITestCallback p_229580_1_) {
      this.field_229577_b_ = p_229580_1_;
      this.tests.forEach((p_229581_1_) -> {
         p_229581_1_.addListener(p_229580_1_);
      });
   }

   public int getFailedRequiredCount() {
      return (int)this.tests.stream().filter(TestTracker::hasFailed).filter(TestTracker::isRequired).count();
   }

   public int getFailedOptionalCount() {
      return (int)this.tests.stream().filter(TestTracker::hasFailed).filter(TestTracker::isOptional).count();
   }

   public int getDoneCount() {
      return (int)this.tests.stream().filter(TestTracker::isDone).count();
   }

   public boolean hasFailedRequired() {
      return this.getFailedRequiredCount() > 0;
   }

   public boolean hasFailedOptional() {
      return this.getFailedOptionalCount() > 0;
   }

   public int getTotalCount() {
      return this.tests.size();
   }

   public boolean isDone() {
      return this.getDoneCount() == this.getTotalCount();
   }

   public String getProgressBar() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append('[');
      this.tests.forEach((p_229582_1_) -> {
         if (!p_229582_1_.hasStarted()) {
            stringbuffer.append(' ');
         } else if (p_229582_1_.hasSucceeded()) {
            stringbuffer.append('+');
         } else if (p_229582_1_.hasFailed()) {
            stringbuffer.append((char)(p_229582_1_.isRequired() ? 'X' : 'x'));
         } else {
            stringbuffer.append('_');
         }

      });
      stringbuffer.append(']');
      return stringbuffer.toString();
   }

   public String toString() {
      return this.getProgressBar();
   }
}