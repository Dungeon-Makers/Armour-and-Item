package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements IToast {
   private final Advancement advancement;
   private boolean playedSound;

   public AdvancementToast(Advancement p_i47490_1_) {
      this.advancement = p_i47490_1_;
   }

   public IToast.Visibility func_193653_a(ToastGui p_193653_1_, long p_193653_2_) {
      p_193653_1_.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo displayinfo = this.advancement.getDisplay();
      p_193653_1_.blit(0, 0, 0, 0, 160, 32);
      if (displayinfo != null) {
         List<String> list = p_193653_1_.getMinecraft().font.func_78271_c(displayinfo.getTitle().func_150254_d(), 125);
         int i = displayinfo.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (list.size() == 1) {
            p_193653_1_.getMinecraft().font.func_211126_b(I18n.get("advancements.toast." + displayinfo.getFrame().getName()), 30.0F, 7.0F, i | -16777216);
            p_193653_1_.getMinecraft().font.func_211126_b(displayinfo.getTitle().func_150254_d(), 30.0F, 18.0F, -1);
         } else {
            int j = 1500;
            float f = 300.0F;
            if (p_193653_2_ < 1500L) {
               int k = MathHelper.floor(MathHelper.clamp((float)(1500L - p_193653_2_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               p_193653_1_.getMinecraft().font.func_211126_b(I18n.get("advancements.toast." + displayinfo.getFrame().getName()), 30.0F, 11.0F, i | k);
            } else {
               int i1 = MathHelper.floor(MathHelper.clamp((float)(p_193653_2_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int l = 16 - list.size() * 9 / 2;

               for(String s : list) {
                  p_193653_1_.getMinecraft().font.func_211126_b(s, 30.0F, (float)l, 16777215 | i1);
                  l += 9;
               }
            }
         }

         if (!this.playedSound && p_193653_2_ > 0L) {
            this.playedSound = true;
            if (displayinfo.getFrame() == FrameType.CHALLENGE) {
               p_193653_1_.getMinecraft().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         p_193653_1_.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, displayinfo.getIcon(), 8, 8);
         return p_193653_2_ >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      } else {
         return IToast.Visibility.HIDE;
      }
   }
}