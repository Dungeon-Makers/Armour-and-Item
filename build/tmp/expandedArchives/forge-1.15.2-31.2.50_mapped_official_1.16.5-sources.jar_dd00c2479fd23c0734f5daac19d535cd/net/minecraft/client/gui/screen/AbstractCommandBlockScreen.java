package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
   protected TextFieldWidget commandEdit;
   protected TextFieldWidget previousEdit;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button outputButton;
   protected boolean trackOutput;
   private CommandSuggestionHelper commandSuggestions;

   public AbstractCommandBlockScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public void tick() {
      this.commandEdit.tick();
   }

   abstract CommandBlockLogic getCommandBlock();

   abstract int getPreviousY();

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.done"), (p_214187_1_) -> {
         this.onDone();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.cancel"), (p_214186_1_) -> {
         this.onClose();
      }));
      this.outputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, "O", (p_214184_1_) -> {
         CommandBlockLogic commandblocklogic = this.getCommandBlock();
         commandblocklogic.setTrackOutput(!commandblocklogic.isTrackOutput());
         this.updateCommandOutput();
      }));
      this.commandEdit = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.get("advMode.command")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + AbstractCommandBlockScreen.this.commandSuggestions.getNarrationMessage();
         }
      };
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setResponder(this::onEdited);
      this.children.add(this.commandEdit);
      this.previousEdit = new TextFieldWidget(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, I18n.get("advMode.previousOutput"));
      this.previousEdit.setMaxLength(32500);
      this.previousEdit.setEditable(false);
      this.previousEdit.setValue("-");
      this.children.add(this.previousEdit);
      this.setInitialFocus(this.commandEdit);
      this.commandEdit.setFocus(true);
      this.commandSuggestions = new CommandSuggestionHelper(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.commandEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.commandEdit.setValue(s);
      this.commandSuggestions.updateCommandInfo();
   }

   protected void updateCommandOutput() {
      if (this.getCommandBlock().isTrackOutput()) {
         this.outputButton.setMessage("O");
         this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
      } else {
         this.outputButton.setMessage("X");
         this.previousEdit.setValue("-");
      }

   }

   protected void onDone() {
      CommandBlockLogic commandblocklogic = this.getCommandBlock();
      this.populateAndSendPacket(commandblocklogic);
      if (!commandblocklogic.isTrackOutput()) {
         commandblocklogic.setLastOutput((ITextComponent)null);
      }

      this.minecraft.setScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   protected abstract void populateAndSendPacket(CommandBlockLogic p_195235_1_);

   public void onClose() {
      this.getCommandBlock().setTrackOutput(this.trackOutput);
      this.minecraft.setScreen((Screen)null);
   }

   private void onEdited(String p_214185_1_) {
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.commandSuggestions.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.commandSuggestions.mouseScrolled(p_mouseScrolled_5_) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.commandSuggestions.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, I18n.get("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandEdit.render(p_render_1_, p_render_2_, p_render_3_);
      int i = 75;
      if (!this.previousEdit.getValue().isEmpty()) {
         i = i + (5 * 9 + 1 + this.getPreviousY() - 135);
         this.drawString(this.font, I18n.get("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
         this.previousEdit.render(p_render_1_, p_render_2_, p_render_3_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.commandSuggestions.func_228114_a_(p_render_1_, p_render_2_);
   }
}