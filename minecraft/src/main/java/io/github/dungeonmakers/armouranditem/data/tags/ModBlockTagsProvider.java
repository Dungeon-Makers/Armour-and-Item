package io.github.dungeonmakers.armouranditem.data.tags;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.TagsInit;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
  public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, ArmourAndItem.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    // ores
    tag(TagsInit.Blocks.ORES_BLACK_DIAMONDS).add(BlockInit.BLACK_DIAMOND_ORE.get(),
        BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE.get());
    tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.ORES_BLACK_DIAMONDS);

    tag(TagsInit.Blocks.ORES_PURPLE_DIAMONDS).add(BlockInit.PURPLE_DIAMOND_ORE.get());
    tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.ORES_PURPLE_DIAMONDS);

    // blocks
    tag(TagsInit.Blocks.STORAGE_BLACK_DIAMOND).add(BlockInit.BLACK_DIAMOND_BLOCK.get());
    tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.STORAGE_BLACK_DIAMOND);

    tag(TagsInit.Blocks.STORAGE_PURPLE_DIAMOND).add(BlockInit.PURPLE_DIAMOND_BLOCK.get());
    tag(Tags.Blocks.ORES).addTag(TagsInit.Blocks.STORAGE_PURPLE_DIAMOND);

  }
}
