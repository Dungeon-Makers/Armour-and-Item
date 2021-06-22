package com.yusuf.armouranditem.data.loot;

import com.yusuf.armouranditem.ArmourAndItem;
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
        add(BlockInit.BLACK_DIAMOND_ORE.get(),
                createOreDrop(BlockInit.BLACK_DIAMOND_ORE.get(), ItemInit.BLACK_DIAMOND_SCRAP.get()));


/*


 */
        //blocks

        dropSelf(BlockInit.BLACK_DIAMOND_BLOCK.get());
       // registerDropSelfLootTable(BlockInit.INFINITUM_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> ArmourAndItem.MOD_ID.equals(block.getRegistryName().getNamespace()))
                .collect(Collectors.toSet());
    }
}
