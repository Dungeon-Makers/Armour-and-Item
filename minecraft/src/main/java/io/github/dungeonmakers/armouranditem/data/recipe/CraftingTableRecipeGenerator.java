package io.github.dungeonmakers.armouranditem.data.recipe;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.data.json.EnchantmentRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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
    ShapedRecipeBuilder.shaped(BlockInit.BLACK_DIAMOND_BLOCK.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("###").pattern("###").pattern("###")
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(BlockInit.PURPLE_DIAMOND_BLOCK.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).pattern("###").pattern("###").pattern("###")
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);


    // tools
    EnchantmentRecipeProvider.shaped(ItemInit.BLACK_DIAMOND_SWORD.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).define('£', Items.STICK).pattern("#")
        .pattern("#").pattern("£").setEnchantment(Enchantments.MENDING, 1, 1)
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    EnchantmentRecipeProvider.shaped(ItemInit.PURPLE_DIAMOND_SWORD.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).define('£', Items.STICK).pattern("#")
        .pattern("#").pattern("£").setEnchantment(Enchantments.MENDING, 1, 1)
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);


    // amour
    ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_HELMET.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("###").pattern("# #")
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_HELMET.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).pattern("###").pattern("# #")
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_CHESTPLATE.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("# #").pattern("###").pattern("###")
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_CHESTPLATE.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).pattern("# #").pattern("###").pattern("###")
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_LEGGINGS.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("###").pattern("# #").pattern("# #")
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_LEGGINGS.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).pattern("###").pattern("# #").pattern("# #")
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_BOOTS.get())
        .define('#', ItemInit.BLACK_DIAMOND.get()).pattern("# #").pattern("# #")
        .unlockedBy("has_black_diamond", has(ItemInit.BLACK_DIAMOND.get())).save(consumer);

    ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_BOOTS.get())
        .define('#', ItemInit.PURPLE_DIAMOND.get()).pattern("# #").pattern("# #")
        .unlockedBy("has_purple_diamond", has(ItemInit.PURPLE_DIAMOND.get())).save(consumer);
  }
}
