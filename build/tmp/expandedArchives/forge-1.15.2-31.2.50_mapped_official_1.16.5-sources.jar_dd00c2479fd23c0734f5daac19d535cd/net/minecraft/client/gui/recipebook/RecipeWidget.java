package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeWidget extends Widget {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private RecipeBookContainer<?> menu;
   private RecipeBook book;
   private RecipeList collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeWidget() {
      super(0, 0, 25, 25, "");
   }

   public void init(RecipeList p_203400_1_, RecipeBookPage p_203400_2_) {
      this.collection = p_203400_1_;
      this.menu = (RecipeBookContainer)p_203400_2_.getMinecraft().player.containerMenu;
      this.book = p_203400_2_.getRecipeBook();
      List<IRecipe<?>> list = p_203400_1_.getRecipes(this.book.func_203432_a(this.menu));

      for(IRecipe<?> irecipe : list) {
         if (this.book.willHighlight(irecipe)) {
            p_203400_2_.recipesShown(list);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeList getCollection() {
      return this.collection;
   }

   public void setPosition(int p_191770_1_, int p_191770_2_) {
      this.x = p_191770_1_;
      this.y = p_191770_2_;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (!Screen.hasControlDown()) {
         this.time += p_renderButton_3_;
      }

      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
      int i = 29;
      if (!this.collection.hasCraftable()) {
         i += 25;
      }

      int j = 206;
      if (this.collection.getRecipes(this.book.func_203432_a(this.menu)).size() > 1) {
         j += 25;
      }

      boolean flag = this.animationTime > 0.0F;
      if (flag) {
         float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         RenderSystem.scalef(f, f, 1.0F);
         RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         this.animationTime -= p_renderButton_3_;
      }

      this.blit(this.x, this.y, i, j, this.width, this.height);
      List<IRecipe<?>> list = this.getOrderedRecipes();
      this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
      ItemStack itemstack = list.get(this.currentIndex).getResultItem();
      int k = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         minecraft.getItemRenderer().renderAndDecorateItem(itemstack, this.x + k + 1, this.y + k + 1);
         --k;
      }

      minecraft.getItemRenderer().renderAndDecorateItem(itemstack, this.x + k, this.y + k);
      if (flag) {
         RenderSystem.popMatrix();
      }

   }

   private List<IRecipe<?>> getOrderedRecipes() {
      List<IRecipe<?>> list = this.collection.getDisplayRecipes(true);
      if (!this.book.func_203432_a(this.menu)) {
         list.addAll(this.collection.getDisplayRecipes(false));
      }

      return list;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public IRecipe<?> getRecipe() {
      List<IRecipe<?>> list = this.getOrderedRecipes();
      return list.get(this.currentIndex);
   }

   public List<String> getTooltipText(Screen p_191772_1_) {
      ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getResultItem();
      List<String> list = p_191772_1_.getTooltipFromItem(itemstack);
      if (this.collection.getRecipes(this.book.func_203432_a(this.menu)).size() > 1) {
         list.add(I18n.get("gui.recipebook.moreRecipes"));
      }

      return list;
   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return p_isValidClickButton_1_ == 0 || p_isValidClickButton_1_ == 1;
   }
}