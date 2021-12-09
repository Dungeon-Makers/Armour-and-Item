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

  public static final RegistryObject<GeneralBlock> BLACK_DIAMOND_ORE =
      register("black_diamond_ore", Blocks.DIAMOND_ORE);

  public static final RegistryObject<GeneralBlock> DEEPSLATE_BLACK_DIAMOND_ORE =
      register("deepslate_black_diamond_ore", Blocks.DIAMOND_ORE);

  public static final RegistryObject<GeneralBlock> BLACK_DIAMOND_BLOCK =
      register("black_diamond_block", Blocks.DIAMOND_BLOCK);
}
