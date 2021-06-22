package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
   private static final Logger LOGGER = LogManager.getLogger();
   private volatile boolean cancelled;
   private volatile boolean finished;
   private volatile boolean error;
   private volatile boolean extracting;
   private volatile File tempFile;
   private volatile File resourcePackPath;
   private volatile HttpGet request;
   private Thread currentThread;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long contentLength(String p_224827_1_) {
      CloseableHttpClient closeablehttpclient = null;
      HttpGet httpget = null;

      long i;
      try {
         httpget = new HttpGet(p_224827_1_);
         closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse closeablehttpresponse = closeablehttpclient.execute(httpget);
         i = Long.parseLong(closeablehttpresponse.getFirstHeader("Content-Length").getValue());
         return i;
      } catch (Throwable var16) {
         LOGGER.error("Unable to get content length for download");
         i = 0L;
      } finally {
         if (httpget != null) {
            httpget.releaseConnection();
         }

         if (closeablehttpclient != null) {
            try {
               closeablehttpclient.close();
            } catch (IOException ioexception) {
               LOGGER.error("Could not close http client", (Throwable)ioexception);
            }
         }

      }

      return i;
   }

   public void func_224830_a(WorldDownload p_224830_1_, String p_224830_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_224830_3_, RealmsAnvilLevelStorageSource p_224830_4_) {
      if (this.currentThread == null) {
         this.currentThread = new Thread(() -> {
            CloseableHttpClient closeablehttpclient = null;

            try {
               this.tempFile = File.createTempFile("backup", ".tar.gz");
               this.request = new HttpGet(p_224830_1_.downloadLink);
               closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
               HttpResponse httpresponse = closeablehttpclient.execute(this.request);
               p_224830_3_.totalBytes = Long.parseLong(httpresponse.getFirstHeader("Content-Length").getValue());
               if (httpresponse.getStatusLine().getStatusCode() == 200) {
                  OutputStream outputstream = new FileOutputStream(this.tempFile);
                  FileDownload.ProgressListener filedownload$progresslistener = new FileDownload.ProgressListener(p_224830_2_.trim(), this.tempFile, p_224830_4_, p_224830_3_, p_224830_1_);
                  FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream = new FileDownload.DownloadCountingOutputStream(outputstream);
                  filedownload$downloadcountingoutputstream.setListener(filedownload$progresslistener);
                  IOUtils.copy(httpresponse.getEntity().getContent(), filedownload$downloadcountingoutputstream);
                  return;
               }

               this.error = true;
               this.request.abort();
            } catch (Exception exception1) {
               LOGGER.error("Caught exception while downloading: " + exception1.getMessage());
               this.error = true;
               return;
            } finally {
               this.request.releaseConnection();
               if (this.tempFile != null) {
                  this.tempFile.delete();
               }

               if (!this.error) {
                  if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.request = new HttpGet(p_224830_1_.resourcePackUrl);
                        HttpResponse httpresponse1 = closeablehttpclient.execute(this.request);
                        p_224830_3_.totalBytes = Long.parseLong(httpresponse1.getFirstHeader("Content-Length").getValue());
                        if (httpresponse1.getStatusLine().getStatusCode() != 200) {
                           this.error = true;
                           this.request.abort();
                           return;
                        }

                        OutputStream outputstream1 = new FileOutputStream(this.tempFile);
                        FileDownload.ResourcePackProgressListener filedownload$resourcepackprogresslistener = new FileDownload.ResourcePackProgressListener(this.tempFile, p_224830_3_, p_224830_1_);
                        FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream1 = new FileDownload.DownloadCountingOutputStream(outputstream1);
                        filedownload$downloadcountingoutputstream1.setListener(filedownload$resourcepackprogresslistener);
                        IOUtils.copy(httpresponse1.getEntity().getContent(), filedownload$downloadcountingoutputstream1);
                     } catch (Exception exception) {
                        LOGGER.error("Caught exception while downloading: " + exception.getMessage());
                        this.error = true;
                     } finally {
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                           this.tempFile.delete();
                        }

                     }
                  } else {
                     this.finished = true;
                  }
               }

               if (closeablehttpclient != null) {
                  try {
                     closeablehttpclient.close();
                  } catch (IOException var90) {
                     LOGGER.error("Failed to close Realms download client");
                  }
               }

            }

         });
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   public void cancel() {
      if (this.request != null) {
         this.request.abort();
      }

      if (this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String p_224828_0_) {
      p_224828_0_ = p_224828_0_.replaceAll("[\\./\"]", "_");

      for(String s : INVALID_FILE_NAMES) {
         if (p_224828_0_.equalsIgnoreCase(s)) {
            p_224828_0_ = "_" + p_224828_0_ + "_";
         }
      }

      return p_224828_0_;
   }

   private void func_224831_a(String p_224831_1_, File p_224831_2_, RealmsAnvilLevelStorageSource p_224831_3_) throws IOException {
      Pattern pattern = Pattern.compile(".*-([0-9]+)$");
      int i = 1;

      for(char c0 : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_224831_1_ = p_224831_1_.replace(c0, '_');
      }

      if (StringUtils.isEmpty(p_224831_1_)) {
         p_224831_1_ = "Realm";
      }

      p_224831_1_ = findAvailableFolderName(p_224831_1_);

      try {
         for(RealmsLevelSummary realmslevelsummary : p_224831_3_.getLevelList()) {
            if (realmslevelsummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(p_224831_1_.toLowerCase(Locale.ROOT))) {
               Matcher matcher = pattern.matcher(realmslevelsummary.getLevelId());
               if (matcher.matches()) {
                  if (Integer.valueOf(matcher.group(1)) > i) {
                     i = Integer.valueOf(matcher.group(1));
                  }
               } else {
                  ++i;
               }
            }
         }
      } catch (Exception exception1) {
         LOGGER.error("Error getting level list", (Throwable)exception1);
         this.error = true;
         return;
      }

      String s;
      if (p_224831_3_.isNewLevelIdAcceptable(p_224831_1_) && i <= 1) {
         s = p_224831_1_;
      } else {
         s = p_224831_1_ + (i == 1 ? "" : "-" + i);
         if (!p_224831_3_.isNewLevelIdAcceptable(s)) {
            boolean flag = false;

            while(!flag) {
               ++i;
               s = p_224831_1_ + (i == 1 ? "" : "-" + i);
               if (p_224831_3_.isNewLevelIdAcceptable(s)) {
                  flag = true;
               }
            }
         }
      }

      TarArchiveInputStream tararchiveinputstream = null;
      File file2 = new File(Realms.getGameDirectoryPath(), "saves");

      try {
         file2.mkdir();
         tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_224831_2_))));

         for(TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry(); tararchiveentry != null; tararchiveentry = tararchiveinputstream.getNextTarEntry()) {
            File file3 = new File(file2, tararchiveentry.getName().replace("world", s));
            if (tararchiveentry.isDirectory()) {
               file3.mkdirs();
            } else {
               file3.createNewFile();
               byte[] abyte = new byte[1024];
               BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file3));
               int j = 0;

               while((j = tararchiveinputstream.read(abyte)) != -1) {
                  bufferedoutputstream.write(abyte, 0, j);
               }

               bufferedoutputstream.close();
               Object object = null;
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Error extracting world", (Throwable)exception);
         this.error = true;
      } finally {
         if (tararchiveinputstream != null) {
            tararchiveinputstream.close();
         }

         if (p_224831_2_ != null) {
            p_224831_2_.delete();
         }

         p_224831_3_.renameLevel(s, s.trim());
         File file1 = new File(file2, s + File.separator + "level.dat");
         Realms.deletePlayerTag(file1);
         this.resourcePackPath = new File(file2, s + File.separator + "resources.zip");
      }

   }

   @OnlyIn(Dist.CLIENT)
   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream p_i51649_2_) {
         super(p_i51649_2_);
      }

      public void setListener(ActionListener p_224804_1_) {
         this.listener = p_224804_1_;
      }

      protected void afterWrite(int p_afterWrite_1_) throws IOException {
         super.afterWrite(p_afterWrite_1_);
         if (this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final RealmsAnvilLevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload field_224817_f;

      private ProgressListener(String p_i51647_2_, File p_i51647_3_, RealmsAnvilLevelStorageSource p_i51647_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51647_5_, WorldDownload p_i51647_6_) {
         this.worldName = p_i51647_2_;
         this.tempFile = p_i51647_3_;
         this.levelStorageSource = p_i51647_4_;
         this.downloadStatus = p_i51647_5_;
         this.field_224817_f = p_i51647_6_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.func_224831_a(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error extracting archive", (Throwable)ioexception);
               FileDownload.this.error = true;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ResourcePackProgressListener(File p_i51645_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51645_3_, WorldDownload p_i51645_4_) {
         this.tempFile = p_i51645_2_;
         this.downloadStatus = p_i51645_3_;
         this.worldDownload = p_i51645_4_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
            try {
               String s = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
               if (s.equals(this.worldDownload.resourcePackHash)) {
                  FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                  FileDownload.this.finished = true;
               } else {
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + s + "). Deleting it.");
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error copying resourcepack file", (Object)ioexception.getMessage());
               FileDownload.this.error = true;
            }
         }

      }
   }
}