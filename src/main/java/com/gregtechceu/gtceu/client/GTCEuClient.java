package com.gregtechceu.gtceu.client;

import com.gregtechceu.gtceu.GTCEu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = GTCEu.MOD_ID, dist = Dist.CLIENT)
public class GTCEuClient {

    public GTCEuClient(IEventBus modBus) {
        ClientInit.init();
    }
}
