package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StonecutterScreen extends ContainerScreen<StonecutterContainer> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private boolean displayRecipes;

   public StonecutterScreen(StonecutterContainer p_i51076_1_, PlayerInventory p_i51076_2_, ITextComponent p_i51076_3_) {
      super(p_i51076_1_, p_i51076_2_, p_i51076_3_);
      p_i51076_1_.registerUpdateListener(this::containerChanged);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      this.font.func_211126_b(this.title.func_150254_d(), 8.0F, 4.0F, 4210752);
      this.font.func_211126_b(this.inventory.getDisplayName().func_150254_d(), 8.0F, (float)(this.imageHeight - 94), 4210752);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      this.renderBackground();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      int k = (int)(41.0F * this.scrollOffs);
      this.blit(i + 119, j + 15 + k, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
      int l = this.leftPos + 52;
      int i1 = this.topPos + 14;
      int j1 = this.startIndex + 12;
      this.func_214141_a(p_146976_2_, p_146976_3_, l, i1, j1);
      this.renderRecipes(l, i1, j1);
   }

   private void func_214141_a(int p_214141_1_, int p_214141_2_, int p_214141_3_, int p_214141_4_, int p_214141_5_) {
      for(int i = this.startIndex; i < p_214141_5_ && i < this.menu.getNumRecipes(); ++i) {
         int j = i - this.startIndex;
         int k = p_214141_3_ + j % 4 * 16;
         int l = j / 4;
         int i1 = p_214141_4_ + l * 18 + 2;
         int j1 = this.imageHeight;
         if (i == this.menu.getSelectedRecipeIndex()) {
            j1 += 18;
         } else if (p_214141_1_ >= k && p_214141_2_ >= i1 && p_214141_1_ < k + 16 && p_214141_2_ < i1 + 18) {
            j1 += 36;
         }

         this.blit(k, i1 - 1, 0, j1, 16, 18);
      }

   }

   private void renderRecipes(int p_214142_1_, int p_214142_2_, int p_214142_3_) {
      List<StonecuttingRecipe> list = this.menu.getRecipes();

      for(int i = this.startIndex; i < p_214142_3_ && i < this.menu.getNumRecipes(); ++i) {
         int j = i - this.startIndex;
         int k = p_214142_1_ + j % 4 * 16;
         int l = j / 4;
         int i1 = p_214142_2_ + l * 18 + 2;
         this.minecraft.getItemRenderer().renderAndDecorateItem(list.get(i).getResultItem(), k, i1);
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.scrolling = false;
      if (this.displayRecipes) {
         int i = this.leftPos + 52;
         int j = this.topPos + 14;
         int k = this.startIndex + 12;

         for(int l = this.startIndex; l < k; ++l) {
            int i1 = l - this.startIndex;
            double d0 = p_mouseClicked_1_ - (double)(i + i1 % 4 * 16);
            double d1 = p_mouseClicked_3_ - (double)(j + i1 / 4 * 18);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
               return true;
            }
         }

         i = this.leftPos + 119;
         j = this.topPos + 9;
         if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ < (double)(i + 12) && p_mouseClicked_3_ >= (double)j && p_mouseClicked_3_ < (double)(j + 54)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.scrolling && this.isScrollBarActive()) {
         int i = this.topPos + 14;
         int j = i + 54;
         this.scrollOffs = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5D) * 4;
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (this.isScrollBarActive()) {
         int i = this.getOffscreenRows();
         this.scrollOffs = (float)((double)this.scrollOffs - p_mouseScrolled_5_ / (double)i);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   private boolean isScrollBarActive() {
      return this.displayRecipes && this.menu.getNumRecipes() > 12;
   }

   protected int getOffscreenRows() {
      return (this.menu.getNumRecipes() + 4 - 1) / 4 - 3;
   }

   private void containerChanged() {
      this.displayRecipes = this.menu.hasInputItem();
      if (!this.displayRecipes) {
         this.scrollOffs = 0.0F;
         this.startIndex = 0;
      }

   }
}