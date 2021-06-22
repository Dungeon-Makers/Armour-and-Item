package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;
   private String line1;
   private String line2;

   public RealmsGenericErrorScreen(RealmsServiceException p_i51767_1_, RealmsScreen p_i51767_2_) {
      this.nextScreen = p_i51767_2_;
      this.errorMessage(p_i51767_1_);
   }

   public RealmsGenericErrorScreen(String p_i51768_1_, RealmsScreen p_i51768_2_) {
      this.nextScreen = p_i51768_2_;
      this.func_224225_a(p_i51768_1_);
   }

   public RealmsGenericErrorScreen(String p_i51769_1_, String p_i51769_2_, RealmsScreen p_i51769_3_) {
      this.nextScreen = p_i51769_3_;
      this.func_224227_a(p_i51769_1_, p_i51769_2_);
   }

   private void errorMessage(RealmsServiceException p_224224_1_) {
      if (p_224224_1_.errorCode == -1) {
         this.line1 = "An error occurred (" + p_224224_1_.httpResultCode + "):";
         this.line2 = p_224224_1_.httpResponseContent;
      } else {
         this.line1 = "Realms (" + p_224224_1_.errorCode + "):";
         String s = "mco.errorMessage." + p_224224_1_.errorCode;
         String s1 = getLocalizedString(s);
         this.line2 = s1.equals(s) ? p_224224_1_.errorMsg : s1;
      }

   }

   private void func_224225_a(String p_224225_1_) {
      this.line1 = "An error occurred: ";
      this.line2 = p_224225_1_;
   }

   private void func_224227_a(String p_224227_1_, String p_224227_2_) {
      this.line1 = p_224227_1_;
      this.line2 = p_224227_2_;
   }

   public void init() {
      Realms.narrateNow(this.line1 + ": " + this.line2);
      this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
         public void onPress() {
            Realms.setScreen(RealmsGenericErrorScreen.this.nextScreen);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   @Override
   public boolean keyPressed(int key, int scanCode, int modifiers) {
      if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
         Realms.setScreen(this.nextScreen);
         return true;
      }
      return super.keyPressed(key, scanCode, modifiers);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
      this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
