package net.minecraft.client.gui.screen;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditStructureScreen extends Screen {
   private final StructureBlockTileEntity structure;
   private Mirror initialMirror = Mirror.NONE;
   private Rotation initialRotation = Rotation.NONE;
   private StructureMode initialMode = StructureMode.DATA;
   private boolean initialEntityIgnoring;
   private boolean initialShowAir;
   private boolean initialShowBoundingBox;
   private TextFieldWidget nameEdit;
   private TextFieldWidget posXEdit;
   private TextFieldWidget posYEdit;
   private TextFieldWidget posZEdit;
   private TextFieldWidget sizeXEdit;
   private TextFieldWidget sizeYEdit;
   private TextFieldWidget sizeZEdit;
   private TextFieldWidget integrityEdit;
   private TextFieldWidget seedEdit;
   private TextFieldWidget dataEdit;
   private Button doneButton;
   private Button cancelButton;
   private Button saveButton;
   private Button loadButton;
   private Button rot0Button;
   private Button rot90Button;
   private Button rot180Button;
   private Button rot270Button;
   private Button modeButton;
   private Button detectButton;
   private Button entitiesButton;
   private Button mirrorButton;
   private Button toggleAirButton;
   private Button toggleBoundingBox;
   private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

   public EditStructureScreen(StructureBlockTileEntity p_i47142_1_) {
      super(new TranslationTextComponent(Blocks.STRUCTURE_BLOCK.getDescriptionId()));
      this.structure = p_i47142_1_;
      this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public void tick() {
      this.nameEdit.tick();
      this.posXEdit.tick();
      this.posYEdit.tick();
      this.posZEdit.tick();
      this.sizeXEdit.tick();
      this.sizeYEdit.tick();
      this.sizeZEdit.tick();
      this.integrityEdit.tick();
      this.seedEdit.tick();
      this.dataEdit.tick();
   }

   private void onDone() {
      if (this.sendToServer(StructureBlockTileEntity.UpdateCommand.UPDATE_DATA)) {
         this.minecraft.setScreen((Screen)null);
      }

   }

   private void onCancel() {
      this.structure.setMirror(this.initialMirror);
      this.structure.setRotation(this.initialRotation);
      this.structure.setMode(this.initialMode);
      this.structure.setIgnoreEntities(this.initialEntityIgnoring);
      this.structure.setShowAir(this.initialShowAir);
      this.structure.setShowBoundingBox(this.initialShowBoundingBox);
      this.minecraft.setScreen((Screen)null);
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done"), (p_214274_1_) -> {
         this.onDone();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel"), (p_214275_1_) -> {
         this.onCancel();
      }));
      this.saveButton = this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.save"), (p_214276_1_) -> {
         if (this.structure.getMode() == StructureMode.SAVE) {
            this.sendToServer(StructureBlockTileEntity.UpdateCommand.SAVE_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.loadButton = this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.load"), (p_214277_1_) -> {
         if (this.structure.getMode() == StructureMode.LOAD) {
            this.sendToServer(StructureBlockTileEntity.UpdateCommand.LOAD_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.modeButton = this.addButton(new Button(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", (p_214280_1_) -> {
         this.structure.nextMode();
         this.updateMode();
      }));
      this.detectButton = this.addButton(new Button(this.width / 2 + 4 + 100, 120, 50, 20, I18n.get("structure_block.button.detect_size"), (p_214278_1_) -> {
         if (this.structure.getMode() == StructureMode.SAVE) {
            this.sendToServer(StructureBlockTileEntity.UpdateCommand.SCAN_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.entitiesButton = this.addButton(new Button(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", (p_214282_1_) -> {
         this.structure.setIgnoreEntities(!this.structure.isIgnoreEntities());
         this.updateEntitiesButton();
      }));
      this.mirrorButton = this.addButton(new Button(this.width / 2 - 20, 185, 40, 20, "MIRROR", (p_214281_1_) -> {
         switch(this.structure.getMirror()) {
         case NONE:
            this.structure.setMirror(Mirror.LEFT_RIGHT);
            break;
         case LEFT_RIGHT:
            this.structure.setMirror(Mirror.FRONT_BACK);
            break;
         case FRONT_BACK:
            this.structure.setMirror(Mirror.NONE);
         }

         this.updateMirrorButton();
      }));
      this.toggleAirButton = this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", (p_214269_1_) -> {
         this.structure.setShowAir(!this.structure.getShowAir());
         this.updateToggleAirButton();
      }));
      this.toggleBoundingBox = this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", (p_214270_1_) -> {
         this.structure.setShowBoundingBox(!this.structure.getShowBoundingBox());
         this.updateToggleBoundingBox();
      }));
      this.rot0Button = this.addButton(new Button(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", (p_214268_1_) -> {
         this.structure.setRotation(Rotation.NONE);
         this.updateDirectionButtons();
      }));
      this.rot90Button = this.addButton(new Button(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", (p_214273_1_) -> {
         this.structure.setRotation(Rotation.CLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.rot180Button = this.addButton(new Button(this.width / 2 + 1 + 20, 185, 40, 20, "180", (p_214272_1_) -> {
         this.structure.setRotation(Rotation.CLOCKWISE_180);
         this.updateDirectionButtons();
      }));
      this.rot270Button = this.addButton(new Button(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", (p_214271_1_) -> {
         this.structure.setRotation(Rotation.COUNTERCLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("structure_block.structure_name")) {
         public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return !EditStructureScreen.this.isValidCharacterForName(this.getValue(), p_charTyped_1_, this.getCursorPosition()) ? false : super.charTyped(p_charTyped_1_, p_charTyped_2_);
         }
      };
      this.nameEdit.setMaxLength(64);
      this.nameEdit.setValue(this.structure.getStructureName());
      this.children.add(this.nameEdit);
      BlockPos blockpos = this.structure.getStructurePos();
      this.posXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 80, 20, I18n.get("structure_block.position.x"));
      this.posXEdit.setMaxLength(15);
      this.posXEdit.setValue(Integer.toString(blockpos.getX()));
      this.children.add(this.posXEdit);
      this.posYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 80, 80, 20, I18n.get("structure_block.position.y"));
      this.posYEdit.setMaxLength(15);
      this.posYEdit.setValue(Integer.toString(blockpos.getY()));
      this.children.add(this.posYEdit);
      this.posZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 80, 80, 20, I18n.get("structure_block.position.z"));
      this.posZEdit.setMaxLength(15);
      this.posZEdit.setValue(Integer.toString(blockpos.getZ()));
      this.children.add(this.posZEdit);
      BlockPos blockpos1 = this.structure.getStructureSize();
      this.sizeXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.size.x"));
      this.sizeXEdit.setMaxLength(15);
      this.sizeXEdit.setValue(Integer.toString(blockpos1.getX()));
      this.children.add(this.sizeXEdit);
      this.sizeYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.size.y"));
      this.sizeYEdit.setMaxLength(15);
      this.sizeYEdit.setValue(Integer.toString(blockpos1.getY()));
      this.children.add(this.sizeYEdit);
      this.sizeZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 120, 80, 20, I18n.get("structure_block.size.z"));
      this.sizeZEdit.setMaxLength(15);
      this.sizeZEdit.setValue(Integer.toString(blockpos1.getZ()));
      this.children.add(this.sizeZEdit);
      this.integrityEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.integrity.integrity"));
      this.integrityEdit.setMaxLength(15);
      this.integrityEdit.setValue(this.decimalFormat.format((double)this.structure.getIntegrity()));
      this.children.add(this.integrityEdit);
      this.seedEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.integrity.seed"));
      this.seedEdit.setMaxLength(31);
      this.seedEdit.setValue(Long.toString(this.structure.getSeed()));
      this.children.add(this.seedEdit);
      this.dataEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 240, 20, I18n.get("structure_block.custom_data"));
      this.dataEdit.setMaxLength(128);
      this.dataEdit.setValue(this.structure.getMetaData());
      this.children.add(this.dataEdit);
      this.initialMirror = this.structure.getMirror();
      this.updateMirrorButton();
      this.initialRotation = this.structure.getRotation();
      this.updateDirectionButtons();
      this.initialMode = this.structure.getMode();
      this.updateMode();
      this.initialEntityIgnoring = this.structure.isIgnoreEntities();
      this.updateEntitiesButton();
      this.initialShowAir = this.structure.getShowAir();
      this.updateToggleAirButton();
      this.initialShowBoundingBox = this.structure.getShowBoundingBox();
      this.updateToggleBoundingBox();
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.nameEdit.getValue();
      String s1 = this.posXEdit.getValue();
      String s2 = this.posYEdit.getValue();
      String s3 = this.posZEdit.getValue();
      String s4 = this.sizeXEdit.getValue();
      String s5 = this.sizeYEdit.getValue();
      String s6 = this.sizeZEdit.getValue();
      String s7 = this.integrityEdit.getValue();
      String s8 = this.seedEdit.getValue();
      String s9 = this.dataEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameEdit.setValue(s);
      this.posXEdit.setValue(s1);
      this.posYEdit.setValue(s2);
      this.posZEdit.setValue(s3);
      this.sizeXEdit.setValue(s4);
      this.sizeYEdit.setValue(s5);
      this.sizeZEdit.setValue(s6);
      this.integrityEdit.setValue(s7);
      this.seedEdit.setValue(s8);
      this.dataEdit.setValue(s9);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void updateEntitiesButton() {
      boolean flag = !this.structure.isIgnoreEntities();
      if (flag) {
         this.entitiesButton.setMessage(I18n.get("options.on"));
      } else {
         this.entitiesButton.setMessage(I18n.get("options.off"));
      }

   }

   private void updateToggleAirButton() {
      boolean flag = this.structure.getShowAir();
      if (flag) {
         this.toggleAirButton.setMessage(I18n.get("options.on"));
      } else {
         this.toggleAirButton.setMessage(I18n.get("options.off"));
      }

   }

   private void updateToggleBoundingBox() {
      boolean flag = this.structure.getShowBoundingBox();
      if (flag) {
         this.toggleBoundingBox.setMessage(I18n.get("options.on"));
      } else {
         this.toggleBoundingBox.setMessage(I18n.get("options.off"));
      }

   }

   private void updateMirrorButton() {
      Mirror mirror = this.structure.getMirror();
      switch(mirror) {
      case NONE:
         this.mirrorButton.setMessage("|");
         break;
      case LEFT_RIGHT:
         this.mirrorButton.setMessage("< >");
         break;
      case FRONT_BACK:
         this.mirrorButton.setMessage("^ v");
      }

   }

   private void updateDirectionButtons() {
      this.rot0Button.active = true;
      this.rot90Button.active = true;
      this.rot180Button.active = true;
      this.rot270Button.active = true;
      switch(this.structure.getRotation()) {
      case NONE:
         this.rot0Button.active = false;
         break;
      case CLOCKWISE_180:
         this.rot180Button.active = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.rot270Button.active = false;
         break;
      case CLOCKWISE_90:
         this.rot90Button.active = false;
      }

   }

   private void updateMode() {
      this.nameEdit.setVisible(false);
      this.posXEdit.setVisible(false);
      this.posYEdit.setVisible(false);
      this.posZEdit.setVisible(false);
      this.sizeXEdit.setVisible(false);
      this.sizeYEdit.setVisible(false);
      this.sizeZEdit.setVisible(false);
      this.integrityEdit.setVisible(false);
      this.seedEdit.setVisible(false);
      this.dataEdit.setVisible(false);
      this.saveButton.visible = false;
      this.loadButton.visible = false;
      this.detectButton.visible = false;
      this.entitiesButton.visible = false;
      this.mirrorButton.visible = false;
      this.rot0Button.visible = false;
      this.rot90Button.visible = false;
      this.rot180Button.visible = false;
      this.rot270Button.visible = false;
      this.toggleAirButton.visible = false;
      this.toggleBoundingBox.visible = false;
      switch(this.structure.getMode()) {
      case SAVE:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.sizeXEdit.setVisible(true);
         this.sizeYEdit.setVisible(true);
         this.sizeZEdit.setVisible(true);
         this.saveButton.visible = true;
         this.detectButton.visible = true;
         this.entitiesButton.visible = true;
         this.toggleAirButton.visible = true;
         break;
      case LOAD:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.integrityEdit.setVisible(true);
         this.seedEdit.setVisible(true);
         this.loadButton.visible = true;
         this.entitiesButton.visible = true;
         this.mirrorButton.visible = true;
         this.rot0Button.visible = true;
         this.rot90Button.visible = true;
         this.rot180Button.visible = true;
         this.rot270Button.visible = true;
         this.toggleBoundingBox.visible = true;
         this.updateDirectionButtons();
         break;
      case CORNER:
         this.nameEdit.setVisible(true);
         break;
      case DATA:
         this.dataEdit.setVisible(true);
      }

      this.modeButton.setMessage(I18n.get("structure_block.mode." + this.structure.getMode().getSerializedName()));
   }

   private boolean sendToServer(StructureBlockTileEntity.UpdateCommand p_210143_1_) {
      BlockPos blockpos = new BlockPos(this.parseCoordinate(this.posXEdit.getValue()), this.parseCoordinate(this.posYEdit.getValue()), this.parseCoordinate(this.posZEdit.getValue()));
      BlockPos blockpos1 = new BlockPos(this.parseCoordinate(this.sizeXEdit.getValue()), this.parseCoordinate(this.sizeYEdit.getValue()), this.parseCoordinate(this.sizeZEdit.getValue()));
      float f = this.parseIntegrity(this.integrityEdit.getValue());
      long i = this.parseSeed(this.seedEdit.getValue());
      this.minecraft.getConnection().send(new CUpdateStructureBlockPacket(this.structure.getBlockPos(), p_210143_1_, this.structure.getMode(), this.nameEdit.getValue(), blockpos, blockpos1, this.structure.getMirror(), this.structure.getRotation(), this.dataEdit.getValue(), this.structure.isIgnoreEntities(), this.structure.getShowAir(), this.structure.getShowBoundingBox(), f, i));
      return true;
   }

   private long parseSeed(String p_189821_1_) {
      try {
         return Long.valueOf(p_189821_1_);
      } catch (NumberFormatException var3) {
         return 0L;
      }
   }

   private float parseIntegrity(String p_189819_1_) {
      try {
         return Float.valueOf(p_189819_1_);
      } catch (NumberFormatException var3) {
         return 1.0F;
      }
   }

   private int parseCoordinate(String p_189817_1_) {
      try {
         return Integer.parseInt(p_189817_1_);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   public void onClose() {
      this.onCancel();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      StructureMode structuremode = this.structure.getMode();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 10, 16777215);
      if (structuremode != StructureMode.DATA) {
         this.drawString(this.font, I18n.get("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
         this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      }

      if (structuremode == StructureMode.LOAD || structuremode == StructureMode.SAVE) {
         this.drawString(this.font, I18n.get("structure_block.position"), this.width / 2 - 153, 70, 10526880);
         this.posXEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.posYEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.posZEdit.render(p_render_1_, p_render_2_, p_render_3_);
         String s = I18n.get("structure_block.include_entities");
         int i = this.font.width(s);
         this.drawString(this.font, s, this.width / 2 + 154 - i, 150, 10526880);
      }

      if (structuremode == StructureMode.SAVE) {
         this.drawString(this.font, I18n.get("structure_block.size"), this.width / 2 - 153, 110, 10526880);
         this.sizeXEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.sizeYEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.sizeZEdit.render(p_render_1_, p_render_2_, p_render_3_);
         String s2 = I18n.get("structure_block.detect_size");
         int k = this.font.width(s2);
         this.drawString(this.font, s2, this.width / 2 + 154 - k, 110, 10526880);
         String s1 = I18n.get("structure_block.show_air");
         int j = this.font.width(s1);
         this.drawString(this.font, s1, this.width / 2 + 154 - j, 70, 10526880);
      }

      if (structuremode == StructureMode.LOAD) {
         this.drawString(this.font, I18n.get("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
         this.integrityEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.seedEdit.render(p_render_1_, p_render_2_, p_render_3_);
         String s3 = I18n.get("structure_block.show_boundingbox");
         int l = this.font.width(s3);
         this.drawString(this.font, s3, this.width / 2 + 154 - l, 70, 10526880);
      }

      if (structuremode == StructureMode.DATA) {
         this.drawString(this.font, I18n.get("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
         this.dataEdit.render(p_render_1_, p_render_2_, p_render_3_);
      }

      String s4 = "structure_block.mode_info." + structuremode.getSerializedName();
      this.drawString(this.font, I18n.get(s4), this.width / 2 - 153, 174, 10526880);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean isPauseScreen() {
      return false;
   }
}