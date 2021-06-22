package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceLoadProgressGui extends LoadingGui {
   private static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojang.png");
   private final Minecraft minecraft;
   private final IAsyncReloader reload;
   private final Consumer<Optional<Throwable>> onFinish;
   private final boolean fadeIn;
   private float currentProgress;
   private long fadeOutStart = -1L;
   private long fadeInStart = -1L;

   public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
      this.minecraft = p_i225928_1_;
      this.reload = p_i225928_2_;
      this.onFinish = p_i225928_3_;
      this.fadeIn = p_i225928_4_;
   }

   public static void registerTextures(Minecraft p_212970_0_) {
      p_212970_0_.getTextureManager().register(MOJANG_STUDIOS_LOGO_LOCATION, new ResourceLoadProgressGui.MojangLogoTexture());
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = this.minecraft.getWindow().getGuiScaledWidth();
      int j = this.minecraft.getWindow().getGuiScaledHeight();
      long k = Util.getMillis();
      if (this.fadeIn && (this.reload.isApplying() || this.minecraft.screen != null) && this.fadeInStart == -1L) {
         this.fadeInStart = k;
      }

      float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
      float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
      float f2;
      if (f >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(0, 0, p_render_3_);
         }

         int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
         fill(0, 0, i, j, 16777215 | l << 24);
         f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && f1 < 1.0F) {
            this.minecraft.screen.render(p_render_1_, p_render_2_, p_render_3_);
         }

         int j1 = MathHelper.ceil(MathHelper.clamp((double)f1, 0.15D, 1.0D) * 255.0D);
         fill(0, 0, i, j, 16777215 | j1 << 24);
         f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
      } else {
         fill(0, 0, i, j, -1);
         f2 = 1.0F;
      }

      int k1 = (this.minecraft.getWindow().getGuiScaledWidth() - 256) / 2;
      int i1 = (this.minecraft.getWindow().getGuiScaledHeight() - 256) / 2;
      this.minecraft.getTextureManager().bind(MOJANG_STUDIOS_LOGO_LOCATION);
      RenderSystem.enableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, f2);
      this.blit(k1, i1, 0, 0, 256, 256);
      float f3 = this.reload.getActualProgress();
      this.currentProgress = MathHelper.clamp(this.currentProgress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
      net.minecraftforge.fml.client.ClientModLoader.renderProgressText();
      if (f < 1.0F) {
         this.func_228181_a_(i / 2 - 150, j / 4 * 3, i / 2 + 150, j / 4 * 3 + 10, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
      }

      if (f >= 2.0F) {
         this.minecraft.setOverlay((LoadingGui)null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
         this.fadeOutStart = Util.getMillis(); // Moved up to guard against inf loops caused by callback
         try {
            this.reload.checkExceptions();
            this.onFinish.accept(Optional.empty());
         } catch (Throwable throwable) {
            this.onFinish.accept(Optional.of(throwable));
         }

         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
         }
      }

   }

   private void func_228181_a_(int p_228181_1_, int p_228181_2_, int p_228181_3_, int p_228181_4_, float p_228181_5_) {
      int i = MathHelper.ceil((float)(p_228181_3_ - p_228181_1_ - 1) * this.currentProgress);
      fill(p_228181_1_ - 1, p_228181_2_ - 1, p_228181_3_ + 1, p_228181_4_ + 1, -16777216 | Math.round((1.0F - p_228181_5_) * 255.0F) << 16 | Math.round((1.0F - p_228181_5_) * 255.0F) << 8 | Math.round((1.0F - p_228181_5_) * 255.0F));
      fill(p_228181_1_, p_228181_2_, p_228181_3_, p_228181_4_, -1);
      fill(p_228181_1_ + 1, p_228181_2_ + 1, p_228181_1_ + i, p_228181_4_ - 1, -16777216 | (int)MathHelper.lerp(1.0F - p_228181_5_, 226.0F, 255.0F) << 16 | (int)MathHelper.lerp(1.0F - p_228181_5_, 40.0F, 255.0F) << 8 | (int)MathHelper.lerp(1.0F - p_228181_5_, 55.0F, 255.0F));
   }

   public boolean isPauseScreen() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   static class MojangLogoTexture extends SimpleTexture {
      public MojangLogoTexture() {
         super(ResourceLoadProgressGui.MOJANG_STUDIOS_LOGO_LOCATION);
      }

      protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
         Minecraft minecraft = Minecraft.getInstance();
         VanillaPack vanillapack = minecraft.getClientPackSource().getVanillaPack();

         try (InputStream inputstream = vanillapack.getResource(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_STUDIOS_LOGO_LOCATION)) {
            SimpleTexture.TextureData simpletexture$texturedata = new SimpleTexture.TextureData((TextureMetadataSection)null, NativeImage.read(inputstream));
            return simpletexture$texturedata;
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureData(ioexception);
         }
      }
   }
}
