package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorGui extends AbstractGui implements ISpectatorMenuRecipient {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft minecraft;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft p_i45527_1_) {
      this.minecraft = p_i45527_1_;
   }

   public void onHotbarSelected(int p_175260_1_) {
      this.lastSelectionTime = Util.getMillis();
      if (this.menu != null) {
         this.menu.selectSlot(p_175260_1_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long i = this.lastSelectionTime - Util.getMillis() + 5000L;
      return MathHelper.clamp((float)i / 2000.0F, 0.0F, 1.0F);
   }

   public void func_195622_a(float p_195622_1_) {
      if (this.menu != null) {
         float f = this.getHotbarAlpha();
         if (f <= 0.0F) {
            this.menu.exit();
         } else {
            int i = this.minecraft.getWindow().getGuiScaledWidth() / 2;
            int j = this.getBlitOffset();
            this.setBlitOffset(-90);
            int k = MathHelper.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0F * f);
            SpectatorDetails spectatordetails = this.menu.getCurrentPage();
            this.func_214456_a(f, i, k, spectatordetails);
            this.setBlitOffset(j);
         }
      }
   }

   protected void func_214456_a(float p_214456_1_, int p_214456_2_, int p_214456_3_, SpectatorDetails p_214456_4_) {
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, p_214456_1_);
      this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      this.blit(p_214456_2_ - 91, p_214456_3_, 0, 0, 182, 22);
      if (p_214456_4_.getSelectedSlot() >= 0) {
         this.blit(p_214456_2_ - 91 - 1 + p_214456_4_.getSelectedSlot() * 20, p_214456_3_ - 1, 0, 22, 24, 22);
      }

      for(int i = 0; i < 9; ++i) {
         this.func_175266_a(i, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + i * 20 + 2, (float)(p_214456_3_ + 3), p_214456_1_, p_214456_4_.getItem(i));
      }

      RenderSystem.disableRescaleNormal();
      RenderSystem.disableBlend();
   }

   private void func_175266_a(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) {
      this.minecraft.getTextureManager().bind(SPECTATOR_LOCATION);
      if (p_175266_5_ != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(p_175266_4_ * 255.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_175266_2_, p_175266_3_, 0.0F);
         float f = p_175266_5_.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.color4f(f, f, f, p_175266_4_);
         p_175266_5_.func_178663_a(f, i);
         RenderSystem.popMatrix();
         String s = String.valueOf((Object)this.minecraft.options.keyHotbarSlots[p_175266_1_].func_197978_k());
         if (i > 3 && p_175266_5_.isEnabled()) {
            this.minecraft.font.func_175063_a(s, (float)(p_175266_2_ + 19 - 2 - this.minecraft.font.width(s)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (i << 24));
         }
      }

   }

   public void func_195623_a() {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         ISpectatorMenuObject ispectatormenuobject = this.menu.getSelectedItem();
         String s = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().func_150254_d() : ispectatormenuobject.getName().func_150254_d();
         if (s != null) {
            int j = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width(s)) / 2;
            int k = this.minecraft.getWindow().getGuiScaledHeight() - 35;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.minecraft.font.func_175063_a(s, (float)j, (float)k, 16777215 + (i << 24));
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu p_175257_1_) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScrolled(double p_195621_1_) {
      int i;
      for(i = this.menu.getSelectedSlot() + (int)p_195621_1_; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i = (int)((double)i + p_195621_1_)) {
         ;
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Util.getMillis();
      }

   }

   public void onMouseMiddleClick() {
      this.lastSelectionTime = Util.getMillis();
      if (this.isMenuActive()) {
         int i = this.menu.getSelectedSlot();
         if (i != -1) {
            this.menu.selectSlot(i);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}