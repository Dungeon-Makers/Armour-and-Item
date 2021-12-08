package io.github.dungeonmakers.armouranditem.data;

import io.github.dungeonmakers.armouranditem.data.loot.LootTableConfiguration;
import io.github.dungeonmakers.armouranditem.data.texture.BlockTextureProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ItemTextureProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public final class DataGeneratorConfiguration {
  private DataGeneratorConfiguration() {}

  public static void gatherData(@NotNull GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    if (event.includeClient()) {
      gen.addProvider(new BlockTextureProvider(gen, existingFileHelper));
      gen.addProvider(new ItemTextureProvider(gen, existingFileHelper));
    } else if (event.includeServer()) {
      gen.addProvider(new LootTableConfiguration(gen));
    } else {
      throw new IllegalStateException("Unhandled data gathering phase");
    }
  }
}
