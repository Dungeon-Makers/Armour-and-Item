package io.github.dungeonmakers.armouranditem.data.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @see RecipeProvider
 */
public class CraftTableJsonGenerator implements RecipeBuilder {
  private final Item result;
  private final int count;
  private final List<String> rows = Lists.newArrayList();
  private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
  private final Advancement.Builder advancement = Advancement.Builder.advancement();
  @Nullable
  private String group;

  public CraftTableJsonGenerator(@NotNull ItemLike itemLike, int count) {
    this.result = itemLike.asItem();
    this.count = count;
  }

  @Contract("_ -> new")
  public static @NotNull CraftTableJsonGenerator shaped(ItemLike itemLike) {
    return shaped(itemLike, 1);
  }

  @Contract("_, _ -> new")
  public static @NotNull CraftTableJsonGenerator shaped(ItemLike itemLike, int count) {
    return new CraftTableJsonGenerator(itemLike, count);
  }

  public CraftTableJsonGenerator define(Character character, Tag<Item> item) {
    return this.define(character, Ingredient.of(item));
  }

  public CraftTableJsonGenerator define(Character character, ItemLike itemLike) {
    return this.define(character, Ingredient.of(itemLike));
  }

  public CraftTableJsonGenerator define(Character character, Ingredient ingredient) {
    if (this.key.containsKey(character)) {
      throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
    } else if (character == ' ') {
      throw new IllegalArgumentException(
          "Symbol ' ' (whitespace) is reserved and cannot be defined");
    } else {
      this.key.put(character, ingredient);
      return this;
    }
  }

  public CraftTableJsonGenerator pattern(String pattern) {
    if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
      throw new IllegalArgumentException("Pattern must be the same width on every line!");
    } else {
      this.rows.add(pattern);
      return this;
    }
  }

  @NotNull
  public CraftTableJsonGenerator unlockedBy(@NotNull String string,
      @NotNull CriterionTriggerInstance criterionTriggerInstance) {
    return this.addCriterion(string, criterionTriggerInstance);
  }

  public @NotNull CraftTableJsonGenerator addCriterion(@NotNull String string,
      @NotNull CriterionTriggerInstance criterionTriggerInstance) {
    this.advancement.addCriterion(string, criterionTriggerInstance);
    return this;
  }


  public @NotNull CraftTableJsonGenerator group(@Nullable String string) {
    this.group = string;
    return this;
  }

  public @NotNull Item getResult() {
    return this.result;
  }

  public void save(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer,
      @NotNull ResourceLocation resourceLocation) {
    this.ensureValid(resourceLocation);
    this.advancement.parent(new ResourceLocation("recipes/root"))
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
        .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
        .requirements(RequirementsStrategy.OR);
    finishedRecipeConsumer.accept(new CraftTableJsonGenerator.Result(resourceLocation, this.result,
        this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement,
        new ResourceLocation(resourceLocation.getNamespace(),
            "recipes/" + Objects.requireNonNull(this.result.getItemCategory()).getRecipeFolderName()
                + "/" + resourceLocation.getPath())));
  }

  private void ensureValid(ResourceLocation resourceLocation) throws IllegalStateException {
    if (!this.rows.isEmpty()) {
      Set<Character> set = Sets.newHashSet(this.key.keySet());
      set.remove(' ');

      for (String s : this.rows) {
        for (int i = 0; i < s.length(); ++i) {
          char c0 = s.charAt(i);
          if (!this.key.containsKey(c0) && c0 != ' ') {
            throw new IllegalStateException(
                "Pattern in recipe " + resourceLocation + " uses undefined symbol '" + c0 + "'");
          }

          set.remove(c0);
        }
      }

      if (!set.isEmpty()) {
        throw new IllegalStateException(
            "Ingredients are defined but not used in pattern for recipe " + resourceLocation);
      } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
        throw new IllegalStateException("Shaped recipe " + resourceLocation
            + " only takes in a single item - should it be a shapeless recipe instead?");
      } else if (this.advancement.getCriteria().isEmpty()) {
        throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
      }
    } else {
      throw new IllegalStateException(
          "No pattern is defined for shaped recipe " + resourceLocation + "!");
    }
  }

  public record Result(ResourceLocation id, Item resultItem, int count,
      String group, List<String> pattern, Map<Character, Ingredient> key,
      Advancement.Builder advancement, ResourceLocation advancementId,
      Enchantment enchantment) implements FinishedRecipe {

    public void serializeRecipeData(@NotNull JsonObject jsonObject) {
      if (!this.group.isEmpty()) {
        jsonObject.addProperty("group", this.group);
      }

      JsonArray jsonarray = new JsonArray();

      for (String s : this.pattern) {
        jsonarray.add(s);
      }

      jsonObject.add("pattern", jsonarray);
      JsonObject jsonobject = new JsonObject();

      for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
        jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
      }

      jsonObject.add("key", jsonobject);
      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.addProperty("item", Registry.ITEM.getKey(this.resultItem).toString());
      if (this.count > 1) {
        jsonobject1.addProperty("count", this.count);
      }

      JsonObject nbtObject = new JsonObject();
      jsonobject1.addProperty("nbt", "nbt");
      nbtObject.addProperty("enchantments",
              Registry.ENCHANTMENT.getKey(this.enchantment).toString());

      jsonObject.add("result", jsonobject1);
      jsonobject1.add("Enchantments", nbtObject);
    }

    public @NotNull RecipeSerializer<?> getType() {
      return RecipeSerializer.SHAPED_RECIPE;
    }

    public @NotNull ResourceLocation getId() {
      return this.id;
    }

    public @NotNull JsonObject serializeAdvancement() {
      return this.advancement.serializeToJson();
    }

    @Nullable
    public ResourceLocation getAdvancementId() {
      return this.advancementId;
    }
  }
}
