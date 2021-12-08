package io.github.dungeonmakers.armouranditem;

import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArmourAndItem.MOD_ID)
public class ArmourAndItem {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "armouranditem";
  public static final String MOD_NAME = "Armour and Item";

  private ArmourAndItem() {
    final var bus = FMLJavaModLoadingContext.get().getModEventBus();
    BlockInit.BLOCKS.register(bus);
    ItemInit.ITEMS.register(bus);
  }
}