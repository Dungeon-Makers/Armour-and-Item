package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageScreen extends SettingsScreen {
   private LanguageScreen.List packSelectionList;
   private final LanguageManager languageManager;
   private OptionButton forceUnicodeButton;
   private Button doneButton;

   public LanguageScreen(Screen p_i1043_1_, GameSettings p_i1043_2_, LanguageManager p_i1043_3_) {
      super(p_i1043_1_, p_i1043_2_, new TranslationTextComponent("options.language"));
      this.languageManager = p_i1043_3_;
   }

   protected void init() {
      this.packSelectionList = new LanguageScreen.List(this.minecraft);
      this.children.add(this.packSelectionList);
      this.forceUnicodeButton = this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, AbstractOption.FORCE_UNICODE_FONT, AbstractOption.FORCE_UNICODE_FONT.func_216743_c(this.options), (p_213037_1_) -> {
         AbstractOption.FORCE_UNICODE_FONT.toggle(this.options);
         this.options.save();
         p_213037_1_.setMessage(AbstractOption.FORCE_UNICODE_FONT.func_216743_c(this.options));
         this.minecraft.resizeDisplay();
      }));
      this.doneButton = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.get("gui.done"), (p_213036_1_) -> {
         LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = this.packSelectionList.getSelected();
         if (languagescreen$list$languageentry != null && !languagescreen$list$languageentry.language.getCode().equals(this.languageManager.getSelected().getCode())) {
            this.languageManager.setSelected(languagescreen$list$languageentry.language);
            this.options.languageCode = languagescreen$list$languageentry.language.getCode();
            net.minecraftforge.client.ForgeHooksClient.refreshResources(this.minecraft, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
            this.font.func_78275_b(this.languageManager.isBidirectional());
            this.doneButton.setMessage(I18n.get("gui.done"));
            this.forceUnicodeButton.setMessage(AbstractOption.FORCE_UNICODE_FONT.func_216743_c(this.options));
            this.options.save();
         }

         this.minecraft.setScreen(this.lastScreen);
      }));
      super.init();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.packSelectionList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, "(" + I18n.get("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends ExtendedList<LanguageScreen.List.LanguageEntry> {
      public List(Minecraft p_i45519_2_) {
         super(p_i45519_2_, LanguageScreen.this.width, LanguageScreen.this.height, 32, LanguageScreen.this.height - 65 + 4, 18);

         for(Language language : LanguageScreen.this.languageManager.getLanguages()) {
            LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = new LanguageScreen.List.LanguageEntry(language);
            this.addEntry(languagescreen$list$languageentry);
            if (LanguageScreen.this.languageManager.getSelected().getCode().equals(language.getCode())) {
               this.setSelected(languagescreen$list$languageentry);
            }
         }

         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }

      }

      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public void setSelected(@Nullable LanguageScreen.List.LanguageEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", p_setSelected_1_.language)).getString());
         }

      }

      protected void renderBackground() {
         LanguageScreen.this.renderBackground();
      }

      protected boolean isFocused() {
         return LanguageScreen.this.getFocused() == this;
      }

      @OnlyIn(Dist.CLIENT)
      public class LanguageEntry extends ExtendedList.AbstractListEntry<LanguageScreen.List.LanguageEntry> {
         private final Language language;

         public LanguageEntry(Language p_i50494_2_) {
            this.language = p_i50494_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            LanguageScreen.this.font.func_78275_b(true);
            List.this.drawCenteredString(LanguageScreen.this.font, this.language.toString(), List.this.width / 2, p_render_2_ + 1, 16777215);
            LanguageScreen.this.font.func_78275_b(LanguageScreen.this.languageManager.getSelected().isBidirectional());
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               this.select();
               return true;
            } else {
               return false;
            }
         }

         private void select() {
            List.this.setSelected(this);
         }
      }
   }
}
