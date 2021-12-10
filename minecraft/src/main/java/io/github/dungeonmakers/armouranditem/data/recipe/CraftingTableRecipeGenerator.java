package io.github.dungeonmakers.armouranditem.data.recipe;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CraftingTableRecipeGenerator extends RecipeProvider {
  public CraftingTableRecipeGenerator(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Contract("_ -> new")
  private static @NotNull ResourceLocation modId(String path) {
    return new ResourceLocation(ArmourAndItem.MOD_ID, path);
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    // blocks
    CraftTableJsonGenerator.shaped(BlockInit.BLACK_DIAMOND_BLOCK.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("###").pattern("###").pattern("###")
        .addCriterion("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);
  }
}
