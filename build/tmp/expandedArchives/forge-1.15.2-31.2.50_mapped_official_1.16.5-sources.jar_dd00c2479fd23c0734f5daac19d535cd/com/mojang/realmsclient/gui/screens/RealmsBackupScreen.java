package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static int lastScrollPosition = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   private List<Backup> backups = Collections.emptyList();
   private String toolTip;
   private RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
   private int selectedBackup = -1;
   private final int slotId;
   private RealmsButton downloadButton;
   private RealmsButton restoreButton;
   private RealmsButton changesButton;
   private Boolean noBackups = false;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;

   public RealmsBackupScreen(RealmsConfigureWorldScreen p_i51777_1_, RealmsServer p_i51777_2_, int p_i51777_3_) {
      this.lastScreen = p_i51777_1_;
      this.serverData = p_i51777_2_;
      this.slotId = p_i51777_3_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
      if (lastScrollPosition != -1) {
         this.backupObjectSelectionList.scroll(lastScrollPosition);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               List<Backup> list = realmsclient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               Realms.execute(() -> {
                  RealmsBackupScreen.this.backups = list;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  RealmsBackupScreen.this.backupObjectSelectionList.clear();

                  for(Backup backup : RealmsBackupScreen.this.backups) {
                     RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
                  }

                  RealmsBackupScreen.this.generateChangeList();
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.func_224098_c();
   }

   private void generateChangeList() {
      if (this.backups.size() > 1) {
         for(int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup1 = this.backups.get(i + 1);
            if (!backup.metadata.isEmpty() && !backup1.metadata.isEmpty()) {
               for(String s : backup.metadata.keySet()) {
                  if (!s.contains("Uploaded") && backup1.metadata.containsKey(s)) {
                     if (!backup.metadata.get(s).equals(backup1.metadata.get(s))) {
                        this.addToChangeList(backup, s);
                     }
                  } else {
                     this.addToChangeList(backup, s);
                  }
               }
            }
         }

      }
   }

   private void addToChangeList(Backup p_224103_1_, String p_224103_2_) {
      if (p_224103_2_.contains("Uploaded")) {
         String s = DateFormat.getDateTimeInstance(3, 3).format(p_224103_1_.lastModifiedDate);
         p_224103_1_.changeList.put(p_224103_2_, s);
         p_224103_1_.setUploadedVersion(true);
      } else {
         p_224103_1_.changeList.put(p_224103_2_, p_224103_1_.metadata.get(p_224103_2_));
      }

   }

   private void func_224098_c() {
      this.buttonsAdd(this.downloadButton = new RealmsButton(2, this.width() - 135, RealmsConstants.func_225109_a(1), 120, 20, getLocalizedString("mco.backup.button.download")) {
         public void onPress() {
            RealmsBackupScreen.this.downloadClicked();
         }
      });
      this.buttonsAdd(this.restoreButton = new RealmsButton(3, this.width() - 135, RealmsConstants.func_225109_a(3), 120, 20, getLocalizedString("mco.backup.button.restore")) {
         public void onPress() {
            RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.selectedBackup);
         }
      });
      this.buttonsAdd(this.changesButton = new RealmsButton(4, this.width() - 135, RealmsConstants.func_225109_a(5), 120, 20, getLocalizedString("mco.backup.changes.tooltip")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.backups.get(RealmsBackupScreen.this.selectedBackup)));
            RealmsBackupScreen.this.selectedBackup = -1;
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsBackupScreen.this.lastScreen);
         }
      });
      this.addWidget(this.backupObjectSelectionList);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 16777215));
      this.focusOn(this.backupObjectSelectionList);
      this.updateButtonStates();
      this.narrateLabels();
   }

   private void updateButtonStates() {
      this.restoreButton.setVisible(this.shouldRestoreButtonBeVisible());
      this.changesButton.setVisible(this.shouldChangesButtonBeVisible());
   }

   private boolean shouldChangesButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !(this.backups.get(this.selectedBackup)).changeList.isEmpty();
      }
   }

   private boolean shouldRestoreButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !this.serverData.expired;
      }
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void restoreClicked(int p_224104_1_) {
      if (p_224104_1_ >= 0 && p_224104_1_ < this.backups.size() && !this.serverData.expired) {
         this.selectedBackup = p_224104_1_;
         Date date = (this.backups.get(p_224104_1_)).lastModifiedDate;
         String s = DateFormat.getDateTimeInstance(3, 3).format(date);
         String s1 = RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
         String s2 = getLocalizedString("mco.configure.world.restore.question.line1", new Object[]{s, s1});
         String s3 = getLocalizedString("mco.configure.world.restore.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, s2, s3, true, 1));
      }

   }

   private void downloadClicked() {
      String s = getLocalizedString("mco.configure.world.restore.download.question.line1");
      String s1 = getLocalizedString("mco.configure.world.restore.download.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, s, s1, true, 2));
   }

   private void downloadWorldData() {
      RealmsTasks.DownloadTask realmstasks$downloadtask = new RealmsTasks.DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), realmstasks$downloadtask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && p_confirmResult_2_ == 1) {
         this.restore();
      } else if (p_confirmResult_2_ == 1) {
         this.selectedBackup = -1;
         Realms.setScreen(this);
      } else if (p_confirmResult_1_ && p_confirmResult_2_ == 2) {
         this.downloadWorldData();
      } else {
         Realms.setScreen(this);
      }

   }

   private void restore() {
      Backup backup = this.backups.get(this.selectedBackup);
      this.selectedBackup = -1;
      RealmsTasks.RestoreTask realmstasks$restoretask = new RealmsTasks.RestoreTask(backup, this.serverData.id, this.lastScreen);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), realmstasks$restoretask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.toolTip = null;
      this.renderBackground();
      this.backupObjectSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 10526880);
      if (this.noBackups) {
         this.drawString(getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 16777215);
      }

      this.downloadButton.active(!this.noBackups);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.toolTip != null) {
         this.func_224090_a(this.toolTip, p_render_1_, p_render_2_);
      }

   }

   protected void func_224090_a(String p_224090_1_, int p_224090_2_, int p_224090_3_) {
      if (p_224090_1_ != null) {
         int i = p_224090_2_ + 12;
         int j = p_224090_3_ - 12;
         int k = this.fontWidth(p_224090_1_);
         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224090_1_, i, j, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionList extends RealmsObjectSelectionList {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
      }

      public void addEntry(Backup p_223867_1_) {
         this.addEntry(RealmsBackupScreen.this.new BackupObjectSelectionListEntry(p_223867_1_));
      }

      public int getRowWidth() {
         return (int)((double)this.width() * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.isFocused(this);
      }

      public int getItemCount() {
         return RealmsBackupScreen.this.backups.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
         RealmsBackupScreen.this.renderBackground();
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ != 0) {
            return false;
         } else if (p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int i = this.width() / 2 - 92;
            int j = this.width();
            int k = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll();
            int l = k / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width() - 5;
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         int i = this.width() - 35;
         int j = p_itemClicked_2_ * this.itemHeight() + 36 - this.getScroll();
         int k = i + 10;
         int l = j - 3;
         if (p_itemClicked_3_ >= (double)i && p_itemClicked_3_ <= (double)(i + 9) && p_itemClicked_5_ >= (double)j && p_itemClicked_5_ <= (double)(j + 9)) {
            if (!(RealmsBackupScreen.this.backups.get(p_itemClicked_2_)).changeList.isEmpty()) {
               RealmsBackupScreen.this.selectedBackup = -1;
               RealmsBackupScreen.lastScrollPosition = this.getScroll();
               Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.backups.get(p_itemClicked_2_)));
            }
         } else if (p_itemClicked_3_ >= (double)k && p_itemClicked_3_ < (double)(k + 13) && p_itemClicked_5_ >= (double)l && p_itemClicked_5_ < (double)(l + 15)) {
            RealmsBackupScreen.lastScrollPosition = this.getScroll();
            RealmsBackupScreen.this.restoreClicked(p_itemClicked_2_);
         }

      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", (RealmsBackupScreen.this.backups.get(p_selectItem_1_)).lastModifiedDate.toString()));
         }

         this.selectInviteListItem(p_selectItem_1_);
      }

      public void selectInviteListItem(int p_223866_1_) {
         RealmsBackupScreen.this.selectedBackup = p_223866_1_;
         RealmsBackupScreen.this.updateButtonStates();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionListEntry extends RealmListEntry {
      final Backup field_223742_a;

      public BackupObjectSelectionListEntry(Backup p_i51657_2_) {
         this.field_223742_a = p_i51657_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223740_a(this.field_223742_a, p_render_3_ - 40, p_render_2_, p_render_6_, p_render_7_);
      }

      private void func_223740_a(Backup p_223740_1_, int p_223740_2_, int p_223740_3_, int p_223740_4_, int p_223740_5_) {
         int i = p_223740_1_.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - p_223740_1_.lastModifiedDate.getTime()) + ")", p_223740_2_ + 40, p_223740_3_ + 1, i);
         RealmsBackupScreen.this.drawString(this.getMediumDatePresentation(p_223740_1_.lastModifiedDate), p_223740_2_ + 40, p_223740_3_ + 12, 8421504);
         int j = RealmsBackupScreen.this.width() - 175;
         int k = -3;
         int l = j - 10;
         int i1 = 0;
         if (!RealmsBackupScreen.this.serverData.expired) {
            this.func_223739_a(j, p_223740_3_ + -3, p_223740_4_, p_223740_5_);
         }

         if (!p_223740_1_.changeList.isEmpty()) {
            this.func_223741_b(l, p_223740_3_ + 0, p_223740_4_, p_223740_5_);
         }

      }

      private String getMediumDatePresentation(Date p_223738_1_) {
         return DateFormat.getDateTimeInstance(3, 3).format(p_223738_1_);
      }

      private void func_223739_a(int p_223739_1_, int p_223739_2_, int p_223739_3_, int p_223739_4_) {
         boolean flag = p_223739_3_ >= p_223739_1_ && p_223739_3_ <= p_223739_1_ + 12 && p_223739_4_ >= p_223739_2_ && p_223739_4_ <= p_223739_2_ + 14 && p_223739_4_ < RealmsBackupScreen.this.height() - 15 && p_223739_4_ > 32;
         RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(p_223739_1_ * 2, p_223739_2_ * 2, 0.0F, flag ? 28.0F : 0.0F, 23, 28, 23, 56);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
         }

      }

      private void func_223741_b(int p_223741_1_, int p_223741_2_, int p_223741_3_, int p_223741_4_) {
         boolean flag = p_223741_3_ >= p_223741_1_ && p_223741_3_ <= p_223741_1_ + 8 && p_223741_4_ >= p_223741_2_ && p_223741_4_ <= p_223741_2_ + 8 && p_223741_4_ < RealmsBackupScreen.this.height() - 15 && p_223741_4_ > 32;
         RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(p_223741_1_ * 2, p_223741_2_ * 2, 0.0F, flag ? 15.0F : 0.0F, 15, 15, 15, 30);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
         }

      }
   }
}