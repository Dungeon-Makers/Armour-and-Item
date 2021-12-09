package io.github.dungeonmakers.armouranditem;

import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.util.ModResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Mod(ArmourAndItem.MOD_ID)
@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArmourAndItem {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "armouranditem";
  public static final String MOD_NAME = "Armour and Item";

  private ArmourAndItem() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BlockInit.BLOCKS.register(bus);
    ItemInit.ITEMS.register(bus);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Contract("_ -> new")
  public static @NotNull ModResourceLocation getId(@NotNull String path) {
    if (path.contains(":")) {
      throw new IllegalArgumentException("path contains namespace");
    }
    return new ModResourceLocation(path);
  }
}
