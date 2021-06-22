package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.Empty3i;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.UndeclaredException;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Main {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void main(String[] p_main_0_) {
      OptionParser optionparser = new OptionParser();
      optionparser.allowsUnrecognizedOptions();
      optionparser.accepts("demo");
      optionparser.accepts("fullscreen");
      optionparser.accepts("checkGlErrors");
      OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
      OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L);
      OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
      OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      OptionSpec<Integer> optionspec15 = optionparser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec<Integer> optionspec16 = optionparser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec<String> optionspec17 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec18 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec19 = optionparser.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> optionspec20 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
      OptionSpec<String> optionspec21 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release");
      OptionSpec<String> optionspec22 = optionparser.nonOptions();
      OptionSet optionset = optionparser.parse(p_main_0_);
      List<String> list = optionset.valuesOf(optionspec22);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String s = parseArgument(optionset, optionspec5);
      Proxy proxy = Proxy.NO_PROXY;
      if (s != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, parseArgument(optionset, optionspec6)));
         } catch (Exception var68) {
            ;
         }
      }

      final String s1 = parseArgument(optionset, optionspec7);
      final String s2 = parseArgument(optionset, optionspec8);
      if (!proxy.equals(Proxy.NO_PROXY) && stringHasValue(s1) && stringHasValue(s2)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(s1, s2.toCharArray());
            }
         });
      }

      int i = parseArgument(optionset, optionspec13);
      int j = parseArgument(optionset, optionspec14);
      OptionalInt optionalint = ofNullable(parseArgument(optionset, optionspec15));
      OptionalInt optionalint1 = ofNullable(parseArgument(optionset, optionspec16));
      boolean flag = optionset.has("fullscreen");
      boolean flag1 = optionset.has("demo");
      String s3 = parseArgument(optionset, optionspec12);
      Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap propertymap = JSONUtils.fromJson(gson, parseArgument(optionset, optionspec17), PropertyMap.class);
      PropertyMap propertymap1 = JSONUtils.fromJson(gson, parseArgument(optionset, optionspec18), PropertyMap.class);
      String s4 = parseArgument(optionset, optionspec21);
      File file1 = parseArgument(optionset, optionspec2);
      File file2 = optionset.has(optionspec3) ? parseArgument(optionset, optionspec3) : new File(file1, "assets/");
      File file3 = optionset.has(optionspec4) ? parseArgument(optionset, optionspec4) : new File(file1, "resourcepacks/");
      String s5 = optionset.has(optionspec10) ? optionspec10.value(optionset) : PlayerEntity.createPlayerUUID(optionspec9.value(optionset)).toString();
      String s6 = optionset.has(optionspec19) ? optionspec19.value(optionset) : null;
      String s7 = parseArgument(optionset, optionspec);
      Integer integer = parseArgument(optionset, optionspec1);
      CrashReport.preload();
      Session session = new Session(optionspec9.value(optionset), s5, optionspec11.value(optionset), optionspec20.value(optionset));
      GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new ScreenSize(i, j, optionalint, optionalint1, flag), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag1, s3, s4), new GameConfiguration.ServerInformation(s7, integer));
      Thread thread = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft minecraft1 = Minecraft.getInstance();
            if (minecraft1 != null) {
               IntegratedServer integratedserver = minecraft1.getSingleplayerServer();
               if (integratedserver != null) {
                  integratedserver.halt(true);
               }

            }
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(thread);
      new Empty3i();

      final Minecraft minecraft;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         minecraft = new Minecraft(gameconfiguration);
         RenderSystem.finishInitialization();
      } catch (UndeclaredException undeclaredexception) {
         LOGGER.warn("Failed to create window: ", (Throwable)undeclaredexception);
         return;
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.forThrowable(throwable1, "Initializing game");
         crashreport.addCategory("Initialization");
         Minecraft.fillReport((LanguageManager)null, gameconfiguration.game.launchVersion, (GameSettings)null, crashreport);
         Minecraft.crash(crashreport);
         return;
      }

      Thread thread1;
      if (minecraft.renderOnThread()) {
         thread1 = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  minecraft.run();
               } catch (Throwable throwable2) {
                  Main.LOGGER.error("Exception in client thread", throwable2);
               }

            }
         };
         thread1.start();

         while(minecraft.isRunning()) {
            ;
         }
      } else {
         thread1 = null;

         try {
            RenderSystem.initGameThread(false);
            minecraft.run();
         } catch (Throwable throwable) {
            LOGGER.error("Unhandled game exception", throwable);
         }
      }

      try {
         minecraft.stop();
         if (thread1 != null) {
            thread1.join();
         }
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedexception);
      } finally {
         minecraft.destroy();
      }

   }

   private static OptionalInt ofNullable(@Nullable Integer p_224732_0_) {
      return p_224732_0_ != null ? OptionalInt.of(p_224732_0_) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T parseArgument(OptionSet p_206236_0_, OptionSpec<T> p_206236_1_) {
      try {
         return p_206236_0_.valueOf(p_206236_1_);
      } catch (Throwable throwable) {
         if (p_206236_1_ instanceof ArgumentAcceptingOptionSpec) {
            ArgumentAcceptingOptionSpec<T> argumentacceptingoptionspec = (ArgumentAcceptingOptionSpec)p_206236_1_;
            List<T> list = argumentacceptingoptionspec.defaultValues();
            if (!list.isEmpty()) {
               return list.get(0);
            }
         }

         throw throwable;
      }
   }

   private static boolean stringHasValue(@Nullable String p_110121_0_) {
      return p_110121_0_ != null && !p_110121_0_.isEmpty();
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}