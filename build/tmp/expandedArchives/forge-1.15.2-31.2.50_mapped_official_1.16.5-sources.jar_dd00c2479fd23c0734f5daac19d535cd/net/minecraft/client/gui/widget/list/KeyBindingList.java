package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public class KeyBindingList extends AbstractOptionList<KeyBindingList.Entry> {
   private final ControlsScreen controlsScreen;
   private int maxNameWidth;

   public KeyBindingList(ControlsScreen p_i45031_1_, Minecraft p_i45031_2_) {
      super(p_i45031_2_, p_i45031_1_.width + 45, p_i45031_1_.height, 43, p_i45031_1_.height - 32, 20);
      this.controlsScreen = p_i45031_1_;
      KeyBinding[] akeybinding = ArrayUtils.clone(p_i45031_2_.options.keyMappings);
      Arrays.sort((Object[])akeybinding);
      String s = null;

      for(KeyBinding keybinding : akeybinding) {
         String s1 = keybinding.getCategory();
         if (!s1.equals(s)) {
            s = s1;
            this.addEntry(new KeyBindingList.CategoryEntry(s1));
         }

         int i = p_i45031_2_.font.width(I18n.get(keybinding.getName()));
         if (i > this.maxNameWidth) {
            this.maxNameWidth = i;
         }

         this.addEntry(new KeyBindingList.KeyEntry(keybinding));
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15 + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryEntry extends KeyBindingList.Entry {
      private final String name;
      private final int width;

      public CategoryEntry(String p_i45028_2_) {
         this.name = I18n.get(p_i45028_2_);
         this.width = KeyBindingList.this.minecraft.font.width(this.name);
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         KeyBindingList.this.minecraft.font.func_211126_b(this.name, (float)(KeyBindingList.this.minecraft.screen.width / 2 - this.width / 2), (float)(p_render_2_ + p_render_5_ - 9 - 1), 16777215);
      }

      public boolean changeFocus(boolean p_changeFocus_1_) {
         return false;
      }

      public List<? extends IGuiEventListener> children() {
         return Collections.emptyList();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends AbstractOptionList.Entry<KeyBindingList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public class KeyEntry extends KeyBindingList.Entry {
      private final KeyBinding key;
      private final String name;
      private final Button changeButton;
      private final Button resetButton;

      private KeyEntry(final KeyBinding p_i45029_2_) {
         this.key = p_i45029_2_;
         this.name = I18n.get(p_i45029_2_.getName());
         this.changeButton = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, this.name, (p_214386_2_) -> {
            KeyBindingList.this.controlsScreen.selectedKey = p_i45029_2_;
         }) {
            protected String getNarrationMessage() {
               return p_i45029_2_.isUnbound() ? I18n.get("narrator.controls.unbound", KeyEntry.this.name) : I18n.get("narrator.controls.bound", KeyEntry.this.name, super.getNarrationMessage());
            }
         };
         this.resetButton = new Button(0, 0, 50, 20, I18n.get("controls.reset"), (p_214387_2_) -> {
            key.setToDefault();
            KeyBindingList.this.minecraft.options.setKey(p_i45029_2_, p_i45029_2_.getDefaultKey());
            KeyBinding.resetMapping();
         }) {
            protected String getNarrationMessage() {
               return I18n.get("narrator.controls.reset", KeyEntry.this.name);
            }
         };
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         boolean flag = KeyBindingList.this.controlsScreen.selectedKey == this.key;
         KeyBindingList.this.minecraft.font.func_211126_b(this.name, (float)(p_render_3_ + 90 - KeyBindingList.this.maxNameWidth), (float)(p_render_2_ + p_render_5_ / 2 - 9 / 2), 16777215);
         this.resetButton.x = p_render_3_ + 190 + 20;
         this.resetButton.y = p_render_2_;
         this.resetButton.active = !this.key.isDefault();
         this.resetButton.render(p_render_6_, p_render_7_, p_render_9_);
         this.changeButton.x = p_render_3_ + 105;
         this.changeButton.y = p_render_2_;
         this.changeButton.setMessage(this.key.func_197978_k());
         boolean flag1 = false;
         boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
         if (!this.key.isUnbound()) {
            for(KeyBinding keybinding : KeyBindingList.this.minecraft.options.keyMappings) {
               if (keybinding != this.key && this.key.same(keybinding)) {
                  flag1 = true;
                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.key);
               }
            }
         }

         if (flag) {
            this.changeButton.setMessage(TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.changeButton.getMessage() + TextFormatting.WHITE + " <");
         } else if (flag1) {
            this.changeButton.setMessage((keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.changeButton.getMessage());
         }

         this.changeButton.render(p_render_6_, p_render_7_, p_render_9_);
      }

      public List<? extends IGuiEventListener> children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.changeButton.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            return this.resetButton.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
         return this.changeButton.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_) || this.resetButton.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }
}
