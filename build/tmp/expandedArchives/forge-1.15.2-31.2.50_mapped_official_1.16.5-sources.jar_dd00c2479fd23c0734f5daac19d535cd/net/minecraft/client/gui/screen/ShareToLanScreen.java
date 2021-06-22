package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShareToLanScreen extends Screen {
   private final Screen lastScreen;
   private Button commandsButton;
   private Button modeButton;
   private String gameModeName = "survival";
   private boolean commands;

   public ShareToLanScreen(Screen p_i1055_1_) {
      super(new TranslationTextComponent("lanServer.title"));
      this.lastScreen = p_i1055_1_;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("lanServer.start"), (p_213082_1_) -> {
         this.minecraft.setScreen((Screen)null);
         int i = HTTPUtil.getAvailablePort();
         ITextComponent itextcomponent;
         if (this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, i)) {
            itextcomponent = new TranslationTextComponent("commands.publish.started", i);
         } else {
            itextcomponent = new TranslationTextComponent("commands.publish.failed");
         }

         this.minecraft.gui.getChat().addMessage(itextcomponent);
         this.minecraft.updateTitle();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (p_213085_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.modeButton = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.gameMode"), (p_213084_1_) -> {
         if ("spectator".equals(this.gameModeName)) {
            this.gameModeName = "creative";
         } else if ("creative".equals(this.gameModeName)) {
            this.gameModeName = "adventure";
         } else if ("adventure".equals(this.gameModeName)) {
            this.gameModeName = "survival";
         } else {
            this.gameModeName = "spectator";
         }

         this.updateSelectionStrings();
      }));
      this.commandsButton = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.allowCommands"), (p_213083_1_) -> {
         this.commands = !this.commands;
         this.updateSelectionStrings();
      }));
      this.updateSelectionStrings();
   }

   private void updateSelectionStrings() {
      this.modeButton.setMessage(I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + this.gameModeName));
      this.commandsButton.setMessage(I18n.get("selectWorld.allowCommands") + ' ' + I18n.get(this.commands ? "options.on" : "options.off"));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.font, I18n.get("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}