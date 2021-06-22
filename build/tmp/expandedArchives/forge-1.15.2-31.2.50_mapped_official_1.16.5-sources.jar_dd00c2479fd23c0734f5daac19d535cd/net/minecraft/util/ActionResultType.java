package net.minecraft.util;

public enum ActionResultType {
   SUCCESS,
   CONSUME,
   PASS,
   FAIL;

   public boolean consumesAction() {
      return this == SUCCESS || this == CONSUME;
   }

   public boolean shouldSwing() {
      return this == SUCCESS;
   }
}