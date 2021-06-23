package com.yusuf.armouranditem.data;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.init.BlockInit;
import com.yusuf.armouranditem.core.init.TagsInit;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, ArmourAndItem.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        //ores
        tag(TagsInit.Blocks.ORES_BLACK_DIAMOND).add(BlockInit.BLACK_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.ORES_BLACK_DIAMOND);

        tag(TagsInit.Blocks.ORES_PURPLE_DIAMOND).add(BlockInit.PURPLE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.ORES_PURPLE_DIAMOND);

        //blocks
        tag(TagsInit.Blocks.STORAGE_BLACK_DIAMOND).add(BlockInit.BLACK_DIAMOND_BLOCK.get());
        tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.STORAGE_BLACK_DIAMOND);
    }
}