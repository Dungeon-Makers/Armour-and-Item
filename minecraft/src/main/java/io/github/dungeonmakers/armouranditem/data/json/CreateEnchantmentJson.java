package io.github.dungeonmakers.armouranditem.data.json;

import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Map;

public class CreateEnchantmentJson {
  Result result;
  int count;
  List<String> rows = Lists.newArrayList();
  String type;
  List<String> pattern;
  Map<Character, Ingredient> key;

  public void setType(String type) {
    this.type = type;
  }

  public void setPattern(List<String> pattern) {
    this.pattern = pattern;
  }

  public void setKey(Map<Character, Ingredient> key) {
    this.key = key;
  }

  public void setResult(Result result) {
    this.result = result;
  }
}


class Result {
  Item item;
  String value;
  List<String> nbt;

  public void setItem(Item item, String value) {
    this.item = item;
    this.value = value;
  }

  public void setNbt(List<String> nbt) {
    this.nbt = nbt;
  }
}


class NBT {
  List<RegisterEnchantment> enchantment;
  int hideFlags;

  public void setEnchantment(List<RegisterEnchantment> enchantment) {
    this.enchantment = enchantment;
  }

  public void setHideFlags(int hideFlags) {
    this.hideFlags = hideFlags;
  }
}


class RegisterEnchantment {
  Enchantment enchantmentName;
  int level;

  public void setEnchantmentName(Enchantment enchantmentName) {
    this.enchantmentName = enchantmentName;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
