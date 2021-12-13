package io.github.dungeonmakers.armouranditem.data.texture;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class ModBlockStateProvider
    extends net.minecraftforge.client.model.generators.BlockStateProvider {

  public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
    super(gen, ArmourAndItem.MOD_ID, exFileHelper);
  }

  @Nonnull
  @Override
  public String getName() {
    return "Armour And Items - Block States/Models";
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlock(BlockInit.BLACK_DIAMOND_ORE.get());
    simpleBlock(BlockInit.BLACK_DIAMOND_BLOCK.get());
    simpleBlock(BlockInit.DEEPSLATE_BLACK_DIAMOND_ORE.get());
    simpleBlock(BlockInit.PURPLE_DIAMOND_ORE.get());
    simpleBlock(BlockInit.PURPLE_DIAMOND_BLOCK.get());
  }
}
