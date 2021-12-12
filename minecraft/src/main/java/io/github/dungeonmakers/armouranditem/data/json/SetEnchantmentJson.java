package io.github.dungeonmakers.armouranditem.data.json;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum SetEnchantmentJson {
  ;
  public static @NotNull CreateEnchantmentJson createEnchantmentJson(List<String> pattern,
      Map<Character, Ingredient> key, Item item, String value, int hideFlags, int level,
      Enchantment enchantmentName) {
    var createEnchantmentJson = new CreateEnchantmentJson();
    createEnchantmentJson.setPattern(pattern);
    createEnchantmentJson.setKey(key);
    createEnchantmentJson.setResult(setResult(item, value, hideFlags, level, enchantmentName));
    return createEnchantmentJson;
  }

  public static @NotNull Result setResult(Item item, String value, int hideFlags, int level,
      Enchantment enchantmentName) {
    var result = new Result();
    result.setItem(item, value);
    result.setNbt(setNbt(hideFlags, level, enchantmentName));
    return result;
  }

  public static @NotNull @Unmodifiable List<String> setNbt(int hideFlags, int level,
      Enchantment enchantmentName) {
    var nbt = new NBT();
    nbt.setEnchantment(Collections.singletonList(setEnchantment(level, enchantmentName)));
    nbt.setHideFlags(hideFlags);
    return Collections.singletonList(nbt.toString());
  }

  public static @NotNull RegisterEnchantment setEnchantment(int level,
      Enchantment enchantmentName) {
    var enchantment = new RegisterEnchantment();
    enchantment.setLevel(level);
    enchantment.setEnchantmentName(enchantmentName);
    return enchantment;
  }
}
