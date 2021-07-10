package com.yusuf.armouranditem.core.init;


import com.yusuf.armouranditem.ArmourAndItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class TagsInit {


    public static final class Blocks {
        ///ores
        public static final ITag.INamedTag<Block> ORES_BLACK_DIAMOND = BlockTags.bind("forge:ores/black_diamond");
        public static final ITag.INamedTag<Block> ORES_PURPLE_DIAMOND = BlockTags.bind("forge:ores/purple_diamond");

        //blocks
        public static final ITag.INamedTag<Block> STORAGE_BLACK_DIAMOND = BlockTags.bind("forge:storage_blocks/black_diamond");
        public static final ITag.INamedTag<Block> STORAGE_PURPLE_DIAMOND = BlockTags.bind("forge:storage_blocks/purple_diamond");


        private static ITag.INamedTag<Block> forge(String path) {
            return BlockTags.bind(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Block> mod(String path) {
            return BlockTags.bind(new ResourceLocation(ArmourAndItem.MOD_ID, path).toString());
        }

    }

    public static final class Items {
        //ores
        public static final ITag.INamedTag<Item> ORES_BLACK_DIAMOND = ItemTags.bind("forge:ores/black_diamond");
        public static final ITag.INamedTag<Item> ORES_PURPLE_DIAMOND = ItemTags.bind("forge:ores/purple_diamond");

        //blocks
        public static final ITag.INamedTag<Item> STORAGE_BLACK_DIAMOND = ItemTags.bind("forge:storage_blocks/black_diamond");
        public static final ITag.INamedTag<Item> STORAGE_PURPLE_DIAMOND = ItemTags.bind("forge:storage_blocks/purple_diamond");

        //inogts
        public static final ITag.INamedTag<Item> INGOTS_BLACK_DIAMOND = ItemTags.bind("forge:ingots/black_diamond");
        public static final ITag.INamedTag<Item> SCRAP_BLACK_DIAMOND_SCRAP = ItemTags.bind("forge:scraps/black_diamond_scrap");
        public static final ITag.INamedTag<Item> INGOTS_PURPLE_DIAMOND = ItemTags.bind("forge:scraps/purple_diamond");


        private static ITag.INamedTag<Item> forge(String path) {
            return ItemTags.bind(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Item> mod(String path) {
            return ItemTags.bind(new ResourceLocation(ArmourAndItem.MOD_ID, path).toString());
        }
    }
}