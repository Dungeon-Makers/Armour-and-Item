package com.yusuf.armouranditem.data;


import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.data.client.ModBlockStateProvider;
import com.yusuf.armouranditem.data.client.ModItemModelProvider;
import com.yusuf.armouranditem.data.lang.ModLangProvider;
import com.yusuf.armouranditem.data.loot.ModLootTables;
import com.yusuf.armouranditem.data.recipe.ModRecipeProvider;
import com.yusuf.armouranditem.data.tags.ModBlockTagsProvider;
import com.yusuf.armouranditem.data.tags.ModItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        //models
        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));

        //tags
        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(blockTags);
        gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));

        //other datagenerator
        gen.addProvider(new ModLangProvider(gen));
        gen.addProvider(new ModLootTables(gen));
        gen.addProvider(new ModRecipeProvider(gen));
    }
}
