package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private final RealmsScreen mainScreen;
   private final int field_224583_e = 0;
   private final int field_224584_f = 1;
   private final int field_224585_g = 2;
   private final String field_224586_h;
   private final String field_224587_i;
   private final String field_224588_j;
   private final String field_224589_k;
   private int daysLeft;
   private String startDate;
   private Subscription.Type type;
   private final String field_224593_o = "https://aka.ms/ExtendJavaRealms";

   public RealmsSubscriptionInfoScreen(RealmsScreen p_i51749_1_, RealmsServer p_i51749_2_, RealmsScreen p_i51749_3_) {
      this.lastScreen = p_i51749_1_;
      this.serverData = p_i51749_2_;
      this.mainScreen = p_i51749_3_;
      this.field_224586_h = getLocalizedString("mco.configure.world.subscription.title");
      this.field_224587_i = getLocalizedString("mco.configure.world.subscription.start");
      this.field_224588_j = getLocalizedString("mco.configure.world.subscription.timeleft");
      this.field_224589_k = getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
   }

   public void init() {
      this.getSubscription(this.serverData.id);
      Realms.narrateNow(this.field_224586_h, this.field_224587_i, this.startDate, this.field_224588_j, this.daysLeftPresentation(this.daysLeft));
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, RealmsConstants.func_225109_a(6), getLocalizedString("mco.configure.world.subscription.extend")) {
         public void onPress() {
            String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + RealmsSubscriptionInfoScreen.this.serverData.remoteSubscriptionId + "&profileId=" + Realms.getUUID();
            Realms.setClipboard(s);
            RealmsUtil.func_225190_c(s);
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.func_225109_a(12), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSubscriptionInfoScreen.this.lastScreen);
         }
      });
      if (this.serverData.expired) {
         this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.func_225109_a(10), getLocalizedString("mco.configure.world.delete.button")) {
            public void onPress() {
               String s = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
               String s1 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSubscriptionInfoScreen.this, RealmsLongConfirmationScreen.Type.Warning, s, s1, true, 1));
            }
         });
      }

   }

   private void getSubscription(long p_224573_1_) {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         Subscription subscription = realmsclient.subscriptionFor(p_224573_1_);
         this.daysLeft = subscription.daysLeft;
         this.startDate = this.localPresentation(subscription.startDate);
         this.type = subscription.type;
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't get subscription");
         Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
      } catch (IOException var6) {
         LOGGER.error("Couldn't parse response subscribing");
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 1 && p_confirmResult_1_) {
         (new Thread("Realms-delete-realm") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                  RealmsSubscriptionInfoScreen.LOGGER.error(realmsserviceexception);
               } catch (IOException ioexception) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                  ioexception.printStackTrace();
               }

               Realms.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
            }
         }).start();
      }

      Realms.setScreen(this);
   }

   private String localPresentation(long p_224574_1_) {
      Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
      calendar.setTimeInMillis(p_224574_1_);
      return DateFormat.getDateTimeInstance().format(calendar.getTime());
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      int i = this.width() / 2 - 100;
      this.drawCenteredString(this.field_224586_h, this.width() / 2, 17, 16777215);
      this.drawString(this.field_224587_i, i, RealmsConstants.func_225109_a(0), 10526880);
      this.drawString(this.startDate, i, RealmsConstants.func_225109_a(1), 16777215);
      if (this.type == Subscription.Type.NORMAL) {
         this.drawString(this.field_224588_j, i, RealmsConstants.func_225109_a(3), 10526880);
      } else if (this.type == Subscription.Type.RECURRING) {
         this.drawString(this.field_224589_k, i, RealmsConstants.func_225109_a(3), 10526880);
      }

      this.drawString(this.daysLeftPresentation(this.daysLeft), i, RealmsConstants.func_225109_a(4), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String daysLeftPresentation(int p_224576_1_) {
      if (p_224576_1_ == -1 && this.serverData.expired) {
         return getLocalizedString("mco.configure.world.subscription.expired");
      } else if (p_224576_1_ <= 1) {
         return getLocalizedString("mco.configure.world.subscription.less_than_a_day");
      } else {
         int i = p_224576_1_ / 30;
         int j = p_224576_1_ % 30;
         StringBuilder stringbuilder = new StringBuilder();
         if (i > 0) {
            stringbuilder.append(i).append(" ");
            if (i == 1) {
               stringbuilder.append(getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
            } else {
               stringbuilder.append(getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
            }
         }

         if (j > 0) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(j).append(" ");
            if (j == 1) {
               stringbuilder.append(getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
            } else {
               stringbuilder.append(getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
            }
         }

         return stringbuilder.toString();
      }
   }
}