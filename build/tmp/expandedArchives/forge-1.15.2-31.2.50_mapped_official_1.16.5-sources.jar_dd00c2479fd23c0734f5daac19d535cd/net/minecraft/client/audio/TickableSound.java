package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TickableSound extends LocatableSound implements ITickableSound {
   protected boolean stopped;

   protected TickableSound(SoundEvent p_i46532_1_, SoundCategory p_i46532_2_) {
      super(p_i46532_1_, p_i46532_2_);
   }

   public boolean isStopped() {
      return this.stopped;
   }
}