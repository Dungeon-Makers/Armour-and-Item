package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiplayerWarningScreen extends Screen {
   private final Screen previous;
   private final ITextComponent TITLE = (new TranslationTextComponent("multiplayerWarning.header")).func_211708_a(TextFormatting.BOLD);
   private final ITextComponent CONTENT = new TranslationTextComponent("multiplayerWarning.message");
   private final ITextComponent CHECK = new TranslationTextComponent("multiplayerWarning.check");
   private final ITextComponent field_230160_e_ = new TranslationTextComponent("gui.proceed");
   private final ITextComponent field_230161_f_ = new TranslationTextComponent("gui.back");
   private CheckboxButton stopShowing;
   private final List<String> field_230163_h_ = Lists.newArrayList();

   public MultiplayerWarningScreen(Screen p_i230052_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.previous = p_i230052_1_;
   }

   protected void init() {
      super.init();
      this.field_230163_h_.clear();
      this.field_230163_h_.addAll(this.font.func_78271_c(this.CONTENT.func_150254_d(), this.width - 50));
      int i = (this.field_230163_h_.size() + 1) * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, this.field_230160_e_.func_150254_d(), (p_230165_1_) -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipMultiplayerWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(new MultiplayerScreen(this.previous));
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.field_230161_f_.func_150254_d(), (p_230164_1_) -> {
         this.minecraft.setScreen(this.previous);
      }));
      this.stopShowing = new CheckboxButton(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.CHECK.func_150254_d(), false);
      this.addButton(this.stopShowing);
   }

   public String getNarrationMessage() {
      return this.TITLE.getString() + "\n" + this.CONTENT.getString();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, this.TITLE.func_150254_d(), this.width / 2, 30, 16777215);
      int i = 70;

      for(String s : this.field_230163_h_) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}