package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup.MAIN;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, ArmourAndItem.MOD_ID);

  public static final RegistryObject<Item> BLACK_DIAMOND =
      ITEMS.register("black_diamond", () -> new Item(new Item.Properties().tab(MAIN)));
}
