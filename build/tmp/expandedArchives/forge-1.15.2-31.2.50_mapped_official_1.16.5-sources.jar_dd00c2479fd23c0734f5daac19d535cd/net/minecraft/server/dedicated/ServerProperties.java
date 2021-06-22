package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public class ServerProperties extends PropertyManager<ServerProperties> {
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String resourcePack = this.get("resource-pack", "");
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final boolean field_219018_l = this.get("generate-structures", true);
   public final Difficulty difficulty = this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
   public final GameType gamemode = this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
   public final String levelName = this.get("level-name", "world");
   public final String field_219022_p = this.get("level-seed", "");
   public final WorldType field_219023_q = this.get("level-type", WorldType::func_77130_a, WorldType::func_211888_a, WorldType.field_77137_b);
   public final String field_219024_r = this.get("generator-settings", "");
   public final int serverPort = this.get("server-port", 25565);
   public final int maxBuildHeight = this.get("max-build-height", (p_218987_0_) -> {
      return MathHelper.clamp((p_218987_0_ + 8) / 16 * 16, 64, 256);
   }, 256);
   public final Boolean announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
   public final boolean enableQuery = this.get("enable-query", false);
   public final int queryPort = this.get("query.port", 25565);
   public final boolean enableRcon = this.get("enable-rcon", false);
   public final int rconPort = this.get("rcon.port", 25575);
   public final String rconPassword = this.get("rcon.password", "");
   public final String resourcePackHash = this.getLegacyString("resource-pack-hash");
   public final String resourcePackSha1 = this.get("resource-pack-sha1", "");
   public final boolean hardcore = this.get("hardcore", false);
   public final boolean allowNether = this.get("allow-nether", true);
   public final boolean spawnMonsters = this.get("spawn-monsters", true);
   public final boolean snooperEnabled;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int functionPermissionLevel;
   public final long maxTickTime;
   public final int viewDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final PropertyManager<ServerProperties>.Property<Integer> playerIdleTimeout;
   public final PropertyManager<ServerProperties>.Property<Boolean> whiteList;

   public ServerProperties(Properties p_i50719_1_) {
      super(p_i50719_1_);
      if (this.get("snooper-enabled", true)) {
         ;
      }

      this.snooperEnabled = false;
      this.useNativeTransport = this.get("use-native-transport", true);
      this.enableCommandBlock = this.get("enable-command-block", false);
      this.spawnProtection = this.get("spawn-protection", 16);
      this.opPermissionLevel = this.get("op-permission-level", 4);
      this.functionPermissionLevel = this.get("function-permission-level", 2);
      this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.viewDistance = this.get("view-distance", 10);
      this.maxPlayers = this.get("max-players", 20);
      this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
      this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
      this.maxWorldSize = this.get("max-world-size", (p_218986_0_) -> {
         return MathHelper.clamp(p_218986_0_, 1, 29999984);
      }, 29999984);
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.whiteList = this.getMutable("white-list", false);
   }

   public static ServerProperties func_218985_a(Path p_218985_0_) {
      return new ServerProperties(loadFromFile(p_218985_0_));
   }

   protected ServerProperties func_212857_b_(Properties p_212857_1_) {
      return new ServerProperties(p_212857_1_);
   }
}