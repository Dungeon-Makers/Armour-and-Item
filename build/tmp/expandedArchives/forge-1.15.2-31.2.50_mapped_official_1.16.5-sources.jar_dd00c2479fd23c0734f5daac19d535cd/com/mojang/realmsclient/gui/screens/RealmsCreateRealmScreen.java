package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.RealmsTasks;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
   private final RealmsServer server;
   private final RealmsMainScreen lastScreen;
   private RealmsEditBox nameBox;
   private RealmsEditBox descriptionBox;
   private RealmsButton createButton;
   private RealmsLabel createRealmLabel;

   public RealmsCreateRealmScreen(RealmsServer p_i51772_1_, RealmsMainScreen p_i51772_2_) {
      this.server = p_i51772_1_;
      this.lastScreen = p_i51772_2_;
   }

   public void tick() {
      if (this.nameBox != null) {
         this.nameBox.tick();
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.tick();
      }

   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.createButton = new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 17, 97, 20, getLocalizedString("mco.create.world")) {
         public void onPress() {
            RealmsCreateRealmScreen.this.createWorld();
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, this.height() / 4 + 120 + 17, 95, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsCreateRealmScreen.this.lastScreen);
         }
      });
      this.createButton.active(false);
      this.nameBox = this.newEditBox(3, this.width() / 2 - 100, 65, 200, 20, getLocalizedString("mco.configure.world.name"));
      this.addWidget(this.nameBox);
      this.focusOn(this.nameBox);
      this.descriptionBox = this.newEditBox(4, this.width() / 2 - 100, 115, 200, 20, getLocalizedString("mco.configure.world.description"));
      this.addWidget(this.descriptionBox);
      this.createRealmLabel = new RealmsLabel(getLocalizedString("mco.selectServer.create"), this.width() / 2, 11, 16777215);
      this.addWidget(this.createRealmLabel);
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.createButton.active(this.valid());
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.lastScreen);
         return true;
      default:
         this.createButton.active(this.valid());
         return false;
      }
   }

   private void createWorld() {
      if (this.valid()) {
         RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this.lastScreen, this.server, this.lastScreen.newScreen(), getLocalizedString("mco.selectServer.create"), getLocalizedString("mco.create.world.subtitle"), 10526880, getLocalizedString("mco.create.world.skip"));
         realmsresetworldscreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
         RealmsTasks.WorldCreationTask realmstasks$worldcreationtask = new RealmsTasks.WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), realmsresetworldscreen);
         RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$worldcreationtask);
         realmslongrunningmcotaskscreen.func_224233_a();
         Realms.setScreen(realmslongrunningmcotaskscreen);
      }

   }

   private boolean valid() {
      return this.nameBox.getValue() != null && !this.nameBox.getValue().trim().isEmpty();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.createRealmLabel.render(this);
      this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 100, 52, 10526880);
      this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 100, 102, 10526880);
      if (this.nameBox != null) {
         this.nameBox.render(p_render_1_, p_render_2_, p_render_3_);
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.render(p_render_1_, p_render_2_, p_render_3_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}