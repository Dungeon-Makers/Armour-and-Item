package net.minecraft.client.gui.screen;

import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private final ITextComponent reason;
   private List<String> field_146305_g;
   private final Screen parent;
   private int textHeight;

   public DisconnectedScreen(Screen p_i45020_1_, String p_i45020_2_, ITextComponent p_i45020_3_) {
      super(new TranslationTextComponent(p_i45020_2_));
      this.parent = p_i45020_1_;
      this.reason = p_i45020_3_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.field_146305_g = this.font.func_78271_c(this.reason.func_150254_d(), this.width - 50);
      this.textHeight = this.field_146305_g.size() * 9;
      this.addButton(new Button(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20, I18n.get("gui.toMenu"), (p_213033_1_) -> {
         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      int i = this.height / 2 - this.textHeight / 2;
      if (this.field_146305_g != null) {
         for(String s : this.field_146305_g) {
            this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
            i += 9;
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}