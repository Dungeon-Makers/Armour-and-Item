package io.github.dungeonmakers.armouranditem.data;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.data.lang.EnLang;
import io.github.dungeonmakers.armouranditem.data.loot.LootTableConfiguration;
import io.github.dungeonmakers.armouranditem.data.recipe.CraftingTableRecipeGenerator;
import io.github.dungeonmakers.armouranditem.data.tags.ModBlockTagsProvider;
import io.github.dungeonmakers.armouranditem.data.tags.ModItemTagsProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ModBlockStateProvider;
import io.github.dungeonmakers.armouranditem.data.texture.ModItemModelProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGeneratorConfiguration {
  private DataGeneratorConfiguration() {}

  // needed tp register the data gen
  @SubscribeEvent
  public static void gatherData(@NotNull GatherDataEvent event) {
    var gen = event.getGenerator();
    var existingFileHelper = event.getExistingFileHelper();
    gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
    gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
    var blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
    gen.addProvider(blockTags);
    gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));
    gen.addProvider(new CraftingTableRecipeGenerator(gen));
    gen.addProvider(new EnLang(gen));
    gen.addProvider(new LootTableConfiguration(gen));
  }
}
