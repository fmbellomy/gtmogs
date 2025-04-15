package com.gregtechceu.gtceu.client;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.client.particle.HazardParticle;
import com.gregtechceu.gtceu.client.particle.MufflerParticle;
import com.gregtechceu.gtceu.client.renderer.entity.GTExplosiveRenderer;
import com.gregtechceu.gtceu.client.renderer.item.GTItemBarRenderer;
import com.gregtechceu.gtceu.common.CommonInit;
import com.gregtechceu.gtceu.common.data.GTBlockEntities;
import com.gregtechceu.gtceu.common.data.GTEntityTypes;
import com.gregtechceu.gtceu.data.particle.GTParticleTypes;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.map.ClientCacheManager;
import com.gregtechceu.gtceu.integration.map.cache.client.GTClientCache;
import com.gregtechceu.gtceu.integration.map.ftbchunks.FTBChunksPlugin;
import com.gregtechceu.gtceu.integration.map.layer.Layers;
import com.gregtechceu.gtceu.integration.map.layer.builtin.FluidRenderLayer;
import com.gregtechceu.gtceu.integration.map.layer.builtin.OreRenderLayer;
import com.gregtechceu.gtceu.utils.input.KeyBind;

import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author KilaBash
 * @date 2023/7/30
 * @implNote ClientInit
 */
public class ClientInit extends CommonInit {

    public static final BiMap<ResourceLocation, GTOreDefinition> CLIENT_ORE_VEINS = HashBiMap.create();
    public static final BiMap<ResourceLocation, BedrockFluidDefinition> CLIENT_FLUID_VEINS = HashBiMap.create();
    public static final BiMap<ResourceLocation, BedrockOreDefinition> CLIENT_BEDROCK_ORE_VEINS = HashBiMap.create();

    public ClientInit() {
        super();
    }

    public static void init() {
        if (!GTCEu.isDataGen()) {
            ClientCacheManager.registerClientCache(GTClientCache.instance, "gtceu");
            Layers.registerLayer(OreRenderLayer::new, "ore_veins");
            Layers.registerLayer(FluidRenderLayer::new, "bedrock_fluids");
        }
    }

    @SubscribeEvent
    public void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GTEntityTypes.DYNAMITE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(GTEntityTypes.POWDERBARREL.get(), GTExplosiveRenderer::new);
        event.registerEntityRenderer(GTEntityTypes.INDUSTRIAL_TNT.get(), GTExplosiveRenderer::new);

        event.registerBlockEntityRenderer(GTBlockEntities.GT_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(GTBlockEntities.GT_HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public void registerKeyBindings(RegisterKeyMappingsEvent event) {
        KeyBind.onRegisterKeyBinds(event);
    }


    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof IGTTool || item instanceof IComponentItem) {
                event.register(item, GTItemBarRenderer.INSTANCE);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(GTCEu.id("hud"), new HudGuiOverlay());
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(GTParticleTypes.HAZARD_PARTICLE.get(), HazardParticle.Provider::new);
        event.registerSpriteSet(GTParticleTypes.MUFFLER_PARTICLE.get(), MufflerParticle.Provider::new);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        MachineOwner.init();
        if (ConfigHolder.INSTANCE.compat.minimap.toggle.ftbChunksIntegration &&
                GTCEu.isModLoaded(GTValues.MODID_FTB_CHUNKS)) {
            FTBChunksPlugin.addEventListeners();
        }
    }
}
