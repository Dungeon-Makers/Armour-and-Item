package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSliderButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
   private RealmsEditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private int column2X;
   private final RealmsWorldOptions options;
   private final RealmsServer.ServerType worldType;
   private final int activeSlot;
   private int difficulty;
   private int gameMode;
   private Boolean pvp;
   private Boolean spawnNPCs;
   private Boolean spawnAnimals;
   private Boolean spawnMonsters;
   private Integer spawnProtection;
   private Boolean commandBlocks;
   private Boolean forceGameMode;
   private RealmsButton pvpButton;
   private RealmsButton spawnAnimalsButton;
   private RealmsButton spawnMonstersButton;
   private RealmsButton spawnNPCsButton;
   private RealmsSliderButton spawnProtectionButton;
   private RealmsButton commandBlocksButton;
   private RealmsButton forceGameModeButton;
   String[] field_224639_b;
   String[] field_224640_c;
   String[][] field_224641_d;
   private RealmsLabel titleLabel;
   private RealmsLabel warningLabel;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen p_i51750_1_, RealmsWorldOptions p_i51750_2_, RealmsServer.ServerType p_i51750_3_, int p_i51750_4_) {
      this.parent = p_i51750_1_;
      this.options = p_i51750_2_;
      this.worldType = p_i51750_3_;
      this.activeSlot = p_i51750_4_;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void tick() {
      this.nameEdit.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         Realms.setScreen(this.parent);
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width() / 2 - this.columnWidth * 2 / 2;
      this.column2X = this.width() / 2 + 10;
      this.func_224609_a();
      this.difficulty = this.options.difficulty;
      this.gameMode = this.options.gameMode;
      if (this.worldType.equals(RealmsServer.ServerType.NORMAL)) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         String s;
         if (this.worldType.equals(RealmsServer.ServerType.ADVENTUREMAP)) {
            s = getLocalizedString("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType.equals(RealmsServer.ServerType.INSPIRATION)) {
            s = getLocalizedString("mco.configure.world.edit.subscreen.inspiration");
         } else {
            s = getLocalizedString("mco.configure.world.edit.subscreen.experience");
         }

         this.warningLabel = new RealmsLabel(s, this.width() / 2, 26, 16711680);
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

      this.nameEdit = this.newEditBox(11, this.column1X + 2, RealmsConstants.func_225109_a(1), this.columnWidth - 4, 20, getLocalizedString("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
      this.focusOn(this.nameEdit);
      this.buttonsAdd(this.pvpButton = new RealmsButton(4, this.column2X, RealmsConstants.func_225109_a(1), this.columnWidth, 20, this.pvpTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.pvp = !RealmsSlotOptionsScreen.this.pvp;
            this.setMessage(RealmsSlotOptionsScreen.this.pvpTitle());
         }
      });
      this.buttonsAdd(new RealmsButton(3, this.column1X, RealmsConstants.func_225109_a(3), this.columnWidth, 20, this.gameModeTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.gameMode = (RealmsSlotOptionsScreen.this.gameMode + 1) % RealmsSlotOptionsScreen.this.field_224640_c.length;
            this.setMessage(RealmsSlotOptionsScreen.this.gameModeTitle());
         }
      });
      this.buttonsAdd(this.spawnAnimalsButton = new RealmsButton(5, this.column2X, RealmsConstants.func_225109_a(3), this.columnWidth, 20, this.spawnAnimalsTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnAnimals = !RealmsSlotOptionsScreen.this.spawnAnimals;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnAnimalsTitle());
         }
      });
      this.buttonsAdd(new RealmsButton(2, this.column1X, RealmsConstants.func_225109_a(5), this.columnWidth, 20, this.difficultyTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.difficulty = (RealmsSlotOptionsScreen.this.difficulty + 1) % RealmsSlotOptionsScreen.this.field_224639_b.length;
            this.setMessage(RealmsSlotOptionsScreen.this.difficultyTitle());
            if (RealmsSlotOptionsScreen.this.worldType.equals(RealmsServer.ServerType.NORMAL)) {
               RealmsSlotOptionsScreen.this.spawnMonstersButton.active(RealmsSlotOptionsScreen.this.difficulty != 0);
               RealmsSlotOptionsScreen.this.spawnMonstersButton.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
            }

         }
      });
      this.buttonsAdd(this.spawnMonstersButton = new RealmsButton(6, this.column2X, RealmsConstants.func_225109_a(5), this.columnWidth, 20, this.spawnMonstersTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnMonsters = !RealmsSlotOptionsScreen.this.spawnMonsters;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
         }
      });
      this.buttonsAdd(this.spawnProtectionButton = new RealmsSlotOptionsScreen.SettingsSlider(8, this.column1X, RealmsConstants.func_225109_a(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      this.buttonsAdd(this.spawnNPCsButton = new RealmsButton(7, this.column2X, RealmsConstants.func_225109_a(7), this.columnWidth, 20, this.spawnNPCsTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnNPCs = !RealmsSlotOptionsScreen.this.spawnNPCs;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnNPCsTitle());
         }
      });
      this.buttonsAdd(this.forceGameModeButton = new RealmsButton(10, this.column1X, RealmsConstants.func_225109_a(9), this.columnWidth, 20, this.forceGameModeTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.forceGameMode = !RealmsSlotOptionsScreen.this.forceGameMode;
            this.setMessage(RealmsSlotOptionsScreen.this.forceGameModeTitle());
         }
      });
      this.buttonsAdd(this.commandBlocksButton = new RealmsButton(9, this.column2X, RealmsConstants.func_225109_a(9), this.columnWidth, 20, this.commandBlocksTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.commandBlocks = !RealmsSlotOptionsScreen.this.commandBlocks;
            this.setMessage(RealmsSlotOptionsScreen.this.commandBlocksTitle());
         }
      });
      if (!this.worldType.equals(RealmsServer.ServerType.NORMAL)) {
         this.pvpButton.active(false);
         this.spawnAnimalsButton.active(false);
         this.spawnNPCsButton.active(false);
         this.spawnMonstersButton.active(false);
         this.spawnProtectionButton.active(false);
         this.commandBlocksButton.active(false);
         this.spawnProtectionButton.active(false);
         this.forceGameModeButton.active(false);
      }

      if (this.difficulty == 0) {
         this.spawnMonstersButton.active(false);
      }

      this.buttonsAdd(new RealmsButton(1, this.column1X, RealmsConstants.func_225109_a(13), this.columnWidth, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.saveSettings();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.column2X, RealmsConstants.func_225109_a(13), this.columnWidth, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSlotOptionsScreen.this.parent);
         }
      });
      this.addWidget(this.nameEdit);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.buttons.options"), this.width() / 2, 17, 16777215));
      if (this.warningLabel != null) {
         this.addWidget(this.warningLabel);
      }

      this.narrateLabels();
   }

   private void func_224609_a() {
      this.field_224639_b = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
      this.field_224640_c = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};
      this.field_224641_d = new String[][]{{getLocalizedString("selectWorld.gameMode.survival.line1"), getLocalizedString("selectWorld.gameMode.survival.line2")}, {getLocalizedString("selectWorld.gameMode.creative.line1"), getLocalizedString("selectWorld.gameMode.creative.line2")}, {getLocalizedString("selectWorld.gameMode.adventure.line1"), getLocalizedString("selectWorld.gameMode.adventure.line2")}};
   }

   private String difficultyTitle() {
      String s = getLocalizedString("options.difficulty");
      return s + ": " + this.field_224639_b[this.difficulty];
   }

   private String gameModeTitle() {
      String s = getLocalizedString("selectWorld.gameMode");
      return s + ": " + this.field_224640_c[this.gameMode];
   }

   private String pvpTitle() {
      return getLocalizedString("mco.configure.world.pvp") + ": " + getLocalizedString(this.pvp ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnAnimalsTitle() {
      return getLocalizedString("mco.configure.world.spawnAnimals") + ": " + getLocalizedString(this.spawnAnimals ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnMonstersTitle() {
      return this.difficulty == 0 ? getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString("mco.configure.world.off") : getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString(this.spawnMonsters ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnNPCsTitle() {
      return getLocalizedString("mco.configure.world.spawnNPCs") + ": " + getLocalizedString(this.spawnNPCs ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String commandBlocksTitle() {
      return getLocalizedString("mco.configure.world.commandBlocks") + ": " + getLocalizedString(this.commandBlocks ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String forceGameModeTitle() {
      return getLocalizedString("mco.configure.world.forceGameMode") + ": " + getLocalizedString(this.forceGameMode ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      String s = getLocalizedString("mco.configure.world.edit.slot.name");
      this.drawString(s, this.column1X + this.columnWidth / 2 - this.fontWidth(s) / 2, RealmsConstants.func_225109_a(0) - 5, 16777215);
      this.titleLabel.render(this);
      if (this.warningLabel != null) {
         this.warningLabel.render(this);
      }

      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      if (!this.worldType.equals(RealmsServer.ServerType.ADVENTUREMAP) && !this.worldType.equals(RealmsServer.ServerType.EXPERIENCE) && !this.worldType.equals(RealmsServer.ServerType.INSPIRATION)) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficulty, this.gameMode, this.options.forceGameMode, this.getSlotName()));
      }

   }

   @OnlyIn(Dist.CLIENT)
   class SettingsSlider extends RealmsSliderButton {
      public SettingsSlider(int p_i51603_2_, int p_i51603_3_, int p_i51603_4_, int p_i51603_5_, int p_i51603_6_, float p_i51603_7_, float p_i51603_8_) {
         super(p_i51603_2_, p_i51603_3_, p_i51603_4_, p_i51603_5_, p_i51603_6_, (double)p_i51603_7_, (double)p_i51603_8_);
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active()) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)this.toValue(this.getValue());
         }
      }

      public String getMessage() {
         return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection") + ": " + (RealmsSlotOptionsScreen.this.spawnProtection == 0 ? RealmsScreen.getLocalizedString("mco.configure.world.off") : RealmsSlotOptionsScreen.this.spawnProtection);
      }
   }
}