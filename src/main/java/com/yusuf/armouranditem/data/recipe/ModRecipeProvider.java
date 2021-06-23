package com.yusuf.armouranditem.data.recipe;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.init.BlockInit;
import com.yusuf.armouranditem.core.init.ItemInit;
import com.yusuf.armouranditem.core.init.TagsInit;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }


    private static ResourceLocation modId(String path) {
        return new ResourceLocation(ArmourAndItem.MOD_ID, path);
    }
    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(ItemInit.BLACK_DIAMOND.get(), 9)
                .requires(BlockInit.BLACK_DIAMOND_BLOCK.get())
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer);

        ShapedRecipeBuilder.shaped(BlockInit.BLACK_DIAMOND_BLOCK.get())
                .define('#',TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ItemInit.PURPLE_DIAMOND.get(), 9)
                .requires(BlockInit.PURPLE_DIAMOND_BLOCK.get())
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer);

        ShapedRecipeBuilder.shaped(BlockInit.PURPLE_DIAMOND_BLOCK.get())
                .define('#',TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer);

        //scraps

         ShapelessRecipeBuilder.shapeless(ItemInit.BLACK_DIAMOND.get(), 1)
                .requires(TagsInit.Items.SCRAP_BLACK_DIAMOND_SCRAP)
                .requires(TagsInit.Items.SCRAP_BLACK_DIAMOND_SCRAP)
                .requires(TagsInit.Items.SCRAP_BLACK_DIAMOND_SCRAP)
                .requires(TagsInit.Items.SCRAP_BLACK_DIAMOND_SCRAP)
                .requires(Items.GOLD_INGOT.getItem())
                .requires(Items.GOLD_INGOT.getItem())
                .requires(Items.GOLD_INGOT.getItem())
                .requires(Items.GOLD_INGOT.getItem())
                .unlockedBy("has_item", has(TagsInit.Items.SCRAP_BLACK_DIAMOND_SCRAP))
                .save(consumer, modId("black_diamond_scrap"));

        //amour and tools
        ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_HELMET.get())
                .define('A', TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern("AAA")
                .pattern("A A")
                .pattern("   ")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer,modId("black_diamond_helmet"));

        ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_CHESTPLATE.get())
                .define('A', TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern("A A")
                .pattern("AAA")
                .pattern("AAA")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer,modId("black_diamond_chestplate"));

        ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_LEGGINGS.get())
                .define('A', TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern("AAA")
                .pattern("A A")
                .pattern("A A")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer,modId("black_diamond_leggings"));

        ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_BOOTS.get())
                .define('A', TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern("   ")
                .pattern("A A")
                .pattern("A A")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer,modId("black_diamond_boot"));

        ShapedRecipeBuilder.shaped(ItemInit.BLACK_DIAMOND_SWORD.get())
                .define('#', Items.STICK.getItem())
                .define('K', TagsInit.Items.INGOTS_BLACK_DIAMOND)
                .pattern(" K ")
                .pattern(" K ")
                .pattern(" # ")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_BLACK_DIAMOND))
                .save(consumer,modId("black_diamond_sword"));

        ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_HELMET.get())
                .define('A', TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern("AAA")
                .pattern("A A")
                .pattern("   ")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer,modId("purple_diamond_helmet"));

        ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_CHESTPLATE.get())
                .define('A', TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern("A A")
                .pattern("AAA")
                .pattern("AAA")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer,modId("purple_diamond_chestplate"));

        ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_LEGGINGS.get())
                .define('A', TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern("AAA")
                .pattern("A A")
                .pattern("A A")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer,modId("purple_diamond_leggings"));

        ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_BOOTS.get())
                .define('A', TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern("   ")
                .pattern("A A")
                .pattern("A A")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer,modId("purple_diamond_boot"));

        ShapedRecipeBuilder.shaped(ItemInit.PURPLE_DIAMOND_SWORD.get())
                .define('#', Items.STICK.getItem())
                .define('K', TagsInit.Items.INGOTS_PURPLE_DIAMOND)
                .pattern(" K ")
                .pattern(" K ")
                .pattern(" # ")
                .unlockedBy("has_item", has(TagsInit.Items.INGOTS_PURPLE_DIAMOND))
                .save(consumer,modId("purple_diamond_sword"));

    }
    private Ingredient ingredient(IItemProvider entry) {
        return Ingredient.of(entry);
    }
}