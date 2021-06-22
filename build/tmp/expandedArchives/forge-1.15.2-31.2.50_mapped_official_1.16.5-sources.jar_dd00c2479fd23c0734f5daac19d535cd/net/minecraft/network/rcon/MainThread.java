package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.ServerProperties;

public class MainThread extends RConThread {
   private final int field_72647_g;
   private String field_72652_i;
   private ServerSocket socket;
   private final String rconPassword;
   private Map<SocketAddress, ClientThread> clients;

   public MainThread(IServer p_i1538_1_) {
      super(p_i1538_1_, "RCON Listener");
      ServerProperties serverproperties = p_i1538_1_.getProperties();
      this.field_72647_g = serverproperties.rconPort;
      this.rconPassword = serverproperties.rconPassword;
      this.field_72652_i = p_i1538_1_.getServerIp();
      if (this.field_72652_i.isEmpty()) {
         this.field_72652_i = "0.0.0.0";
      }

      this.func_72646_f();
      this.socket = null;
   }

   private void func_72646_f() {
      this.clients = Maps.newHashMap();
   }

   private void clearClients() {
      Iterator<Entry<SocketAddress, ClientThread>> iterator = this.clients.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<SocketAddress, ClientThread> entry = iterator.next();
         if (!entry.getValue().isRunning()) {
            iterator.remove();
         }
      }

   }

   public void run() {
      this.func_72609_b("RCON running on " + this.field_72652_i + ":" + this.field_72647_g);

      try {
         while(this.running) {
            try {
               Socket socket = this.socket.accept();
               socket.setSoTimeout(500);
               ClientThread clientthread = new ClientThread(this.field_72617_b, this.rconPassword, socket);
               clientthread.func_72602_a();
               this.clients.put(socket.getRemoteSocketAddress(), clientthread);
               this.clearClients();
            } catch (SocketTimeoutException var7) {
               this.clearClients();
            } catch (IOException ioexception) {
               if (this.running) {
                  this.func_72609_b("IO: " + ioexception.getMessage());
               }
            }
         }
      } finally {
         this.func_72608_b(this.socket);
      }

   }

   public void func_72602_a() {
      if (this.rconPassword.isEmpty()) {
         this.func_72606_c("No rcon password set in server.properties, rcon disabled!");
      } else if (0 < this.field_72647_g && 65535 >= this.field_72647_g) {
         if (!this.running) {
            try {
               this.socket = new ServerSocket(this.field_72647_g, 0, InetAddress.getByName(this.field_72652_i));
               this.socket.setSoTimeout(500);
               super.func_72602_a();
            } catch (IOException ioexception) {
               this.func_72606_c("Unable to initialise rcon on " + this.field_72652_i + ":" + this.field_72647_g + " : " + ioexception.getMessage());
            }

         }
      } else {
         this.func_72606_c("Invalid rcon port " + this.field_72647_g + " found in server.properties, rcon disabled!");
      }
   }

   public void stop() {
      super.stop();

      for(Entry<SocketAddress, ClientThread> entry : this.clients.entrySet()) {
         entry.getValue().stop();
      }

      this.func_72608_b(this.socket);
      this.func_72646_f();
   }
}