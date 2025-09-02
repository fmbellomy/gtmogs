package com.quantumgarbage.gtmogs.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

import com.quantumgarbage.gtmogs.GTMOGS;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod(value = GTMOGS.MOD_ID, dist = Dist.CLIENT)
public class GTMOGSClient {
    public GTMOGSClient(IEventBus modBus, FMLModContainer container) {
        ClientInit.init(modBus);
    }
}
