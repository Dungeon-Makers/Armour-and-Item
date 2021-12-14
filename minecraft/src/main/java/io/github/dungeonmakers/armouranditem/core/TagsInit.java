package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public enum TagsInit {
  ;
  public enum Blocks {
    ;
    // ores
    public static final Tag.Named<Block> ORES_BLACK_DIAMONDS = forge("ores/black_diamonds");
    public static final Tag.Named<Block> ORES_PURPLE_DIAMONDS = forge("ores/purple_diamonds");

    // blocks
    public static final Tag.Named<Block> STORAGE_BLACK_DIAMOND =
        forge("storage_blocks/black_diamond");
    public static final Tag.Named<Block> STORAGE_PURPLE_DIAMOND =
        forge("storage_blocks/purple_diamond");


    private static Tag.@NotNull Named<Block> forge(String path) {
      return BlockTags.bind(new ResourceLocation("forge", path).toString());
    }
  }

  public enum Items {
    ;
    // ores
    public static final Tag.Named<Item> ORES_BLACK_DIAMONDS = forge("ores/black_diamonds");
    public static final Tag.Named<Item> ORES_PURPLE_DIAMONDS = forge("ores/purple_diamonds");

    // blocks
    public static final Tag.Named<Item> STORAGE_BLACK_DIAMOND =
        forge("storage_blocks/black_diamond");
    public static final Tag.Named<Item> STORAGE_PURPLE_DIAMOND =
        forge("storage_blocks/purple_diamond");

    // ingots
    public static final Tag.Named<Item> INGOTS_BLACK_DIAMOND = forge("ingots/black_diamond");
    public static final Tag.Named<Item> INGOTS_PURPLE_DIAMOND = forge("ingots/purple_diamond");

    private static Tag.@NotNull Named<Item> forge(String path) {
      return ItemTags.bind(new ResourceLocation("forge", path).toString());
    }
  }
}
