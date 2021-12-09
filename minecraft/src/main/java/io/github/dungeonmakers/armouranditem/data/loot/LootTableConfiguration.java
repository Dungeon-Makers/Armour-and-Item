package io.github.dungeonmakers.armouranditem.data.loot;

import com.mojang.datafixers.util.Pair;
import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTableConfiguration extends LootTableProvider {
  public LootTableConfiguration(DataGenerator dataGeneratorIn) {
    super(dataGeneratorIn);
  }

  @Override
  public @NotNull String getName() {
    return ArmourAndItem.MOD_NAME + " Loot Tables";
  }

  @Override
  protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
    return List.of(Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK));
  }

  @Override
  protected void validate(@NotNull Map<ResourceLocation, LootTable> map,
      @NotNull ValidationContext validationtracker) {
    map.forEach((id, table) -> LootTables.validate(validationtracker, id, table));
  }
}
