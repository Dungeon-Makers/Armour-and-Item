package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Realms {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

   public static boolean isTouchScreen() {
      return Minecraft.getInstance().options.touchscreen;
   }

   public static Proxy getProxy() {
      return Minecraft.getInstance().getProxy();
   }

   public static String sessionId() {
      Session session = Minecraft.getInstance().getUser();
      return session == null ? null : session.getSessionId();
   }

   public static String userName() {
      Session session = Minecraft.getInstance().getUser();
      return session == null ? null : session.getName();
   }

   public static long currentTimeMillis() {
      return Util.getMillis();
   }

   public static String getSessionId() {
      return Minecraft.getInstance().getUser().getSessionId();
   }

   public static String getUUID() {
      return Minecraft.getInstance().getUser().getUuid();
   }

   public static String getName() {
      return Minecraft.getInstance().getUser().getName();
   }

   public static String uuidToName(String p_uuidToName_0_) {
      return Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_uuidToName_0_), (String)null), false).getName();
   }

   public static <V> CompletableFuture<V> execute(Supplier<V> p_execute_0_) {
      return Minecraft.getInstance().submit(p_execute_0_);
   }

   public static void execute(Runnable p_execute_0_) {
      Minecraft.getInstance().execute(p_execute_0_);
   }

   public static void setScreen(RealmsScreen p_setScreen_0_) {
      execute(() -> {
         setScreenDirect(p_setScreen_0_);
         return null;
      });
   }

   public static void setScreenDirect(RealmsScreen p_setScreenDirect_0_) {
      Minecraft.getInstance().setScreen(p_setScreenDirect_0_.getProxy());
   }

   public static String getGameDirectoryPath() {
      return Minecraft.getInstance().gameDirectory.getAbsolutePath();
   }

   public static int survivalId() {
      return GameType.SURVIVAL.getId();
   }

   public static int creativeId() {
      return GameType.CREATIVE.getId();
   }

   public static int adventureId() {
      return GameType.ADVENTURE.getId();
   }

   public static int spectatorId() {
      return GameType.SPECTATOR.getId();
   }

   public static void setConnectedToRealms(boolean p_setConnectedToRealms_0_) {
      Minecraft.getInstance().setConnectedToRealms(p_setConnectedToRealms_0_);
   }

   public static CompletableFuture<?> downloadResourcePack(String p_downloadResourcePack_0_, String p_downloadResourcePack_1_) {
      return Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(p_downloadResourcePack_0_, p_downloadResourcePack_1_);
   }

   public static void clearResourcePack() {
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.getInstance().options.realmsNotifications;
   }

   public static boolean inTitleScreen() {
      return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof MainMenuScreen;
   }

   public static void deletePlayerTag(File p_deletePlayerTag_0_) {
      if (p_deletePlayerTag_0_.exists()) {
         try {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(p_deletePlayerTag_0_));
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
            compoundnbt1.remove("Player");
            CompressedStreamTools.writeCompressed(compoundnbt, new FileOutputStream(p_deletePlayerTag_0_));
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

   }

   public static void openUri(String p_openUri_0_) {
      Util.getPlatform().openUri(p_openUri_0_);
   }

   public static void setClipboard(String p_setClipboard_0_) {
      Minecraft.getInstance().keyboardHandler.setClipboard(p_setClipboard_0_);
   }

   public static String getMinecraftVersionString() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public static ResourceLocation resourceLocation(String p_resourceLocation_0_) {
      return new ResourceLocation(p_resourceLocation_0_);
   }

   public static String getLocalizedString(String p_getLocalizedString_0_, Object... p_getLocalizedString_1_) {
      return I18n.get(p_getLocalizedString_0_, p_getLocalizedString_1_);
   }

   public static void bind(String p_bind_0_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_bind_0_);
      Minecraft.getInstance().getTextureManager().bind(resourcelocation);
   }

   public static void narrateNow(String p_narrateNow_0_) {
      NarratorChatListener narratorchatlistener = NarratorChatListener.INSTANCE;
      narratorchatlistener.clear();
      narratorchatlistener.handle(ChatType.SYSTEM, new StringTextComponent(fixNarrationNewlines(p_narrateNow_0_)));
   }

   private static String fixNarrationNewlines(String p_fixNarrationNewlines_0_) {
      return p_fixNarrationNewlines_0_.replace("\\n", System.lineSeparator());
   }

   public static void narrateNow(String... p_narrateNow_0_) {
      narrateNow(Arrays.asList(p_narrateNow_0_));
   }

   public static void narrateNow(Iterable<String> p_narrateNow_0_) {
      narrateNow(joinNarrations(p_narrateNow_0_));
   }

   public static String joinNarrations(Iterable<String> p_joinNarrations_0_) {
      return String.join(System.lineSeparator(), p_joinNarrations_0_);
   }

   public static void narrateRepeatedly(String p_narrateRepeatedly_0_) {
      REPEATED_NARRATOR.narrate(fixNarrationNewlines(p_narrateRepeatedly_0_));
   }
}