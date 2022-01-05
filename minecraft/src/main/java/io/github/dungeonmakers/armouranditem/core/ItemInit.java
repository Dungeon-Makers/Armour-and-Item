package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.core.material.CustomArmourMaterial;
import io.github.dungeonmakers.armouranditem.core.material.CustomToolMaterial;
import io.github.dungeonmakers.armouranditem.core.util.InitUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.*;


import static io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup.MAIN;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS = InitUtil.create(ForgeRegistries.ITEMS);

  // ingots
  public static final RegistryObject<Item> BLACK_DIAMOND = register("black_diamond");
  public static final RegistryObject<Item> PURPLE_DIAMOND = register("purple_diamond");

  // armour
  public static final RegistryObject<ArmorItem> BLACK_DIAMOND_HELMET = register(
      "black_diamond_helmet", CustomArmourMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlot.HEAD);

  public static final RegistryObject<ArmorItem> BLACK_DIAMOND_CHESTPLATE = register(
      "black_diamond_chestplate", CustomArmourMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlot.CHEST);


  public static final RegistryObject<ArmorItem> BLACK_DIAMOND_LEGGINGS = register(
      "black_diamond_leggings", CustomArmourMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlot.LEGS);

  public static final RegistryObject<ArmorItem> BLACK_DIAMOND_BOOTS = register(
      "black_diamond_boots", CustomArmourMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlot.FEET);

  public static final RegistryObject<ArmorItem> PURPLE_DIAMOND_HELMET = register(
      "purple_diamond_helmet", CustomArmourMaterial.PURPLE_DIAMOND_ARMOUR, EquipmentSlot.HEAD);

  public static final RegistryObject<ArmorItem> PURPLE_DIAMOND_CHESTPLATE = register(
      "purple_diamond_chestplate", CustomArmourMaterial.PURPLE_DIAMOND_ARMOUR, EquipmentSlot.CHEST);


  public static final RegistryObject<ArmorItem> PURPLE_DIAMOND_LEGGINGS = register(
      "purple_diamond_leggings", CustomArmourMaterial.PURPLE_DIAMOND_ARMOUR, EquipmentSlot.LEGS);

  public static final RegistryObject<ArmorItem> PURPLE_DIAMOND_BOOTS = register(
      "purple_diamond_boots", CustomArmourMaterial.PURPLE_DIAMOND_ARMOUR, EquipmentSlot.FEET);


  // tools
  public static final RegistryObject<SwordItem> BLACK_DIAMOND_SWORD =
      register("black_diamond_sword", CustomToolMaterial.BLACK_DIAMOND_SWORD, 7, 7f);

  public static final RegistryObject<SwordItem> PURPLE_DIAMOND_SWORD =
      register("purple_diamond_sword", CustomToolMaterial.PURPLE_DIAMOND_SWORD, 8, 7.5f);


  private static RegistryObject<Item> register(String name) {
    return ITEMS.register(name, () -> new Item(new Item.Properties().tab(MAIN)));
  }

  private static RegistryObject<ArmorItem> register(String name, ArmorMaterial armorMaterial,
      EquipmentSlot equipmentSlot) {
    return ITEMS.register(name,
        () -> new ArmorItem(armorMaterial, equipmentSlot, new Item.Properties().tab(MAIN)));
  }

  private static RegistryObject<SwordItem> register(String name, Tier customToolMaterial,
      int attackDamage, float attackSpeed) {
    return ITEMS.register(name, () -> new SwordItem(customToolMaterial, attackDamage, attackSpeed,
        new Item.Properties().stacksTo(1).tab(MAIN)));
  }
}
