package com.yusuf.armouranditem;

import com.yusuf.armouranditem.core.init.BlockInit;
import com.yusuf.armouranditem.core.init.FeatureInit;
import com.yusuf.armouranditem.core.init.ItemInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("armouranditem")
@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArmourAndItem {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "armouranditem";

    public ArmourAndItem() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(bus);
        BlockInit.BLOCKS.register(bus);


       MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, FeatureInit::addOres);
        MinecraftForge.EVENT_BUS.register(this);

    }
}
