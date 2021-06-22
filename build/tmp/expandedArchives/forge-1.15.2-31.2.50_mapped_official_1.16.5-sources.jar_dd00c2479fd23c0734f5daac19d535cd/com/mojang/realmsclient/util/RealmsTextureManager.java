package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> TEXTURES = Maps.newHashMap();
   private static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.newHashMap();
   private static final Map<String, String> FETCHED_SKINS = Maps.newHashMap();
   private static final Logger LOGGER = LogManager.getLogger();

   public static void bindWorldTemplate(String p_225202_0_, String p_225202_1_) {
      if (p_225202_1_ == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int i = getTextureId(p_225202_0_, p_225202_1_);
         RenderSystem.bindTexture(i);
      }
   }

   public static void withBoundFace(String p_225205_0_, Runnable p_225205_1_) {
      RenderSystem.pushTextureAttributes();

      try {
         bindFace(p_225205_0_);
         p_225205_1_.run();
      } finally {
         RenderSystem.popAttributes();
      }

   }

   private static void bindDefaultFace(UUID p_225204_0_) {
      RealmsScreen.bind((p_225204_0_.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
   }

   private static void bindFace(final String p_225200_0_) {
      UUID uuid = UUIDTypeAdapter.fromString(p_225200_0_);
      if (TEXTURES.containsKey(p_225200_0_)) {
         RenderSystem.bindTexture((TEXTURES.get(p_225200_0_)).textureId);
      } else if (SKIN_FETCH_STATUS.containsKey(p_225200_0_)) {
         if (!SKIN_FETCH_STATUS.get(p_225200_0_)) {
            bindDefaultFace(uuid);
         } else if (FETCHED_SKINS.containsKey(p_225200_0_)) {
            int i = getTextureId(p_225200_0_, FETCHED_SKINS.get(p_225200_0_));
            RenderSystem.bindTexture(i);
         } else {
            bindDefaultFace(uuid);
         }

      } else {
         SKIN_FETCH_STATUS.put(p_225200_0_, false);
         bindDefaultFace(uuid);
         Thread thread = new Thread("Realms Texture Downloader") {
            public void run() {
               Map<Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(p_225200_0_);
               if (map.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture minecraftprofiletexture = map.get(Type.SKIN);
                  String s = minecraftprofiletexture.getUrl();
                  HttpURLConnection httpurlconnection = null;
                  RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", (Object)s);

                  try {
                     httpurlconnection = (HttpURLConnection)(new URL(s)).openConnection(Realms.getProxy());
                     httpurlconnection.setDoInput(true);
                     httpurlconnection.setDoOutput(false);
                     httpurlconnection.connect();
                     if (httpurlconnection.getResponseCode() / 100 == 2) {
                        BufferedImage bufferedimage;
                        try {
                           bufferedimage = ImageIO.read(httpurlconnection.getInputStream());
                        } catch (Exception var17) {
                           RealmsTextureManager.SKIN_FETCH_STATUS.remove(p_225200_0_);
                           return;
                        } finally {
                           IOUtils.closeQuietly(httpurlconnection.getInputStream());
                        }

                        bufferedimage = (new SkinProcessor()).process(bufferedimage);
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedimage, "png", bytearrayoutputstream);
                        RealmsTextureManager.FETCHED_SKINS.put(p_225200_0_, (new Base64()).encodeToString(bytearrayoutputstream.toByteArray()));
                        RealmsTextureManager.SKIN_FETCH_STATUS.put(p_225200_0_, true);
                        return;
                     }

                     RealmsTextureManager.SKIN_FETCH_STATUS.remove(p_225200_0_);
                  } catch (Exception exception) {
                     RealmsTextureManager.LOGGER.error("Couldn't download http texture", (Throwable)exception);
                     RealmsTextureManager.SKIN_FETCH_STATUS.remove(p_225200_0_);
                     return;
                  } finally {
                     if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                     }

                  }

               } else {
                  RealmsTextureManager.SKIN_FETCH_STATUS.put(p_225200_0_, true);
               }
            }
         };
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static int getTextureId(String p_225203_0_, String p_225203_1_) {
      int i;
      if (TEXTURES.containsKey(p_225203_0_)) {
         RealmsTextureManager.RealmsTexture realmstexturemanager$realmstexture = TEXTURES.get(p_225203_0_);
         if (realmstexturemanager$realmstexture.image.equals(p_225203_1_)) {
            return realmstexturemanager$realmstexture.textureId;
         }

         RenderSystem.deleteTexture(realmstexturemanager$realmstexture.textureId);
         i = realmstexturemanager$realmstexture.textureId;
      } else {
         i = GlStateManager._genTexture();
      }

      IntBuffer intbuffer = null;
      int j = 0;
      int k = 0;

      try {
         InputStream inputstream = new ByteArrayInputStream((new Base64()).decode(p_225203_1_));

         BufferedImage bufferedimage;
         try {
            bufferedimage = ImageIO.read(inputstream);
         } finally {
            IOUtils.closeQuietly(inputstream);
         }

         j = bufferedimage.getWidth();
         k = bufferedimage.getHeight();
         int[] lvt_8_1_ = new int[j * k];
         bufferedimage.getRGB(0, 0, j, k, lvt_8_1_, 0, j);
         intbuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
         intbuffer.put(lvt_8_1_);
         ((Buffer)intbuffer).flip();
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(i);
      TextureUtil.initTexture(intbuffer, j, k);
      TEXTURES.put(p_225203_0_, new RealmsTextureManager.RealmsTexture(p_225203_1_, i));
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsTexture {
      String image;
      int textureId;

      public RealmsTexture(String p_i51693_1_, int p_i51693_2_) {
         this.image = p_i51693_1_;
         this.textureId = p_i51693_2_;
      }
   }
}