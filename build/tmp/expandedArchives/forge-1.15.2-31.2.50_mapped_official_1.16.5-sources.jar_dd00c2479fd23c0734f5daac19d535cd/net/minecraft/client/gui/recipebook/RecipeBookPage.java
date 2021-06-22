package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookPage {
   private final List<RecipeWidget> buttons = Lists.newArrayListWithCapacity(20);
   private RecipeWidget hoveredButton;
   private final RecipeOverlayGui overlay = new RecipeOverlayGui();
   private Minecraft minecraft;
   private final List<IRecipeUpdateListener> showListeners = Lists.newArrayList();
   private List<RecipeList> recipeCollections;
   private ToggleWidget forwardButton;
   private ToggleWidget backButton;
   private int totalPages;
   private int currentPage;
   private RecipeBook recipeBook;
   private IRecipe<?> lastClickedRecipe;
   private RecipeList lastClickedRecipeCollection;

   public RecipeBookPage() {
      for(int i = 0; i < 20; ++i) {
         this.buttons.add(new RecipeWidget());
      }

   }

   public void init(Minecraft p_194194_1_, int p_194194_2_, int p_194194_3_) {
      this.minecraft = p_194194_1_;
      this.recipeBook = p_194194_1_.player.getRecipeBook();

      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).setPosition(p_194194_2_ + 11 + 25 * (i % 5), p_194194_3_ + 31 + 25 * (i / 5));
      }

      this.forwardButton = new ToggleWidget(p_194194_2_ + 93, p_194194_3_ + 137, 12, 17, false);
      this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookGui.RECIPE_BOOK_LOCATION);
      this.backButton = new ToggleWidget(p_194194_2_ + 38, p_194194_3_ + 137, 12, 17, true);
      this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookGui.RECIPE_BOOK_LOCATION);
   }

   public void addListener(RecipeBookGui p_193732_1_) {
      this.showListeners.remove(p_193732_1_);
      this.showListeners.add(p_193732_1_);
   }

   public void updateCollections(List<RecipeList> p_194192_1_, boolean p_194192_2_) {
      this.recipeCollections = p_194192_1_;
      this.totalPages = (int)Math.ceil((double)p_194192_1_.size() / 20.0D);
      if (this.totalPages <= this.currentPage || p_194192_2_) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int i = 20 * this.currentPage;

      for(int j = 0; j < this.buttons.size(); ++j) {
         RecipeWidget recipewidget = this.buttons.get(j);
         if (i + j < this.recipeCollections.size()) {
            RecipeList recipelist = this.recipeCollections.get(i + j);
            recipewidget.init(recipelist, this);
            recipewidget.visible = true;
         } else {
            recipewidget.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
      this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
   }

   public void func_194191_a(int p_194191_1_, int p_194191_2_, int p_194191_3_, int p_194191_4_, float p_194191_5_) {
      if (this.totalPages > 1) {
         String s = this.currentPage + 1 + "/" + this.totalPages;
         int i = this.minecraft.font.width(s);
         this.minecraft.font.func_211126_b(s, (float)(p_194191_1_ - i / 2 + 73), (float)(p_194191_2_ + 141), -1);
      }

      this.hoveredButton = null;

      for(RecipeWidget recipewidget : this.buttons) {
         recipewidget.render(p_194191_3_, p_194191_4_, p_194191_5_);
         if (recipewidget.visible && recipewidget.isHovered()) {
            this.hoveredButton = recipewidget;
         }
      }

      this.backButton.render(p_194191_3_, p_194191_4_, p_194191_5_);
      this.forwardButton.render(p_194191_3_, p_194191_4_, p_194191_5_);
      this.overlay.render(p_194191_3_, p_194191_4_, p_194191_5_);
   }

   public void func_193721_a(int p_193721_1_, int p_193721_2_) {
      if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         this.minecraft.screen.renderTooltip(this.hoveredButton.getTooltipText(this.minecraft.screen), p_193721_1_, p_193721_2_);
      }

   }

   @Nullable
   public IRecipe<?> getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeList getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean mouseClicked(double p_198955_1_, double p_198955_3_, int p_198955_5_, int p_198955_6_, int p_198955_7_, int p_198955_8_, int p_198955_9_) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if (this.overlay.isVisible()) {
         if (this.overlay.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if (this.forwardButton.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         for(RecipeWidget recipewidget : this.buttons) {
            if (recipewidget.mouseClicked(p_198955_1_, p_198955_3_, p_198955_5_)) {
               if (p_198955_5_ == 0) {
                  this.lastClickedRecipe = recipewidget.getRecipe();
                  this.lastClickedRecipeCollection = recipewidget.getCollection();
               } else if (p_198955_5_ == 1 && !this.overlay.isVisible() && !recipewidget.isOnlyOption()) {
                  this.overlay.init(this.minecraft, recipewidget.getCollection(), recipewidget.x, recipewidget.y, p_198955_6_ + p_198955_8_ / 2, p_198955_7_ + 13 + p_198955_9_ / 2, (float)recipewidget.getWidth());
               }

               return true;
            }
         }

         return false;
      }
   }

   public void recipesShown(List<IRecipe<?>> p_194195_1_) {
      for(IRecipeUpdateListener irecipeupdatelistener : this.showListeners) {
         irecipeupdatelistener.recipesShown(p_194195_1_);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public RecipeBook getRecipeBook() {
      return this.recipeBook;
   }
}