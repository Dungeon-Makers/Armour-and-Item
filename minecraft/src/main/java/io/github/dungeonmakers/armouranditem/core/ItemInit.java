package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup;
import io.github.dungeonmakers.armouranditem.core.material.CustomToolMaterial;
import io.github.dungeonmakers.armouranditem.core.util.InitUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.*;

import static io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup.MAIN;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS = InitUtil.create(ForgeRegistries.ITEMS);

  public static final RegistryObject<Item> BLACK_DIAMOND = register("black_diamond");
  public static final RegistryObject<SwordItem> BLACK_DIAMOND_SWORD = ITEMS
      .register("black_diamond_sword", () -> new SwordItem(CustomToolMaterial.BLACK_DIAMOND_SWORD,
          0, 7f, new Item.Properties().stacksTo(1).durability(1562).tab(MainItemGroup.MAIN)));

  private static RegistryObject<Item> register(String name) {
    return ITEMS.register(name, () -> new Item(new Item.Properties().tab(MAIN)));
  }
}
