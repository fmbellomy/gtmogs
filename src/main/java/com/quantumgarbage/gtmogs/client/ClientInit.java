package com.quantumgarbage.gtmogs.client;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.GTValues;

import com.quantumgarbage.gtmogs.config.ConfigHolder;

import com.quantumgarbage.gtmogs.integration.map.ClientCacheManager;
import com.quantumgarbage.gtmogs.integration.map.cache.client.GTClientCache;
import com.quantumgarbage.gtmogs.integration.map.ftbchunks.FTBChunksPlugin;
import com.quantumgarbage.gtmogs.integration.map.layer.Layers;
import com.quantumgarbage.gtmogs.integration.map.layer.builtin.OreRenderLayer;
import com.quantumgarbage.gtmogs.utils.input.KeyBind;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.register(ClientInit.class);
        if (!GTMOGS.isDataGen()) {
            ClientCacheManager.registerClientCache(GTClientCache.instance, "gtmogs");
            Layers.registerLayer(OreRenderLayer::new, "ore_veins");
        }
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        KeyBind.onRegisterKeyBinds(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ConfigHolder.INSTANCE.compat.minimap.toggle.ftbChunksIntegration &&
                GTMOGS.isModLoaded(GTValues.MODID_FTB_CHUNKS)) {
            FTBChunksPlugin.addEventListeners();
        }
    }
}
