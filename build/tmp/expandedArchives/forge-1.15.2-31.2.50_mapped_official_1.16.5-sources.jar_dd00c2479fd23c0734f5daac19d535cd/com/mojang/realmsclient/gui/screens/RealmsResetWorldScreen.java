package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreenWithCallback<WorldTemplate> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private final RealmsScreen field_224459_e;
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private String title = getLocalizedString("mco.reset.world.title");
   private String subtitle = getLocalizedString("mco.reset.world.warning");
   private String buttonTitle = getLocalizedString("gui.cancel");
   private int subtitleColor = 16711680;
   private final int field_224466_l = 0;
   private final int field_224467_m = 100;
   private WorldTemplatePaginatedList templates;
   private WorldTemplatePaginatedList adventuremaps;
   private WorldTemplatePaginatedList experiences;
   private WorldTemplatePaginatedList inspirations;
   public int slot = -1;
   private RealmsResetWorldScreen.ResetType typeToReset = RealmsResetWorldScreen.ResetType.NONE;
   private RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset;
   private WorldTemplate worldTemplateToReset;
   private String resetTitle;
   private int field_224476_v = -1;

   public RealmsResetWorldScreen(RealmsScreen p_i51756_1_, RealmsServer p_i51756_2_, RealmsScreen p_i51756_3_) {
      this.lastScreen = p_i51756_1_;
      this.serverData = p_i51756_2_;
      this.field_224459_e = p_i51756_3_;
   }

   public RealmsResetWorldScreen(RealmsScreen p_i51757_1_, RealmsServer p_i51757_2_, RealmsScreen p_i51757_3_, String p_i51757_4_, String p_i51757_5_, int p_i51757_6_, String p_i51757_7_) {
      this(p_i51757_1_, p_i51757_2_, p_i51757_3_);
      this.title = p_i51757_4_;
      this.subtitle = p_i51757_5_;
      this.subtitleColor = p_i51757_6_;
      this.buttonTitle = p_i51757_7_;
   }

   public void func_224444_a(int p_224444_1_) {
      this.field_224476_v = p_224444_1_;
   }

   public void setSlot(int p_224445_1_) {
      this.slot = p_224445_1_;
   }

   public void setResetTitle(String p_224432_1_) {
      this.resetTitle = p_224432_1_;
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 40, RealmsConstants.func_225109_a(14) - 10, 80, 20, this.buttonTitle) {
         public void onPress() {
            Realms.setScreen(RealmsResetWorldScreen.this.lastScreen);
         }
      });
      (new Thread("Realms-reset-world-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               WorldTemplatePaginatedList worldtemplatepaginatedlist = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.NORMAL);
               WorldTemplatePaginatedList worldtemplatepaginatedlist1 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.ADVENTUREMAP);
               WorldTemplatePaginatedList worldtemplatepaginatedlist2 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.EXPERIENCE);
               WorldTemplatePaginatedList worldtemplatepaginatedlist3 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.INSPIRATION);
               Realms.execute(() -> {
                  RealmsResetWorldScreen.this.templates = worldtemplatepaginatedlist;
                  RealmsResetWorldScreen.this.adventuremaps = worldtemplatepaginatedlist1;
                  RealmsResetWorldScreen.this.experiences = worldtemplatepaginatedlist2;
                  RealmsResetWorldScreen.this.inspirations = worldtemplatepaginatedlist3;
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.addWidget(this.titleLabel = new RealmsLabel(this.title, this.width() / 2, 7, 16777215));
      this.addWidget(this.subtitleLabel = new RealmsLabel(this.subtitle, this.width() / 2, 22, this.subtitleColor));
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(1), RealmsConstants.func_225109_a(0) + 10, getLocalizedString("mco.reset.world.generate"), -1L, "realms:textures/gui/realms/new_world.png", RealmsResetWorldScreen.ResetType.GENERATE) {
         public void onPress() {
            Realms.setScreen(new RealmsResetNormalWorldScreen(RealmsResetWorldScreen.this, RealmsResetWorldScreen.this.title));
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(2), RealmsConstants.func_225109_a(0) + 10, getLocalizedString("mco.reset.world.upload"), -1L, "realms:textures/gui/realms/upload.png", RealmsResetWorldScreen.ResetType.UPLOAD) {
         public void onPress() {
            Realms.setScreen(new RealmsSelectFileToUploadScreen(RealmsResetWorldScreen.this.serverData.id, RealmsResetWorldScreen.this.slot != -1 ? RealmsResetWorldScreen.this.slot : RealmsResetWorldScreen.this.serverData.activeSlot, RealmsResetWorldScreen.this));
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(3), RealmsConstants.func_225109_a(0) + 10, getLocalizedString("mco.reset.world.template"), -1L, "realms:textures/gui/realms/survival_spawn.png", RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.ServerType.NORMAL, RealmsResetWorldScreen.this.templates);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.reset.world.template"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(1), RealmsConstants.func_225109_a(6) + 20, getLocalizedString("mco.reset.world.adventure"), -1L, "realms:textures/gui/realms/adventure.png", RealmsResetWorldScreen.ResetType.ADVENTURE) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.ServerType.ADVENTUREMAP, RealmsResetWorldScreen.this.adventuremaps);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.reset.world.adventure"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(2), RealmsConstants.func_225109_a(6) + 20, getLocalizedString("mco.reset.world.experience"), -1L, "realms:textures/gui/realms/experience.png", RealmsResetWorldScreen.ResetType.EXPERIENCE) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.ServerType.EXPERIENCE, RealmsResetWorldScreen.this.experiences);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.reset.world.experience"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.TexturedButton(this.frame(3), RealmsConstants.func_225109_a(6) + 20, getLocalizedString("mco.reset.world.inspiration"), -1L, "realms:textures/gui/realms/inspiration.png", RealmsResetWorldScreen.ResetType.INSPIRATION) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.ServerType.INSPIRATION, RealmsResetWorldScreen.this.inspirations);
            realmsselectworldtemplatescreen.func_224483_a(RealmsScreen.getLocalizedString("mco.reset.world.inspiration"));
            Realms.setScreen(realmsselectworldtemplatescreen);
         }
      });
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

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   private int frame(int p_224434_1_) {
      return this.width() / 2 - 130 + (p_224434_1_ - 1) * 100;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.subtitleLabel.render(this);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private void func_224440_a(int p_224440_1_, int p_224440_2_, String p_224440_3_, long p_224440_4_, String p_224440_6_, RealmsResetWorldScreen.ResetType p_224440_7_, boolean p_224440_8_, boolean p_224440_9_) {
      if (p_224440_4_ == -1L) {
         bind(p_224440_6_);
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_224440_4_), p_224440_6_);
      }

      if (p_224440_8_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RealmsScreen.blit(p_224440_1_ + 2, p_224440_2_ + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      bind("realms:textures/gui/realms/slot_frame.png");
      if (p_224440_8_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RealmsScreen.blit(p_224440_1_, p_224440_2_ + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      this.drawCenteredString(p_224440_3_, p_224440_1_ + 30, p_224440_2_, p_224440_8_ ? 10526880 : 16777215);
   }

   void callback(WorldTemplate p_223627_1_) {
      if (p_223627_1_ != null) {
         if (this.slot == -1) {
            this.resetWorldWithTemplate(p_223627_1_);
         } else {
            switch(p_223627_1_.type) {
            case WORLD_TEMPLATE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
               break;
            case ADVENTUREMAP:
               this.typeToReset = RealmsResetWorldScreen.ResetType.ADVENTURE;
               break;
            case EXPERIENCE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.EXPERIENCE;
               break;
            case INSPIRATION:
               this.typeToReset = RealmsResetWorldScreen.ResetType.INSPIRATION;
            }

            this.worldTemplateToReset = p_223627_1_;
            this.switchSlot();
         }
      }

   }

   private void switchSlot() {
      this.func_224446_a(this);
   }

   public void func_224446_a(RealmsScreen p_224446_1_) {
      RealmsTasks.SwitchSlotTask realmstasks$switchslottask = new RealmsTasks.SwitchSlotTask(this.serverData.id, this.slot, p_224446_1_, 100);
      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$switchslottask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 100 && p_confirmResult_1_) {
         switch(this.typeToReset) {
         case ADVENTURE:
         case SURVIVAL_SPAWN:
         case EXPERIENCE:
         case INSPIRATION:
            if (this.worldTemplateToReset != null) {
               this.resetWorldWithTemplate(this.worldTemplateToReset);
            }
            break;
         case GENERATE:
            if (this.worldInfoToReset != null) {
               this.triggerResetWorld(this.worldInfoToReset);
            }
            break;
         default:
            return;
         }

      } else {
         if (p_confirmResult_1_) {
            Realms.setScreen(this.field_224459_e);
            if (this.field_224476_v != -1) {
               this.field_224459_e.confirmResult(true, this.field_224476_v);
            }
         }

      }
   }

   public void resetWorldWithTemplate(WorldTemplate p_224435_1_) {
      RealmsTasks.ResettingWorldTask realmstasks$resettingworldtask = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.field_224459_e, p_224435_1_);
      if (this.resetTitle != null) {
         realmstasks$resettingworldtask.func_225012_c(this.resetTitle);
      }

      if (this.field_224476_v != -1) {
         realmstasks$resettingworldtask.func_225011_a(this.field_224476_v);
      }

      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$resettingworldtask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo p_224438_1_) {
      if (this.slot == -1) {
         this.triggerResetWorld(p_224438_1_);
      } else {
         this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
         this.worldInfoToReset = p_224438_1_;
         this.switchSlot();
      }

   }

   private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo p_224437_1_) {
      RealmsTasks.ResettingWorldTask realmstasks$resettingworldtask = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.field_224459_e, p_224437_1_.seed, p_224437_1_.levelType, p_224437_1_.generateStructures);
      if (this.resetTitle != null) {
         realmstasks$resettingworldtask.func_225012_c(this.resetTitle);
      }

      if (this.field_224476_v != -1) {
         realmstasks$resettingworldtask.func_225011_a(this.field_224476_v);
      }

      RealmsLongRunningMcoTaskScreen realmslongrunningmcotaskscreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, realmstasks$resettingworldtask);
      realmslongrunningmcotaskscreen.func_224233_a();
      Realms.setScreen(realmslongrunningmcotaskscreen);
   }

   @OnlyIn(Dist.CLIENT)
   static enum ResetType {
      NONE,
      GENERATE,
      UPLOAD,
      ADVENTURE,
      SURVIVAL_SPAWN,
      EXPERIENCE,
      INSPIRATION;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResetWorldInfo {
      String seed;
      int levelType;
      boolean generateStructures;

      public ResetWorldInfo(String p_i51560_1_, int p_i51560_2_, boolean p_i51560_3_) {
         this.seed = p_i51560_1_;
         this.levelType = p_i51560_2_;
         this.generateStructures = p_i51560_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract class TexturedButton extends RealmsButton {
      private final long field_223822_a;
      private final String image;
      private final RealmsResetWorldScreen.ResetType field_223825_d;

      public TexturedButton(int p_i51562_2_, int p_i51562_3_, String p_i51562_4_, long p_i51562_5_, String p_i51562_7_, RealmsResetWorldScreen.ResetType p_i51562_8_) {
         super(100 + p_i51562_8_.ordinal(), p_i51562_2_, p_i51562_3_, 60, 72, p_i51562_4_);
         this.field_223822_a = p_i51562_5_;
         this.image = p_i51562_7_;
         this.field_223825_d = p_i51562_8_;
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsResetWorldScreen.this.func_224440_a(this.func_214457_x(), this.func_223291_y_(), this.getProxy().getMessage(), this.field_223822_a, this.image, this.field_223825_d, this.getProxy().isHovered(), this.getProxy().isMouseOver((double)p_renderButton_1_, (double)p_renderButton_2_));
      }
   }
}