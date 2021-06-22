package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatScreen extends Screen {
   private String historyBuffer = "";
   private int historyPos = -1;
   protected TextFieldWidget input;
   private String initial = "";
   private CommandSuggestionHelper commandSuggestions;

   public ChatScreen(String p_i1024_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.initial = p_i1024_1_;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.get("chat.editBox")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ChatScreen.this.commandSuggestions.getNarrationMessage();
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.children.add(this.input);
      this.commandSuggestions = new CommandSuggestionHelper(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestions.updateCommandInfo();
      this.setInitialFocus(this.input);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.input.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.setChatLine(s);
      this.commandSuggestions.updateCommandInfo();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   public void tick() {
      this.input.tick();
   }

   private void onEdited(String p_212997_1_) {
      String s = this.input.getValue();
      this.commandSuggestions.setAllowSuggestions(!s.equals(this.initial));
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.commandSuggestions.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ == 256) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (p_keyPressed_1_ == 265) {
            this.moveInHistory(-1);
            return true;
         } else if (p_keyPressed_1_ == 264) {
            this.moveInHistory(1);
            return true;
         } else if (p_keyPressed_1_ == 266) {
            this.minecraft.gui.getChat().scrollChat((double)(this.minecraft.gui.getChat().getLinesPerPage() - 1));
            return true;
         } else if (p_keyPressed_1_ == 267) {
            this.minecraft.gui.getChat().scrollChat((double)(-this.minecraft.gui.getChat().getLinesPerPage() + 1));
            return true;
         } else {
            return false;
         }
      } else {
         String s = this.input.getValue().trim();
         if (!s.isEmpty()) {
            this.sendMessage(s);
         }

         this.minecraft.setScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (p_mouseScrolled_5_ > 1.0D) {
         p_mouseScrolled_5_ = 1.0D;
      }

      if (p_mouseScrolled_5_ < -1.0D) {
         p_mouseScrolled_5_ = -1.0D;
      }

      if (this.commandSuggestions.mouseScrolled(p_mouseScrolled_5_)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            p_mouseScrolled_5_ *= 7.0D;
         }

         this.minecraft.gui.getChat().scrollChat(p_mouseScrolled_5_);
         return true;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.commandSuggestions.mouseClicked((double)((int)p_mouseClicked_1_), (double)((int)p_mouseClicked_3_), p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_5_ == 0) {
            ITextComponent itextcomponent = this.minecraft.gui.getChat().func_194817_a(p_mouseClicked_1_, p_mouseClicked_3_);
            if (itextcomponent != null && this.handleComponentClicked(itextcomponent)) {
               return true;
            }
         }

         return this.input.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
      if (p_insertText_2_) {
         this.input.setValue(p_insertText_1_);
      } else {
         this.input.insertText(p_insertText_1_);
      }

   }

   public void moveInHistory(int p_146402_1_) {
      int i = this.historyPos + p_146402_1_;
      int j = this.minecraft.gui.getChat().getRecentChat().size();
      i = MathHelper.clamp(i, 0, j);
      if (i != this.historyPos) {
         if (i == j) {
            this.historyPos = j;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == j) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get(i));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = i;
         }
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
      this.input.render(p_render_1_, p_render_2_, p_render_3_);
      this.commandSuggestions.func_228114_a_(p_render_1_, p_render_2_);
      ITextComponent itextcomponent = this.minecraft.gui.getChat().func_194817_a((double)p_render_1_, (double)p_render_2_);
      if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
         this.renderComponentHoverEffect(itextcomponent, p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String p_208604_1_) {
      this.input.setValue(p_208604_1_);
   }
}