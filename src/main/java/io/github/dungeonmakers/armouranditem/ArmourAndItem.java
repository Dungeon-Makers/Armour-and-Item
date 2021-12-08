package io.github.dungeonmakers.armouranditem;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.common.Mod.*;

@Mod("armouranditem")
@EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ArmourAndItem {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "armouranditem";

    private ArmourAndItem() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
