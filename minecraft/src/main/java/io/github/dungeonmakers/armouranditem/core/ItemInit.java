package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.core.util.InitUtil;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.*;

import static io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup.MAIN;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS = InitUtil.create(ForgeRegistries.ITEMS);

  public static final RegistryObject<Item> BLACK_DIAMOND = register("black_diamond");

  private static RegistryObject<Item> register(String name) {
    return ITEMS.register(name, () -> new Item(new Item.Properties().tab(MAIN)));
  }
}
