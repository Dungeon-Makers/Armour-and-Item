package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerListScreen extends Screen {
   private Button selectButton;
   private final ServerData serverData;
   private TextFieldWidget ipEdit;
   private final BooleanConsumer callback;
   private final Screen lastScreen;

   public ServerListScreen(Screen p_i225926_1_, BooleanConsumer p_i225926_2_, ServerData p_i225926_3_) {
      super(new TranslationTextComponent("selectServer.direct"));
      this.lastScreen = p_i225926_1_;
      this.serverData = p_i225926_3_;
      this.callback = p_i225926_2_;
   }

   public void tick() {
      this.ipEdit.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.getFocused() != this.ipEdit || p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      } else {
         this.onSelect();
         return true;
      }
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.selectButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.get("selectServer.select"), (p_213026_1_) -> {
         this.onSelect();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.get("gui.cancel"), (p_213025_1_) -> {
         this.callback.accept(false);
      }));
      this.ipEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20, I18n.get("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setFocus(true);
      this.ipEdit.setValue(this.minecraft.options.lastMpIp);
      this.ipEdit.setResponder((p_213024_1_) -> {
         this.updateSelectButtonStatus();
      });
      this.children.add(this.ipEdit);
      this.setInitialFocus(this.ipEdit);
      this.updateSelectButtonStatus();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.ipEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.ipEdit.setValue(s);
   }

   private void onSelect() {
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.options.lastMpIp = this.ipEdit.getValue();
      this.minecraft.options.save();
   }

   private void updateSelectButtonStatus() {
      String s = this.ipEdit.getValue();
      this.selectButton.active = !s.isEmpty() && s.split(":").length > 0 && s.indexOf(32) == -1;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
      this.ipEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}