package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends SettingsScreen {
   public KeyBinding selectedKey;
   public long lastKeySelection;
   private KeyBindingList controlList;
   private Button resetButton;

   public ControlsScreen(Screen p_i1027_1_, GameSettings p_i1027_2_) {
      super(p_i1027_1_, p_i1027_2_, new TranslationTextComponent("controls.title"));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.get("options.mouse_settings"), (p_213126_1_) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }));
      this.addButton(AbstractOption.AUTO_JUMP.createButton(this.options, this.width / 2 - 155 + 160, 18, 150));
      this.controlList = new KeyBindingList(this, this.minecraft);
      this.children.add(this.controlList);
      this.resetButton = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.get("controls.resetAll"), (p_213125_1_) -> {
         for(KeyBinding keybinding : this.options.keyMappings) {
            keybinding.setToDefault();
         }

         KeyBinding.resetMapping();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.get("gui.done"), (p_213124_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputMappings.Type.MOUSE.getOrCreate(p_mouseClicked_5_));
         this.selectedKey = null;
         KeyBinding.resetMapping();
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.selectedKey != null) {
         if (p_keyPressed_1_ == 256) {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.UNKNOWN);
            this.options.setKey(this.selectedKey, InputMappings.UNKNOWN);
         } else {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getKey(p_keyPressed_1_, p_keyPressed_2_));
            this.options.setKey(this.selectedKey, InputMappings.getKey(p_keyPressed_1_, p_keyPressed_2_));
         }

         if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey()))
         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         KeyBinding.resetMapping();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.controlList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyBinding keybinding : this.options.keyMappings) {
         if (!keybinding.isDefault()) {
            flag = true;
            break;
         }
      }

      this.resetButton.active = flag;
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
