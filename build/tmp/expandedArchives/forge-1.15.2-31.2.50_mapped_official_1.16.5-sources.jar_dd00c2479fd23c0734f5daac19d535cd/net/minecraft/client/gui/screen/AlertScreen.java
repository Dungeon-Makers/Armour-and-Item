package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlertScreen extends Screen {
   private final Runnable callback;
   protected final ITextComponent text;
   private final List<String> field_201553_i = Lists.newArrayList();
   protected final String okButton;
   private int delayTicker;

   public AlertScreen(Runnable p_i48623_1_, ITextComponent p_i48623_2_, ITextComponent p_i48623_3_) {
      this(p_i48623_1_, p_i48623_2_, p_i48623_3_, "gui.back");
   }

   public AlertScreen(Runnable p_i49786_1_, ITextComponent p_i49786_2_, ITextComponent p_i49786_3_, String p_i49786_4_) {
      super(p_i49786_2_);
      this.callback = p_i49786_1_;
      this.text = p_i49786_3_;
      this.okButton = I18n.get(p_i49786_4_);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, (p_212983_1_) -> {
         this.callback.run();
      }));
      this.field_201553_i.clear();
      this.field_201553_i.addAll(this.font.func_78271_c(this.text.func_150254_d(), this.width - 50));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.field_201553_i) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }
}