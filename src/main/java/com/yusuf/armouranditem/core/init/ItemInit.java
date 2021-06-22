package com.yusuf.armouranditem.core.init;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.itemgroup.MainItemGroup;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArmourAndItem.MOD_ID);

    public static final RegistryObject<Item> BLACK_DIAMOND = ITEMS.register("black_diamond",
            () -> new Item(new Item.Properties().tab(MainItemGroup.MAIN)));
    public static final RegistryObject<Item> BLACK_DIAMOND_SCRAP = ITEMS.register("black_diamond_scrap",
            () -> new Item(new Item.Properties().tab(MainItemGroup.MAIN)));
}