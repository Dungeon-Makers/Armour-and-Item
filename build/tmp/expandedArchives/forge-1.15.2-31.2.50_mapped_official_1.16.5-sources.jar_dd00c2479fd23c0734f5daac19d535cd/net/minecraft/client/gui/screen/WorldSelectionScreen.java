package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionScreen extends Screen {
   protected final Screen lastScreen;
   private String toolTip;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected TextFieldWidget searchBox;
   private WorldSelectionList list;

   public WorldSelectionScreen(Screen p_i46592_1_) {
      super(new TranslationTextComponent("selectWorld.title"));
      this.lastScreen = p_i46592_1_;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public void tick() {
      this.searchBox.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.get("selectWorld.search"));
      this.searchBox.setResponder((p_214329_1_) -> {
         this.list.refreshList(() -> {
            return p_214329_1_;
         }, false);
      });
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.searchBox.getValue();
      }, this.list);
      this.children.add(this.searchBox);
      this.children.add(this.list);
      this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, I18n.get("selectWorld.select"), (p_214325_1_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.Entry::joinWorld);
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, I18n.get("selectWorld.create"), (p_214326_1_) -> {
         this.minecraft.setScreen(new CreateWorldScreen(this));
      }));
      this.renameButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, I18n.get("selectWorld.edit"), (p_214323_1_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.Entry::editWorld);
      }));
      this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, I18n.get("selectWorld.delete"), (p_214330_1_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.Entry::deleteWorld);
      }));
      this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, I18n.get("selectWorld.recreate"), (p_214328_1_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.Entry::recreateWorld);
      }));
      this.addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, I18n.get("gui.cancel"), (p_214327_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.updateButtonStatus(false);
      this.setInitialFocus(this.searchBox);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.searchBox.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.searchBox.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.toolTip = null;
      this.list.render(p_render_1_, p_render_2_, p_render_3_);
      this.searchBox.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 8, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.toolTip != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.toolTip)), p_render_1_, p_render_2_);
      }

   }

   public void func_184861_a(String p_184861_1_) {
      this.toolTip = p_184861_1_;
   }

   public void updateButtonStatus(boolean p_214324_1_) {
      this.selectButton.active = p_214324_1_;
      this.deleteButton.active = p_214324_1_;
      this.renameButton.active = p_214324_1_;
      this.copyButton.active = p_214324_1_;
   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }

   }
}