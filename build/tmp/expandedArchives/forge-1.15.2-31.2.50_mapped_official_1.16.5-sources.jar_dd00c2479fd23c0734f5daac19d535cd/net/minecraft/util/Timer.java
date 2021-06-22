package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   public int field_74280_b;
   public float partialTick;
   public float tickDelta;
   private long lastMs;
   private final float msPerTick;

   public Timer(float p_i49528_1_, long p_i49528_2_) {
      this.msPerTick = 1000.0F / p_i49528_1_;
      this.lastMs = p_i49528_2_;
   }

   public void func_74275_a(long p_74275_1_) {
      this.tickDelta = (float)(p_74275_1_ - this.lastMs) / this.msPerTick;
      this.lastMs = p_74275_1_;
      this.partialTick += this.tickDelta;
      this.field_74280_b = (int)this.partialTick;
      this.partialTick -= (float)this.field_74280_b;
   }
}