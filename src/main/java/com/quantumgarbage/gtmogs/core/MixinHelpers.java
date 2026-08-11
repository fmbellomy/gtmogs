package com.quantumgarbage.gtmogs.core;

import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.GTValues;
import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.integration.kjs.GTMOGSServerEvents;
import com.quantumgarbage.gtmogs.integration.kjs.events.GTOreVeinKubeEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MixinHelpers {

    private static <T> Collector<T, ?, ArrayList<T>> toArrayList() {
        return Collectors.toCollection(ArrayList::new);
    }

    public static TagLoader.EntryWithSource makeItemEntry(ItemLike item) {
        return makeElementEntry(item.asItem().builtInRegistryHolder().key().location());
    }

    public static TagLoader.EntryWithSource makeBlockEntry(Supplier<? extends Block> block) {
        return makeBlockEntry(block.get());
    }

    public static TagLoader.EntryWithSource makeBlockEntry(Block block) {
        return makeElementEntry(block.builtInRegistryHolder().key().location());
    }

    public static TagLoader.EntryWithSource makeFluidEntry(Fluid fluid) {
        return makeElementEntry(fluid.builtInRegistryHolder().key().location());
    }

    public static TagLoader.EntryWithSource makeElementEntry(ResourceLocation id) {
        return new TagLoader.EntryWithSource(TagEntry.element(id), GTValues.CUSTOM_TAG_SOURCE);
    }

    public static TagLoader.EntryWithSource makeTagEntry(TagKey<?> tag) {
        return new TagLoader.EntryWithSource(TagEntry.tag(tag.location()), GTValues.CUSTOM_TAG_SOURCE);
    }

    public static void postKJSVeinEvents(RegistryAccess.Frozen registries) {
        if (!GTMOGS.Mods.isKubeJSLoaded()) {
            return;
        }

        KJSCallWrapper.updateRegistryAccessContainer(registries);

        KJSCallWrapper.postEventWithRegistry(KJSCallWrapper::postOreVeinEvent,
                registries.registryOrThrow(GTRegistries.ORE_VEIN_REGISTRY));
    }

    private static final class KJSCallWrapper {

        private static <T> void postEventWithRegistry(Consumer<WritableRegistry<T>> eventProvider,
                                                      Registry<T> registry) {
            if (registry instanceof MappedRegistry<T> writable) {
                // unfreeze the registry, register to it, refreeze it.
                writable.unfreeze();
                try {
                    eventProvider.accept(writable);
                } finally {
                    writable.freeze();
                }
            }
        }

        private static void postOreVeinEvent(WritableRegistry<OreVeinDefinition> registry) {
            GTMOGSServerEvents.ORE_VEIN_MODIFICATION.post(new GTOreVeinKubeEvent(registry));
        }

        private static void updateRegistryAccessContainer(RegistryAccess.Frozen registriesWithEverything) {
            if (RegistryAccessContainer.current.access().registries().count() <
                    registriesWithEverything.registries().count()) {
                RegistryAccessContainer.current = new RegistryAccessContainer(registriesWithEverything);
            }
        }
    }

    public static final class ClientCallWrapper {

        public static Level getClientLevel() {
            return Minecraft.getInstance().level;
        }
    }
}
