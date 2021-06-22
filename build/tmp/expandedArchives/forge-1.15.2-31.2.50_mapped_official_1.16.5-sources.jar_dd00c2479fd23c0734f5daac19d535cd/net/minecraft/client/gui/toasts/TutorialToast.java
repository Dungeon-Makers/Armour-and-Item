package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialToast implements IToast {
   private final TutorialToast.Icons icon;
   private final String title;
   private final String message;
   private IToast.Visibility visibility = IToast.Visibility.SHOW;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(TutorialToast.Icons p_i47487_1_, ITextComponent p_i47487_2_, @Nullable ITextComponent p_i47487_3_, boolean p_i47487_4_) {
      this.icon = p_i47487_1_;
      this.title = p_i47487_2_.func_150254_d();
      this.message = p_i47487_3_ == null ? null : p_i47487_3_.func_150254_d();
      this.progressable = p_i47487_4_;
   }

   public IToast.Visibility func_193653_a(ToastGui p_193653_1_, long p_193653_2_) {
      p_193653_1_.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      p_193653_1_.blit(0, 0, 0, 96, 160, 32);
      this.icon.func_193697_a(p_193653_1_, 6, 6);
      if (this.message == null) {
         p_193653_1_.getMinecraft().font.func_211126_b(this.title, 30.0F, 12.0F, -11534256);
      } else {
         p_193653_1_.getMinecraft().font.func_211126_b(this.title, 30.0F, 7.0F, -11534256);
         p_193653_1_.getMinecraft().font.func_211126_b(this.message, 30.0F, 18.0F, -16777216);
      }

      if (this.progressable) {
         AbstractGui.fill(3, 28, 157, 29, -1);
         float f = (float)MathHelper.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(p_193653_2_ - this.lastProgressTime) / 100.0F));
         int i;
         if (this.progress >= this.lastProgress) {
            i = -16755456;
         } else {
            i = -11206656;
         }

         AbstractGui.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
         this.lastProgress = f;
         this.lastProgressTime = p_193653_2_;
      }

      return this.visibility;
   }

   public void hide() {
      this.visibility = IToast.Visibility.HIDE;
   }

   public void updateProgress(float p_193669_1_) {
      this.progress = p_193669_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1);

      private final int x;
      private final int y;

      private Icons(int p_i47576_3_, int p_i47576_4_) {
         this.x = p_i47576_3_;
         this.y = p_i47576_4_;
      }

      public void func_193697_a(AbstractGui p_193697_1_, int p_193697_2_, int p_193697_3_) {
         RenderSystem.enableBlend();
         p_193697_1_.blit(p_193697_2_, p_193697_3_, 176 + this.x * 20, this.y * 20, 20, 20);
         RenderSystem.enableBlend();
      }
   }
}