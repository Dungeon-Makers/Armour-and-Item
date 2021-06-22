package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeList {
   private final List<IRecipe<?>> recipes = Lists.newArrayList();
   private final Set<IRecipe<?>> craftable = Sets.newHashSet();
   private final Set<IRecipe<?>> fitsDimensions = Sets.newHashSet();
   private final Set<IRecipe<?>> known = Sets.newHashSet();
   private boolean singleResultItem = true;

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook p_194214_1_) {
      for(IRecipe<?> irecipe : this.recipes) {
         if (p_194214_1_.contains(irecipe)) {
            this.known.add(irecipe);
         }
      }

   }

   public void canCraft(RecipeItemHelper p_194210_1_, int p_194210_2_, int p_194210_3_, RecipeBook p_194210_4_) {
      for(int i = 0; i < this.recipes.size(); ++i) {
         IRecipe<?> irecipe = this.recipes.get(i);
         boolean flag = irecipe.canCraftInDimensions(p_194210_2_, p_194210_3_) && p_194210_4_.contains(irecipe);
         if (flag) {
            this.fitsDimensions.add(irecipe);
         } else {
            this.fitsDimensions.remove(irecipe);
         }

         if (flag && p_194210_1_.canCraft(irecipe, (IntList)null)) {
            this.craftable.add(irecipe);
         } else {
            this.craftable.remove(irecipe);
         }
      }

   }

   public boolean isCraftable(IRecipe<?> p_194213_1_) {
      return this.craftable.contains(p_194213_1_);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<IRecipe<?>> getRecipes(boolean p_194208_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();
      Set<IRecipe<?>> set = p_194208_1_ ? this.craftable : this.fitsDimensions;

      for(IRecipe<?> irecipe : this.recipes) {
         if (set.contains(irecipe)) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public List<IRecipe<?>> getDisplayRecipes(boolean p_194207_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(IRecipe<?> irecipe : this.recipes) {
         if (this.fitsDimensions.contains(irecipe) && this.craftable.contains(irecipe) == p_194207_1_) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public void func_192709_a(IRecipe<?> p_192709_1_) {
      this.recipes.add(p_192709_1_);
      if (this.singleResultItem) {
         ItemStack itemstack = this.recipes.get(0).getResultItem();
         ItemStack itemstack1 = p_192709_1_.getResultItem();
         this.singleResultItem = ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1);
      }

   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}