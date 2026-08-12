package com.quantumgarbage.gtmogs.api.registry;

import com.quantumgarbage.gtmogs.api.worldgen.IWorldGenLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.IdMappingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.worldgen.DimensionMarker;
import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class GTRegistries {

    private static final SequencedSet<ResourceLocation> LOAD_ORDER = new LinkedHashSet<>();
    private static final LinkedHashMap<ResourceKey<Registry<?>>, Registry<?>> REGISTRIES = new LinkedHashMap<>();

    private GTRegistries() {}

    // spotless:off
    public static final class Keys {
        private Keys() {}



        // Datapack registries

        public static final ResourceKey<Registry<OreVeinDefinition>> ORE_VEIN = makeRegistryKey(GTMOGS.id("ore_vein"));

        // Other registries

        public static final ResourceKey<Registry<DimensionMarker>> DIMENSION_MARKER = makeRegistryKey(GTMOGS.id("dimension_marker"));
        public static final ResourceKey<Registry<IWorldGenLayer>> WORLD_GEN_LAYER = makeRegistryKey(GTMOGS.id("world_gen_layer"));

        private static <T> ResourceKey<Registry<T>> makeRegistryKey(ResourceLocation registryId) {
            return ResourceKey.createRegistryKey(registryId);
        }
    }

    // GT Registries

    // Be careful when changing the order of these static fields, as changing the order of them also changes the order of registry load.



    public static final Registry<DimensionMarker> DIMENSION_MARKERS = makeRegistry(Keys.DIMENSION_MARKER, false);

    public static final Registry<IWorldGenLayer> WORLD_GEN_LAYERS = makeRegistry(Keys.WORLD_GEN_LAYER);

    // spotless:on

    public static <T> MappedRegistry<T> makeRegistry(ResourceKey<Registry<T>> key) {
        return makeRegistry(key, true);
    }

    public static <T> MappedRegistry<T> makeRegistry(ResourceKey<Registry<T>> key, boolean sync) {
        MappedRegistry<T> registry = (MappedRegistry<T>) new RegistryBuilder<>(key)
                .sync(sync)
                .create();
        addRegistryToLoadOrder(key, registry);
        return registry;
    }

    private static final Table<Registry<?>, ResourceLocation, Object> TO_REGISTER = HashBasedTable.create();
    private static boolean isFrozen = true;

    public static <V, T extends V> T register(Registry<V> registry, ResourceLocation name, T value) {
        if (!isFrozen) {
            Registry.register(registry, name, value);
        } else {
            TO_REGISTER.put(registry, name, value);
        }
        return value;
    }

    // ignore the generics and hope the registered objects are still correctly typed :3
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void actuallyRegister(RegisterEvent event) {
        for (Registry reg : TO_REGISTER.rowKeySet()) {
            event.register(reg.key(), helper -> {
                TO_REGISTER.row(reg).forEach(helper::register);
            });
        }
        TO_REGISTER.clear();
    }

    private static void onUnfreeze(RegisterEvent event) {
        isFrozen = false;
    }

    private static void onFreeze(IdMappingEvent event) {
        isFrozen = event.isFrozen();
    }

    public static void init(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGHEST, GTRegistries::onUnfreeze);
        eventBus.addListener(EventPriority.LOW, GTRegistries::actuallyRegister);
        NeoForge.EVENT_BUS.addListener(GTRegistries::onFreeze);
    }

    @SuppressWarnings("unchecked")
    private static void addRegistryToLoadOrder(ResourceKey<? extends Registry<?>> key, @Nullable Registry<?> registry) {
        LOAD_ORDER.add(key.location());
        if (registry != null) {
            REGISTRIES.put((ResourceKey<Registry<?>>) key, registry);
        }
    }

    @UnmodifiableView
    public static SequencedSet<ResourceLocation> getRegistryOrder() {
        return Collections.unmodifiableSequencedSet(LOAD_ORDER);
    }

    @UnmodifiableView
    public static Collection<Registry<?>> getRegistries() {
        return Collections.unmodifiableCollection(REGISTRIES.values());
    }
}

