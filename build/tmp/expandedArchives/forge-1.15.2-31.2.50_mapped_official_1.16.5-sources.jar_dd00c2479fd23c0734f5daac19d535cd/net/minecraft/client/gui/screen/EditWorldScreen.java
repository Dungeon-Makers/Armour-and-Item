package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private Button renameButton;
   private final BooleanConsumer callback;
   private TextFieldWidget nameEdit;
   private final String field_184860_g;

   public EditWorldScreen(BooleanConsumer p_i51073_1_, String p_i51073_2_) {
      super(new TranslationTextComponent("selectWorld.edit.title"));
      this.callback = p_i51073_1_;
      this.field_184860_g = p_i51073_2_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.get("selectWorld.edit.resetIcon"), (p_214309_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getLevelSource();
         FileUtils.deleteQuietly(saveformat1.func_186352_b(this.field_184860_g, "icon.png"));
         p_214309_1_.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.get("selectWorld.edit.openFolder"), (p_214303_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getLevelSource();
         Util.getPlatform().openFile(saveformat1.func_186352_b(this.field_184860_g, "icon.png").getParentFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.get("selectWorld.edit.backup"), (p_214304_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getLevelSource();
         func_200212_a(saveformat1, this.field_184860_g);
         this.callback.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.get("selectWorld.edit.backupFolder"), (p_214302_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getLevelSource();
         Path path = saveformat1.getBackupPath();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Util.getPlatform().openFile(path.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.get("selectWorld.edit.optimize"), (p_214310_1_) -> {
         this.minecraft.setScreen(new ConfirmBackupScreen(this, (p_214305_1_, p_214305_2_) -> {
            if (p_214305_1_) {
               func_200212_a(this.minecraft.getLevelSource(), this.field_184860_g);
            }

            this.minecraft.setScreen(new OptimizeWorldScreen(this.callback, this.field_184860_g, this.minecraft.getLevelSource(), p_214305_2_));
         }, new TranslationTextComponent("optimizeWorld.confirm.title"), new TranslationTextComponent("optimizeWorld.confirm.description"), true));
      }));
      this.renameButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.get("selectWorld.edit.save"), (p_214308_1_) -> {
         this.onRename();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.get("gui.cancel"), (p_214306_1_) -> {
         this.callback.accept(false);
      }));
      button.active = this.minecraft.getLevelSource().func_186352_b(this.field_184860_g, "icon.png").isFile();
      SaveFormat saveformat = this.minecraft.getLevelSource();
      WorldInfo worldinfo = saveformat.func_75803_c(this.field_184860_g);
      String s = worldinfo == null ? "" : worldinfo.getLevelName();
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.get("selectWorld.enterName"));
      this.nameEdit.setValue(s);
      this.nameEdit.setResponder((p_214301_1_) -> {
         this.renameButton.active = !p_214301_1_.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.nameEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameEdit.setValue(s);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onRename() {
      SaveFormat saveformat = this.minecraft.getLevelSource();
      saveformat.func_75806_a(this.field_184860_g, this.nameEdit.getValue().trim());
      this.callback.accept(true);
   }

   public static void func_200212_a(SaveFormat p_200212_0_, String p_200212_1_) {
      ToastGui toastgui = Minecraft.getInstance().getToasts();
      long i = 0L;
      IOException ioexception = null;

      try {
         i = p_200212_0_.func_197713_h(p_200212_1_);
      } catch (IOException ioexception1) {
         ioexception = ioexception1;
      }

      ITextComponent itextcomponent;
      ITextComponent itextcomponent1;
      if (ioexception != null) {
         itextcomponent = new TranslationTextComponent("selectWorld.edit.backupFailed");
         itextcomponent1 = new StringTextComponent(ioexception.getMessage());
      } else {
         itextcomponent = new TranslationTextComponent("selectWorld.edit.backupCreated", p_200212_1_);
         itextcomponent1 = new TranslationTextComponent("selectWorld.edit.backupSize", MathHelper.ceil((double)i / 1048576.0D));
      }

      toastgui.addToast(new SystemToast(SystemToast.Type.WORLD_BACKUP, itextcomponent, itextcomponent1));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}