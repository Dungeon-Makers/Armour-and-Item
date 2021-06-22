package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SettingsScreen extends Screen {
   protected final Screen lastScreen;
   protected final GameSettings options;

   public SettingsScreen(Screen p_i225930_1_, GameSettings p_i225930_2_, ITextComponent p_i225930_3_) {
      super(p_i225930_3_);
      this.lastScreen = p_i225930_1_;
      this.options = p_i225930_2_;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}