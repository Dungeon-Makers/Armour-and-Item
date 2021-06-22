package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmScreen extends Screen {
   private final ITextComponent title2;
   private final List<String> field_175298_s = Lists.newArrayList();
   protected String yesButton;
   protected String noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(BooleanConsumer p_i51119_1_, ITextComponent p_i51119_2_, ITextComponent p_i51119_3_) {
      this(p_i51119_1_, p_i51119_2_, p_i51119_3_, I18n.get("gui.yes"), I18n.get("gui.no"));
   }

   public ConfirmScreen(BooleanConsumer p_i51120_1_, ITextComponent p_i51120_2_, ITextComponent p_i51120_3_, String p_i51120_4_, String p_i51120_5_) {
      super(p_i51120_2_);
      this.callback = p_i51120_1_;
      this.title2 = p_i51120_3_;
      this.yesButton = p_i51120_4_;
      this.noButton = p_i51120_5_;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.title2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, (p_213002_1_) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, (p_213001_1_) -> {
         this.callback.accept(false);
      }));
      this.field_175298_s.clear();
      this.field_175298_s.addAll(this.font.func_78271_c(this.title2.func_150254_d(), this.width - 50));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.field_175298_s) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void setDelay(int p_146350_1_) {
      this.delayTicker = p_146350_1_;

      for(Widget widget : this.buttons) {
         widget.active = false;
      }

   }

   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }
}