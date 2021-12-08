package io.github.dungeonmakers.armouranditem.core;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.util.GeneralBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static io.github.dungeonmakers.armouranditem.core.util.GeneralBlock.register;

public enum BlockInit {
  ;
  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(ForgeRegistries.BLOCKS, ArmourAndItem.MOD_ID);

  public static final RegistryObject<GeneralBlock> BLACK_DIAMOND =
      register("copper_ore", Blocks.IRON_ORE);
}
