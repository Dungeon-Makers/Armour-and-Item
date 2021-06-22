package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.FullscreenResolutionOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VideoSettingsScreen extends SettingsScreen {
   private OptionsRowList list;
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AMBIENT_OCCLUSION, AbstractOption.FRAMERATE_LIMIT, AbstractOption.ENABLE_VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.USE_FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS};
   private int oldMipmaps;

   public VideoSettingsScreen(Screen p_i1062_1_, GameSettings p_i1062_2_) {
      super(p_i1062_1_, p_i1062_2_, new TranslationTextComponent("options.videoTitle"));
   }

   protected void init() {
      this.oldMipmaps = this.options.mipmapLevels;
      this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(new FullscreenResolutionOption(this.minecraft.getWindow()));
      this.list.addBig(AbstractOption.BIOME_BLEND_RADIUS);
      this.list.addSmall(OPTIONS);
      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.get("gui.done"), (p_213106_1_) -> {
         this.minecraft.options.save();
         this.minecraft.getWindow().changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      if (this.options.mipmapLevels != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel(this.options.mipmapLevels);
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      int i = this.options.guiScale;
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         if (this.options.guiScale != i) {
            this.minecraft.resizeDisplay();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      int i = this.options.guiScale;
      if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         return true;
      } else if (this.list.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         if (this.options.guiScale != i) {
            this.minecraft.resizeDisplay();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.list.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 5, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}