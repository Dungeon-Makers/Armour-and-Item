package net.minecraft.util.registry;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream STDOUT = System.out;
   private static boolean isBootstrapped;
   private static final Logger LOGGER = LogManager.getLogger();

   public static void bootStrap() {
      if (!isBootstrapped) {
         isBootstrapped = true;
         if (Registry.REGISTRY.isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
         } else {
            FireBlock.bootStrap();
            ComposterBlock.bootStrap();
            if (EntityType.getKey(EntityType.PLAYER) == null) {
               throw new IllegalStateException("Failed loading EntityTypes");
            } else {
               PotionBrewing.bootStrap();
               EntityOptions.bootStrap();
               IDispenseItemBehavior.bootStrap();
               ArgumentTypes.bootStrap();
               if (false) // skip redirectOutputToLog, Forge already redirects stdout and stderr output to log so that they print with more context
               wrapStreams();
            }
         }
      }
   }

   private static <T> void func_218819_a(Registry<T> p_218819_0_, Function<T, String> p_218819_1_, Set<String> p_218819_2_) {
      LanguageMap languagemap = LanguageMap.getInstance();
      p_218819_0_.iterator().forEachRemaining((p_218818_3_) -> {
         String s = p_218819_1_.apply(p_218818_3_);
         if (!languagemap.func_210813_b(s)) {
            p_218819_2_.add(s);
         }

      });
   }

   public static Set<String> getMissingTranslations() {
      Set<String> set = new TreeSet<>();
      func_218819_a(Registry.ENTITY_TYPE, EntityType::getDescriptionId, set);
      func_218819_a(Registry.MOB_EFFECT, Effect::getDescriptionId, set);
      func_218819_a(Registry.ITEM, Item::getDescriptionId, set);
      func_218819_a(Registry.ENCHANTMENT, Enchantment::getDescriptionId, set);
      func_218819_a(Registry.field_212624_m, Biome::func_210773_k, set);
      func_218819_a(Registry.BLOCK, Block::getDescriptionId, set);
      func_218819_a(Registry.CUSTOM_STAT, (p_218820_0_) -> {
         return "stat." + p_218820_0_.toString().replace(':', '.');
      }, set);
      return set;
   }

   public static void validate() {
      if (!isBootstrapped) {
         throw new IllegalArgumentException("Not bootstrapped");
      } else {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            getMissingTranslations().forEach((p_218817_0_) -> {
               LOGGER.error("Missing translations: " + p_218817_0_);
            });
         }

      }
   }

   private static void wrapStreams() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", STDOUT));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", STDOUT));
      }

   }

   public static void realStdoutPrintln(String p_179870_0_) {
      STDOUT.println(p_179870_0_);
   }
}
