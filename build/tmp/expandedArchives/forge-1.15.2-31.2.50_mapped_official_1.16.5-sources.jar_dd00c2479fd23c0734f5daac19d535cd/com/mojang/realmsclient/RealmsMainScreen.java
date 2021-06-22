package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.KeyCombo;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean overrideConfigure;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   private static List<ResourceLocation> teaserImages = ImmutableList.of();
   private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
   private static int lastScrollYPosition = -1;
   private final RealmsScreen lastScreen;
   private volatile RealmsMainScreen.ServerList realmSelectionList;
   private long selectedServerId = -1L;
   private RealmsButton playButton;
   private RealmsButton backButton;
   private RealmsButton renewButton;
   private RealmsButton configureButton;
   private RealmsButton leaveButton;
   private String toolTip;
   private List<RealmsServer> realmsServers = Lists.newArrayList();
   private volatile int numberOfPendingInvites;
   private int animTick;
   private static volatile boolean hasParentalConsent;
   private static volatile boolean checkedParentalConsent;
   private static volatile boolean checkedClientCompatability;
   private boolean hasFetchedServers;
   private boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   private volatile boolean hasUnreadNews;
   private volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private static RealmsScreen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private List<KeyCombo> keyCombos;
   private int clicks;
   private ReentrantLock connectLock = new ReentrantLock();
   private boolean field_224005_M;
   private RealmsMainScreen.InfoButton showPopupButton;
   private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
   private RealmsMainScreen.NewsButton newsButton;
   private RealmsButton createTrialButton;
   private RealmsButton buyARealmButton;
   private RealmsButton closeButton;

   public RealmsMainScreen(RealmsScreen p_i51792_1_) {
      this.lastScreen = p_i51792_1_;
      this.inviteNarrationLimiter = RateLimiter.create((double)0.016666668F);
   }

   public boolean shouldShowMessageInList() {
      if (this.hasParentalConsent() && this.hasFetchedServers) {
         if (this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer realmsserver : this.realmsServers) {
               if (realmsserver.ownerUUID.equals(Realms.getUUID())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      if (this.hasParentalConsent() && this.hasFetchedServers) {
         if (this.popupOpenedByUser) {
            return true;
         } else {
            return this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty() ? true : this.realmsServers.isEmpty();
         }
      } else {
         return false;
      }
   }

   public void init() {
      this.keyCombos = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         overrideConfigure = !overrideConfigure;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
            this.switchToProd();
         } else {
            this.switchToStage();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
            this.switchToProd();
         } else {
            this.switchToLocal();
         }

      }));
      if (realmsGenericErrorScreen != null) {
         Realms.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if (checkedClientCompatability && !this.hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         this.checkUnreadNews();
         if (!this.dontSetConnectedToRealms) {
            Realms.setConnectedToRealms(false);
         }

         this.setKeyboardHandlerSendRepeatsToGui(true);
         if (this.hasParentalConsent()) {
            REALMS_DATA_FETCHER.forceUpdate();
         }

         this.showingPopup = false;
         this.func_223970_d();
      }
   }

   private boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addButtons() {
      this.buttonsAdd(this.configureButton = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.configure")) {
         public void onPress() {
            RealmsMainScreen.this.configureClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
         }
      });
      this.buttonsAdd(this.playButton = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.play")) {
         public void onPress() {
            RealmsMainScreen.this.func_223914_p();
         }
      });
      this.buttonsAdd(this.backButton = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            if (!RealmsMainScreen.this.justClosedPopup) {
               Realms.setScreen(RealmsMainScreen.this.lastScreen);
            }

         }
      });
      this.buttonsAdd(this.renewButton = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.expiredRenew")) {
         public void onPress() {
            RealmsMainScreen.this.onRenew();
         }
      });
      this.buttonsAdd(this.leaveButton = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.leave")) {
         public void onPress() {
            RealmsMainScreen.this.leaveClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
         }
      });
      this.buttonsAdd(this.pendingInvitesButton = new RealmsMainScreen.PendingInvitesButton());
      this.buttonsAdd(this.newsButton = new RealmsMainScreen.NewsButton());
      this.buttonsAdd(this.showPopupButton = new RealmsMainScreen.InfoButton());
      this.buttonsAdd(this.closeButton = new RealmsMainScreen.CloseButton());
      this.buttonsAdd(this.createTrialButton = new RealmsButton(6, this.width() / 2 + 52, this.popupY0() + 137 - 20, 98, 20, getLocalizedString("mco.selectServer.trial")) {
         public void onPress() {
            RealmsMainScreen.this.func_223988_r();
         }
      });
      this.buttonsAdd(this.buyARealmButton = new RealmsButton(5, this.width() / 2 + 52, this.popupY0() + 160 - 20, 98, 20, getLocalizedString("mco.selectServer.buy")) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://aka.ms/BuyJavaRealms");
         }
      });
      RealmsServer realmsserver = this.findServer(this.selectedServerId);
      this.updateButtonStates(realmsserver);
   }

   private void updateButtonStates(RealmsServer p_223915_1_) {
      this.playButton.active(this.shouldPlayButtonBeActive(p_223915_1_) && !this.shouldShowPopup());
      this.renewButton.setVisible(this.shouldRenewButtonBeActive(p_223915_1_));
      this.configureButton.setVisible(this.shouldConfigureButtonBeVisible(p_223915_1_));
      this.leaveButton.setVisible(this.shouldLeaveButtonBeVisible(p_223915_1_));
      boolean flag = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
      this.createTrialButton.setVisible(flag);
      this.createTrialButton.active(flag);
      this.buyARealmButton.setVisible(this.shouldShowPopup());
      this.closeButton.setVisible(this.shouldShowPopup() && this.popupOpenedByUser);
      this.renewButton.active(!this.shouldShowPopup());
      this.configureButton.active(!this.shouldShowPopup());
      this.leaveButton.active(!this.shouldShowPopup());
      this.newsButton.active(true);
      this.pendingInvitesButton.active(true);
      this.backButton.active(true);
      this.showPopupButton.active(!this.shouldShowPopup());
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && this.hasParentalConsent() && this.hasFetchedServers;
   }

   private boolean shouldPlayButtonBeActive(RealmsServer p_223897_1_) {
      return p_223897_1_ != null && !p_223897_1_.expired && p_223897_1_.state == RealmsServer.Status.OPEN;
   }

   private boolean shouldRenewButtonBeActive(RealmsServer p_223920_1_) {
      return p_223920_1_ != null && p_223920_1_.expired && this.isSelfOwnedServer(p_223920_1_);
   }

   private boolean shouldConfigureButtonBeVisible(RealmsServer p_223941_1_) {
      return p_223941_1_ != null && this.isSelfOwnedServer(p_223941_1_);
   }

   private boolean shouldLeaveButtonBeVisible(RealmsServer p_223959_1_) {
      return p_223959_1_ != null && !this.isSelfOwnedServer(p_223959_1_);
   }

   public void func_223970_d() {
      if (this.hasParentalConsent() && this.hasFetchedServers) {
         this.addButtons();
      }

      this.realmSelectionList = new RealmsMainScreen.ServerList();
      if (lastScrollYPosition != -1) {
         this.realmSelectionList.scroll(lastScrollYPosition);
      }

      this.addWidget(this.realmSelectionList);
      this.focusOn(this.realmSelectionList);
   }

   public void tick() {
      this.tickButtons();
      this.justClosedPopup = false;
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

      if (this.hasParentalConsent()) {
         REALMS_DATA_FETCHER.init();
         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> list = REALMS_DATA_FETCHER.getServers();
            this.realmSelectionList.clear();
            boolean flag = !this.hasFetchedServers;
            if (flag) {
               this.hasFetchedServers = true;
            }

            if (list != null) {
               boolean flag1 = false;

               for(RealmsServer realmsserver : list) {
                  if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
                     flag1 = true;
                  }
               }

               this.realmsServers = list;
               if (this.shouldShowMessageInList()) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.TrialServerEntry());
               }

               for(RealmsServer realmsserver1 : this.realmsServers) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.ServerEntry(realmsserver1));
               }

               if (!regionsPinged && flag1) {
                  regionsPinged = true;
                  this.pingRegions();
               }
            }

            if (flag) {
               this.addButtons();
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
               Realms.narrateNow(getLocalizedString("mco.configure.world.invite.narration", new Object[]{this.numberOfPendingInvites}));
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean flag2 = REALMS_DATA_FETCHER.isTrialAvailable();
            if (flag2 != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = flag2;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = flag2;
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsserverplayerlists = REALMS_DATA_FETCHER.getLivestats();

            label87:
            for(RealmsServerPlayerList realmsserverplayerlist : realmsserverplayerlists.servers) {
               Iterator iterator = this.realmsServers.iterator();

               RealmsServer realmsserver2;
               while(true) {
                  if (!iterator.hasNext()) {
                     continue label87;
                  }

                  realmsserver2 = (RealmsServer)iterator.next();
                  if (realmsserver2.id == realmsserverplayerlist.serverId) {
                     break;
                  }
               }

               realmsserver2.updateServerPing(realmsserverplayerlist);
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
            this.newsLink = REALMS_DATA_FETCHER.newsLink();
         }

         REALMS_DATA_FETCHER.markClean();
         if (this.shouldShowPopup()) {
            ++this.carouselTick;
         }

         if (this.showPopupButton != null) {
            this.showPopupButton.setVisible(this.shouldShowPopupButton());
         }

      }
   }

   private void func_223921_a(String p_223921_1_) {
      Realms.setClipboard(p_223921_1_);
      RealmsUtil.func_225190_c(p_223921_1_);
   }

   private void pingRegions() {
      (new Thread(() -> {
         List<RegionPingResult> list = Ping.pingAllRegions();
         RealmsClient realmsclient = RealmsClient.create();
         PingResult pingresult = new PingResult();
         pingresult.pingResults = list;
         pingresult.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            realmsclient.sendPingResults(pingresult);
         } catch (Throwable throwable) {
            LOGGER.warn("Could not send ping result to Realms: ", throwable);
         }

      })).start();
   }

   private List<Long> getOwnedNonExpiredWorldIds() {
      List<Long> list = Lists.newArrayList();

      for(RealmsServer realmsserver : this.realmsServers) {
         if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
            list.add(realmsserver.id);
         }
      }

      return list;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
      this.stopRealmsFetcher();
   }

   private void func_223914_p() {
      RealmsServer realmsserver = this.findServer(this.selectedServerId);
      if (realmsserver != null) {
         this.play(realmsserver, this);
      }
   }

   private void onRenew() {
      RealmsServer realmsserver = this.findServer(this.selectedServerId);
      if (realmsserver != null) {
         String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsserver.remoteSubscriptionId + "&profileId=" + Realms.getUUID() + "&ref=" + (realmsserver.expiredTrial ? "expiredTrial" : "expiredRealm");
         this.func_223921_a(s);
      }
   }

   private void func_223988_r() {
      if (this.trialsAvailable && !this.createdTrial) {
         RealmsUtil.func_225190_c("https://aka.ms/startjavarealmstrial");
         Realms.setScreen(this.lastScreen);
      }
   }

   private void checkClientCompatability() {
      if (!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else if (realmsclient$compatibleversionresponse.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else {
                     RealmsMainScreen.this.checkParentalConsent();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", (Object)realmsserviceexception.toString());
                  if (realmsserviceexception.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.error.invalid.session.title"), RealmsScreen.getLocalizedString("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else {
                     Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
                  }
               } catch (IOException ioexception) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", (Object)ioexception.getMessage());
                  Realms.setScreen(new RealmsGenericErrorScreen(ioexception.getMessage(), RealmsMainScreen.this.lastScreen));
               }
            }
         }).start();
      }

   }

   private void checkUnreadNews() {
   }

   private void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               Boolean obool = realmsclient.mcoEnabled();
               if (obool) {
                  RealmsMainScreen.LOGGER.info("Realms is available for this user");
                  RealmsMainScreen.hasParentalConsent = true;
               } else {
                  RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                  RealmsMainScreen.hasParentalConsent = false;
                  Realms.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
               }

               RealmsMainScreen.checkedParentalConsent = true;
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", (Object)realmsserviceexception.toString());
               Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
            } catch (IOException ioexception) {
               RealmsMainScreen.LOGGER.error("Couldn't connect to realms: ", (Object)ioexception.getMessage());
               Realms.setScreen(new RealmsGenericErrorScreen(ioexception.getMessage(), RealmsMainScreen.this.lastScreen));
            }

         }
      }).start();
   }

   private void switchToStage() {
      if (!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + realmsserviceexception);
               } catch (IOException ioexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + ioexception.getMessage());
               }

            }
         }).start();
      }

   }

   private void switchToLocal() {
      if (!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + realmsserviceexception);
               } catch (IOException ioexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't parse response connecting to Realms: " + ioexception.getMessage());
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      REALMS_DATA_FETCHER.forceUpdate();
   }

   private void stopRealmsFetcher() {
      REALMS_DATA_FETCHER.stop();
   }

   private void configureClicked(RealmsServer p_223966_1_) {
      if (Realms.getUUID().equals(p_223966_1_.ownerUUID) || overrideConfigure) {
         this.saveListScrollPosition();
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.execute(() -> {
            minecraft.setScreen((new RealmsConfigureWorldScreen(this, p_223966_1_.id)).getProxy());
         });
      }

   }

   private void leaveClicked(@Nullable RealmsServer p_223906_1_) {
      if (p_223906_1_ != null && !Realms.getUUID().equals(p_223906_1_.ownerUUID)) {
         this.saveListScrollPosition();
         String s = getLocalizedString("mco.configure.world.leave.question.line1");
         String s1 = getLocalizedString("mco.configure.world.leave.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 4));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = this.realmSelectionList.getScroll();
   }

   private RealmsServer findServer(long p_223967_1_) {
      for(RealmsServer realmsserver : this.realmsServers) {
         if (realmsserver.id == p_223967_1_) {
            return realmsserver;
         }
      }

      return null;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 4) {
         if (p_confirmResult_1_) {
            (new Thread("Realms-leave-server") {
               public void run() {
                  try {
                     RealmsServer realmsserver = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                     if (realmsserver != null) {
                        RealmsClient realmsclient = RealmsClient.create();
                        realmsclient.uninviteMyselfFrom(realmsserver.id);
                        RealmsMainScreen.REALMS_DATA_FETCHER.removeItem(realmsserver);
                        RealmsMainScreen.this.realmsServers.remove(realmsserver);
                        RealmsMainScreen.this.realmSelectionList.children().removeIf((p_230230_1_) -> {
                           return p_230230_1_ instanceof RealmsMainScreen.ServerEntry && ((RealmsMainScreen.ServerEntry)p_230230_1_).serverData.id == RealmsMainScreen.this.selectedServerId;
                        });
                        RealmsMainScreen.this.realmSelectionList.setSelected(-1);
                        RealmsMainScreen.this.updateButtonStates((RealmsServer)null);
                        RealmsMainScreen.this.selectedServerId = -1L;
                        RealmsMainScreen.this.playButton.active(false);
                     }
                  } catch (RealmsServiceException realmsserviceexception) {
                     RealmsMainScreen.LOGGER.error("Couldn't configure world");
                     Realms.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this));
                  }

               }
            }).start();
         }

         Realms.setScreen(this);
      }

   }

   public void removeSelection() {
      this.selectedServerId = -1L;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void onClosePopup() {
      if (this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         Realms.setScreen(this.lastScreen);
      }

   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.keyCombos.forEach((p_227920_1_) -> {
         p_227920_1_.keyPressed(p_charTyped_1_);
      });
      return true;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224005_M = false;
      this.toolTip = null;
      this.renderBackground();
      this.realmSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_223883_a(this.width() / 2 - 50, 7);
      if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
         this.func_223888_E();
      }

      if (RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
         this.func_223964_D();
      }

      if (this.shouldShowPopup()) {
         this.func_223980_b(p_render_1_, p_render_2_);
      } else {
         if (this.showingPopup) {
            this.updateButtonStates((RealmsServer)null);
            if (!this.hasWidget(this.realmSelectionList)) {
               this.addWidget(this.realmSelectionList);
            }

            RealmsServer realmsserver = this.findServer(this.selectedServerId);
            this.playButton.active(this.shouldPlayButtonBeActive(realmsserver));
         }

         this.showingPopup = false;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.toolTip != null) {
         this.func_223922_a(this.toolTip, p_render_1_, p_render_2_);
      }

      if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int k = 8;
         int i = 8;
         int j = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            j = 8;
         }

         RealmsScreen.blit(this.createTrialButton.func_214457_x() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.func_223291_y_() + this.createTrialButton.getHeight() / 2 - 4, 0.0F, (float)j, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223883_a(int p_223883_1_, int p_223883_2_) {
      RealmsScreen.bind("realms:textures/gui/title/realms.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(p_223883_1_ * 2, p_223883_2_ * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      RenderSystem.popMatrix();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.isOutsidePopup(p_mouseClicked_1_, p_mouseClicked_3_) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   private boolean isOutsidePopup(double p_223979_1_, double p_223979_3_) {
      int i = this.popupX0();
      int j = this.popupY0();
      return p_223979_1_ < (double)(i - 5) || p_223979_1_ > (double)(i + 315) || p_223979_3_ < (double)(j - 5) || p_223979_3_ > (double)(j + 171);
   }

   private void func_223980_b(int p_223980_1_, int p_223980_2_) {
      int i = this.popupX0();
      int j = this.popupY0();
      String s = getLocalizedString("mco.selectServer.popup");
      List<String> list = this.fontSplit(s, 100);
      if (!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates((RealmsServer)null);
         if (this.hasWidget(this.realmSelectionList)) {
            this.removeWidget(this.realmSelectionList);
         }

         Realms.narrateNow(s);
      }

      if (this.hasFetchedServers) {
         this.showingPopup = true;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      RealmsScreen.bind("realms:textures/gui/realms/darken.png");
      RenderSystem.pushMatrix();
      int k = 0;
      int l = 32;
      RealmsScreen.blit(0, 32, 0.0F, 0.0F, this.width(), this.height() - 40 - 32, 310, 166);
      RenderSystem.popMatrix();
      RenderSystem.disableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.bind("realms:textures/gui/realms/popup.png");
      RenderSystem.pushMatrix();
      RealmsScreen.blit(i, j, 0.0F, 0.0F, 310, 166, 310, 166);
      RenderSystem.popMatrix();
      if (!teaserImages.isEmpty()) {
         RealmsScreen.bind(teaserImages.get(this.carouselIndex).toString());
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         RenderSystem.popMatrix();
         if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
               this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
               this.hasSwitchedCarouselImage = true;
            }
         } else {
            this.hasSwitchedCarouselImage = false;
         }
      }

      int i1 = 0;

      for(String s1 : list) {
         int j1 = this.width() / 2 + 52;
         ++i1;
         this.drawString(s1, j1, j + 10 * i1 - 3, 8421504, false);
      }

   }

   private int popupX0() {
      return (this.width() - 310) / 2;
   }

   private int popupY0() {
      return this.height() / 2 - 80;
   }

   private void func_223960_a(int p_223960_1_, int p_223960_2_, int p_223960_3_, int p_223960_4_, boolean p_223960_5_, boolean p_223960_6_) {
      int i = this.numberOfPendingInvites;
      boolean flag = this.inPendingInvitationArea((double)p_223960_1_, (double)p_223960_2_);
      boolean flag1 = p_223960_6_ && p_223960_5_;
      if (flag1) {
         float f = 0.25F + (1.0F + RealmsMth.sin((float)this.animTick * 0.5F)) * 0.25F;
         int j = -16777216 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
         j = -16777216 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ - 1, j, j);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ - 1, p_223960_4_ + 18, j, j);
         this.fillGradient(p_223960_3_ + 17, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ + 17, p_223960_3_ + 18, p_223960_4_ + 18, j, j);
      }

      RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      boolean flag3 = p_223960_6_ && p_223960_5_;
      RealmsScreen.blit(p_223960_3_, p_223960_4_ - 6, flag3 ? 16.0F : 0.0F, 0.0F, 15, 25, 31, 25);
      RenderSystem.popMatrix();
      boolean flag4 = p_223960_6_ && i != 0;
      if (flag4) {
         int k = (Math.min(i, 6) - 1) * 8;
         int l = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57F), RealmsMth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223960_3_ + 4, p_223960_4_ + 4 + l, (float)k, flag ? 8.0F : 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

      int j1 = p_223960_1_ + 12;
      boolean flag2 = p_223960_6_ && flag;
      if (flag2) {
         String s = getLocalizedString(i == 0 ? "mco.invites.nopending" : "mco.invites.pending");
         int i1 = this.fontWidth(s);
         this.fillGradient(j1 - 3, p_223960_2_ - 3, j1 + i1 + 3, p_223960_2_ + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(s, j1, p_223960_2_, -1);
      }

   }

   private boolean inPendingInvitationArea(double p_223931_1_, double p_223931_3_) {
      int i = this.width() / 2 + 50;
      int j = this.width() / 2 + 66;
      int k = 11;
      int l = 23;
      if (this.numberOfPendingInvites != 0) {
         i -= 3;
         j += 3;
         k -= 5;
         l += 5;
      }

      return (double)i <= p_223931_1_ && p_223931_1_ <= (double)j && (double)k <= p_223931_3_ && p_223931_3_ <= (double)l;
   }

   public void play(RealmsServer p_223911_1_, RealmsScreen p_223911_2_) {
      if (p_223911_1_ != null) {
         try {
            if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException var4) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.func_223950_b(p_223911_1_, p_223911_2_);
      }

   }

   private void func_223950_b(RealmsServer p_223950_1_, RealmsScreen p_223950_2_) {
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(p_223950_2_, new RealmsTasks.RealmsGetServerDetailsTask(this, p_223950_2_, p_223950_1_, this.connectLock));
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   private boolean isSelfOwnedServer(RealmsServer p_223885_1_) {
      return p_223885_1_.ownerUUID != null && p_223885_1_.ownerUUID.equals(Realms.getUUID());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer p_223991_1_) {
      return p_223991_1_.ownerUUID != null && p_223991_1_.ownerUUID.equals(Realms.getUUID()) && !p_223991_1_.expired;
   }

   private void func_223907_a(int p_223907_1_, int p_223907_2_, int p_223907_3_, int p_223907_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223907_1_, p_223907_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223907_3_ >= p_223907_1_ && p_223907_3_ <= p_223907_1_ + 9 && p_223907_4_ >= p_223907_2_ && p_223907_4_ <= p_223907_2_ + 27 && p_223907_4_ < this.height() - 40 && p_223907_4_ > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void func_223909_a(int p_223909_1_, int p_223909_2_, int p_223909_3_, int p_223909_4_, int p_223909_5_) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      if (this.animTick % 20 < 10) {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      RenderSystem.popMatrix();
      if (p_223909_3_ >= p_223909_1_ && p_223909_3_ <= p_223909_1_ + 9 && p_223909_4_ >= p_223909_2_ && p_223909_4_ <= p_223909_2_ + 27 && p_223909_4_ < this.height() - 40 && p_223909_4_ > 32 && !this.shouldShowPopup()) {
         if (p_223909_5_ <= 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if (p_223909_5_ == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{p_223909_5_});
         }
      }

   }

   private void func_223987_b(int p_223987_1_, int p_223987_2_, int p_223987_3_, int p_223987_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223987_1_, p_223987_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223987_3_ >= p_223987_1_ && p_223987_3_ <= p_223987_1_ + 9 && p_223987_4_ >= p_223987_2_ && p_223987_4_ <= p_223987_2_ + 27 && p_223987_4_ < this.height() - 40 && p_223987_4_ > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void func_223912_c(int p_223912_1_, int p_223912_2_, int p_223912_3_, int p_223912_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223912_1_, p_223912_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223912_3_ >= p_223912_1_ && p_223912_3_ <= p_223912_1_ + 9 && p_223912_4_ >= p_223912_2_ && p_223912_4_ <= p_223912_2_ + 27 && p_223912_4_ < this.height() - 40 && p_223912_4_ > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private void func_223945_d(int p_223945_1_, int p_223945_2_, int p_223945_3_, int p_223945_4_) {
      boolean flag = false;
      if (p_223945_3_ >= p_223945_1_ && p_223945_3_ <= p_223945_1_ + 28 && p_223945_4_ >= p_223945_2_ && p_223945_4_ <= p_223945_2_ + 28 && p_223945_4_ < this.height() - 40 && p_223945_4_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223945_1_, p_223945_2_, flag ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (flag) {
         this.toolTip = getLocalizedString("mco.selectServer.leave");
      }

   }

   private void func_223916_e(int p_223916_1_, int p_223916_2_, int p_223916_3_, int p_223916_4_) {
      boolean flag = false;
      if (p_223916_3_ >= p_223916_1_ && p_223916_3_ <= p_223916_1_ + 28 && p_223916_4_ >= p_223916_2_ && p_223916_4_ <= p_223916_2_ + 28 && p_223916_4_ < this.height() - 40 && p_223916_4_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223916_1_, p_223916_2_, flag ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (flag) {
         this.toolTip = getLocalizedString("mco.selectServer.configure");
      }

   }

   protected void func_223922_a(String p_223922_1_, int p_223922_2_, int p_223922_3_) {
      if (p_223922_1_ != null) {
         int i = 0;
         int j = 0;

         for(String s : p_223922_1_.split("\n")) {
            int k = this.fontWidth(s);
            if (k > j) {
               j = k;
            }
         }

         int l = p_223922_2_ - j - 5;
         int i1 = p_223922_3_;
         if (l < 0) {
            l = p_223922_2_ + 12;
         }

         for(String s1 : p_223922_1_.split("\n")) {
            this.fillGradient(l - 3, i1 - (i == 0 ? 3 : 0) + i, l + j + 3, i1 + 8 + 3 + i, -1073741824, -1073741824);
            this.fontDrawShadow(s1, l, i1 + i, 16777215);
            i += 10;
         }

      }
   }

   private void func_223933_a(int p_223933_1_, int p_223933_2_, int p_223933_3_, int p_223933_4_, boolean p_223933_5_) {
      boolean flag = false;
      if (p_223933_1_ >= p_223933_3_ && p_223933_1_ <= p_223933_3_ + 20 && p_223933_2_ >= p_223933_4_ && p_223933_2_ <= p_223933_4_ + 20) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223933_3_, p_223933_4_, p_223933_5_ ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (flag) {
         this.toolTip = getLocalizedString("mco.selectServer.info");
      }

   }

   private void func_223982_a(int p_223982_1_, int p_223982_2_, boolean p_223982_3_, int p_223982_4_, int p_223982_5_, boolean p_223982_6_, boolean p_223982_7_) {
      boolean flag = false;
      if (p_223982_1_ >= p_223982_4_ && p_223982_1_ <= p_223982_4_ + 20 && p_223982_2_ >= p_223982_5_ && p_223982_2_ <= p_223982_5_ + 20) {
         flag = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
      if (p_223982_7_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
      }

      RenderSystem.pushMatrix();
      boolean flag1 = p_223982_7_ && p_223982_6_;
      RealmsScreen.blit(p_223982_4_, p_223982_5_, flag1 ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (flag && p_223982_7_) {
         this.toolTip = getLocalizedString("mco.news");
      }

      if (p_223982_3_ && p_223982_7_) {
         int i = flag ? 0 : (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57F), RealmsMth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223982_4_ + 10, p_223982_5_ + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223964_D() {
      String s = "LOCAL!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("LOCAL!", 0, 0, 8388479);
      RenderSystem.popMatrix();
   }

   private void func_223888_E() {
      String s = "STAGE!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("STAGE!", 0, 0, -256);
      RenderSystem.popMatrix();
   }

   public RealmsMainScreen newScreen() {
      return new RealmsMainScreen(this.lastScreen);
   }

   public static void updateTeaserImages(IResourceManager p_227932_0_) {
      Collection<ResourceLocation> collection = p_227932_0_.listResources("textures/gui/images", (p_227934_0_) -> {
         return p_227934_0_.endsWith(".png");
      });
      teaserImages = collection.stream().filter((p_227931_0_) -> {
         return p_227931_0_.getNamespace().equals("realms");
      }).collect(ImmutableList.toImmutableList());
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends RealmsButton {
      public CloseButton() {
         super(11, RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(this.func_214457_x(), this.func_223291_y_(), 0.0F, this.getProxy().isHovered() ? 12.0F : 0.0F, 12, 12, 12, 24);
         RenderSystem.popMatrix();
         if (this.getProxy().isMouseOver((double)p_renderButton_1_, (double)p_renderButton_2_)) {
            RealmsMainScreen.this.toolTip = this.getProxy().getMessage();
         }

      }

      public void onPress() {
         RealmsMainScreen.this.onClosePopup();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InfoButton extends RealmsButton {
      public InfoButton() {
         super(10, RealmsMainScreen.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223933_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered());
      }

      public void onPress() {
         RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends RealmsButton {
      public NewsButton() {
         super(9, RealmsMainScreen.this.width() - 62, 6, 20, 20, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString("mco.news"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         if (RealmsMainScreen.this.newsLink != null) {
            RealmsUtil.func_225190_c(RealmsMainScreen.this.newsLink);
            if (RealmsMainScreen.this.hasUnreadNews) {
               RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.readFile();
               realmspersistence$realmspersistencedata.hasUnreadNews = false;
               RealmsMainScreen.this.hasUnreadNews = false;
               RealmsPersistence.writeFile(realmspersistence$realmspersistencedata);
            }

         }
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223982_a(p_renderButton_1_, p_renderButton_2_, RealmsMainScreen.this.hasUnreadNews, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends RealmsButton {
      public PendingInvitesButton() {
         super(8, RealmsMainScreen.this.width() / 2 + 47, 6, 22, 22, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString(RealmsMainScreen.this.numberOfPendingInvites == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         RealmsPendingInvitesScreen realmspendinginvitesscreen = new RealmsPendingInvitesScreen(RealmsMainScreen.this.lastScreen);
         Realms.setScreen(realmspendinginvitesscreen);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223960_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmListEntry {
      final RealmsServer serverData;

      public ServerEntry(RealmsServer p_i51666_2_) {
         this.serverData = p_i51666_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223731_a(this.serverData, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.serverData.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsMainScreen.this.selectedServerId = -1L;
            Realms.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
         } else {
            RealmsMainScreen.this.selectedServerId = this.serverData.id;
         }

         return true;
      }

      private void func_223731_a(RealmsServer p_223731_1_, int p_223731_2_, int p_223731_3_, int p_223731_4_, int p_223731_5_) {
         this.func_223733_b(p_223731_1_, p_223731_2_ + 36, p_223731_3_, p_223731_4_, p_223731_5_);
      }

      private void func_223733_b(RealmsServer p_223733_1_, int p_223733_2_, int p_223733_3_, int p_223733_4_, int p_223733_5_) {
         if (p_223733_1_.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_223733_2_ + 10, p_223733_3_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            RenderSystem.popMatrix();
            float f = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int k2 = -16777216 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
            RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), p_223733_2_ + 10 + 40 + 75, p_223733_3_ + 12, k2);
         } else {
            int i = 225;
            int j = 2;
            if (p_223733_1_.expired) {
               RealmsMainScreen.this.func_223907_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (p_223733_1_.state == RealmsServer.Status.CLOSED) {
               RealmsMainScreen.this.func_223912_c(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(p_223733_1_) && p_223733_1_.daysLeft < 7) {
               RealmsMainScreen.this.func_223909_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_, p_223733_1_.daysLeft);
            } else if (p_223733_1_.state == RealmsServer.Status.OPEN) {
               RealmsMainScreen.this.func_223987_b(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!RealmsMainScreen.this.isSelfOwnedServer(p_223733_1_) && !RealmsMainScreen.overrideConfigure) {
               RealmsMainScreen.this.func_223945_d(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else {
               RealmsMainScreen.this.func_223916_e(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!"0".equals(p_223733_1_.serverPing.nrOfPlayers)) {
               String s = ChatFormatting.GRAY + "" + p_223733_1_.serverPing.nrOfPlayers;
               RealmsMainScreen.this.drawString(s, p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(s), p_223733_3_ + 3, 8421504);
               if (p_223733_4_ >= p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(s) && p_223733_4_ <= p_223733_2_ + 207 && p_223733_5_ >= p_223733_3_ + 1 && p_223733_5_ <= p_223733_3_ + 10 && p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.toolTip = p_223733_1_.serverPing.playerList;
               }
            }

            if (RealmsMainScreen.this.isSelfOwnedServer(p_223733_1_) && p_223733_1_.expired) {
               boolean flag = false;
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RealmsScreen.bind("minecraft:textures/gui/widgets.png");
               RenderSystem.pushMatrix();
               RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               String s2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
               String s3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
               if (p_223733_1_.expiredTrial) {
                  s2 = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
                  s3 = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
               }

               int l = RealmsMainScreen.this.fontWidth(s3) + 17;
               int i1 = 16;
               int j1 = p_223733_2_ + RealmsMainScreen.this.fontWidth(s2) + 8;
               int k1 = p_223733_3_ + 13;
               if (p_223733_4_ >= j1 && p_223733_4_ < j1 + l && p_223733_5_ > k1 && p_223733_5_ <= k1 + 16 & p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  flag = true;
                  RealmsMainScreen.this.field_224005_M = true;
               }

               int l1 = flag ? 2 : 1;
               RealmsScreen.blit(j1, k1, 0.0F, (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1 + l / 2, k1, (float)(200 - l / 2), (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1, k1 + 8, 0.0F, (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RealmsScreen.blit(j1 + l / 2, k1 + 8, (float)(200 - l / 2), (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RenderSystem.popMatrix();
               RenderSystem.disableBlend();
               int i2 = p_223733_3_ + 11 + 5;
               int j2 = flag ? 16777120 : 16777215;
               RealmsMainScreen.this.drawString(s2, p_223733_2_ + 2, i2 + 1, 15553363);
               RealmsMainScreen.this.drawCenteredString(s3, j1 + l / 2, i2 + 1, j2);
            } else {
               if (p_223733_1_.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  int l2 = 13413468;
                  String s1 = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                  int k = RealmsMainScreen.this.fontWidth(s1);
                  RealmsMainScreen.this.drawString(s1, p_223733_2_ + 2, p_223733_3_ + 12, 13413468);
                  RealmsMainScreen.this.drawString(p_223733_1_.getMinigameName(), p_223733_2_ + 2 + k, p_223733_3_ + 12, 8421504);
               } else {
                  RealmsMainScreen.this.drawString(p_223733_1_.getDescription(), p_223733_2_ + 2, p_223733_3_ + 12, 8421504);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(p_223733_1_)) {
                  RealmsMainScreen.this.drawString(p_223733_1_.owner, p_223733_2_ + 2, p_223733_3_ + 12 + 11, 8421504);
               }
            }

            RealmsMainScreen.this.drawString(p_223733_1_.getName(), p_223733_2_ + 2, p_223733_3_ + 1, 16777215);
            RealmsTextureManager.withBoundFace(p_223733_1_.ownerUUID, () -> {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            });
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerList extends RealmsObjectSelectionList<RealmListEntry> {
      public ServerList() {
         super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 40, 36);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.isFocused(this);
      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 32 && p_keyPressed_1_ != 335) {
            return false;
         } else {
            RealmListEntry realmlistentry = this.getSelected();
            return realmlistentry == null ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : realmlistentry.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ == 0 && p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int i = RealmsMainScreen.this.realmSelectionList.getRowLeft();
            int j = this.getScrollbarPosition();
            int k = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int l = k / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.itemClicked(k, l, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
               RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
               this.selectItem(l);
            }

            return true;
         } else {
            return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            RealmsServer realmsserver;
            if (RealmsMainScreen.this.shouldShowMessageInList()) {
               if (p_selectItem_1_ == 0) {
                  Realms.narrateNow(RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2"));
                  realmsserver = null;
               } else {
                  if (p_selectItem_1_ - 1 >= RealmsMainScreen.this.realmsServers.size()) {
                     RealmsMainScreen.this.selectedServerId = -1L;
                     return;
                  }

                  realmsserver = RealmsMainScreen.this.realmsServers.get(p_selectItem_1_ - 1);
               }
            } else {
               if (p_selectItem_1_ >= RealmsMainScreen.this.realmsServers.size()) {
                  RealmsMainScreen.this.selectedServerId = -1L;
                  return;
               }

               realmsserver = RealmsMainScreen.this.realmsServers.get(p_selectItem_1_);
            }

            RealmsMainScreen.this.updateButtonStates(realmsserver);
            if (realmsserver == null) {
               RealmsMainScreen.this.selectedServerId = -1L;
            } else if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
               Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
               RealmsMainScreen.this.selectedServerId = -1L;
            } else {
               RealmsMainScreen.this.selectedServerId = realmsserver.id;
               if (RealmsMainScreen.this.clicks >= 10 && RealmsMainScreen.this.playButton.active()) {
                  RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
               }

               Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", realmsserver.name));
            }
         }
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         if (RealmsMainScreen.this.shouldShowMessageInList()) {
            if (p_itemClicked_2_ == 0) {
               RealmsMainScreen.this.popupOpenedByUser = true;
               return;
            }

            --p_itemClicked_2_;
         }

         if (p_itemClicked_2_ < RealmsMainScreen.this.realmsServers.size()) {
            RealmsServer realmsserver = RealmsMainScreen.this.realmsServers.get(p_itemClicked_2_);
            if (realmsserver != null) {
               if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
                  RealmsMainScreen.this.selectedServerId = -1L;
                  Realms.setScreen(new RealmsCreateRealmScreen(realmsserver, RealmsMainScreen.this));
               } else {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
               }

               if (RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
                  RealmsMainScreen.this.configureClicked(realmsserver);
               } else if (RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
                  RealmsMainScreen.this.leaveClicked(realmsserver);
               } else if (RealmsMainScreen.this.isSelfOwnedServer(realmsserver) && realmsserver.expired && RealmsMainScreen.this.field_224005_M) {
                  RealmsMainScreen.this.onRenew();
               }

            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialServerEntry extends RealmListEntry {
      public TrialServerEntry() {
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223736_a(p_render_1_, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void func_223736_a(int p_223736_1_, int p_223736_2_, int p_223736_3_, int p_223736_4_, int p_223736_5_) {
         int i = p_223736_3_ + 8;
         int j = 0;
         String s = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
         boolean flag = false;
         if (p_223736_2_ <= p_223736_4_ && p_223736_4_ <= RealmsMainScreen.this.realmSelectionList.getScroll() && p_223736_3_ <= p_223736_5_ && p_223736_5_ <= p_223736_3_ + 32) {
            flag = true;
         }

         int k = 8388479;
         if (flag && !RealmsMainScreen.this.shouldShowPopup()) {
            k = 6077788;
         }

         for(String s1 : s.split("\\\\n")) {
            RealmsMainScreen.this.drawCenteredString(s1, RealmsMainScreen.this.width() / 2, i + j, k);
            j += 10;
         }

      }
   }
}