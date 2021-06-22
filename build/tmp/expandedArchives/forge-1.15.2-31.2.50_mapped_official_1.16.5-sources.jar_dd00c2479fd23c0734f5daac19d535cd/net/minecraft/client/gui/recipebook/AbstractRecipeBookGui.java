package net.minecraft.client.gui.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRecipeBookGui extends RecipeBookGui {
   private Iterator<Item> iterator;
   private Set<Item> fuels;
   private Slot fuelSlot;
   private Item fuel;
   private float time;

   protected boolean toggleFiltering() {
      boolean flag = !this.func_212962_b();
      this.func_212959_a(flag);
      return flag;
   }

   protected abstract boolean func_212962_b();

   protected abstract void func_212959_a(boolean p_212959_1_);

   public boolean isVisible() {
      return this.func_212963_d();
   }

   protected abstract boolean func_212963_d();

   protected void setVisible(boolean p_193006_1_) {
      this.func_212957_c(p_193006_1_);
      if (!p_193006_1_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   protected abstract void func_212957_c(boolean p_212957_1_);

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
   }

   protected String func_205703_f() {
      return I18n.get(this.filterButton.isStateTriggered() ? this.func_212960_g() : "gui.recipebook.toggleRecipes.all");
   }

   protected abstract String func_212960_g();

   public void slotClicked(@Nullable Slot p_191874_1_) {
      super.slotClicked(p_191874_1_);
      if (p_191874_1_ != null && p_191874_1_.index < this.menu.getSize()) {
         this.fuelSlot = null;
      }

   }

   public void setupGhostRecipe(IRecipe<?> p_193951_1_, List<Slot> p_193951_2_) {
      ItemStack itemstack = p_193951_1_.getResultItem();
      this.ghostRecipe.setRecipe(p_193951_1_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_193951_2_.get(2)).x, (p_193951_2_.get(2)).y);
      NonNullList<Ingredient> nonnulllist = p_193951_1_.getIngredients();
      this.fuelSlot = p_193951_2_.get(1);
      if (this.fuels == null) {
         this.fuels = this.getFuelItems();
      }

      this.iterator = this.fuels.iterator();
      this.fuel = null;
      Iterator<Ingredient> iterator = nonnulllist.iterator();

      for(int i = 0; i < 2; ++i) {
         if (!iterator.hasNext()) {
            return;
         }

         Ingredient ingredient = iterator.next();
         if (!ingredient.isEmpty()) {
            Slot slot = p_193951_2_.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
         }
      }

   }

   protected abstract Set<Item> getFuelItems();

   public void func_191864_a(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
      super.func_191864_a(p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
      if (this.fuelSlot != null) {
         if (!Screen.hasControlDown()) {
            this.time += p_191864_4_;
         }

         int i = this.fuelSlot.x + p_191864_1_;
         int j = this.fuelSlot.y + p_191864_2_;
         AbstractGui.fill(i, j, i + 16, j + 16, 822018048);
         this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), i, j);
         RenderSystem.depthFunc(516);
         AbstractGui.fill(i, j, i + 16, j + 16, 822083583);
         RenderSystem.depthFunc(515);
      }
   }

   private Item getFuel() {
      if (this.fuel == null || this.time > 30.0F) {
         this.time = 0.0F;
         if (this.iterator == null || !this.iterator.hasNext()) {
            if (this.fuels == null) {
               this.fuels = this.getFuelItems();
            }

            this.iterator = this.fuels.iterator();
         }

         this.fuel = this.iterator.next();
      }

      return this.fuel;
   }
}