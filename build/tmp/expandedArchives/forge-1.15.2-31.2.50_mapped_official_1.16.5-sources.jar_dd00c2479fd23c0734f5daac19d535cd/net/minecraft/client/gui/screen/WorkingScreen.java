package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorkingScreen extends Screen implements IProgressUpdate {
   private String field_146591_a = "";
   private String stage = "";
   private int progress;
   private boolean stop;

   public WorkingScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void progressStartNoAbort(ITextComponent p_200210_1_) {
      this.progressStart(p_200210_1_);
   }

   public void progressStart(ITextComponent p_200211_1_) {
      this.field_146591_a = p_200211_1_.func_150254_d();
      this.progressStage(new TranslationTextComponent("progress.working"));
   }

   public void progressStage(ITextComponent p_200209_1_) {
      this.stage = p_200209_1_.func_150254_d();
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int p_73718_1_) {
      this.progress = p_73718_1_;
   }

   public void stop() {
      this.stop = true;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.stop) {
         if (!this.minecraft.isConnectedToRealms()) {
            this.minecraft.setScreen((Screen)null);
         }

      } else {
         this.renderBackground();
         this.drawCenteredString(this.font, this.field_146591_a, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(p_render_1_, p_render_2_, p_render_3_);
      }
   }
}