package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
   private static final RealmsDataFetcher field_224265_a = new RealmsDataFetcher();
   private volatile int numberOfPendingInvites;
   private static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   private static boolean validClient;
   private static boolean hasUnreadNews;
   private static final List<RealmsDataFetcher.Task> field_224271_g = Arrays.asList(RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS);

   public RealmsNotificationsScreen(RealmsScreen p_i51763_1_) {
   }

   public void init() {
      this.checkIfMcoEnabled();
      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void tick() {
      if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !validClient) && !field_224265_a.isStopped()) {
         field_224265_a.stop();
      } else if (validClient && Realms.getRealmsNotificationsEnabled()) {
         field_224265_a.func_225077_a(field_224271_g);
         if (field_224265_a.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = field_224265_a.getPendingInvitesCount();
         }

         if (field_224265_a.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            trialAvailable = field_224265_a.isTrialAvailable();
         }

         if (field_224265_a.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            hasUnreadNews = field_224265_a.hasUnreadNews();
         }

         field_224265_a.markClean();
      }
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (!realmsclient$compatibleversionresponse.equals(RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
                     return;
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  if (realmsserviceexception.httpResultCode != 401) {
                     RealmsNotificationsScreen.checkedMcoAvailability = false;
                  }

                  return;
               } catch (IOException var4) {
                  RealmsNotificationsScreen.checkedMcoAvailability = false;
                  return;
               }

               RealmsNotificationsScreen.validClient = true;
            }
         }).start();
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (validClient) {
         this.func_224262_a(p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   private void func_224262_a(int p_224262_1_, int p_224262_2_) {
      int i = this.numberOfPendingInvites;
      int j = 24;
      int k = this.height() / 4 + 48;
      int l = this.width() / 2 + 80;
      int i1 = k + 48 + 2;
      int j1 = 0;
      if (hasUnreadNews) {
         RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.4F, 0.4F, 0.4F);
         RealmsScreen.blit((int)((double)(l + 2 - j1) * 2.5D), (int)((double)i1 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
         RenderSystem.popMatrix();
         j1 += 14;
      }

      if (i != 0) {
         RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(l - j1, i1 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
         RenderSystem.popMatrix();
         j1 += 16;
      }

      if (trialAvailable) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int k1 = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            k1 = 8;
         }

         RealmsScreen.blit(l + 4 - j1, i1 + 4, 0.0F, (float)k1, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   public void removed() {
      field_224265_a.stop();
   }
}