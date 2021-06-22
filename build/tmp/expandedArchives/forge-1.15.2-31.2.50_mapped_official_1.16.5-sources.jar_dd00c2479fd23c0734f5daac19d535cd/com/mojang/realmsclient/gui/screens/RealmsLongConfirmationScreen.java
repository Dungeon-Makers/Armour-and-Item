package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
   private final RealmsLongConfirmationScreen.Type type;
   private final String line2;
   private final String line3;
   protected final RealmsConfirmResultListener field_224250_a;
   protected final String field_224251_b;
   protected final String field_224252_c;
   private final String field_224257_h;
   protected final int field_224253_d;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(RealmsConfirmResultListener p_i51765_1_, RealmsLongConfirmationScreen.Type p_i51765_2_, String p_i51765_3_, String p_i51765_4_, boolean p_i51765_5_, int p_i51765_6_) {
      this.field_224250_a = p_i51765_1_;
      this.field_224253_d = p_i51765_6_;
      this.type = p_i51765_2_;
      this.line2 = p_i51765_3_;
      this.line3 = p_i51765_4_;
      this.yesNoQuestion = p_i51765_5_;
      this.field_224251_b = getLocalizedString("gui.yes");
      this.field_224252_c = getLocalizedString("gui.no");
      this.field_224257_h = getLocalizedString("mco.gui.ok");
   }

   public void init() {
      Realms.narrateNow(this.type.text, this.line2, this.line3);
      if (this.yesNoQuestion) {
         this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.func_225109_a(8), 100, 20, this.field_224251_b) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.field_224250_a.confirmResult(true, RealmsLongConfirmationScreen.this.field_224253_d);
            }
         });
         this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.func_225109_a(8), 100, 20, this.field_224252_c) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.field_224250_a.confirmResult(false, RealmsLongConfirmationScreen.this.field_224253_d);
            }
         });
      } else {
         this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, RealmsConstants.func_225109_a(8), 100, 20, this.field_224257_h) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.field_224250_a.confirmResult(true, RealmsLongConfirmationScreen.this.field_224253_d);
            }
         });
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.field_224250_a.confirmResult(false, this.field_224253_d);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.type.text, this.width() / 2, RealmsConstants.func_225109_a(2), this.type.colorCode);
      this.drawCenteredString(this.line2, this.width() / 2, RealmsConstants.func_225109_a(4), 16777215);
      this.drawCenteredString(this.line3, this.width() / 2, RealmsConstants.func_225109_a(6), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      Warning("Warning!", 16711680),
      Info("Info!", 8226750);

      public final int colorCode;
      public final String text;

      private Type(String p_i51697_3_, int p_i51697_4_) {
         this.text = p_i51697_3_;
         this.colorCode = p_i51697_4_;
      }
   }
}