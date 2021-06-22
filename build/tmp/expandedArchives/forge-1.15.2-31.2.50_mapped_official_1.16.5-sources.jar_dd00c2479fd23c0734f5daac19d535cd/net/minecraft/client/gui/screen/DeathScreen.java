package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathScreen extends Screen {
   private int delayTicker;
   private final ITextComponent causeOfDeath;
   private final boolean hardcore;

   public DeathScreen(@Nullable ITextComponent p_i51118_1_, boolean p_i51118_2_) {
      super(new TranslationTextComponent(p_i51118_2_ ? "deathScreen.title.hardcore" : "deathScreen.title"));
      this.causeOfDeath = p_i51118_1_;
      this.hardcore = p_i51118_2_;
   }

   protected void init() {
      this.delayTicker = 0;
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? I18n.get("deathScreen.spectate") : I18n.get("deathScreen.respawn"), (p_213021_1_) -> {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }));
      Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, I18n.get("deathScreen.titleScreen"), (p_213020_1_) -> {
         if (this.hardcore) {
            confirmResult(true);
            this.exitToTitleScreen();
         } else {
            ConfirmScreen confirmscreen = new ConfirmScreen(this::confirmResult, new TranslationTextComponent("deathScreen.quit.confirm"), new StringTextComponent(""), I18n.get("deathScreen.titleScreen"), I18n.get("deathScreen.respawn"));
            this.minecraft.setScreen(confirmscreen);
            confirmscreen.setDelay(20);
         }
      }));
      if (!this.hardcore && this.minecraft.getUser() == null) {
         button.active = false;
      }

      for(Widget widget : this.buttons) {
         widget.active = false;
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void confirmResult(boolean p_213022_1_) {
      if (p_213022_1_) {
         this.exitToTitleScreen();
      } else {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }

   }

   private void exitToTitleScreen() {
      if (this.minecraft.level != null) {
         this.minecraft.level.disconnect();
      }

      this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
      this.minecraft.setScreen(new MainMenuScreen());
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2 / 2, 30, 16777215);
      RenderSystem.popMatrix();
      if (this.causeOfDeath != null) {
         this.drawCenteredString(this.font, this.causeOfDeath.func_150254_d(), this.width / 2, 85, 16777215);
      }

      this.drawCenteredString(this.font, I18n.get("deathScreen.score") + ": " + TextFormatting.YELLOW + this.minecraft.player.getScore(), this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && p_render_2_ > 85 && p_render_2_ < 85 + 9) {
         ITextComponent itextcomponent = this.func_184870_b(p_render_1_);
         if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
            this.renderComponentHoverEffect(itextcomponent, p_render_1_, p_render_2_);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @Nullable
   public ITextComponent func_184870_b(int p_184870_1_) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int i = this.minecraft.font.width(this.causeOfDeath.func_150254_d());
         int j = this.width / 2 - i / 2;
         int k = this.width / 2 + i / 2;
         int l = j;
         if (p_184870_1_ >= j && p_184870_1_ <= k) {
            for(ITextComponent itextcomponent : this.causeOfDeath) {
               l += this.minecraft.font.width(RenderComponentsUtil.func_178909_a(itextcomponent.getContents(), false));
               if (l > p_184870_1_) {
                  return itextcomponent;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.causeOfDeath != null && p_mouseClicked_3_ > 85.0D && p_mouseClicked_3_ < (double)(85 + 9)) {
         ITextComponent itextcomponent = this.func_184870_b((int)p_mouseClicked_1_);
         if (itextcomponent != null && itextcomponent.getStyle().getClickEvent() != null && itextcomponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClicked(itextcomponent);
            return false;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      ++this.delayTicker;
      if (this.delayTicker == 20) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }
}
