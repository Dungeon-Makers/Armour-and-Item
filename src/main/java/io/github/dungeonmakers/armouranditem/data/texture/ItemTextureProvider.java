package io.github.dungeonmakers.armouranditem.data.texture;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTextureProvider extends ItemModelProvider {

  public ItemTextureProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, ArmourAndItem.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    block("black_diamond_ore");
    block("black_diamond_block");
  }

  private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
    return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
  }

  private ItemModelBuilder tool(ModelFile itemhandHeld, String name) {
    return getBuilder(name).parent(itemhandHeld).texture("layer0", "item/" + name);
  }

  private void block(String name) {
    withExistingParent(name, modLoc("block/" + name));
  }
}
