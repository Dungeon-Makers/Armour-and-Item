package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int field_224239_c = 666;
   private final int field_224240_d = 667;
   private final RealmsScreen lastScreen;
   private final LongRunningTask field_224242_f;
   private volatile String title = "";
   private volatile boolean field_224244_h;
   private volatile String errorMessage;
   private volatile boolean aborted;
   private int animTicks;
   private final LongRunningTask task;
   private final int buttonLength = 212;
   public static final String[] SYMBOLS = new String[]{"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

   public RealmsLongRunningMcoTaskScreen(RealmsScreen p_i51764_1_, LongRunningTask p_i51764_2_) {
      this.lastScreen = p_i51764_1_;
      this.task = p_i51764_2_;
      p_i51764_2_.setScreen(this);
      this.field_224242_f = p_i51764_2_;
   }

   public void func_224233_a() {
      Thread thread = new Thread(this.field_224242_f, "Realms-long-running-task");
      thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void tick() {
      super.tick();
      Realms.narrateRepeatedly(this.title);
      ++this.animTicks;
      this.task.tick();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.cancelOrBackButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void init() {
      this.task.init();
      this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, RealmsConstants.func_225109_a(12), 212, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
         }
      });
   }

   private void cancelOrBackButtonClicked() {
      this.aborted = true;
      this.task.abortTask();
      Realms.setScreen(this.lastScreen);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, RealmsConstants.func_225109_a(3), 16777215);
      if (!this.field_224244_h) {
         this.drawCenteredString(SYMBOLS[this.animTicks % SYMBOLS.length], this.width() / 2, RealmsConstants.func_225109_a(8), 8421504);
      }

      if (this.field_224244_h) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, RealmsConstants.func_225109_a(8), 16711680);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void func_224231_a(String p_224231_1_) {
      this.field_224244_h = true;
      this.errorMessage = p_224231_1_;
      Realms.narrateNow(p_224231_1_);
      this.buttonsClear();
      this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
         }
      });
   }

   public void setTitle(String p_224234_1_) {
      this.title = p_224234_1_;
   }

   public boolean aborted() {
      return this.aborted;
   }
}