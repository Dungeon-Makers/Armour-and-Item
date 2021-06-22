package net.minecraft.client.gui.fonts;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextInputUtil {
   private final Minecraft field_216900_a;
   private final FontRenderer field_216901_b;
   private final Supplier<String> getMessageFn;
   private final Consumer<String> setMessageFn;
   private final int field_216904_e;
   private int cursorPos;
   private int selectionPos;

   public TextInputUtil(Minecraft p_i51124_1_, Supplier<String> p_i51124_2_, Consumer<String> p_i51124_3_, int p_i51124_4_) {
      this.field_216900_a = p_i51124_1_;
      this.field_216901_b = p_i51124_1_.font;
      this.getMessageFn = p_i51124_2_;
      this.setMessageFn = p_i51124_3_;
      this.field_216904_e = p_i51124_4_;
      this.func_216899_b();
   }

   public boolean charTyped(char p_216894_1_) {
      if (SharedConstants.isAllowedChatCharacter(p_216894_1_)) {
         this.insertText(Character.toString(p_216894_1_));
      }

      return true;
   }

   private void insertText(String p_216892_1_) {
      if (this.selectionPos != this.cursorPos) {
         this.func_216893_f();
      }

      String s = this.getMessageFn.get();
      this.cursorPos = MathHelper.clamp(this.cursorPos, 0, s.length());
      String s1 = (new StringBuilder(s)).insert(this.cursorPos, p_216892_1_).toString();
      if (this.field_216901_b.width(s1) <= this.field_216904_e) {
         this.setMessageFn.accept(s1);
         this.selectionPos = this.cursorPos = Math.min(s1.length(), this.cursorPos + p_216892_1_.length());
      }

   }

   public boolean keyPressed(int p_216897_1_) {
      String s = this.getMessageFn.get();
      if (Screen.isSelectAll(p_216897_1_)) {
         this.selectionPos = 0;
         this.cursorPos = s.length();
         return true;
      } else if (Screen.isCopy(p_216897_1_)) {
         this.field_216900_a.keyboardHandler.setClipboard(this.func_216895_e());
         return true;
      } else if (Screen.isPaste(p_216897_1_)) {
         this.insertText(SharedConstants.filterText(TextFormatting.stripFormatting(this.field_216900_a.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
         this.selectionPos = this.cursorPos;
         return true;
      } else if (Screen.isCut(p_216897_1_)) {
         this.field_216900_a.keyboardHandler.setClipboard(this.func_216895_e());
         this.func_216893_f();
         return true;
      } else if (p_216897_1_ == 259) {
         if (!s.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
               this.func_216893_f();
            } else if (this.cursorPos > 0) {
               s = (new StringBuilder(s)).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
               this.selectionPos = this.cursorPos = Math.max(0, this.cursorPos - 1);
               this.setMessageFn.accept(s);
            }
         }

         return true;
      } else if (p_216897_1_ == 261) {
         if (!s.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
               this.func_216893_f();
            } else if (this.cursorPos < s.length()) {
               s = (new StringBuilder(s)).deleteCharAt(Math.max(0, this.cursorPos)).toString();
               this.setMessageFn.accept(s);
            }
         }

         return true;
      } else if (p_216897_1_ == 263) {
         int j = this.field_216901_b.isBidirectional() ? 1 : -1;
         if (Screen.hasControlDown()) {
            this.cursorPos = this.field_216901_b.func_216863_a(s, j, this.cursorPos, true);
         } else {
            this.cursorPos = Math.max(0, Math.min(s.length(), this.cursorPos + j));
         }

         if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if (p_216897_1_ == 262) {
         int i = this.field_216901_b.isBidirectional() ? -1 : 1;
         if (Screen.hasControlDown()) {
            this.cursorPos = this.field_216901_b.func_216863_a(s, i, this.cursorPos, true);
         } else {
            this.cursorPos = Math.max(0, Math.min(s.length(), this.cursorPos + i));
         }

         if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if (p_216897_1_ == 268) {
         this.cursorPos = 0;
         if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else if (p_216897_1_ == 269) {
         this.cursorPos = this.getMessageFn.get().length();
         if (!Screen.hasShiftDown()) {
            this.selectionPos = this.cursorPos;
         }

         return true;
      } else {
         return false;
      }
   }

   private String func_216895_e() {
      String s = this.getMessageFn.get();
      int i = Math.min(this.cursorPos, this.selectionPos);
      int j = Math.max(this.cursorPos, this.selectionPos);
      return s.substring(i, j);
   }

   private void func_216893_f() {
      if (this.selectionPos != this.cursorPos) {
         String s = this.getMessageFn.get();
         int i = Math.min(this.cursorPos, this.selectionPos);
         int j = Math.max(this.cursorPos, this.selectionPos);
         String s1 = s.substring(0, i) + s.substring(j);
         this.cursorPos = i;
         this.selectionPos = this.cursorPos;
         this.setMessageFn.accept(s1);
      }
   }

   public void func_216899_b() {
      this.selectionPos = this.cursorPos = this.getMessageFn.get().length();
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }
}