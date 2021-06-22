package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IngameMenuScreen extends Screen {
   private final boolean showPauseMenu;

   public IngameMenuScreen(boolean p_i51519_1_) {
      super(p_i51519_1_ ? new TranslationTextComponent("menu.game") : new TranslationTextComponent("menu.paused"));
      this.showPauseMenu = p_i51519_1_;
   }

   protected void init() {
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }

   }

   private void createPauseMenu() {
      int i = -16;
      int j = 98;
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.get("menu.returnToGame"), (p_213070_1_) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.get("gui.advancements"), (p_213065_1_) -> {
         this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.get("gui.stats"), (p_213066_1_) -> {
         this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
      }));
      String s = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.get("menu.sendFeedback"), (p_213072_2_) -> {
         this.minecraft.setScreen(new ConfirmOpenLinkScreen((p_213069_2_) -> {
            if (p_213069_2_) {
               Util.getPlatform().openUri(s);
            }

            this.minecraft.setScreen(this);
         }, s, true));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.get("menu.reportBugs"), (p_213063_1_) -> {
         this.minecraft.setScreen(new ConfirmOpenLinkScreen((p_213064_1_) -> {
            if (p_213064_1_) {
               Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/snapshotbugs?ref=game", true));
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.get("menu.options"), (p_213071_1_) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      Button button = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.get("menu.shareToLan"), (p_213068_1_) -> {
         this.minecraft.setScreen(new ShareToLanScreen(this));
      }));
      button.active = this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();
      Button button1 = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.get("menu.returnToMenu"), (p_213067_1_) -> {
         boolean flag = this.minecraft.isLocalServer();
         boolean flag1 = this.minecraft.isConnectedToRealms();
         p_213067_1_.active = false;
         this.minecraft.level.disconnect();
         if (flag) {
            this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
         } else {
            this.minecraft.clearLevel();
         }

         if (flag) {
            this.minecraft.setScreen(new MainMenuScreen());
         } else if (flag1) {
            RealmsBridge realmsbridge = new RealmsBridge();
            realmsbridge.switchToRealms(new MainMenuScreen());
         } else {
            this.minecraft.setScreen(new MultiplayerScreen(new MainMenuScreen()));
         }

      }));
      if (!this.minecraft.isLocalServer()) {
         button1.setMessage(I18n.get("menu.disconnect"));
      }

   }

   public void tick() {
      super.tick();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.showPauseMenu) {
         this.renderBackground();
         this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 40, 16777215);
      } else {
         this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 10, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}