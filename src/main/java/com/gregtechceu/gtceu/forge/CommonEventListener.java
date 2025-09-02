package com.gregtechceu.gtceu.forge;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import com.gregtechceu.gtceu.common.capability.WorldIDSaveData;
import com.gregtechceu.gtceu.common.data.loader.PostRegistryListener;

import com.gregtechceu.gtceu.data.command.GTCommands;

import com.gregtechceu.gtceu.integration.map.ClientCacheManager;
import com.gregtechceu.gtceu.integration.map.WaypointManager;
import com.gregtechceu.gtceu.integration.map.cache.server.ServerCache;
import com.gregtechceu.gtceu.utils.TaskHandler;

import net.minecraft.server.level.ServerLevel;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import net.neoforged.neoforge.event.tick.LevelTickEvent;


@EventBusSubscriber(modid = GTCEu.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEventListener {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        GTCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddReloadListenerEvent event) {
        GTRegistries.updateFrozenRegistry(event.getRegistryAccess());
        event.addListener(PostRegistryListener.INSTANCE);
    }

    @SubscribeEvent
    public static void levelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            TaskHandler.onTickUpdate(serverLevel);
        }
    }

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            WaypointManager.updateDimension(event.getLevel());
        } else if (event.getLevel() instanceof ServerLevel serverLevel) {
            ServerCache.instance.maybeInitWorld(serverLevel);
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            TaskHandler.onWorldUnLoad(serverLevel);
            ServerCache.instance.invalidateWorld(serverLevel);
        } else if (event.getLevel().isClientSide()) {
            ClientCacheManager.saveCaches(event.getLevel().registryAccess());
        }
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        ServerLevel mainLevel = event.getServer().overworld();
        WorldIDSaveData.init(mainLevel);
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        ServerCache.instance.clear();
    }

}
