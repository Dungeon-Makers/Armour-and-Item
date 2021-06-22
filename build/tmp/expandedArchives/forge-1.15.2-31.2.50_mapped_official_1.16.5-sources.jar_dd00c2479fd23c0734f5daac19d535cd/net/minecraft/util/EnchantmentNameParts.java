package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentNameParts {
   private static final EnchantmentNameParts INSTANCE = new EnchantmentNameParts();
   private final Random random = new Random();
   private final String[] words = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette".split(" ");

   private EnchantmentNameParts() {
   }

   public static EnchantmentNameParts getInstance() {
      return INSTANCE;
   }

   public String func_148334_a(FontRenderer p_148334_1_, int p_148334_2_) {
      int i = this.random.nextInt(2) + 3;
      String s = "";

      for(int j = 0; j < i; ++j) {
         if (j > 0) {
            s = s + " ";
         }

         s = s + this.words[this.random.nextInt(this.words.length)];
      }

      List<String> list = p_148334_1_.func_78271_c(s, p_148334_2_);
      return org.apache.commons.lang3.StringUtils.join((Iterable<?>)(list.size() >= 2 ? list.subList(0, 2) : list), " ");
   }

   public void initSeed(long p_148335_1_) {
      this.random.setSeed(p_148335_1_);
   }
}