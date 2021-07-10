package com.yusuf.armouranditem.data.lang;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.init.BlockInit;
import com.yusuf.armouranditem.core.init.ItemInit;
import com.yusuf.armouranditem.core.itemgroup.MainItemGroup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.RegistryObject;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, ArmourAndItem.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        //block
        block(BlockInit.BLACK_DIAMOND_BLOCK, "Black Diamond Block");
        block(BlockInit.PURPLE_DIAMOND_BLOCK, "Purple Diamond Block");

        //ores
        block(BlockInit.BLACK_DIAMOND_ORE, "Black Diamond Ore");
        block(BlockInit.PURPLE_DIAMOND_ORE, "Purple Diamond Ore");


        //ingots
        item(ItemInit.BLACK_DIAMOND, "Black Diamond");
        item(ItemInit.BLACK_DIAMOND_SCRAP, "Black Diamond scrap");
        item(ItemInit.PURPLE_DIAMOND, "Purple Diamond");


        //tools and armour
        item(ItemInit.BLACK_DIAMOND_HELMET, "Black Diamond Helmet");
        item(ItemInit.BLACK_DIAMOND_CHESTPLATE, "Black Diamond Chestplate");
        item(ItemInit.BLACK_DIAMOND_LEGGINGS, "Black Diamond Leggings");
        item(ItemInit.BLACK_DIAMOND_BOOTS, "Black Diamond Boots");
        item(ItemInit.PURPLE_DIAMOND_HELMET, "Purple Diamond Helmet");
        item(ItemInit.PURPLE_DIAMOND_CHESTPLATE, "Purple Diamond Chestplate");
        item(ItemInit.PURPLE_DIAMOND_LEGGINGS, "Purple Diamond Leggings");
        item(ItemInit.PURPLE_DIAMOND_BOOTS, "Purple Diamond Boots");
        item(ItemInit.BLACK_DIAMOND_SWORD, "Black Diamond Sword");


        //others
        add(MainItemGroup.MAIN.getDisplayName().getString(), "Armour and Item Tab");
    }

    private <T extends Item> void item(RegistryObject<T> entry, String name) {
        add(entry.get(), name);
    }

    private <T extends Block> void block(RegistryObject<T> entry, String name) {
        add(entry.get(), name);
    }
}
