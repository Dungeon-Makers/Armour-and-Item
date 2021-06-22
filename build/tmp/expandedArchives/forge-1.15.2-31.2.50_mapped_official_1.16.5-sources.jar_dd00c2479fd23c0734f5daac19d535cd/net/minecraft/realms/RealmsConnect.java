package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted;
   private NetworkManager connection;

   public RealmsConnect(RealmsScreen p_i1079_1_) {
      this.onlineScreen = p_i1079_1_;
   }

   public void connect(final String p_connect_1_, final int p_connect_2_) {
      Realms.setConnectedToRealms(true);
      Realms.narrateNow(Realms.getLocalizedString("mco.connect.success"));
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress inetaddress = null;

            try {
               inetaddress = InetAddress.getByName(p_connect_1_);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = NetworkManager.connectToServer(inetaddress, p_connect_2_, Minecraft.getInstance().options.useNativeTransport());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.setListener(new ClientLoginNetHandler(RealmsConnect.this.connection, Minecraft.getInstance(), RealmsConnect.this.onlineScreen.getProxy(), (p_209500_0_) -> {
               }));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new CHandshakePacket(p_connect_1_, p_connect_2_, ProtocolType.LOGIN));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new CLoginStartPacket(Minecraft.getInstance().getUser().getGameProfile()));
            } catch (UnknownHostException unknownhostexception) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)unknownhostexception);
               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslationTextComponent("disconnect.genericReason", "Unknown host '" + p_connect_1_ + "'")));
            } catch (Exception exception) {
               Realms.clearResourcePack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)exception);
               String s = exception.toString();
               if (inetaddress != null) {
                  String s1 = inetaddress + ":" + p_connect_2_;
                  s = s.replaceAll(s1, "");
               }

               Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslationTextComponent("disconnect.genericReason", s)));
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if (this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect(new TranslationTextComponent("disconnect.genericReason"));
         this.connection.handleDisconnection();
      }

   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }

   }
}