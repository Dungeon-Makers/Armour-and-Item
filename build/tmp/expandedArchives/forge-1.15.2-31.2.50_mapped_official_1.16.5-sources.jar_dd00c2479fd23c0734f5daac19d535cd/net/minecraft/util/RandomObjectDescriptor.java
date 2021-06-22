package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RandomObjectDescriptor {
   private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook"};
   private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue"};

   public static String getEntityName(UUID p_229748_0_) {
      Random random = getRandom(p_229748_0_);
      return getRandomString(random, NAMES_FIRST_PART) + getRandomString(random, NAMES_SECOND_PART);
   }

   private static String getRandomString(Random p_218809_0_, String[] p_218809_1_) {
      return p_218809_1_[p_218809_0_.nextInt(p_218809_1_.length)];
   }

   private static Random getRandom(UUID p_218808_0_) {
      return new Random((long)(p_218808_0_.hashCode() >> 2));
   }
}