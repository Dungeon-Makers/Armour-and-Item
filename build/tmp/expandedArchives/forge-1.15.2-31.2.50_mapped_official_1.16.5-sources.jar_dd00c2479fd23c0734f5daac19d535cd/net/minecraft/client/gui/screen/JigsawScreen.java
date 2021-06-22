package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JigsawScreen extends Screen {
   private final JigsawTileEntity jigsawEntity;
   private TextFieldWidget field_214260_b;
   private TextFieldWidget field_214261_c;
   private TextFieldWidget finalStateEdit;
   private Button doneButton;

   public JigsawScreen(JigsawTileEntity p_i51083_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.jigsawEntity = p_i51083_1_;
   }

   public void tick() {
      this.field_214260_b.tick();
      this.field_214261_c.tick();
      this.finalStateEdit.tick();
   }

   private void onDone() {
      this.sendToServer();
      this.minecraft.setScreen((Screen)null);
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void sendToServer() {
      this.minecraft.getConnection().send(new CUpdateJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.field_214260_b.getValue()), new ResourceLocation(this.field_214261_c.getValue()), this.finalStateEdit.getValue()));
   }

   public void onClose() {
      this.onCancel();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done"), (p_214255_1_) -> {
         this.onDone();
      }));
      this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel"), (p_214252_1_) -> {
         this.onCancel();
      }));
      this.field_214261_c = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("jigsaw_block.target_pool"));
      this.field_214261_c.setMaxLength(128);
      this.field_214261_c.setValue(this.jigsawEntity.func_214056_d().toString());
      this.field_214261_c.setResponder((p_214254_1_) -> {
         this.updateValidity();
      });
      this.children.add(this.field_214261_c);
      this.field_214260_b = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 300, 20, I18n.get("jigsaw_block.attachement_type"));
      this.field_214260_b.setMaxLength(128);
      this.field_214260_b.setValue(this.jigsawEntity.func_214053_c().toString());
      this.field_214260_b.setResponder((p_214251_1_) -> {
         this.updateValidity();
      });
      this.children.add(this.field_214260_b);
      this.finalStateEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 300, 20, I18n.get("jigsaw_block.final_state"));
      this.finalStateEdit.setMaxLength(256);
      this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
      this.children.add(this.finalStateEdit);
      this.setInitialFocus(this.field_214261_c);
      this.updateValidity();
   }

   protected void updateValidity() {
      this.doneButton.active = ResourceLocation.isValidResourceLocation(this.field_214260_b.getValue()) & ResourceLocation.isValidResourceLocation(this.field_214261_c.getValue());
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.field_214260_b.getValue();
      String s1 = this.field_214261_c.getValue();
      String s2 = this.finalStateEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.field_214260_b.setValue(s);
      this.field_214261_c.setValue(s1);
      this.finalStateEdit.setValue(s2);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (!this.doneButton.active || p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawString(this.font, I18n.get("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
      this.field_214261_c.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawString(this.font, I18n.get("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
      this.field_214260_b.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawString(this.font, I18n.get("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
      this.finalStateEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}