package io.github.dungeonmakers.armouranditem.data.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum CreateEnchantmentObjects {
  ;
  public static @NotNull JsonObject jsonObject(ResourceLocation id, Item result, int count,
      @NotNull String group, List<String> pattern, Map<Character, Ingredient> key,
      Advancement.Builder advancement, Enchantment enchantment, int enchantmentLevel, int hideFlags,
      ResourceLocation advancementId) {
    JsonObject jsonObject = new JsonObject();
    if (!group.isEmpty()) {
      jsonObject.addProperty("group", group);
    }

    JsonArray jsonArray = new JsonArray();

    for (String s : pattern) {
      jsonArray.add(s);
    }

    jsonObject.add("pattern", jsonArray);
    JsonObject jsonobject = new JsonObject();

    for (Map.Entry<Character, Ingredient> entry : key.entrySet()) {
      jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
    }

    jsonObject.add("key", jsonobject);
    JsonObject jsonObject1 = new JsonObject();
    jsonObject1.addProperty("item", Registry.ITEM.getKey(result).toString());
    if (count > 1) {
      jsonObject1.addProperty("count", count);
    }

    // enchantment
    JsonArray jsonArray1 = new JsonArray();
    JsonObject jsonObject2 = new JsonObject();
    JsonObject jsonObject3 = new JsonObject();
    jsonObject3.addProperty("id",
        Objects.requireNonNull(Registry.ENCHANTMENT.getKey(enchantment)).toString());
    jsonObject3.addProperty("lvl", enchantmentLevel);
    jsonArray1.add(jsonObject3);
    jsonObject2.add("Enchantments", jsonArray1);
    jsonObject2.addProperty("HideFlags", hideFlags);
    jsonObject1.add("nbt", jsonObject2);

    jsonObject.add("result", jsonObject1);

    return jsonObject;
  }
}
