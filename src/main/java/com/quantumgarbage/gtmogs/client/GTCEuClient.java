package com.quantumgarbage.gtmogs.client;

import com.quantumgarbage.gtmogs.GTMOGS;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = GTMOGS.MOD_ID, dist = Dist.CLIENT)
public class GTCEuClient {

    public GTCEuClient(IEventBus modBus, FMLModContainer container) {
        ClientInit.init(modBus);
    }
}
