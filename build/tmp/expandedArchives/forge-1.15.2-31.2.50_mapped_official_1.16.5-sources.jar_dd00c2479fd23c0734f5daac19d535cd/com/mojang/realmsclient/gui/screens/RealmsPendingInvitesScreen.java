package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ListButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
public class RealmsPendingInvitesScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private String toolTip;
   private boolean loaded;
   private RealmsPendingInvitesScreen.InvitationList pendingInvitationSelectionList;
   private RealmsLabel titleLabel;
   private int selectedInvite = -1;
   private RealmsButton acceptButton;
   private RealmsButton rejectButton;

   public RealmsPendingInvitesScreen(RealmsScreen p_i51761_1_) {
      this.lastScreen = p_i51761_1_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.InvitationList();
      (new Thread("Realms-pending-invitations-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               List<PendingInvite> list = realmsclient.pendingInvites().pendingInvites;
               List<RealmsPendingInvitesScreen.InvitationEntry> list1 = list.stream().map((p_225146_1_) -> {
                  return RealmsPendingInvitesScreen.this.new InvitationEntry(p_225146_1_);
               }).collect(Collectors.toList());
               Realms.execute(() -> {
                  RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list1);
               });
            } catch (RealmsServiceException var7) {
               RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
            } finally {
               RealmsPendingInvitesScreen.this.loaded = true;
            }

         }
      }).start();
      this.buttonsAdd(this.acceptButton = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.accept")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.accept(RealmsPendingInvitesScreen.this.selectedInvite);
            RealmsPendingInvitesScreen.this.selectedInvite = -1;
            RealmsPendingInvitesScreen.this.updateButtonStates();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, getLocalizedString("gui.done")) {
         public void onPress() {
            Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.lastScreen));
         }
      });
      this.buttonsAdd(this.rejectButton = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.reject")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.reject(RealmsPendingInvitesScreen.this.selectedInvite);
            RealmsPendingInvitesScreen.this.selectedInvite = -1;
            RealmsPendingInvitesScreen.this.updateButtonStates();
         }
      });
      this.titleLabel = new RealmsLabel(getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215);
      this.addWidget(this.titleLabel);
      this.addWidget(this.pendingInvitationSelectionList);
      this.narrateLabels();
      this.updateButtonStates();
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(new RealmsMainScreen(this.lastScreen));
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void updateList(int p_224318_1_) {
      this.pendingInvitationSelectionList.removeAtIndex(p_224318_1_);
   }

   private void reject(final int p_224321_1_) {
      if (p_224321_1_ < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-reject-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.rejectInvitation((RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(p_224321_1_)).pendingInvite.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(p_224321_1_);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
               }

            }
         }).start();
      }

   }

   private void accept(final int p_224329_1_) {
      if (p_224329_1_ < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-accept-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.acceptInvitation((RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(p_224329_1_)).pendingInvite.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(p_224329_1_);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
               }

            }
         }).start();
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.toolTip = null;
      this.renderBackground();
      this.pendingInvitationSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.titleLabel.render(this);
      if (this.toolTip != null) {
         this.func_224322_a(this.toolTip, p_render_1_, p_render_2_);
      }

      if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
         this.drawCenteredString(getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   protected void func_224322_a(String p_224322_1_, int p_224322_2_, int p_224322_3_) {
      if (p_224322_1_ != null) {
         int i = p_224322_2_ + 12;
         int j = p_224322_3_ - 12;
         int k = this.fontWidth(p_224322_1_);
         this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(p_224322_1_, i, j, 16777215);
      }
   }

   private void updateButtonStates() {
      this.acceptButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
      this.rejectButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
   }

   private boolean shouldAcceptAndRejectButtonBeVisible(int p_224316_1_) {
      return p_224316_1_ != -1;
   }

   public static String func_224330_a(PendingInvite p_224330_0_) {
      return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - p_224330_0_.date.getTime());
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationEntry extends RealmListEntry {
      final PendingInvite pendingInvite;
      private final List<ListButton> rowButtons;

      InvitationEntry(PendingInvite p_i51623_2_) {
         this.pendingInvite = p_i51623_2_;
         this.rowButtons = Arrays.asList(new RealmsPendingInvitesScreen.InvitationEntry.AcceptButton(), new RealmsPendingInvitesScreen.InvitationEntry.RejectButton());
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223749_a(this.pendingInvite, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         ListButton.func_225119_a(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, p_mouseClicked_5_, p_mouseClicked_1_, p_mouseClicked_3_);
         return true;
      }

      private void func_223749_a(PendingInvite p_223749_1_, int p_223749_2_, int p_223749_3_, int p_223749_4_, int p_223749_5_) {
         RealmsPendingInvitesScreen.this.drawString(p_223749_1_.worldName, p_223749_2_ + 38, p_223749_3_ + 1, 16777215);
         RealmsPendingInvitesScreen.this.drawString(p_223749_1_.worldOwnerName, p_223749_2_ + 38, p_223749_3_ + 12, 8421504);
         RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.func_224330_a(p_223749_1_), p_223749_2_ + 38, p_223749_3_ + 24, 8421504);
         ListButton.func_225124_a(this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, p_223749_2_, p_223749_3_, p_223749_4_, p_223749_5_);
         RealmsTextureManager.withBoundFace(p_223749_1_.worldOwnerUuid, () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(p_223749_2_, p_223749_3_, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            RealmsScreen.blit(p_223749_2_, p_223749_3_, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
         });
      }

      @OnlyIn(Dist.CLIENT)
      class AcceptButton extends ListButton {
         AcceptButton() {
            super(15, 15, 215, 5);
         }

         protected void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_) {
            RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_225120_1_, p_225120_2_, p_225120_3_ ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
            RenderSystem.popMatrix();
            if (p_225120_3_) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
            }

         }

         public void onClick(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.accept(p_225121_1_);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RejectButton extends ListButton {
         RejectButton() {
            super(15, 15, 235, 5);
         }

         protected void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_) {
            RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_225120_1_, p_225120_2_, p_225120_3_ ? 19.0F : 0.0F, 0.0F, 18, 18, 37, 18);
            RenderSystem.popMatrix();
            if (p_225120_3_) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
            }

         }

         public void onClick(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.reject(p_225121_1_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.InvitationEntry> {
      public InvitationList() {
         super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
      }

      public void removeAtIndex(int p_223872_1_) {
         this.remove(p_223872_1_);
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 260;
      }

      public boolean isFocused() {
         return RealmsPendingInvitesScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsPendingInvitesScreen.this.renderBackground();
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            List<RealmsPendingInvitesScreen.InvitationEntry> list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
            PendingInvite pendinginvite = (list.get(p_selectItem_1_)).pendingInvite;
            String s = RealmsScreen.getLocalizedString("narrator.select.list.position", p_selectItem_1_ + 1, list.size());
            String s1 = Realms.joinNarrations(Arrays.asList(pendinginvite.worldName, pendinginvite.worldOwnerName, RealmsPendingInvitesScreen.func_224330_a(pendinginvite), s));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", s1));
         }

         this.selectInviteListItem(p_selectItem_1_);
      }

      public void selectInviteListItem(int p_223873_1_) {
         RealmsPendingInvitesScreen.this.selectedInvite = p_223873_1_;
         RealmsPendingInvitesScreen.this.updateButtonStates();
      }
   }
}