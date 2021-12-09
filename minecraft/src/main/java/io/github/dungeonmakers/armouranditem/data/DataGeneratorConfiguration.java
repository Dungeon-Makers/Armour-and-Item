package io.github.dungeonmakers.armouranditem.data;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.data.lang.EnLang;
import io.github.dungeonmakers.armouranditem.data.loot.LootTableConfiguration;
import io.github.dungeonmakers.armouranditem.data.texture.ModBlockStateProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ModItemModelProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGeneratorConfiguration {
  private DataGeneratorConfiguration() {}

  @SubscribeEvent
  public static void gatherData(@NotNull GatherDataEvent event) {
    var gen = event.getGenerator();
    var existingFileHelper = event.getExistingFileHelper();
    gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
    gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
    gen.addProvider(new EnLang(gen));
    gen.addProvider(new LootTableConfiguration(gen));
  }
}
