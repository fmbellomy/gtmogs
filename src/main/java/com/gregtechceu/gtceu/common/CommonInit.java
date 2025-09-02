package com.gregtechceu.gtceu.common;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.api.addon.IGTAddon;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.WorldGenLayers;

import com.gregtechceu.gtceu.api.worldgen.generator.VeinGenerators;

import com.gregtechceu.gtceu.common.pack.GTDynamicDataPack;
import com.gregtechceu.gtceu.common.pack.GTDynamicResourcePack;
import com.gregtechceu.gtceu.common.pack.GTPackSource;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.core.mixins.registrate.AbstractRegistrateAccessor;

import com.gregtechceu.gtceu.data.command.GTCommandArguments;

import com.gregtechceu.gtceu.data.misc.GTDimensionMarkers;

import com.gregtechceu.gtceu.integration.kjs.GTCEuStartupEvents;
import com.gregtechceu.gtceu.integration.kjs.GTKubeJSPlugin;
import com.gregtechceu.gtceu.integration.map.WaypointManager;

import com.gregtechceu.gtceu.utils.input.KeyBind;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.fluids.crafting.*;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.callback.BakeCallback;

import com.google.common.collect.Multimaps;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcjty.theoneprobe.api.ITheOneProbe;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

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

            event.addRepositorySource(new GTPackSource("gtceu:dynamic_assets",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    GTDynamicResourcePack::new));
        } else if (event.getPackType() == PackType.SERVER_DATA) {
            // Clear old data
            GTDynamicDataPack.clearServer();

            // LOADING MOVED TO ReloadableServerResourcesMixin

            event.addRepositorySource(new GTPackSource("gtceu:dynamic_data",
                    event.getPackType(),
                    Pack.Position.BOTTOM,
                    GTDynamicDataPack::new));
        }
    }
}
