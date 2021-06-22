package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.IDN;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AddServerScreen extends Screen {
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private TextFieldWidget ipEdit;
   private TextFieldWidget nameEdit;
   private Button serverPackButton;
   private final Screen lastScreen;
   private final Predicate<String> addressFilter = (p_210141_0_) -> {
      if (StringUtils.isNullOrEmpty(p_210141_0_)) {
         return true;
      } else {
         String[] astring = p_210141_0_.split(":");
         if (astring.length == 0) {
            return true;
         } else {
            try {
               String s = IDN.toASCII(astring[0]);
               return true;
            } catch (IllegalArgumentException var3) {
               return false;
            }
         }
      }
   };

   public AddServerScreen(Screen p_i225927_1_, BooleanConsumer p_i225927_2_, ServerData p_i225927_3_) {
      super(new TranslationTextComponent("addServer.title"));
      this.lastScreen = p_i225927_1_;
      this.callback = p_i225927_2_;
      this.serverData = p_i225927_3_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.ipEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, I18n.get("addServer.enterName"));
      this.nameEdit.setFocus(true);
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder(this::onEdited);
      this.children.add(this.nameEdit);
      this.ipEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, I18n.get("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setFilter(this.addressFilter);
      this.ipEdit.setResponder(this::onEdited);
      this.children.add(this.ipEdit);
      this.serverPackButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().func_150254_d(), (p_213031_1_) -> {
         this.serverData.setResourcePackStatus(ServerData.ServerResourceMode.values()[(this.serverData.getResourcePackStatus().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
         this.serverPackButton.setMessage(I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().func_150254_d());
      }));
      this.addButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, I18n.get("addServer.add"), (p_213030_1_) -> {
         this.onAdd();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, I18n.get("gui.cancel"), (p_213029_1_) -> {
         this.callback.accept(false);
      }));
      this.cleanUp();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.ipEdit.getValue();
      String s1 = this.nameEdit.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.ipEdit.setValue(s);
      this.nameEdit.setValue(s1);
   }

   private void onEdited(String p_213028_1_) {
      this.cleanUp();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onAdd() {
      this.serverData.name = this.nameEdit.getValue();
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.cleanUp();
      this.minecraft.setScreen(this.lastScreen);
   }

   private void cleanUp() {
      String s = this.ipEdit.getValue();
      boolean flag = !s.isEmpty() && s.split(":").length > 0 && s.indexOf(32) == -1;
      this.addButton.active = flag && !this.nameEdit.getValue().isEmpty();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 17, 16777215);
      this.drawString(this.font, I18n.get("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
      this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      this.ipEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}