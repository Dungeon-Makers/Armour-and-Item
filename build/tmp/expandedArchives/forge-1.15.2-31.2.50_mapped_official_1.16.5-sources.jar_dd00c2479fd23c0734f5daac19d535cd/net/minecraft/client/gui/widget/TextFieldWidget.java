package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextFieldWidget extends Widget implements IRenderable, IGuiEventListener {
   private final FontRenderer font;
   private String value = "";
   private int maxLength = 32;
   private int frame;
   private boolean bordered = true;
   private boolean canLoseFocus = true;
   private boolean isEditable = true;
   private boolean shiftPressed;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor = 14737632;
   private int textColorUneditable = 7368816;
   private String suggestion;
   private Consumer<String> responder;
   private Predicate<String> filter = Predicates.alwaysTrue();
   private BiFunction<String, Integer, String> formatter = (p_195610_0_, p_195610_1_) -> {
      return p_195610_0_;
   };

   public TextFieldWidget(FontRenderer p_i51137_1_, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String p_i51137_6_) {
      this(p_i51137_1_, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, (TextFieldWidget)null, p_i51137_6_);
   }

   public TextFieldWidget(FontRenderer p_i51138_1_, int p_i51138_2_, int p_i51138_3_, int p_i51138_4_, int p_i51138_5_, @Nullable TextFieldWidget p_i51138_6_, String p_i51138_7_) {
      super(p_i51138_2_, p_i51138_3_, p_i51138_4_, p_i51138_5_, p_i51138_7_);
      this.font = p_i51138_1_;
      if (p_i51138_6_ != null) {
         this.setValue(p_i51138_6_.getValue());
      }

   }

   public void setResponder(Consumer<String> p_212954_1_) {
      this.responder = p_212954_1_;
   }

   public void setFormatter(BiFunction<String, Integer, String> p_195607_1_) {
      this.formatter = p_195607_1_;
   }

   public void tick() {
      ++this.frame;
   }

   protected String getNarrationMessage() {
      String s = this.getMessage();
      return s.isEmpty() ? "" : I18n.get("gui.narrate.editBox", s, this.value);
   }

   public void setValue(String p_146180_1_) {
      if (this.filter.test(p_146180_1_)) {
         if (p_146180_1_.length() > this.maxLength) {
            this.value = p_146180_1_.substring(0, this.maxLength);
         } else {
            this.value = p_146180_1_;
         }

         this.moveCursorToEnd();
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(p_146180_1_);
      }
   }

   public String getValue() {
      return this.value;
   }

   public String getHighlighted() {
      int i = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int j = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      return this.value.substring(i, j);
   }

   public void setFilter(Predicate<String> p_200675_1_) {
      this.filter = p_200675_1_;
   }

   public void insertText(String p_146191_1_) {
      String s = "";
      String s1 = SharedConstants.filterText(p_146191_1_);
      int i = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int j = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      int k = this.maxLength - this.value.length() - (i - j);
      if (!this.value.isEmpty()) {
         s = s + this.value.substring(0, i);
      }

      int l;
      if (k < s1.length()) {
         s = s + s1.substring(0, k);
         l = k;
      } else {
         s = s + s1;
         l = s1.length();
      }

      if (!this.value.isEmpty() && j < this.value.length()) {
         s = s + this.value.substring(j);
      }

      if (this.filter.test(s)) {
         this.value = s;
         this.setCursorPosition(i + l);
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(this.value);
      }
   }

   private void onValueChange(String p_212951_1_) {
      if (this.responder != null) {
         this.responder.accept(p_212951_1_);
      }

      this.nextNarration = Util.getMillis() + 500L;
   }

   private void deleteText(int p_212950_1_) {
      if (Screen.hasControlDown()) {
         this.deleteWords(p_212950_1_);
      } else {
         this.deleteChars(p_212950_1_);
      }

   }

   public void deleteWords(int p_146177_1_) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            this.deleteChars(this.getWordPosition(p_146177_1_) - this.cursorPos);
         }
      }
   }

   public void deleteChars(int p_146175_1_) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            boolean flag = p_146175_1_ < 0;
            int i = flag ? this.cursorPos + p_146175_1_ : this.cursorPos;
            int j = flag ? this.cursorPos : this.cursorPos + p_146175_1_;
            String s = "";
            if (i >= 0) {
               s = this.value.substring(0, i);
            }

            if (j < this.value.length()) {
               s = s + this.value.substring(j);
            }

            if (this.filter.test(s)) {
               this.value = s;
               if (flag) {
                  this.moveCursor(p_146175_1_);
               }

               this.onValueChange(this.value);
            }
         }
      }
   }

   public int getWordPosition(int p_146187_1_) {
      return this.getWordPosition(p_146187_1_, this.getCursorPosition());
   }

   private int getWordPosition(int p_146183_1_, int p_146183_2_) {
      return this.getWordPosition(p_146183_1_, p_146183_2_, true);
   }

   private int getWordPosition(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
      int i = p_146197_2_;
      boolean flag = p_146197_1_ < 0;
      int j = Math.abs(p_146197_1_);

      for(int k = 0; k < j; ++k) {
         if (!flag) {
            int l = this.value.length();
            i = this.value.indexOf(32, i);
            if (i == -1) {
               i = l;
            } else {
               while(p_146197_3_ && i < l && this.value.charAt(i) == ' ') {
                  ++i;
               }
            }
         } else {
            while(p_146197_3_ && i > 0 && this.value.charAt(i - 1) == ' ') {
               --i;
            }

            while(i > 0 && this.value.charAt(i - 1) != ' ') {
               --i;
            }
         }
      }

      return i;
   }

   public void moveCursor(int p_146182_1_) {
      this.moveCursorTo(this.cursorPos + p_146182_1_);
   }

   public void moveCursorTo(int p_146190_1_) {
      this.setCursorPosition(p_146190_1_);
      if (!this.shiftPressed) {
         this.setHighlightPos(this.cursorPos);
      }

      this.onValueChange(this.value);
   }

   public void setCursorPosition(int p_212422_1_) {
      this.cursorPos = MathHelper.clamp(p_212422_1_, 0, this.value.length());
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.value.length());
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (!this.canConsumeInput()) {
         return false;
      } else {
         this.shiftPressed = Screen.hasShiftDown();
         if (Screen.isSelectAll(p_keyPressed_1_)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
         } else if (Screen.isCopy(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
         } else if (Screen.isPaste(p_keyPressed_1_)) {
            if (this.isEditable) {
               this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }

            return true;
         } else if (Screen.isCut(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
               this.insertText("");
            }

            return true;
         } else {
            switch(p_keyPressed_1_) {
            case 259:
               if (this.isEditable) {
                  this.shiftPressed = false;
                  this.deleteText(-1);
                  this.shiftPressed = Screen.hasShiftDown();
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return false;
            case 261:
               if (this.isEditable) {
                  this.shiftPressed = false;
                  this.deleteText(1);
                  this.shiftPressed = Screen.hasShiftDown();
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(1));
               } else {
                  this.moveCursor(1);
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(-1));
               } else {
                  this.moveCursor(-1);
               }

               return true;
            case 268:
               this.moveCursorToStart();
               return true;
            case 269:
               this.moveCursorToEnd();
               return true;
            }
         }
      }
   }

   public boolean canConsumeInput() {
      return this.isVisible() && this.isFocused() && this.isEditable();
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (!this.canConsumeInput()) {
         return false;
      } else if (SharedConstants.isAllowedChatCharacter(p_charTyped_1_)) {
         if (this.isEditable) {
            this.insertText(Character.toString(p_charTyped_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (!this.isVisible()) {
         return false;
      } else {
         boolean flag = p_mouseClicked_1_ >= (double)this.x && p_mouseClicked_1_ < (double)(this.x + this.width) && p_mouseClicked_3_ >= (double)this.y && p_mouseClicked_3_ < (double)(this.y + this.height);
         if (this.canLoseFocus) {
            this.setFocus(flag);
         }

         if (this.isFocused() && flag && p_mouseClicked_5_ == 0) {
            int i = MathHelper.floor(p_mouseClicked_1_) - this.x;
            if (this.bordered) {
               i -= 4;
            }

            String s = this.font.func_78269_a(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.func_78269_a(s, i).length() + this.displayPos);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setFocus(boolean p_146195_1_) {
      super.setFocused(p_146195_1_);
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.isVisible()) {
         if (this.isBordered()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int i = this.isEditable ? this.textColor : this.textColorUneditable;
         int j = this.cursorPos - this.displayPos;
         int k = this.highlightPos - this.displayPos;
         String s = this.font.func_78269_a(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean flag = j >= 0 && j <= s.length();
         boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
         int l = this.bordered ? this.x + 4 : this.x;
         int i1 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
         int j1 = l;
         if (k > s.length()) {
            k = s.length();
         }

         if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = this.font.func_175063_a(this.formatter.apply(s1, this.displayPos), (float)l, (float)i1, i);
         }

         boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int k1 = j1;
         if (!flag) {
            k1 = j > 0 ? l + this.width : l;
         } else if (flag2) {
            k1 = j1 - 1;
            --j1;
         }

         if (!s.isEmpty() && flag && j < s.length()) {
            this.font.func_175063_a(this.formatter.apply(s.substring(j), this.cursorPos), (float)j1, (float)i1, i);
         }

         if (!flag2 && this.suggestion != null) {
            this.font.func_175063_a(this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
         }

         if (flag1) {
            if (flag2) {
               AbstractGui.fill(k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            } else {
               this.font.func_175063_a("_", (float)k1, (float)i1, i);
            }
         }

         if (k != j) {
            int l1 = l + this.font.width(s.substring(0, k));
            this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
         }

      }
   }

   private void renderHighlight(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
      if (p_146188_1_ < p_146188_3_) {
         int i = p_146188_1_;
         p_146188_1_ = p_146188_3_;
         p_146188_3_ = i;
      }

      if (p_146188_2_ < p_146188_4_) {
         int j = p_146188_2_;
         p_146188_2_ = p_146188_4_;
         p_146188_4_ = j;
      }

      if (p_146188_3_ > this.x + this.width) {
         p_146188_3_ = this.x + this.width;
      }

      if (p_146188_1_ > this.x + this.width) {
         p_146188_1_ = this.x + this.width;
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.vertex((double)p_146188_1_, (double)p_146188_4_, 0.0D).endVertex();
      bufferbuilder.vertex((double)p_146188_3_, (double)p_146188_4_, 0.0D).endVertex();
      bufferbuilder.vertex((double)p_146188_3_, (double)p_146188_2_, 0.0D).endVertex();
      bufferbuilder.vertex((double)p_146188_1_, (double)p_146188_2_, 0.0D).endVertex();
      tessellator.end();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   public void setMaxLength(int p_146203_1_) {
      this.maxLength = p_146203_1_;
      if (this.value.length() > p_146203_1_) {
         this.value = this.value.substring(0, p_146203_1_);
         this.onValueChange(this.value);
      }

   }

   private int getMaxLength() {
      return this.maxLength;
   }

   public int getCursorPosition() {
      return this.cursorPos;
   }

   private boolean isBordered() {
      return this.bordered;
   }

   public void setBordered(boolean p_146185_1_) {
      this.bordered = p_146185_1_;
   }

   public void setTextColor(int p_146193_1_) {
      this.textColor = p_146193_1_;
   }

   public void setTextColorUneditable(int p_146204_1_) {
      this.textColorUneditable = p_146204_1_;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return this.visible && this.isEditable ? super.changeFocus(p_changeFocus_1_) : false;
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return this.visible && p_isMouseOver_1_ >= (double)this.x && p_isMouseOver_1_ < (double)(this.x + this.width) && p_isMouseOver_3_ >= (double)this.y && p_isMouseOver_3_ < (double)(this.y + this.height);
   }

   protected void onFocusedChanged(boolean p_onFocusedChanged_1_) {
      if (p_onFocusedChanged_1_) {
         this.frame = 0;
      }

   }

   private boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean p_146184_1_) {
      this.isEditable = p_146184_1_;
   }

   public int getInnerWidth() {
      return this.isBordered() ? this.width - 8 : this.width;
   }

   public void setHighlightPos(int p_146199_1_) {
      int i = this.value.length();
      this.highlightPos = MathHelper.clamp(p_146199_1_, 0, i);
      if (this.font != null) {
         if (this.displayPos > i) {
            this.displayPos = i;
         }

         int j = this.getInnerWidth();
         String s = this.font.func_78269_a(this.value.substring(this.displayPos), j);
         int k = s.length() + this.displayPos;
         if (this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.func_78262_a(this.value, j, true).length();
         }

         if (this.highlightPos > k) {
            this.displayPos += this.highlightPos - k;
         } else if (this.highlightPos <= this.displayPos) {
            this.displayPos -= this.displayPos - this.highlightPos;
         }

         this.displayPos = MathHelper.clamp(this.displayPos, 0, i);
      }

   }

   public void setCanLoseFocus(boolean p_146205_1_) {
      this.canLoseFocus = p_146205_1_;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_146189_1_) {
      this.visible = p_146189_1_;
   }

   public void setSuggestion(@Nullable String p_195612_1_) {
      this.suggestion = p_195612_1_;
   }

   public int getScreenX(int p_195611_1_) {
      return p_195611_1_ > this.value.length() ? this.x : this.x + this.font.width(this.value.substring(0, p_195611_1_));
   }

   public void setX(int p_212952_1_) {
      this.x = p_212952_1_;
   }
}