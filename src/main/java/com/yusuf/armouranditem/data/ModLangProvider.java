package com.yusuf.armouranditem.data;

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
        //ores
        block(BlockInit.BLACK_DIAMOND_ORE, "Black Diamond Ore");

        //ingots
        item(ItemInit.BLACK_DIAMOND, "Black Diamond");
        item(ItemInit.BLACK_DIAMOND_SCRAP, "Black Diamond scrap");

        //tools and armour
        item(ItemInit.BLACK_DIAMOND_HELMET, "Black Diamond Helmet");
        item(ItemInit.BLACK_DIAMOND_CHESTPLATE, "Black Diamond Chestplate");
        item(ItemInit.BLACK_DIAMOND_LEGGINGS, "Black Diamond Leggings");
        item(ItemInit.BLACK_DIAMOND_BOOTS, "Black Diamond Boots");
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
