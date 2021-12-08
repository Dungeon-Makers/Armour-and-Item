package io.github.dungeonmakers.armouranditem.core.itemgroup;

import io.github.dungeonmakers.armouranditem.core.BlockInit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MainItemGroup extends CreativeModeTab {
    public static final MainItemGroup MAIN = new MainItemGroup(CreativeModeTab.TABS.length, "main");

    public MainItemGroup(int index, String label) {
        super(index, label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(BlockInit.BLACK_DIAMOND.get());
    }
}
