package com.mojang.realmsclient.exception;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RetryCallException extends RealmsServiceException {
   public final int delaySeconds;

   public RetryCallException(int p_i51783_1_) {
      super(503, "Retry operation", -1, "");
      if (p_i51783_1_ >= 0 && p_i51783_1_ <= 120) {
         this.delaySeconds = p_i51783_1_;
      } else {
         this.delaySeconds = 5;
      }

   }
}