/*
package com.yusuf.armouranditem.data.loot;

import com.yusuf.armouranditem.Main;
import com.yusuf.armouranditem.core.init.BlockInit;
import com.yusuf.armouranditem.core.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {
    @Override
    protected void addTables() {
        //ores
        registerLootTable(BlockInit.BLACK_DIAMOND_ORE.get(),
                droppingItemWithFortune(BlockInit.BLACK_DIAMOND_ORE.get(), ItemInit.BLACK_DIAMOND_SCRAP.get()));

        registerLootTable(BlockInit.INFINITUM_ORE.get(),
                droppingItemWithFortune(BlockInit.INFINITUM_ORE.get(), ItemInit.INFINITUM_SCRAP.get()));
        //blocks

        registerDropSelfLootTable(BlockInit.BLACK_DIAMOND_BLOCK.get());
        registerDropSelfLootTable(BlockInit.INFINITUM_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> Main.MOD_ID.equals(block.getRegistryName().getNamespace()))
                .collect(Collectors.toSet());
    }
}
*/