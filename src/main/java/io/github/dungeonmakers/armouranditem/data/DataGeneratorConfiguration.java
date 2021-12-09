package io.github.dungeonmakers.armouranditem.data;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.data.loot.LootTableConfiguration;
import io.github.dungeonmakers.armouranditem.data.texture.ModBlockStateProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ModItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGeneratorConfiguration {
  private DataGeneratorConfiguration() {}

  @SubscribeEvent
  public static void gatherData(@NotNull GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    if (event.includeClient()) {
      gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
      gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
    } else if (event.includeServer()) {
      gen.addProvider(new LootTableConfiguration(gen));
    } else {
      ArmourAndItem.LOGGER.error("Unhandled data gathering phase");
      throw new IllegalStateException("Unhandled data gathering phase");
    }
  }
}
