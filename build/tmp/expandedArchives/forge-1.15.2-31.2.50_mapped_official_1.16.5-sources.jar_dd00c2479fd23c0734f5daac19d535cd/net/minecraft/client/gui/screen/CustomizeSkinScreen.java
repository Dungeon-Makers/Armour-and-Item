package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomizeSkinScreen extends SettingsScreen {
   public CustomizeSkinScreen(Screen p_i225931_1_, GameSettings p_i225931_2_) {
      super(p_i225931_1_, p_i225931_2_, new TranslationTextComponent("options.skinCustomisation.title"));
   }

   protected void init() {
      int i = 0;

      for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
         this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.func_175358_a(playermodelpart), (p_213080_2_) -> {
            this.options.toggleModelPart(playermodelpart);
            p_213080_2_.setMessage(this.func_175358_a(playermodelpart));
         }));
         ++i;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, AbstractOption.MAIN_HAND, AbstractOption.MAIN_HAND.func_216720_c(this.options), (p_213081_1_) -> {
         AbstractOption.MAIN_HAND.toggle(this.options, 1);
         this.options.save();
         p_213081_1_.setMessage(AbstractOption.MAIN_HAND.func_216720_c(this.options));
         this.options.broadcastOptions();
      }));
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, I18n.get("gui.done"), (p_213079_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_175358_a(PlayerModelPart p_175358_1_) {
      String s;
      if (this.options.getModelParts().contains(p_175358_1_)) {
         s = I18n.get("options.on");
      } else {
         s = I18n.get("options.off");
      }

      return p_175358_1_.getName().func_150254_d() + ": " + s;
   }
}