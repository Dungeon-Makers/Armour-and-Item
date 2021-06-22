package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   private RealmsButton uploadButton;
   private final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private List<RealmsLevelSummary> levelList = Lists.newArrayList();
   private int selectedWorld = -1;
   private RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private String field_224556_j;
   private String field_224557_k;
   private final String[] field_224558_l = new String[4];
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private RealmsLabel noWorldsLabel;

   public RealmsSelectFileToUploadScreen(long p_i51754_1_, int p_i51754_3_, RealmsResetWorldScreen p_i51754_4_) {
      this.lastScreen = p_i51754_4_;
      this.worldId = p_i51754_1_;
      this.slotId = p_i51754_3_;
   }

   private void loadLevelList() throws Exception {
      RealmsAnvilLevelStorageSource realmsanvillevelstoragesource = this.getLevelStorageSource();
      this.levelList = realmsanvillevelstoragesource.getLevelList();
      Collections.sort(this.levelList);

      for(RealmsLevelSummary realmslevelsummary : this.levelList) {
         this.worldSelectionList.func_223881_a(realmslevelsummary);
      }

   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception exception) {
         LOGGER.error("Couldn't load level list", (Throwable)exception);
         Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", exception.getMessage(), this.lastScreen));
         return;
      }

      this.field_224556_j = getLocalizedString("selectWorld.world");
      this.field_224557_k = getLocalizedString("selectWorld.conversion");
      this.field_224558_l[Realms.survivalId()] = getLocalizedString("gameMode.survival");
      this.field_224558_l[Realms.creativeId()] = getLocalizedString("gameMode.creative");
      this.field_224558_l[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
      this.field_224558_l[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
      this.addWidget(this.worldSelectionList);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSelectFileToUploadScreen.this.lastScreen);
         }
      });
      this.buttonsAdd(this.uploadButton = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")) {
         public void onPress() {
            RealmsSelectFileToUploadScreen.this.upload();
         }
      });
      this.uploadButton.active(this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size());
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
      this.addWidget(this.subtitleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, RealmsConstants.func_225109_a(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.addWidget(this.noWorldsLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.none"), this.width() / 2, this.height() / 2 - 20, 16777215));
      } else {
         this.noWorldsLabel = null;
      }

      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         RealmsLevelSummary realmslevelsummary = this.levelList.get(this.selectedWorld);
         Realms.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, realmslevelsummary));
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.worldSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.titleLabel.render(this);
      this.subtitleLabel.render(this);
      if (this.noWorldsLabel != null) {
         this.noWorldsLabel.render(this);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void tick() {
      super.tick();
   }

   private String func_224532_a(RealmsLevelSummary p_224532_1_) {
      return this.field_224558_l[p_224532_1_.getGameMode()];
   }

   private String func_224533_b(RealmsLevelSummary p_224533_1_) {
      return this.DATE_FORMAT.format(new Date(p_224533_1_.getLastPlayed()));
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionEntry extends RealmListEntry {
      final RealmsLevelSummary levelSummary;

      public WorldSelectionEntry(RealmsLevelSummary p_i51738_2_) {
         this.levelSummary = p_i51738_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223758_a(this.levelSummary, p_render_1_, p_render_3_, p_render_2_, p_render_5_, Tezzelator.instance, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void func_223758_a(RealmsLevelSummary p_223758_1_, int p_223758_2_, int p_223758_3_, int p_223758_4_, int p_223758_5_, Tezzelator p_223758_6_, int p_223758_7_, int p_223758_8_) {
         String s = p_223758_1_.getLevelName();
         if (s == null || s.isEmpty()) {
            s = RealmsSelectFileToUploadScreen.this.field_224556_j + " " + (p_223758_2_ + 1);
         }

         String s1 = p_223758_1_.getLevelId();
         s1 = s1 + " (" + RealmsSelectFileToUploadScreen.this.func_224533_b(p_223758_1_);
         s1 = s1 + ")";
         String s2 = "";
         if (p_223758_1_.isRequiresConversion()) {
            s2 = RealmsSelectFileToUploadScreen.this.field_224557_k + " " + s2;
         } else {
            s2 = RealmsSelectFileToUploadScreen.this.func_224532_a(p_223758_1_);
            if (p_223758_1_.isHardcore()) {
               s2 = ChatFormatting.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + ChatFormatting.RESET;
            }

            if (p_223758_1_.hasCheats()) {
               s2 = s2 + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
            }
         }

         RealmsSelectFileToUploadScreen.this.drawString(s, p_223758_3_ + 2, p_223758_4_ + 1, 16777215);
         RealmsSelectFileToUploadScreen.this.drawString(s1, p_223758_3_ + 2, p_223758_4_ + 12, 8421504);
         RealmsSelectFileToUploadScreen.this.drawString(s2, p_223758_3_ + 2, p_223758_4_ + 12 + 10, 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionList extends RealmsObjectSelectionList {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width(), RealmsSelectFileToUploadScreen.this.height(), RealmsConstants.func_225109_a(0), RealmsSelectFileToUploadScreen.this.height() - 40, 36);
      }

      public void func_223881_a(RealmsLevelSummary p_223881_1_) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldSelectionEntry(p_223881_1_));
      }

      public int getItemCount() {
         return RealmsSelectFileToUploadScreen.this.levelList.size();
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      public boolean isFocused() {
         return RealmsSelectFileToUploadScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsSelectFileToUploadScreen.this.renderBackground();
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            RealmsLevelSummary realmslevelsummary = RealmsSelectFileToUploadScreen.this.levelList.get(p_selectItem_1_);
            String s = RealmsScreen.getLocalizedString("narrator.select.list.position", p_selectItem_1_ + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
            String s1 = Realms.joinNarrations(Arrays.asList(realmslevelsummary.getLevelName(), RealmsSelectFileToUploadScreen.this.func_224533_b(realmslevelsummary), RealmsSelectFileToUploadScreen.this.func_224532_a(realmslevelsummary), s));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", s1));
         }

         RealmsSelectFileToUploadScreen.this.selectedWorld = p_selectItem_1_;
         RealmsSelectFileToUploadScreen.this.uploadButton.active(RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore());
      }
   }
}