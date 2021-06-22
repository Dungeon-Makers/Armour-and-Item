/*
package com.yusuf.armouranditem.data.client;

import com.yusuf.armouranditem.Main;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class ModItemModelProvider extends ItemModelProvider {


    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ArmourAndItem.MOD_ID, existingFileHelper);
    }



    @Override
    protected void registerModels() {

        withExistingParent("black_diamond_ore", modLoc("block/black_diamond_ore"));
        withExistingParent("black_diamond_block", modLoc("block/black_diamond_block"));
        withExistingParent("infinitum_ore", modLoc("block/infinitum_ore"));
        withExistingParent("infinitum_block", modLoc("block/infinitum_block"));

        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        ModelFile itemhandHeld = getExistingFile(mcLoc("item/handheld"));

        //items
        builder(itemGenerated, "black_diamond_scrap");




        //tools



    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
    private ItemModelBuilder tool(ModelFile itemhandHeld, String name) {
        return getBuilder(name).parent(itemhandHeld).texture("layer0",  "item/" + name);
    }
}

 */
