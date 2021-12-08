package io.github.dungeonmakers.armouranditem.data.loot;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.BlockInit;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

public class BlockLootTable extends BlockLoot {
  @Override
  protected void addTables() {

    dropSelf(BlockInit.BLACK_DIAMOND_BLOCK.get());
  }

  @Override
  protected @NotNull Iterable<Block> getKnownBlocks() {
    return ForgeRegistries.BLOCKS.getValues().stream()
        .filter(block -> ArmourAndItem.MOD_ID
            .equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
        .collect(Collectors.toSet());
  }
}
