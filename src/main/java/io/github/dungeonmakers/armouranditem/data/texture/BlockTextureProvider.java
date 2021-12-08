package io.github.dungeonmakers.armouranditem.data.texture;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockTextureProvider extends BlockStateProvider {
  public BlockTextureProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
    super(gen, ArmourAndItem.MOD_ID, exFileHelper);
  }

  @Nonnull
  @Override
  public String getName() {
    return ArmourAndItem.MOD_NAME + " Block Textures";
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlock(BlockInit.BLACK_DIAMOND_ORE.get());
    simpleBlock(BlockInit.BLACK_DIAMOND_BLOCK.get());
  }
}
