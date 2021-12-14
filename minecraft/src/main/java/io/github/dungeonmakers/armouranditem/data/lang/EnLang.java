package io.github.dungeonmakers.armouranditem.data.lang;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class EnLang extends LanguageProvider {
  public EnLang(DataGenerator gen) {
    super(gen, ArmourAndItem.MOD_ID, "en_us");
  }

  @Override
  protected void addTranslations() {
    block(BlockInit.BLACK_DIAMOND_BLOCK, "Black Diamond Block");
    block(BlockInit.BLACK_DIAMOND_ORE, "Black Diamond Ore");
    block(BlockInit.PURPLE_DIAMOND_ORE, "Purple Diamond Ore");
    block(BlockInit.PURPLE_DIAMOND_BLOCK, "Purple Diamond Block");
    block(BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE, "DEEPSLATE Black Diamond Ore");

    // ingots
    item(ItemInit.BLACK_DIAMOND, "Black Diamond");
    item(ItemInit.PURPLE_DIAMOND, "Purple Diamond");
    item(ItemInit.BLACK_DIAMOND_SWORD, "Black Diamond Sword");

  }

  protected <T extends Item> void item(@NotNull RegistryObject<T> entry, String name) {
    add(entry.get(), name);
  }

  protected <T extends Block> void block(@NotNull RegistryObject<T> entry, String name) {
    add(entry.get(), name);
  }

  protected void add(@NotNull Component translatableComponent, String lang) {
    super.add(translatableComponent.getString(), lang);
  }
}
