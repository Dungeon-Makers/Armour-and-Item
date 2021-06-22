package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends Texture implements AutoCloseable {
   private NativeImage pixels;

   public DynamicTexture(NativeImage p_i48124_1_) {
      this.pixels = p_i48124_1_;
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
            this.upload();
         });
      } else {
         TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
         this.upload();
      }

   }

   public DynamicTexture(int p_i48125_1_, int p_i48125_2_, boolean p_i48125_3_) {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      this.pixels = new NativeImage(p_i48125_1_, p_i48125_2_, p_i48125_3_);
      TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
   }

   public void load(IResourceManager p_195413_1_) throws IOException {
   }

   public void upload() {
      this.bind();
      this.pixels.upload(0, 0, 0, false);
   }

   @Nullable
   public NativeImage getPixels() {
      return this.pixels;
   }

   public void setPixels(NativeImage p_195415_1_) throws Exception {
      this.pixels.close();
      this.pixels = p_195415_1_;
   }

   public void close() {
      this.pixels.close();
      this.releaseId();
      this.pixels = null;
   }
}