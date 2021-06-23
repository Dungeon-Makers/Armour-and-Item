
package com.yusuf.armouranditem.data.client;

import com.yusuf.armouranditem.ArmourAndItem;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {


    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ArmourAndItem.MOD_ID, existingFileHelper);
    }



    @Override
    protected void registerModels() {

        withExistingParent("black_diamond_ore", modLoc("block/black_diamond_ore"));
        withExistingParent("black_diamond_block", modLoc("block/black_diamond_block"));
        withExistingParent("purple_diamond_ore", modLoc("block/purple_diamond_ore"));
        withExistingParent("purple_diamond_block", modLoc("block/purple_diamond_block"));


        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        ModelFile itemhandHeld = getExistingFile(mcLoc("item/handheld"));

        //items
        builder(itemGenerated, "black_diamond_scrap");
        builder(itemGenerated, "black_diamond");
        builder(itemGenerated, "black_diamond_helmet");
        builder(itemGenerated, "black_diamond_chestplate");
        builder(itemGenerated, "black_diamond_leggings");
        builder(itemGenerated, "black_diamond_boots");
        builder(itemGenerated, "purple_diamond");
        builder(itemGenerated, "purple_diamond_helmet");
        builder(itemGenerated, "purple_diamond_chestplate");
        builder(itemGenerated, "purple_diamond_leggings");
        builder(itemGenerated, "purple_diamond_boots");
        builder(itemGenerated, "purple_diamond_sword");

        //tools
        tool(itemhandHeld, "black_diamond_sword");



    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
    private ItemModelBuilder tool(ModelFile itemhandHeld, String name) {
        return getBuilder(name).parent(itemhandHeld).texture("layer0",  "item/" + name);
    }
}


