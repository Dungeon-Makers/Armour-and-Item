package com.yusuf.armouranditem.core.util;

import com.yusuf.armouranditem.ArmourAndItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ArmourAndItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    private ClientEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
    }
}
