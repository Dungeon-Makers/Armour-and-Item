package com.yusuf.armouranditem.core.init;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.common.material.CustomArmorMaterial;
import com.yusuf.armouranditem.common.material.CustomToolMaterial;
import com.yusuf.armouranditem.core.itemgroup.MainItemGroup;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArmourAndItem.MOD_ID);

    public static final RegistryObject<Item> BLACK_DIAMOND = ITEMS.register("black_diamond",
            () -> new Item(new Item.Properties().tab(MainItemGroup.MAIN)));

    public static final RegistryObject<Item> PURPLE_DIAMOND = ITEMS.register("purple_diamond",
            () -> new Item(new Item.Properties().tab(MainItemGroup.MAIN)));

    public static final RegistryObject<Item> BLACK_DIAMOND_SCRAP = ITEMS.register("black_diamond_scrap",
            () -> new Item(new Item.Properties().tab(MainItemGroup.MAIN)));


    //tools

    public static final RegistryObject<SwordItem> BLACK_DIAMOND_SWORD = ITEMS.register("black_diamond_sword",
            () -> new SwordItem(CustomToolMaterial.BLACK_DIAMOND_SWORD, 0, 7f,
                    new Item.Properties().stacksTo(1).durability(600).tab(MainItemGroup.MAIN)));


    // black diamond armour
    public static final RegistryObject<Item> BLACK_DIAMOND_HELMET = ITEMS.register("black_diamond_helmet",
            () -> new ArmorItem(CustomArmorMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlotType.HEAD,
                    new Item.Properties().tab(MainItemGroup.MAIN)));

    public static final RegistryObject<Item> BLACK_DIAMOND_CHESTPLATE = ITEMS.register("black_diamond_chestplate",
            () -> new ArmorItem(CustomArmorMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlotType.CHEST,
                    new Item.Properties().tab(MainItemGroup.MAIN)));

    public static final RegistryObject<Item> BLACK_DIAMOND_LEGGINGS = ITEMS.register("black_diamond_leggings",
            () -> new ArmorItem(CustomArmorMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlotType.LEGS,
                    new Item.Properties().tab(MainItemGroup.MAIN)));

    public static final RegistryObject<Item> BLACK_DIAMOND_BOOTS = ITEMS.register("black_diamond_boots",
            () -> new ArmorItem(CustomArmorMaterial.BLACK_DIAMOND_ARMOUR, EquipmentSlotType.FEET,
                    new Item.Properties().tab(MainItemGroup.MAIN)));

}