package com.yusuf.armouranditem.data;


import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.data.client.ModBlockStateProvider;
import com.yusuf.armouranditem.data.client.ModItemModelProvider;
import com.yusuf.armouranditem.data.loot.ModLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    private DataGenerators() {}
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
         gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
         gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));

         //other datagenerator
         gen.addProvider(new ModLangProvider(gen));
         gen.addProvider(new ModLootTables(gen));
    }
}
