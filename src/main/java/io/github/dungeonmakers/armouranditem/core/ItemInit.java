package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public enum ItemInit {
  ;
  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, ArmourAndItem.MOD_ID);
}
