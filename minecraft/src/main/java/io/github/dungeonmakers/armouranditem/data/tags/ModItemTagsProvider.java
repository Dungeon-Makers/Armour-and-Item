package io.github.dungeonmakers.armouranditem.data.tags;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.TagsInit;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
  public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
      ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, ArmourAndItem.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    // ores
    copy(TagsInit.Blocks.ORES_BLACK_DIAMONDS, TagsInit.Items.ORES_BLACK_DIAMONDS);
    copy(TagsInit.Blocks.ORES_PURPLE_DIAMONDS, TagsInit.Items.ORES_PURPLE_DIAMONDS);

    // blocks
    copy(TagsInit.Blocks.STORAGE_BLACK_DIAMOND, TagsInit.Items.STORAGE_BLACK_DIAMOND);
    copy(TagsInit.Blocks.STORAGE_PURPLE_DIAMOND, TagsInit.Items.STORAGE_PURPLE_DIAMOND);

    // ingots
    tag(TagsInit.Items.INGOTS_BLACK_DIAMOND).add(ItemInit.BLACK_DIAMOND.get());
    tag(TagsInit.Items.INGOTS_PURPLE_DIAMOND).add(ItemInit.PURPLE_DIAMOND.get());
  }
}
