package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends ContainerScreen<T> implements IRecipeShownListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   public final AbstractRecipeBookGui recipeBookComponent;
   private boolean widthTooNarrow;
   private final ResourceLocation texture;

   public AbstractFurnaceScreen(T p_i51104_1_, AbstractRecipeBookGui p_i51104_2_, PlayerInventory p_i51104_3_, ITextComponent p_i51104_4_, ResourceLocation p_i51104_5_) {
      super(p_i51104_1_, p_i51104_3_, p_i51104_4_);
      this.recipeBookComponent = p_i51104_2_;
      this.texture = p_i51104_5_;
   }

   public void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
      this.addButton((new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_214087_1_) -> {
         this.recipeBookComponent.initVisuals(this.widthTooNarrow);
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
         ((ImageButton)p_214087_1_).setPosition(this.leftPos + 20, this.height / 2 - 49);
      })));
   }

   public void tick() {
      super.tick();
      this.recipeBookComponent.tick();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.func_146976_a(p_render_3_, p_render_1_, p_render_2_);
         this.recipeBookComponent.render(p_render_1_, p_render_2_, p_render_3_);
      } else {
         this.recipeBookComponent.render(p_render_1_, p_render_2_, p_render_3_);
         super.render(p_render_1_, p_render_2_, p_render_3_);
         this.recipeBookComponent.func_191864_a(this.leftPos, this.topPos, true, p_render_3_);
      }

      this.func_191948_b(p_render_1_, p_render_2_);
      this.recipeBookComponent.func_191876_c(this.leftPos, this.topPos, p_render_1_, p_render_2_);
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      String s = this.title.func_150254_d();
      this.font.func_211126_b(s, (float)(this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, 4210752);
      this.font.func_211126_b(this.inventory.getDisplayName().func_150254_d(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(this.texture);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (((AbstractFurnaceContainer)this.menu).isLit()) {
         int k = ((AbstractFurnaceContainer)this.menu).getLitProgress();
         this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = ((AbstractFurnaceContainer)this.menu).getBurnProgress();
      this.blit(i + 79, j + 34, 176, 14, l + 1, 16);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.recipeBookComponent.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void slotClicked(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      super.slotClicked(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
      this.recipeBookComponent.slotClicked(p_184098_1_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.recipeBookComponent.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(p_195361_1_, p_195361_3_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_195361_7_) && flag;
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.recipeBookComponent.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public RecipeBookGui getRecipeBookComponent() {
      return this.recipeBookComponent;
   }

   public void removed() {
      this.recipeBookComponent.removed();
      super.removed();
   }
}