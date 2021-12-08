package io.github.dungeonmakers.armouranditem.core.util;

import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.itemgroup.MainItemGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GeneralBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public GeneralBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> blockBlockStateBuilder) {
        blockBlockStateBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    public static <T extends Block> RegistryObject<T> registerSpecial(String name, Supplier<T> supplier) {
        RegistryObject<T> blockReg = BlockInit.BLOCKS.register(name, supplier);
        ItemInit.ITEMS.register(name, () -> new BlockItem(blockReg.get(), new Item.Properties().tab(MainItemGroup.MAIN)));
        return blockReg;
    }

    public static RegistryObject<GeneralBlock> register(String name, Supplier<GeneralBlock> supplier) {
        RegistryObject<GeneralBlock> blockReg = BlockInit.BLOCKS.register(name, supplier);
        ItemInit.ITEMS.register(name, () -> new BlockItem(blockReg.get(), new Item.Properties().tab(MainItemGroup.MAIN)));
        return blockReg;
    }

    public static RegistryObject<GeneralBlock> register(String name, Block existingBlock) {
        return register(name, () -> new GeneralBlock(BlockBehaviour.Properties.copy(existingBlock)));
    }
}
