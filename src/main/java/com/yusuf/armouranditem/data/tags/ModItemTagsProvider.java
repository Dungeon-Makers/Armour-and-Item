package com.yusuf.armouranditem.data.tags;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.init.ItemInit;
import com.yusuf.armouranditem.core.init.TagsInit;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, ArmourAndItem.MOD_ID, existingFileHelper);
    }
    @Override
    protected void addTags() {
        //ores
        copy(TagsInit.Blocks.ORES_BLACK_DIAMOND, TagsInit.Items.ORES_BLACK_DIAMOND);
        copy(TagsInit.Blocks.ORES_PURPLE_DIAMOND, TagsInit.Items.ORES_PURPLE_DIAMOND);

        //blocks
        copy(TagsInit.Blocks.STORAGE_BLACK_DIAMOND, TagsInit.Items.STORAGE_BLACK_DIAMOND);
        copy(TagsInit.Blocks.STORAGE_PURPLE_DIAMOND, TagsInit.Items.STORAGE_PURPLE_DIAMOND);

        //ingots
        tag(TagsInit.Items.INGOTS_BLACK_DIAMOND).add(ItemInit.BLACK_DIAMOND.get());
        tag(TagsInit.Items.INGOTS_PURPLE_DIAMOND).add(ItemInit.PURPLE_DIAMOND.get());
    }
}