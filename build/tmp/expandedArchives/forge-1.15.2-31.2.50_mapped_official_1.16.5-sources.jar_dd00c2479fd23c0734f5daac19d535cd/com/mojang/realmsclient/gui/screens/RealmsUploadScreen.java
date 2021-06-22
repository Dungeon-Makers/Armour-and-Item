package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsResetWorldScreen lastScreen;
   private final RealmsLevelSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   private volatile String errorMessage;
   private volatile String status;
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   private RealmsButton backButton;
   private RealmsButton cancelButton;
   private int field_224712_q;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int field_224714_s;
   private Long previousWrittenBytes;
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private static final ReentrantLock field_224718_w = new ReentrantLock();

   public RealmsUploadScreen(long p_i51747_1_, int p_i51747_3_, RealmsResetWorldScreen p_i51747_4_, RealmsLevelSummary p_i51747_5_) {
      this.worldId = p_i51747_1_;
      this.slotId = p_i51747_3_;
      this.lastScreen = p_i51747_4_;
      this.selectedLevel = p_i51747_5_;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create((double)0.1F);
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.backButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsUploadScreen.this.onBack();
         }
      };
      this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsUploadScreen.this.onCancel();
         }
      });
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.upload();
         } else {
            this.lastScreen.func_224446_a(this);
         }
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && !this.uploadStarted) {
         this.uploadStarted = true;
         Realms.setScreen(this);
         this.upload();
      }

   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void onBack() {
      this.lastScreen.confirmResult(true, 0);
   }

   private void onCancel() {
      this.cancelled = true;
      Realms.setScreen(this.lastScreen);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         if (this.showDots) {
            this.onCancel();
         } else {
            this.onBack();
         }

         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
         this.status = getLocalizedString("mco.upload.verifying");
         this.cancelButton.active(false);
      }

      this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
      if (this.showDots) {
         this.func_224678_e();
      }

      if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
         this.func_224681_f();
         this.func_224664_g();
      }

      if (this.errorMessage != null) {
         String[] astring = this.errorMessage.split("\\\\n");

         for(int i = 0; i < astring.length; ++i) {
            this.drawCenteredString(astring[i], this.width() / 2, 110 + 12 * i, 16711680);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private void func_224678_e() {
      int i = this.fontWidth(this.status);
      if (this.field_224712_q % 10 == 0) {
         ++this.field_224714_s;
      }

      this.drawString(DOTS[this.field_224714_s % DOTS.length], this.width() / 2 + i / 2 + 5, 50, 16777215);
   }

   private void func_224681_f() {
      double d0 = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0D;
      if (d0 > 100.0D) {
         d0 = 100.0D;
      }

      this.progress = String.format(Locale.ROOT, "%.1f", d0);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double d1 = (double)(this.width() / 2 - 100);
      double d2 = 0.5D;
      Tezzelator tezzelator = Tezzelator.instance;
      tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
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

   private void func_224664_g() {
      if (this.field_224712_q % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long i = System.currentTimeMillis() - this.previousTimeSnapshot;
            if (i == 0L) {
               i = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / i;
            this.func_224673_c(this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = System.currentTimeMillis();
      } else {
         this.func_224673_c(this.bytesPersSecond);
      }

   }

   private void func_224673_c(long p_224673_1_) {
      if (p_224673_1_ > 0L) {
         int i = this.fontWidth(this.progress);
         String s = "(" + func_224671_a(p_224673_1_) + ")";
         this.drawString(s, this.width() / 2 + i / 2 + 15, 84, 16777215);
      }

   }

   public static String func_224671_a(long p_224671_0_) {
      int i = 1024;
      if (p_224671_0_ < 1024L) {
         return p_224671_0_ + " B";
      } else {
         int j = (int)(Math.log((double)p_224671_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(j - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)p_224671_0_ / Math.pow(1024.0D, (double)j), s);
      }
   }

   public void tick() {
      super.tick();
      ++this.field_224712_q;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         List<String> list = Lists.newArrayList();
         list.add(this.status);
         if (this.progress != null) {
            list.add(this.progress + "%");
         }

         if (this.errorMessage != null) {
            list.add(this.errorMessage);
         }

         Realms.narrateNow(String.join(System.lineSeparator(), list));
      }

   }

   public static RealmsUploadScreen.Unit func_224665_b(long p_224665_0_) {
      if (p_224665_0_ < 1024L) {
         return RealmsUploadScreen.Unit.B;
      } else {
         int i = (int)(Math.log((double)p_224665_0_) / Math.log(1024.0D));
         String s = "KMGTPE".charAt(i - 1) + "";

         try {
            return RealmsUploadScreen.Unit.valueOf(s + "B");
         } catch (Exception var5) {
            return RealmsUploadScreen.Unit.GB;
         }
      }
   }

   public static double func_224691_a(long p_224691_0_, RealmsUploadScreen.Unit p_224691_2_) {
      return p_224691_2_.equals(RealmsUploadScreen.Unit.B) ? (double)p_224691_0_ : (double)p_224691_0_ / Math.pow(1024.0D, (double)p_224691_2_.ordinal());
   }

   public static String func_224667_b(long p_224667_0_, RealmsUploadScreen.Unit p_224667_2_) {
      return String.format("%." + (p_224667_2_.equals(RealmsUploadScreen.Unit.GB) ? "1" : "0") + "f %s", func_224691_a(p_224667_0_, p_224667_2_), p_224667_2_.name());
   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread(() -> {
         File file1 = null;
         RealmsClient realmsclient = RealmsClient.create();
         long i = this.worldId;

         try {
            if (field_224718_w.tryLock(1L, TimeUnit.SECONDS)) {
               this.status = getLocalizedString("mco.upload.preparing");
               UploadInfo uploadinfo = null;

               for(int j = 0; j < 20; ++j) {
                  try {
                     if (this.cancelled) {
                        this.uploadCancelled();
                        return;
                     }

                     uploadinfo = realmsclient.requestUploadInfo(i, UploadTokenCache.get(i));
                     break;
                  } catch (RetryCallException retrycallexception) {
                     Thread.sleep((long)(retrycallexception.delaySeconds * 1000));
                  }
               }

               if (uploadinfo == null) {
                  this.status = getLocalizedString("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.put(i, uploadinfo.getToken());
               if (!uploadinfo.isWorldClosed()) {
                  this.status = getLocalizedString("mco.upload.close.failure");
                  return;
               }

               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               File file2 = new File(Realms.getGameDirectoryPath(), "saves");
               file1 = this.tarGzipArchive(new File(file2, this.selectedLevel.getLevelId()));
               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               if (this.verify(file1)) {
                  this.status = getLocalizedString("mco.upload.uploading", new Object[]{this.selectedLevel.getLevelName()});
                  FileUpload fileupload = new FileUpload(file1, this.worldId, this.slotId, uploadinfo, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), this.uploadStatus);
                  fileupload.upload((p_227992_3_) -> {
                     if (p_227992_3_.statusCode >= 200 && p_227992_3_.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = getLocalizedString("mco.upload.done");
                        this.backButton.setMessage(getLocalizedString("gui.done"));
                        UploadTokenCache.invalidate(i);
                     } else if (p_227992_3_.statusCode == 400 && p_227992_3_.errorMessage != null) {
                        this.errorMessage = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.errorMessage});
                     } else {
                        this.errorMessage = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.statusCode});
                     }

                  });

                  while(!fileupload.isFinished()) {
                     if (this.cancelled) {
                        fileupload.cancel();
                        this.uploadCancelled();
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException var19) {
                        LOGGER.error("Failed to check Realms file upload status");
                     }
                  }

                  return;
               }

               long k = file1.length();
               RealmsUploadScreen.Unit realmsuploadscreen$unit = func_224665_b(k);
               RealmsUploadScreen.Unit realmsuploadscreen$unit1 = func_224665_b(5368709120L);
               if (func_224667_b(k, realmsuploadscreen$unit).equals(func_224667_b(5368709120L, realmsuploadscreen$unit1)) && realmsuploadscreen$unit != RealmsUploadScreen.Unit.B) {
                  RealmsUploadScreen.Unit realmsuploadscreen$unit2 = RealmsUploadScreen.Unit.values()[realmsuploadscreen$unit.ordinal() - 1];
                  this.errorMessage = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.selectedLevel.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(k, realmsuploadscreen$unit2), func_224667_b(5368709120L, realmsuploadscreen$unit2)});
                  return;
               }

               this.errorMessage = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.selectedLevel.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(k, realmsuploadscreen$unit), func_224667_b(5368709120L, realmsuploadscreen$unit1)});
               return;
            }
         } catch (IOException ioexception) {
            this.errorMessage = getLocalizedString("mco.upload.failed", new Object[]{ioexception.getMessage()});
            return;
         } catch (RealmsServiceException realmsserviceexception) {
            this.errorMessage = getLocalizedString("mco.upload.failed", new Object[]{realmsserviceexception.toString()});
            return;
         } catch (InterruptedException var23) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } finally {
            this.uploadFinished = true;
            if (field_224718_w.isHeldByCurrentThread()) {
               field_224718_w.unlock();
               this.showDots = false;
               this.childrenClear();
               this.buttonsAdd(this.backButton);
               if (file1 != null) {
                  LOGGER.debug("Deleting file " + file1.getAbsolutePath());
                  file1.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void uploadCancelled() {
      this.status = getLocalizedString("mco.upload.cancelled");
      LOGGER.debug("Upload was cancelled");
   }

   private boolean verify(File p_224692_1_) {
      return p_224692_1_.length() < 5368709120L;
   }

   private File tarGzipArchive(File p_224675_1_) throws IOException {
      TarArchiveOutputStream tararchiveoutputstream = null;

      File file2;
      try {
         File file1 = File.createTempFile("realms-upload-file", ".tar.gz");
         tararchiveoutputstream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file1)));
         tararchiveoutputstream.setLongFileMode(3);
         this.addFileToTarGz(tararchiveoutputstream, p_224675_1_.getAbsolutePath(), "world", true);
         tararchiveoutputstream.finish();
         file2 = file1;
      } finally {
         if (tararchiveoutputstream != null) {
            tararchiveoutputstream.close();
         }

      }

      return file2;
   }

   private void addFileToTarGz(TarArchiveOutputStream p_224669_1_, String p_224669_2_, String p_224669_3_, boolean p_224669_4_) throws IOException {
      if (!this.cancelled) {
         File file1 = new File(p_224669_2_);
         String s = p_224669_4_ ? p_224669_3_ : p_224669_3_ + file1.getName();
         TarArchiveEntry tararchiveentry = new TarArchiveEntry(file1, s);
         p_224669_1_.putArchiveEntry(tararchiveentry);
         if (file1.isFile()) {
            IOUtils.copy(new FileInputStream(file1), p_224669_1_);
            p_224669_1_.closeArchiveEntry();
         } else {
            p_224669_1_.closeArchiveEntry();
            File[] afile = file1.listFiles();
            if (afile != null) {
               for(File file2 : afile) {
                  this.addFileToTarGz(p_224669_1_, file2.getAbsolutePath(), s + "/", false);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum Unit {
      B,
      KB,
      MB,
      GB;
   }
}