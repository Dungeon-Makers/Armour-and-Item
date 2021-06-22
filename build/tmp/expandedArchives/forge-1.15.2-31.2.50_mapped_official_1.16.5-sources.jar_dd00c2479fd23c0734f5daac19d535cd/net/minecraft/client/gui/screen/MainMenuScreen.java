package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MainMenuScreen extends Screen {
   public static final RenderSkyboxCube CUBE_MAP = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
   private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
   private final boolean minceraftEasterEgg;
   @Nullable
   private String splash;
   private Button resetDemoButton;
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   private boolean realmsNotificationsInitialized;
   private Screen realmsNotificationsScreen;
   private int copyrightWidth;
   private int copyrightX;
   private final RenderSkybox panorama = new RenderSkybox(CUBE_MAP);
   private final boolean fading;
   private long fadeInStart;
   private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

   public MainMenuScreen() {
      this(false);
   }

   public MainMenuScreen(boolean p_i51107_1_) {
      super(new TranslationTextComponent("narrator.screen.title"));
      this.fading = p_i51107_1_;
      this.minceraftEasterEgg = (double)(new Random()).nextFloat() < 1.0E-4D;
   }

   private boolean realmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

   }

   public static CompletableFuture<Void> preloadResources(TextureManager p_213097_0_, Executor p_213097_1_) {
      return CompletableFuture.allOf(p_213097_0_.preload(MINECRAFT_LOGO, p_213097_1_), p_213097_0_.preload(MINECRAFT_EDITION, p_213097_1_), p_213097_0_.preload(PANORAMA_OVERLAY, p_213097_1_), CUBE_MAP.preload(p_213097_0_, p_213097_1_));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splash == null) {
         this.splash = this.minecraft.getSplashManager().getSplash();
      }

      this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
      this.copyrightX = this.width - this.copyrightWidth - 2;
      int i = 24;
      int j = this.height / 4 + 48;
      Button modbutton = null;
      if (this.minecraft.isDemo()) {
         this.createDemoMenuOptions(j, 24);
      } else {
         this.createNormalMenuOptions(j, 24);
         modbutton = this.addButton(new Button(this.width / 2 - 100, j + 24 * 2, 98, 20, I18n.get("fml.menu.mods"), button -> {
            this.minecraft.setScreen(new net.minecraftforge.fml.client.gui.screen.ModListScreen(this));
         }));
      }

      this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (p_213090_1_) -> {
         this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
      }, I18n.get("narrator.button.language")));
      this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.get("menu.options"), (p_213096_1_) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.get("menu.quit"), (p_213094_1_) -> {
         this.minecraft.stop();
      }));
      this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, (p_213088_1_) -> {
         this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options));
      }, I18n.get("narrator.button.accessibility")));
      this.minecraft.setConnectedToRealms(false);
      if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
         RealmsBridge realmsbridge = new RealmsBridge();
         this.realmsNotificationsScreen = realmsbridge.getNotificationScreen(this);
         this.realmsNotificationsInitialized = true;
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
      }
      modUpdateNotification = net.minecraftforge.client.gui.NotificationModUpdateScreen.init(this, modbutton);

   }

   private void createNormalMenuOptions(int p_73969_1_, int p_73969_2_) {
      this.addButton(new Button(this.width / 2 - 100, p_73969_1_, 200, 20, I18n.get("menu.singleplayer"), (p_213089_1_) -> {
         this.minecraft.setScreen(new WorldSelectionScreen(this));
      }));
      this.addButton(new Button(this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, 200, 20, I18n.get("menu.multiplayer"), (p_213086_1_) -> {
         if (this.minecraft.options.skipMultiplayerWarning) {
            this.minecraft.setScreen(new MultiplayerScreen(this));
         } else {
            this.minecraft.setScreen(new MultiplayerWarningScreen(this));
         }

      }));
      this.addButton(new Button(this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.get("menu.online"), (p_213095_1_) -> {
         this.realmsButtonClicked();
      }));
   }

   private void createDemoMenuOptions(int p_73972_1_, int p_73972_2_) {
      this.addButton(new Button(this.width / 2 - 100, p_73972_1_, 200, 20, I18n.get("menu.playdemo"), (p_213092_1_) -> {
         this.minecraft.func_71371_a("Demo_World", "Demo_World", MinecraftServer.DEMO_SETTINGS);
      }));
      this.resetDemoButton = this.addButton(new Button(this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, 200, 20, I18n.get("menu.resetdemo"), (p_213091_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getLevelSource();
         WorldInfo worldinfo1 = saveformat1.func_75803_c("Demo_World");
         if (worldinfo1 != null) {
            this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", worldinfo1.getLevelName()), I18n.get("selectWorld.deleteButton"), I18n.get("gui.cancel")));
         }

      }));
      SaveFormat saveformat = this.minecraft.getLevelSource();
      WorldInfo worldinfo = saveformat.func_75803_c("Demo_World");
      if (worldinfo == null) {
         this.resetDemoButton.active = false;
      }

   }

   private void realmsButtonClicked() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
      fill(0, 0, this.width, this.height, -1);
      this.panorama.render(p_render_3_, MathHelper.clamp(f, 0.0F, 1.0F));
      int i = 274;
      int j = this.width / 2 - 137;
      int k = 30;
      this.minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.fading ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
      blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      float f1 = this.fading ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
      int l = MathHelper.ceil(f1 * 255.0F) << 24;
      if ((l & -67108864) != 0) {
         this.minecraft.getTextureManager().bind(MINECRAFT_LOGO);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
         if (this.minceraftEasterEgg) {
            this.blit(j + 0, 30, 0, 0, 99, 44);
            this.blit(j + 99, 30, 129, 0, 27, 44);
            this.blit(j + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         } else {
            this.blit(j + 0, 30, 0, 0, 155, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         }

         this.minecraft.getTextureManager().bind(MINECRAFT_EDITION);
         blit(j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
         net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, this.font, this.width, this.height);
         if (this.splash != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
            RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMillis() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
            f2 = f2 * 100.0F / (float)(this.font.width(this.splash) + 32);
            RenderSystem.scalef(f2, f2, f2);
            this.drawCenteredString(this.font, this.splash, 0, -8, 16776960 | l);
            RenderSystem.popMatrix();
         }

         String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
         if (this.minecraft.isDemo()) {
            s = s + " Demo";
         } else {
            s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         if (this.minecraft.isProbablyModded()) {
            s = s + I18n.get("menu.modded");
         }

         net.minecraftforge.fml.BrandingControl.forEachLine(true, true, (brdline, brd) ->
            this.drawString(this.font, brd, 2, this.height - ( 10 + brdline * (this.font.lineHeight + 1)), 16777215 | l)
         );

         net.minecraftforge.fml.BrandingControl.forEachAboveCopyrightLine((brdline, brd) ->
              this.drawString(this.font, brd, this.width - font.width(brd), this.height - (10 + (brdline + 1) * ( this.font.lineHeight + 1)), 16777215 | l)
         );
         this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX, this.height - 10, 16777215 | l);
         if (p_render_1_ > this.copyrightX && p_render_1_ < this.copyrightX + this.copyrightWidth && p_render_2_ > this.height - 10 && p_render_2_ < this.height) {
            fill(this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height, 16777215 | l);
         }

         for(Widget widget : this.buttons) {
            widget.setAlpha(f1);
         }

         super.render(p_render_1_, p_render_2_, p_render_3_);
         if (this.realmsNotificationsEnabled() && f1 >= 1.0F) {
            this.realmsNotificationsScreen.render(p_render_1_, p_render_2_, p_render_3_);
         }
         modUpdateNotification.render(p_render_1_, p_render_2_, p_render_3_);

      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_1_ > (double)this.copyrightX && p_mouseClicked_1_ < (double)(this.copyrightX + this.copyrightWidth) && p_mouseClicked_3_ > (double)(this.height - 10) && p_mouseClicked_3_ < (double)this.height) {
            this.minecraft.setScreen(new WinGameScreen(false, Runnables.doNothing()));
         }

         return false;
      }
   }

   public void removed() {
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.removed();
      }

   }

   private void confirmDemo(boolean p_213087_1_) {
      if (p_213087_1_) {
         SaveFormat saveformat = this.minecraft.getLevelSource();
         saveformat.func_75802_e("Demo_World");
      }

      this.minecraft.setScreen(this);
   }
}
