package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final WorldDownload worldDownload;
   private final String downloadTitle;
   private final RateLimiter narrationRateLimiter;
   private RealmsButton cancelButton;
   private final String worldName;
   private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
   private volatile String errorMessage;
   private volatile String status;
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean showDots = true;
   private volatile boolean finished;
   private volatile boolean extracting;
   private Long previousWrittenBytes;
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private int animTick;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex;
   private final int field_224196_v = 100;
   private int field_224197_w = -1;
   private boolean checked;
   private static final ReentrantLock field_224199_y = new ReentrantLock();

   public RealmsDownloadLatestWorldScreen(RealmsScreen p_i51770_1_, WorldDownload p_i51770_2_, String p_i51770_3_) {
      this.lastScreen = p_i51770_1_;
      this.worldName = p_i51770_3_;
      this.worldDownload = p_i51770_2_;
      this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
      this.downloadTitle = getLocalizedString("mco.download.title");
      this.narrationRateLimiter = RateLimiter.create((double)0.1F);
   }

   public void func_224167_a(int p_224167_1_) {
      this.field_224197_w = p_224167_1_;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsDownloadLatestWorldScreen.this.cancelled = true;
            RealmsDownloadLatestWorldScreen.this.backButtonClicked();
         }
      });
      this.checkDownloadSize();
   }

   private void checkDownloadSize() {
      if (!this.finished) {
         if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            String s = getLocalizedString("mco.download.confirmation.line1", new Object[]{func_224150_b(5368709120L)});
            String s1 = getLocalizedString("mco.download.confirmation.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, s, s1, false, 100));
         } else {
            this.downloadSave();
         }

      }
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      this.checked = true;
      Realms.setScreen(this);
      this.downloadSave();
   }

   private long getContentLength(String p_224152_1_) {
      FileDownload filedownload = new FileDownload();
      return filedownload.contentLength(p_224152_1_);
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         List<String> list = Lists.newArrayList();
         list.add(this.downloadTitle);
         list.add(this.status);
         if (this.progress != null) {
            list.add(this.progress + "%");
            list.add(func_224153_a(this.bytesPersSecond));
         }

         if (this.errorMessage != null) {
            list.add(this.errorMessage);
         }

         String s = String.join(System.lineSeparator(), list);
         Realms.narrateNow(s);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.cancelled = true;
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void backButtonClicked() {
      if (this.finished && this.field_224197_w != -1 && this.errorMessage == null) {
         this.lastScreen.confirmResult(true, this.field_224197_w);
      }

      Realms.setScreen(this.lastScreen);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (this.extracting && !this.finished) {
         this.status = getLocalizedString("mco.download.extracting");
      }

      this.drawCenteredString(this.downloadTitle, this.width() / 2, 20, 16777215);
      this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
      if (this.showDots) {
         this.func_224161_e();
      }

      if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
         this.func_224164_f();
         this.func_224149_g();
      }

      if (this.errorMessage != null) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, 110, 16711680);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private void func_224161_e() {
      int i = this.fontWidth(this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + i / 2 + 5, 50, 16777215);
   }

   private void func_224164_f() {
      double d0 = this.downloadStatus.bytesWritten.doubleValue() / this.downloadStatus.totalBytes.doubleValue() * 100.0D;
      this.progress = String.format(Locale.ROOT, "%.1f", d0);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      Tezzelator tezzelator = Tezzelator.instance;
      tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      double d1 = (double)(this.width() / 2 - 100);
      double d2 = 0.5D;
      tezzelator.vertex(d1 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      tezzelator.vertex(d1, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1 + 200.0D * d0 / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.vertex(d1, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tezzelator.end();
      RenderSystem.enableTexture();
      this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
   }

   private void func_224149_g() {
      if (this.animTick % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long i = System.currentTimeMillis() - this.previousTimeSnapshot;
            if (i == 0L) {
               i = 1L;
            }

            this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / i;
            this.func_224156_c(this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.downloadStatus.bytesWritten;
         this.previousTimeSnapshot = System.currentTimeMillis();
      } else {
         this.func_224156_c(this.bytesPersSecond);
      }

   }

   private void func_224156_c(long p_224156_1_) {
      if (p_224156_1_ > 0L) {
         int i = this.fontWidth(this.progress);
         String s = "(" + func_224153_a(p_224156_1_) + ")";
         this.drawString(s, this.width() / 2 + i / 2 + 15, 84, 16777215);
      }

   }

   public static String func_224153_a(long p_224153_0_) {
      int i = 1024;
      if (p_224153_0_ < 1024L) {
         return p_224153_0_ + " B/s";
      } else {
         int j = (int)(Math.log((double)p_224153_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(j - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)p_224153_0_ / Math.pow(1024.0D, (double)j), s);
      }
   }

   public static String func_224150_b(long p_224150_0_) {
      int i = 1024;
      if (p_224150_0_ < 1024L) {
         return p_224150_0_ + " B";
      } else {
         int j = (int)(Math.log((double)p_224150_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(j - 1) + "";
         return String.format(Locale.ROOT, "%.0f %sB", (double)p_224150_0_ / Math.pow(1024.0D, (double)j), s);
      }
   }

   private void downloadSave() {
      (new Thread(() -> {
         try {
            if (field_224199_y.tryLock(1L, TimeUnit.SECONDS)) {
               this.status = getLocalizedString("mco.download.preparing");
               if (this.cancelled) {
                  this.downloadCancelled();
                  return;
               }

               this.status = getLocalizedString("mco.download.downloading", new Object[]{this.worldName});
               FileDownload filedownload = new FileDownload();
               filedownload.contentLength(this.worldDownload.downloadLink);
               filedownload.func_224830_a(this.worldDownload, this.worldName, this.downloadStatus, this.getLevelStorageSource());

               while(!filedownload.isFinished()) {
                  if (filedownload.isError()) {
                     filedownload.cancel();
                     this.errorMessage = getLocalizedString("mco.download.failed");
                     this.cancelButton.setMessage(getLocalizedString("gui.done"));
                     return;
                  }

                  if (filedownload.isExtracting()) {
                     this.extracting = true;
                  }

                  if (this.cancelled) {
                     filedownload.cancel();
                     this.downloadCancelled();
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var8) {
                     LOGGER.error("Failed to check Realms backup download status");
                  }
               }

               this.finished = true;
               this.status = getLocalizedString("mco.download.done");
               this.cancelButton.setMessage(getLocalizedString("gui.done"));
               return;
            }
         } catch (InterruptedException var9) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } catch (Exception exception) {
            this.errorMessage = getLocalizedString("mco.download.failed");
            exception.printStackTrace();
            return;
         } finally {
            if (!field_224199_y.isHeldByCurrentThread()) {
               return;
            }

            field_224199_y.unlock();
            this.showDots = false;
            this.finished = true;
         }

      })).start();
   }

   private void downloadCancelled() {
      this.status = getLocalizedString("mco.download.cancelled");
   }

   @OnlyIn(Dist.CLIENT)
   public class DownloadStatus {
      public volatile Long bytesWritten = 0L;
      public volatile Long totalBytes = 0L;
   }
}