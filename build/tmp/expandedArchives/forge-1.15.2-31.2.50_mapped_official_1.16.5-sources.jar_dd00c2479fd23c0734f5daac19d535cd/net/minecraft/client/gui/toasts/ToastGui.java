package net.minecraft.client.gui.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastGui extends AbstractGui {
   private final Minecraft minecraft;
   private final ToastGui.ToastInstance<?>[] visible = new ToastGui.ToastInstance[5];
   private final Deque<IToast> queued = Queues.newArrayDeque();

   public ToastGui(Minecraft p_i47388_1_) {
      this.minecraft = p_i47388_1_;
   }

   public void func_195625_a() {
      if (!this.minecraft.options.hideGui) {
         for(int i = 0; i < this.visible.length; ++i) {
            ToastGui.ToastInstance<?> toastinstance = this.visible[i];
            if (toastinstance != null && toastinstance.render(this.minecraft.getWindow().getGuiScaledWidth(), i)) {
               this.visible[i] = null;
            }

            if (this.visible[i] == null && !this.queued.isEmpty()) {
               this.visible[i] = new ToastGui.ToastInstance(this.queued.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends IToast> T getToast(Class<? extends T> p_192990_1_, Object p_192990_2_) {
      for(ToastGui.ToastInstance<?> toastinstance : this.visible) {
         if (toastinstance != null && p_192990_1_.isAssignableFrom(toastinstance.getToast().getClass()) && toastinstance.getToast().getToken().equals(p_192990_2_)) {
            return (T)toastinstance.getToast();
         }
      }

      for(IToast itoast : this.queued) {
         if (p_192990_1_.isAssignableFrom(itoast.getClass()) && itoast.getToken().equals(p_192990_2_)) {
            return (T)itoast;
         }
      }

      return (T)null;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.queued.clear();
   }

   public void addToast(IToast p_192988_1_) {
      this.queued.add(p_192988_1_);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   @OnlyIn(Dist.CLIENT)
   class ToastInstance<T extends IToast> {
      private final T toast;
      private long animationTime = -1L;
      private long visibleTime = -1L;
      private IToast.Visibility visibility = IToast.Visibility.SHOW;

      private ToastInstance(T p_i47483_2_) {
         this.toast = p_i47483_2_;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long p_193686_1_) {
         float f = MathHelper.clamp((float)(p_193686_1_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
         f = f * f;
         return this.visibility == IToast.Visibility.HIDE ? 1.0F - f : f;
      }

      public boolean render(int p_193684_1_, int p_193684_2_) {
         long i = Util.getMillis();
         if (this.animationTime == -1L) {
            this.animationTime = i;
            this.visibility.playSound(ToastGui.this.minecraft.getSoundManager());
         }

         if (this.visibility == IToast.Visibility.SHOW && i - this.animationTime <= 600L) {
            this.visibleTime = i;
         }

         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_193684_1_ - 160.0F * this.getVisibility(i), (float)(p_193684_2_ * 32), (float)(800 + p_193684_2_));
         IToast.Visibility itoast$visibility = this.toast.func_193653_a(ToastGui.this, i - this.visibleTime);
         RenderSystem.popMatrix();
         if (itoast$visibility != this.visibility) {
            this.animationTime = i - (long)((int)((1.0F - this.getVisibility(i)) * 600.0F));
            this.visibility = itoast$visibility;
            this.visibility.playSound(ToastGui.this.minecraft.getSoundManager());
         }

         return this.visibility == IToast.Visibility.HIDE && i - this.animationTime > 600L;
      }
   }
}