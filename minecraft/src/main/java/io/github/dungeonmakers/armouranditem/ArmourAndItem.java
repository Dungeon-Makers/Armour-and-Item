package io.github.dungeonmakers.armouranditem;

import io.github.dungeonmakers.armouranditem.core.BlockInit;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.OreGen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(ArmourAndItem.MOD_ID)
public class ArmourAndItem {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "armouranditem";
  public static final String MOD_NAME = "Armour and Item";

  @SuppressWarnings("java:S1118")
  public ArmourAndItem() {
    final var bus = FMLJavaModLoadingContext.get().getModEventBus();
    BlockInit.BLOCKS.register(bus);
    ItemInit.ITEMS.register(bus);
    bus.addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);
    LOGGER.info("Armour and Item loaded");
  }

  private void setup(final @NotNull FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      OreGen.registerConfigured();
      OreGen.registerPlaced();
    });
  }
}
