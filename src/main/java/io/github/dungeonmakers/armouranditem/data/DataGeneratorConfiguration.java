package io.github.dungeonmakers.armouranditem.data;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.data.loot.LootTableConfiguration;
import io.github.dungeonmakers.armouranditem.data.texture.BlockTextureProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ItemTextureProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import static net.minecraftforge.fml.common.Mod.*;

@EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
enum DataGeneratorConfiguration {
  ;
  @SubscribeEvent
  public static void gatherData(@NotNull GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    // textures
    gen.addProvider(new BlockTextureProvider(gen, existingFileHelper));
    gen.addProvider(new ItemTextureProvider(gen, existingFileHelper));

    // loot tables
    gen.addProvider(new LootTableConfiguration(gen));
  }
}
