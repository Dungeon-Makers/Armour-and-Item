package net.minecraft.client.gui.toasts;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IToast {
   ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");
   Object NO_TOKEN = new Object();

   IToast.Visibility func_193653_a(ToastGui p_193653_1_, long p_193653_2_);

   default Object getToken() {
      return NO_TOKEN;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Visibility {
      SHOW(SoundEvents.UI_TOAST_IN),
      HIDE(SoundEvents.UI_TOAST_OUT);

      private final SoundEvent soundEvent;

      private Visibility(SoundEvent p_i47607_3_) {
         this.soundEvent = p_i47607_3_;
      }

      public void playSound(SoundHandler p_194169_1_) {
         p_194169_1_.play(SimpleSound.forUI(this.soundEvent, 1.0F, 1.0F));
      }
   }
}