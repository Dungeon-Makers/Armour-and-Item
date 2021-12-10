package io.github.dungeonmakers.armouranditem.data.texture;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.data.util.NameUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ModItemModelProvider
    extends net.minecraftforge.client.model.generators.ItemModelProvider {
  private static final String GENERATED_ITEM = "item/generated";

  public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, ArmourAndItem.MOD_ID, existingFileHelper);
  }

  @Nonnull
  @Override
  public String getName() {
    return "Armour And Items - Item Models";
  }

  @Override
  protected void registerModels() {
    // blocks
    BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(this::blockItemModel);

    ModelFile itemGenerated = getExistingFile(new ResourceLocation(GENERATED_ITEM));

    // items
    builder(ItemInit.BLACK_DIAMOND.get(), itemGenerated);
  }

  private void blockItemModel(Block block) {
    if (block == BlockInit.BLACK_DIAMOND_BLOCK.get())
      builder(block, getExistingFile(mcLoc(GENERATED_ITEM)), "block/black_diamond_block");
    else if (block == BlockInit.BLACK_DIAMOND_ORE.get())
      builder(block, getExistingFile(mcLoc(GENERATED_ITEM)), "block/black_diamond_ore");
    else if (block == BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE.get())
      builder(block, getExistingFile(mcLoc(GENERATED_ITEM)), "block/deepslate_black_diamond_ore");
    else if (block.asItem() != Items.AIR) {
      String name = NameUtils.from(block).getPath();
      withExistingParent(name, modLoc("block/" + name));
    }
  }

  private ItemModelBuilder builder(ItemLike item) {
    return getBuilder(NameUtils.fromItem(item).getPath());
  }

  private ItemModelBuilder builder(ItemLike item, ModelFile parent) {
    String name = NameUtils.fromItem(item).getPath();
    return builder(item, parent, "item/" + name);
  }

  private ItemModelBuilder builder(ItemLike item, ModelFile parent, String texture) {
    return getBuilder(NameUtils.fromItem(item).getPath()).parent(parent).texture("layer0", texture);
  }
}
