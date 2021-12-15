package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup;
import io.github.dungeonmakers.armouranditem.core.material.CustomArmourMaterial;
import io.github.dungeonmakers.armouranditem.core.material.CustomToolMaterial;
import io.github.dungeonmakers.armouranditem.core.material.base.BaseArmorMaterial;
import io.github.dungeonmakers.armouranditem.core.util.InitUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

import static io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup.MAIN;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS = InitUtil.create(ForgeRegistries.ITEMS);

  // ingots
  public static final RegistryObject<Item> BLACK_DIAMOND = register("black_diamond");
  public static final RegistryObject<Item> PURPLE_DIAMOND = register("purple_diamond");

  //armour
  public static final RegistryObject<ArmorItem> BLACK_DIAMOND_HELMET =
          register("black_diamond_helmet", () -> new ArmorItem(CustomArmourMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlot.HEAD, new Item.Properties().tab(MAIN)));

  // tools
  public static final RegistryObject<SwordItem> BLACK_DIAMOND_SWORD = ITEMS
      .register("black_diamond_sword", () -> new SwordItem(CustomToolMaterial.BLACK_DIAMOND_SWORD,
          0, 7f, new Item.Properties().stacksTo(1).durability(1562).tab(MainItemGroup.MAIN)));

  private static RegistryObject<Item> register(String name) {
    return ITEMS.register(name, () -> new Item(new Item.Properties().tab(MAIN)));
  }

  private static RegistryObject<ArmorItem> register(String name, BaseArmorMaterial customArmourMaterial, EquipmentSlot equipmentSlot) {
    return ITEMS.register(name, () -> new ArmorItem(customArmourMaterial, equipmentSlot, new Item.Properties().tab(MAIN)));
  }
}
