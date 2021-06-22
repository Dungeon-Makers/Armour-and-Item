package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmBackupScreen extends Screen {
   private final Screen lastScreen;
   protected final ConfirmBackupScreen.ICallback listener;
   private final ITextComponent description;
   private final boolean promptForCacheErase;
   private final List<String> field_212112_u = Lists.newArrayList();
   private final String field_212995_f;
   private final String field_212114_g;
   private final String field_212115_h;
   private final String field_212116_i;
   private CheckboxButton eraseCache;

   public ConfirmBackupScreen(Screen p_i51122_1_, ConfirmBackupScreen.ICallback p_i51122_2_, ITextComponent p_i51122_3_, ITextComponent p_i51122_4_, boolean p_i51122_5_) {
      super(p_i51122_3_);
      this.lastScreen = p_i51122_1_;
      this.listener = p_i51122_2_;
      this.description = p_i51122_4_;
      this.promptForCacheErase = p_i51122_5_;
      this.field_212995_f = I18n.get("selectWorld.backupEraseCache");
      this.field_212114_g = I18n.get("selectWorld.backupJoinConfirmButton");
      this.field_212115_h = I18n.get("selectWorld.backupJoinSkipButton");
      this.field_212116_i = I18n.get("gui.cancel");
   }

   protected void init() {
      super.init();
      this.field_212112_u.clear();
      this.field_212112_u.addAll(this.font.func_78271_c(this.description.func_150254_d(), this.width - 50));
      int i = (this.field_212112_u.size() + 1) * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + i, 150, 20, this.field_212114_g, (p_212993_1_) -> {
         this.listener.proceed(true, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.field_212115_h, (p_212992_1_) -> {
         this.listener.proceed(false, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 80, 124 + i, 150, 20, this.field_212116_i, (p_212991_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.eraseCache = new CheckboxButton(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.field_212995_f, false);
      if (this.promptForCacheErase) {
         this.addButton(this.eraseCache);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 50, 16777215);
      int i = 70;

      for(String s : this.field_212112_u) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ICallback {
      void proceed(boolean p_proceed_1_, boolean p_proceed_2_);
   }
}