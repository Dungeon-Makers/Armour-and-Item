package com.yusuf.armouranditem.core.init;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.itemgroup.MainItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.yusuf.armouranditem.core.init.ItemInit.ITEMS;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArmourAndItem.MOD_ID);
 //blocks

    public static final RegistryObject<GeneralBlock> BLACK_DIAMOND_BLOCK = register("black_diamond_block", Blocks.ANCIENT_DEBRIS);
//ores
public static final RegistryObject<GeneralBlock> BLACK_DIAMOND_ORE = register("black_diamond_ore", Blocks.ANCIENT_DEBRIS);
    public static final RegistryObject<GeneralBlock> PURPLE_DIAMOND_ORE = register("purple_diamond_ore", Blocks.ANCIENT_DEBRIS);


    private static <T extends Block> RegistryObject<T> registerSpecial(String name, Supplier<T> supplier) {
        RegistryObject<T> blockReg = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(blockReg.get(), new Item.Properties().tab(MainItemGroup.MAIN)));
        return blockReg;
    }

    private static RegistryObject<GeneralBlock> register(String name, Supplier<GeneralBlock> supplier) {
        RegistryObject<GeneralBlock> blockReg = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(blockReg.get(), new Item.Properties().tab(MainItemGroup.MAIN)));
        return blockReg;
    }
    private static RegistryObject<GeneralBlock> register(String name, Block existingBlock) {
        return register(name, () -> new GeneralBlock(AbstractBlock.Properties.copy(existingBlock)));
    }


}