package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.registry.GTRegistry;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.data.worldgen.GTOreVeins;
import com.gregtechceu.gtceu.integration.map.cache.server.ServerCache;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashSet;
import java.util.Set;

public class PostRegistryListener extends SimplePreparableReloadListener<Void> {

    public static final PostRegistryListener INSTANCE = new PostRegistryListener();

    private PostRegistryListener() {}

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profiler) {
        var registry = GTRegistries.builtinRegistry().registryOrThrow(GTRegistries.ORE_VEIN_REGISTRY);

        buildVeinGenerators(registry);
        GTOreVeins.updateLargestVeinSize(registry);
        ServerCache.instance.oreVeinDefinitionsChanged(registry);
        WorldGeneratorUtils.invalidateOreVeinCache();
    }

    public static void buildVeinGenerators(Registry<OreVeinDefinition> registry) {
        var iterator = registry.asHolderIdMap().iterator();
        Set<Holder<OreVeinDefinition>> toRemove = new HashSet<>();
        while (iterator.hasNext()) {
            var definition = iterator.next();
            if (definition.value().veinGenerator() != null) {
                definition.value().veinGenerator().build();
            } else {
                toRemove.add(definition);
            }
        }
        if (registry instanceof GTRegistry<OreVeinDefinition> registry1) {
            for (var def : toRemove) {
                registry1.remove(def);
            }
        }
    }

}
