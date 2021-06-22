package net.minecraft.network.rcon;

import net.minecraft.server.dedicated.ServerProperties;

public interface IServer {
   ServerProperties getProperties();

   String getServerIp();

   int getServerPort();

   String getServerName();

   String getServerVersion();

   int getPlayerCount();

   int getMaxPlayers();

   String[] getPlayerNames();

   String func_71270_I();

   String getPluginNames();

   String runCommand(String p_71252_1_);

   boolean func_71239_B();

   void func_71244_g(String p_71244_1_);

   void func_71236_h(String p_71236_1_);

   void func_71201_j(String p_71201_1_);

   void func_71198_k(String p_71198_1_);
}