package net.minecraft.test;

import javax.annotation.Nullable;

class TestTickResult {
   @Nullable
   public final Long expectedDelay;
   public final Runnable assertion;

   private TestTickResult() {
      this.expectedDelay = 0L;
      this.assertion = () -> {};
}
}