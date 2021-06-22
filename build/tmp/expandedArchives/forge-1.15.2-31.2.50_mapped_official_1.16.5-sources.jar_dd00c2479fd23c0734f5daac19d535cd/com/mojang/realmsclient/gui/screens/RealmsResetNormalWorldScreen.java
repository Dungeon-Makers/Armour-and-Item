package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private final RealmsResetWorldScreen lastScreen;
   private RealmsLabel titleLabel;
   private RealmsEditBox seedEdit;
   private Boolean generateStructures = true;
   private Integer levelTypeIndex = 0;
   String[] field_224353_a;
   private final int field_224359_g = 0;
   private final int field_224360_h = 1;
   private final int field_224361_i = 4;
   private RealmsButton field_224362_j;
   private RealmsButton field_224363_k;
   private RealmsButton field_224364_l;
   private String buttonTitle = getLocalizedString("mco.backup.button.reset");

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i51758_1_) {
      this.lastScreen = p_i51758_1_;
   }

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i51759_1_, String p_i51759_2_) {
      this(p_i51759_1_);
      this.buttonTitle = p_i51759_2_;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.field_224353_a = new String[]{getLocalizedString("generator.default"), getLocalizedString("generator.flat"), getLocalizedString("generator.largeBiomes"), getLocalizedString("generator.amplified")};
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 8, RealmsConstants.func_225109_a(12), 97, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsResetNormalWorldScreen.this.lastScreen);
         }
      });
      this.buttonsAdd(this.field_224362_j = new RealmsButton(1, this.width() / 2 - 102, RealmsConstants.func_225109_a(12), 97, 20, this.buttonTitle) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.func_224350_a();
         }
      });
      this.seedEdit = this.newEditBox(4, this.width() / 2 - 100, RealmsConstants.func_225109_a(2), 200, 20, getLocalizedString("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.seedEdit.setValue("");
      this.addWidget(this.seedEdit);
      this.focusOn(this.seedEdit);
      this.buttonsAdd(this.field_224363_k = new RealmsButton(2, this.width() / 2 - 102, RealmsConstants.func_225109_a(4), 205, 20, this.func_224347_b()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.levelTypeIndex = (RealmsResetNormalWorldScreen.this.levelTypeIndex + 1) % RealmsResetNormalWorldScreen.this.field_224353_a.length;
            this.setMessage(RealmsResetNormalWorldScreen.this.func_224347_b());
         }
      });
      this.buttonsAdd(this.field_224364_l = new RealmsButton(3, this.width() / 2 - 102, RealmsConstants.func_225109_a(6) - 2, 205, 20, this.func_224351_c()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.generateStructures = !RealmsResetNormalWorldScreen.this.generateStructures;
            this.setMessage(RealmsResetNormalWorldScreen.this.func_224351_c());
         }
      });
      this.titleLabel = new RealmsLabel(getLocalizedString("mco.reset.world.generate"), this.width() / 2, 17, 16777215);
      this.addWidget(this.titleLabel);
      this.narrateLabels();
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

   private void func_224350_a() {
      this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.reset.world.seed"), this.width() / 2 - 100, RealmsConstants.func_225109_a(1), 10526880);
      this.seedEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_224347_b() {
      String s = getLocalizedString("selectWorld.mapType");
      return s + " " + this.field_224353_a[this.levelTypeIndex];
   }

   private String func_224351_c() {
      return getLocalizedString("selectWorld.mapFeatures") + " " + getLocalizedString(this.generateStructures ? "mco.configure.world.on" : "mco.configure.world.off");
   }
}