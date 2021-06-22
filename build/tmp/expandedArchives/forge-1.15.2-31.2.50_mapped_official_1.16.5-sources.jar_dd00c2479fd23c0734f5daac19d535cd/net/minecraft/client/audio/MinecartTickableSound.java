package net.minecraft.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartTickableSound extends TickableSound {
   private final AbstractMinecartEntity minecart;
   private float pitch = 0.0F;

   public MinecartTickableSound(AbstractMinecartEntity p_i48614_1_) {
      super(SoundEvents.MINECART_RIDING, SoundCategory.NEUTRAL);
      this.minecart = p_i48614_1_;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
      this.x = (float)p_i48614_1_.getX();
      this.y = (float)p_i48614_1_.getY();
      this.z = (float)p_i48614_1_.getZ();
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if (this.minecart.removed) {
         this.stopped = true;
      } else {
         this.x = (float)this.minecart.getX();
         this.y = (float)this.minecart.getY();
         this.z = (float)this.minecart.getZ();
         float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(this.minecart.getDeltaMovement()));
         if ((double)f >= 0.01D) {
            this.pitch = MathHelper.clamp(this.pitch + 0.0025F, 0.0F, 1.0F);
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 0.7F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      }
   }
}