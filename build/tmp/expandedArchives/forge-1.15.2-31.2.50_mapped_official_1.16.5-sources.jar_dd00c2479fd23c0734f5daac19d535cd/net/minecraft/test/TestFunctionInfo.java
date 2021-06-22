package net.minecraft.test;

import java.util.function.Consumer;

public class TestFunctionInfo {
   private final String batchName;
   private final String testName;
   private final String structureName;
   private final boolean required;
   private final Consumer<TestTrackerHolder> function;
   private final int maxTicks;
   private final long setupTicks;

   private TestFunctionInfo() {
      this.batchName = "";
      this.testName = "";
      this.structureName = "";
      this.required = true;
      this.function = (p) -> {};
      this.maxTicks = 0;
      this.setupTicks = 0L;
   }

   public void run(TestTrackerHolder p_229658_1_) {
      this.function.accept(p_229658_1_);
   }

   public String getTestName() {
      return this.testName;
   }

   public String getStructureName() {
      return this.structureName;
   }

   public String toString() {
      return this.testName;
   }

   public int getMaxTicks() {
      return this.maxTicks;
   }

   public boolean isRequired() {
      return this.required;
   }

   public String getBatchName() {
      return this.batchName;
   }

   public long getSetupTicks() {
      return this.setupTicks;
   }
}