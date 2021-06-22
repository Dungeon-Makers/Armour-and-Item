
package com.yusuf.armouranditem.data.client;

import com.yusuf.armouranditem.ArmourAndItem;
import com.yusuf.armouranditem.core.init.BlockInit;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider{
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ArmourAndItem.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
       simpleBlock(BlockInit.BLACK_DIAMOND_ORE.get());
       simpleBlock(BlockInit.BLACK_DIAMOND_BLOCK.get());
        //axisBlock(BlockInit.INFINITUM_ORE.get(),modLoc("block/infinitum_ore_side"), modLoc("block/infinitum_ore_top"));
       // simpleBlock(BlockInit.INFINITUM_BLOCK.get());




    }
}


