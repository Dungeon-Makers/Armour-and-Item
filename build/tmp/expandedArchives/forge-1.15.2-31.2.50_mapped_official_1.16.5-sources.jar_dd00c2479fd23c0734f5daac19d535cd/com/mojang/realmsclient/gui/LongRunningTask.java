package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LongRunningTask implements Runnable {
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   public void setScreen(RealmsLongRunningMcoTaskScreen p_224987_1_) {
      this.longRunningMcoTaskScreen = p_224987_1_;
   }

   public void func_224986_a(String p_224986_1_) {
      this.longRunningMcoTaskScreen.func_224231_a(p_224986_1_);
   }

   public void setTitle(String p_224989_1_) {
      this.longRunningMcoTaskScreen.setTitle(p_224989_1_);
   }

   public boolean aborted() {
      return this.longRunningMcoTaskScreen.aborted();
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
   }
}