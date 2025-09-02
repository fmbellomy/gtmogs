package com.quantumgarbage.gtmogs.common;

import com.quantumgarbage.gtmogs.api.addon.AddonFinder;
import com.quantumgarbage.gtmogs.api.addon.IGTAddon;

import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.api.worldgen.WorldGenLayers;

import com.quantumgarbage.gtmogs.api.worldgen.generator.VeinGenerators;

import com.quantumgarbage.gtmogs.common.pack.GTDynamicDataPack;
import com.quantumgarbage.gtmogs.common.pack.GTDynamicResourcePack;
import com.quantumgarbage.gtmogs.common.pack.GTPackSource;

import com.quantumgarbage.gtmogs.data.command.GTCommandArguments;

import com.quantumgarbage.gtmogs.data.misc.GTDimensionMarkers;

import com.quantumgarbage.gtmogs.integration.map.WaypointManager;

import com.quantumgarbage.gtmogs.utils.input.KeyBind;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CommonInit {

    private static IEventBus modBus;

    public static void init(final IEventBus modBus) {
        CommonInit.modBus = modBus;
        modBus.register(CommonInit.class);


        GTRegistries.init(modBus);

        AddonFinder.getAddonList().forEach(IGTAddon::gtInitComplete);
    }

    // Only register everything once.
    private static boolean didRunRegistration = false;

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (didRunRegistration) {
            return;
        }
        didRunRegistration = true;



        GTDimensionMarkers.init();


        GTCommandArguments.COMMAND_ARGUMENT_TYPES.register(modBus);
        WorldGenLayers.registerAll();
        VeinGenerators.registerAddonGenerators();
        WaypointManager.init();

        KeyBind.init();

    }



    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        GTRegistries.getRegistries().forEach(event::register);
    }

    @SubscribeEvent
    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(GTRegistries.ORE_VEIN_REGISTRY,
                OreVeinDefinition.DIRECT_CODEC, OreVeinDefinition.DIRECT_CODEC);
    }

    @SubscribeEvent
    public static void loadComplete(FMLLoadCompleteEvent event) {}

    @SubscribeEvent
    public static void registerPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            // Clear old data
            GTDynamicResourcePack.clearClient();

            event.addRepositorySource(new GTPackSource("gtmogs:dynamic_assets",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    GTDynamicResourcePack::new));
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            // Clear old data
            GTDynamicDataPack.clearServer();

            // LOADING MOVED TO ReloadableServerResourcesMixin

            event.addRepositorySource(new GTPackSource("gtmogs:dynamic_data",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    GTDynamicDataPack::new));
        }
    }
}
