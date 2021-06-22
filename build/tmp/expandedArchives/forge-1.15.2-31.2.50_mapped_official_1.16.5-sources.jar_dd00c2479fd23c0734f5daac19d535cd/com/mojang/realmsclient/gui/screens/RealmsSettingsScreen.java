package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
   private final RealmsConfigureWorldScreen configureWorldScreen;
   private final RealmsServer serverData;
   private final int field_224567_c = 212;
   private RealmsButton doneButton;
   private RealmsEditBox descEdit;
   private RealmsEditBox nameEdit;
   private RealmsLabel titleLabel;

   public RealmsSettingsScreen(RealmsConfigureWorldScreen p_i51751_1_, RealmsServer p_i51751_2_) {
      this.configureWorldScreen = p_i51751_1_;
      this.serverData = p_i51751_2_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.descEdit.tick();
      this.doneButton.active(this.nameEdit.getValue() != null && !this.nameEdit.getValue().trim().isEmpty());
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      int i = this.width() / 2 - 106;
      this.buttonsAdd(this.doneButton = new RealmsButton(1, i - 2, RealmsConstants.func_225109_a(12), 106, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSettingsScreen.this.save();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, RealmsConstants.func_225109_a(12), 106, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSettingsScreen.this.configureWorldScreen);
         }
      });
      this.buttonsAdd(new RealmsButton(5, this.width() / 2 - 53, RealmsConstants.func_225109_a(0), 106, 20, getLocalizedString(this.serverData.state.equals(RealmsServer.Status.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")) {
         public void onPress() {
            if (RealmsSettingsScreen.this.serverData.state.equals(RealmsServer.Status.OPEN)) {
               String s = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
               String s1 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSettingsScreen.this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 5));
            } else {
               RealmsSettingsScreen.this.configureWorldScreen.func_224383_a(false, RealmsSettingsScreen.this);
            }

         }
      });
      this.nameEdit = this.newEditBox(2, i, RealmsConstants.func_225109_a(4), 212, 20, getLocalizedString("mco.configure.world.name"));
      this.nameEdit.setMaxLength(32);
      if (this.serverData.getName() != null) {
         this.nameEdit.setValue(this.serverData.getName());
      }

      this.addWidget(this.nameEdit);
      this.focusOn(this.nameEdit);
      this.descEdit = this.newEditBox(3, i, RealmsConstants.func_225109_a(8), 212, 20, getLocalizedString("mco.configure.world.description"));
      this.descEdit.setMaxLength(32);
      if (this.serverData.getDescription() != null) {
         this.descEdit.setValue(this.serverData.getDescription());
      }

      this.addWidget(this.descEdit);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 16777215));
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      switch(p_confirmResult_2_) {
      case 5:
         if (p_confirmResult_1_) {
            this.configureWorldScreen.func_224405_a(this);
         } else {
            Realms.setScreen(this);
         }
      default:
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.configureWorldScreen);
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, RealmsConstants.func_225109_a(3), 10526880);
      this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, RealmsConstants.func_225109_a(7), 10526880);
      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      this.descEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void save() {
      this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
   }
}