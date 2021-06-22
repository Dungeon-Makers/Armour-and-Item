package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nullable;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate> implements RealmsServerSlotButton.IHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private String toolTip;
   private final RealmsMainScreen lastScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private int leftX;
   private int rightX;
   private final int field_224420_h = 80;
   private final int field_224421_i = 5;
   private RealmsButton playersButton;
   private RealmsButton settingsButton;
   private RealmsButton subscriptionButton;
   private RealmsButton optionsButton;
   private RealmsButton backupButton;
   private RealmsButton resetWorldButton;
   private RealmsButton switchMinigameButton;
   private boolean stateChanged;
   private int animTick;
   private int clicks;

   public RealmsConfigureWorldScreen(RealmsMainScreen p_i51774_1_, long p_i51774_2_) {
      this.lastScreen = p_i51774_1_;
      this.serverId = p_i51774_2_;
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.leftX = this.width() / 2 - 187;
      this.rightX = this.width() / 2 + 190;
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.playersButton = new RealmsButton(2, this.centerButton(0, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.players")) {
         public void onPress() {
            Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData));
         }
      });
      this.buttonsAdd(this.settingsButton = new RealmsButton(3, this.centerButton(1, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.settings")) {
         public void onPress() {
            Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone()));
         }
      });
      this.buttonsAdd(this.subscriptionButton = new RealmsButton(4, this.centerButton(2, 3), RealmsConstants.func_225109_a(0), 100, 20, getLocalizedString("mco.configure.world.buttons.subscription")) {
         public void onPress() {
            Realms.setScreen(new RealmsSubscriptionInfoScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.lastScreen));
         }
      });

      for(int i = 1; i < 5; ++i) {
         this.addSlotButton(i);
      }

      this.buttonsAdd(this.switchMinigameButton = new RealmsButton(8, this.leftButton(0), RealmsConstants.func_225109_a(13) - 5, 100, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsConfigureWorldScreen.this, RealmsServer.ServerType.MINIGAME);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
      this.buttonsAdd(this.optionsButton = new RealmsButton(5, this.leftButton(0), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.options")) {
         public void onPress() {
            Realms.setScreen(new RealmsSlotOptionsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.slots.get(RealmsConfigureWorldScreen.this.serverData.activeSlot).clone(), RealmsConfigureWorldScreen.this.serverData.worldType, RealmsConfigureWorldScreen.this.serverData.activeSlot));
         }
      });
      this.buttonsAdd(this.backupButton = new RealmsButton(6, this.leftButton(1), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.backup")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.serverData.activeSlot));
         }
      });
      this.buttonsAdd(this.resetWorldButton = new RealmsButton(7, this.leftButton(2), RealmsConstants.func_225109_a(13) - 5, 90, 20, getLocalizedString("mco.configure.world.buttons.resetworld")) {
         public void onPress() {
            Realms.setScreen(new RealmsResetWorldScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.getNewScreen()));
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.rightX - 80 + 8, RealmsConstants.func_225109_a(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsConfigureWorldScreen.this.backButtonClicked();
         }
      });
      this.backupButton.active(true);
      if (this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active(false);
         this.settingsButton.active(false);
         this.subscriptionButton.active(false);
      } else {
         this.disableButtons();
         if (this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private void addSlotButton(int p_224402_1_) {
      int i = this.frame(p_224402_1_);
      int j = RealmsConstants.func_225109_a(5) + 5;
      int k = 100 + p_224402_1_;
      RealmsServerSlotButton realmsserverslotbutton = new RealmsServerSlotButton(i, j, 80, 80, () -> {
         return this.serverData;
      }, (p_224391_1_) -> {
         this.toolTip = p_224391_1_;
      }, k, p_224402_1_, this);
      this.getProxy().buttonsAdd(realmsserverslotbutton);
   }

   private int leftButton(int p_224411_1_) {
      return this.leftX + p_224411_1_ * 95;
   }

   private int centerButton(int p_224374_1_, int p_224374_2_) {
      return this.width() / 2 - (p_224374_2_ * 105 - 5) / 2 + p_224374_1_ * 105;
   }

   public void tick() {
      this.tickButtons();
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.toolTip = null;
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.func_225109_a(4), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.serverData == null) {
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
      } else {
         String s = this.serverData.getName();
         int i = this.fontWidth(s);
         int j = this.serverData.state == RealmsServer.Status.CLOSED ? 10526880 : 8388479;
         int k = this.fontWidth(getLocalizedString("mco.configure.world.title"));
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
         this.drawCenteredString(s, this.width() / 2, 24, j);
         int l = Math.min(this.centerButton(2, 3) + 80 - 11, this.width() / 2 + i / 2 + k / 2 + 10);
         this.func_224379_a(l, 7, p_render_1_, p_render_2_);
         if (this.isMinigame()) {
            this.drawString(getLocalizedString("mco.configure.current.minigame") + ": " + this.serverData.getMinigameName(), this.leftX + 80 + 20 + 10, RealmsConstants.func_225109_a(13), 16777215);
         }

         if (this.toolTip != null) {
            this.func_224394_a(this.toolTip, p_render_1_, p_render_2_);
         }

      }
   }

   private int frame(int p_224368_1_) {
      return this.leftX + (p_224368_1_ - 1) * 98;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.lastScreen.removeSelection();
      }

      Realms.setScreen(this.lastScreen);
   }

   private void fetchServerData(long p_224387_1_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();

         try {
            this.serverData = realmsclient.getOwnWorld(p_224387_1_);
            this.disableButtons();
            if (this.isMinigame()) {
               this.func_224375_k();
            } else {
               this.func_224399_i();
            }
         } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get own world");
            Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception.getMessage(), this.lastScreen));
         } catch (IOException var6) {
            LOGGER.error("Couldn't parse response getting own world");
         }

      })).start();
   }

   private void disableButtons() {
      this.playersButton.active(!this.serverData.expired);
      this.settingsButton.active(!this.serverData.expired);
      this.subscriptionButton.active(true);
      this.switchMinigameButton.active(!this.serverData.expired);
      this.optionsButton.active(!this.serverData.expired);
      this.resetWorldButton.active(!this.serverData.expired);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   private void joinRealm(RealmsServer p_224385_1_) {
      if (this.serverData.state == RealmsServer.Status.OPEN) {
         this.lastScreen.play(p_224385_1_, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      } else {
         this.func_224383_a(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
      }

   }

   public void func_224366_a(int p_224366_1_, RealmsServerSlotButton.Action p_224366_2_, boolean p_224366_3_, boolean p_224366_4_) {
      switch(p_224366_2_) {
      case NOTHING:
         break;
      case JOIN:
         this.joinRealm(this.serverData);
         break;
      case SWITCH_SLOT:
         if (p_224366_3_) {
            this.switchToMinigame();
         } else if (p_224366_4_) {
            this.switchToEmptySlot(p_224366_1_, this.serverData);
         } else {
            this.switchToFullSlot(p_224366_1_, this.serverData);
         }
         break;
      default:
         throw new IllegalStateException("Unknown action " + p_224366_2_);
      }

   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
      realmsselectworldtemplatescreen.func_224483_a(getLocalizedString("mco.template.title.minigame"));
      realmsselectworldtemplatescreen.func_224492_b(getLocalizedString("mco.minigame.world.info.line1") + "\\n" + getLocalizedString("mco.minigame.world.info.line2"));
      Realms.setScreen(realmsselectworldtemplatescreen);
   }

   private void switchToFullSlot(int p_224403_1_, RealmsServer p_224403_2_) {
      String s = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String s1 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((p_227973_3_, p_227973_4_) -> {
         if (p_227973_3_) {
            this.func_224406_a(p_224403_2_.id, p_224403_1_);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 9));
   }

   private void switchToEmptySlot(int p_224388_1_, RealmsServer p_224388_2_) {
      String s = getLocalizedString("mco.configure.world.slot.switch.question.line1");
      String s1 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen((p_227970_3_, p_227970_4_) -> {
         if (p_227970_3_) {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, p_224388_2_, this.getNewScreen(), getLocalizedString("mco.configure.world.switch.slot"), getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, getLocalizedString("gui.cancel"));
            realmsresetworldscreen.setSlot(p_224388_1_);
            realmsresetworldscreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
            Realms.setScreen(realmsresetworldscreen);
         } else {
            Realms.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 10));
   }

   protected void func_224394_a(String p_224394_1_, int p_224394_2_, int p_224394_3_) {
      if (p_224394_1_ != null) {
         int i = p_224394_2_ + 12;
         int j = p_224394_3_ - 12;
         int k = this.fontWidth(p_224394_1_);
         if (i + k + 3 > this.rightX) {
            i = i - k - 20;
         }

         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224394_1_, i, j, 16777215);
      }
   }

   private void func_224379_a(int p_224379_1_, int p_224379_2_, int p_224379_3_, int p_224379_4_) {
      if (this.serverData.expired) {
         this.func_224408_b(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
      } else if (this.serverData.state == RealmsServer.Status.CLOSED) {
         this.func_224409_d(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
      } else if (this.serverData.state == RealmsServer.Status.OPEN) {
         if (this.serverData.daysLeft < 7) {
            this.func_224381_a(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_, this.serverData.daysLeft);
         } else {
            this.func_224382_c(p_224379_1_, p_224379_2_, p_224379_3_, p_224379_4_);
         }
      }

   }

   private void func_224408_b(int p_224408_1_, int p_224408_2_, int p_224408_3_, int p_224408_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224408_1_, p_224408_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224408_3_ >= p_224408_1_ && p_224408_3_ <= p_224408_1_ + 9 && p_224408_4_ >= p_224408_2_ && p_224408_4_ <= p_224408_2_ + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void func_224381_a(int p_224381_1_, int p_224381_2_, int p_224381_3_, int p_224381_4_, int p_224381_5_) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      if (this.animTick % 20 < 10) {
         RealmsScreen.blit(p_224381_1_, p_224381_2_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(p_224381_1_, p_224381_2_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      RenderSystem.popMatrix();
      if (p_224381_3_ >= p_224381_1_ && p_224381_3_ <= p_224381_1_ + 9 && p_224381_4_ >= p_224381_2_ && p_224381_4_ <= p_224381_2_ + 27) {
         if (p_224381_5_ <= 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if (p_224381_5_ == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{p_224381_5_});
         }
      }

   }

   private void func_224382_c(int p_224382_1_, int p_224382_2_, int p_224382_3_, int p_224382_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224382_1_, p_224382_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224382_3_ >= p_224382_1_ && p_224382_3_ <= p_224382_1_ + 9 && p_224382_4_ >= p_224382_2_ && p_224382_4_ <= p_224382_2_ + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void func_224409_d(int p_224409_1_, int p_224409_2_, int p_224409_3_, int p_224409_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_224409_1_, p_224409_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_224409_3_ >= p_224409_1_ && p_224409_3_ <= p_224409_1_ + 9 && p_224409_4_ >= p_224409_2_ && p_224409_4_ <= p_224409_2_ + 27) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType.equals(RealmsServer.ServerType.MINIGAME);
   }

   private void hideRegularButtons() {
      this.func_224378_a(this.optionsButton);
      this.func_224378_a(this.backupButton);
      this.func_224378_a(this.resetWorldButton);
   }

   private void func_224378_a(RealmsButton p_224378_1_) {
      p_224378_1_.setVisible(false);
      this.removeButton(p_224378_1_);
   }

   private void func_224399_i() {
      this.func_224404_b(this.optionsButton);
      this.func_224404_b(this.backupButton);
      this.func_224404_b(this.resetWorldButton);
   }

   private void func_224404_b(RealmsButton p_224404_1_) {
      p_224404_1_.setVisible(true);
      this.buttonsAdd(p_224404_1_);
   }

   private void hideMinigameButtons() {
      this.func_224378_a(this.switchMinigameButton);
   }

   private void func_224375_k() {
      this.func_224404_b(this.switchMinigameButton);
   }

   public void saveSlotSettings(RealmsWorldOptions p_224386_1_) {
      RealmsWorldOptions realmsworldoptions = this.serverData.slots.get(this.serverData.activeSlot);
      p_224386_1_.templateId = realmsworldoptions.templateId;
      p_224386_1_.templateImage = realmsworldoptions.templateImage;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.updateSlot(this.serverData.id, this.serverData.activeSlot, p_224386_1_);
         this.serverData.slots.put(this.serverData.activeSlot, p_224386_1_);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save slot settings");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      } catch (UnsupportedEncodingException var6) {
         LOGGER.error("Couldn't save slot settings");
      }

      Realms.setScreen(this);
   }

   public void saveSettings(String p_224410_1_, String p_224410_2_) {
      String s = p_224410_2_ != null && !p_224410_2_.trim().isEmpty() ? p_224410_2_ : null;
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.update(this.serverData.id, p_224410_1_, s);
         this.serverData.setName(p_224410_1_);
         this.serverData.setDescription(s);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't save settings");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
         return;
      } catch (UnsupportedEncodingException var7) {
         LOGGER.error("Couldn't save settings");
      }

      Realms.setScreen(this);
   }

   public void func_224383_a(boolean p_224383_1_, RealmsScreen p_224383_2_) {
      RealmsTasks.OpenServerTask realmstasks$openservertask = new RealmsTasks.OpenServerTask(this.serverData, this, this.lastScreen, p_224383_1_);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_224383_2_, realmstasks$openservertask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void func_224405_a(RealmsScreen p_224405_1_) {
      RealmsTasks.CloseServerTask realmstasks$closeservertask = new RealmsTasks.CloseServerTask(this.serverData, this);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_224405_1_, realmstasks$closeservertask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   void callback(WorldTemplate p_223627_1_) {
      if (p_223627_1_ != null) {
         if (WorldTemplate.Type.MINIGAME.equals(p_223627_1_.type)) {
            this.func_224393_b(p_223627_1_);
         }

      }
   }

   private void func_224406_a(long p_224406_1_, int p_224406_3_) {
      RealmsConfigureWorldScreen realmsconfigureworldscreen = this.getNewScreen();
      RealmsTasks.SwitchSlotTask realmstasks$switchslottask = new RealmsTasks.SwitchSlotTask(p_224406_1_, p_224406_3_, (p_227971_1_, p_227971_2_) -> {
         Realms.setScreen(realmsconfigureworldscreen);
      }, 11);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$switchslottask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   private void func_224393_b(WorldTemplate p_224393_1_) {
      RealmsTasks.SwitchMinigameTask realmstasks$switchminigametask = new RealmsTasks.SwitchMinigameTask(this.serverData.id, p_224393_1_, this.getNewScreen());
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$switchminigametask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}