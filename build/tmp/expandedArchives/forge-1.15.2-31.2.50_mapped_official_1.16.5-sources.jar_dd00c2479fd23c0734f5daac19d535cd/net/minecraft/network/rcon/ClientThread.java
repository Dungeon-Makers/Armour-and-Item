package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientThread extends RConThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean authed;
   private Socket client;
   private final byte[] buf = new byte[1460];
   private final String rconPassword;

   ClientThread(IServer p_i50687_1_, String p_i50687_2_, Socket p_i50687_3_) {
      super(p_i50687_1_, "RCON Client");
      this.client = p_i50687_3_;

      try {
         this.client.setSoTimeout(0);
      } catch (Exception var5) {
         this.running = false;
      }

      this.rconPassword = p_i50687_2_;
      this.func_72609_b("Rcon connection from: " + p_i50687_3_.getInetAddress());
   }

   public void run() {
         try {
            while(true) {
            if (!this.running) {
               return;
            }

            BufferedInputStream bufferedinputstream = new BufferedInputStream(this.client.getInputStream());
            int i = bufferedinputstream.read(this.buf, 0, 1460);
            if (10 <= i) {
               int j = 0;
               int k = RConUtils.intFromByteArray(this.buf, 0, i);
               if (k != i - 4) {
                  return;
               }

               j = j + 4;
               int l = RConUtils.intFromByteArray(this.buf, j, i);
               j = j + 4;
               int i1 = RConUtils.intFromByteArray(this.buf, j);
               j = j + 4;
               switch(i1) {
               case 2:
                  if (this.authed) {
                     String s1 = RConUtils.stringFromByteArray(this.buf, j, i);

                     try {
                        this.sendCmdResponse(l, this.field_72617_b.runCommand(s1));
                     } catch (Exception exception) {
                        this.sendCmdResponse(l, "Error executing: " + s1 + " (" + exception.getMessage() + ")");
                     }
                     continue;
                  }

                  this.sendAuthFailure();
                  continue;
               case 3:
                  String s = RConUtils.stringFromByteArray(this.buf, j, i);
                  int j1 = j + s.length();
                  if (!s.isEmpty() && s.equals(this.rconPassword)) {
                     this.authed = true;
                     this.send(l, 2, "");
                     continue;
                  }

                  this.authed = false;
                  this.sendAuthFailure();
                  continue;
               default:
                  this.sendCmdResponse(l, String.format("Unknown request %s", Integer.toHexString(i1)));
                  continue;
               }
            }
            }
         } catch (SocketTimeoutException var17) {
            return;
         } catch (IOException var18) {
            return;
         } catch (Exception exception1) {
            LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception1);
            return;
         } finally {
            this.closeSocket();
         }
      }

   private void send(int p_72654_1_, int p_72654_2_, String p_72654_3_) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1248);
      DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
      byte[] abyte = p_72654_3_.getBytes("UTF-8");
      dataoutputstream.writeInt(Integer.reverseBytes(abyte.length + 10));
      dataoutputstream.writeInt(Integer.reverseBytes(p_72654_1_));
      dataoutputstream.writeInt(Integer.reverseBytes(p_72654_2_));
      dataoutputstream.write(abyte);
      dataoutputstream.write(0);
      dataoutputstream.write(0);
      this.client.getOutputStream().write(bytearrayoutputstream.toByteArray());
   }

   private void sendAuthFailure() throws IOException {
      this.send(-1, 2, "");
   }

   private void sendCmdResponse(int p_72655_1_, String p_72655_2_) throws IOException {
      int i = p_72655_2_.length();

      while(true) {
         int j = 4096 <= i ? 4096 : i;
         this.send(p_72655_1_, 0, p_72655_2_.substring(0, j));
         p_72655_2_ = p_72655_2_.substring(j);
         i = p_72655_2_.length();
         if (0 == i) {
            break;
         }
      }

   }

   public void stop() {
      super.stop();
      this.closeSocket();
   }

   private void closeSocket() {
      if (null != this.client) {
         try {
            this.client.close();
         } catch (IOException ioexception) {
            this.func_72606_c("IO: " + ioexception.getMessage());
         }

         this.client = null;
      }
   }
}