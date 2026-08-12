package com.quantumgarbage.gtmogs.common.data.loader;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;

import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.api.worldgen.WorldGeneratorUtils;
import com.quantumgarbage.gtmogs.data.worldgen.GTOreVeins;
import com.quantumgarbage.gtmogs.integration.map.cache.server.ServerCache;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@NotNullByDefault
public class PostRegistryListener extends ContextAwareReloadListener implements PreparableReloadListener {

    public static final PostRegistryListener INSTANCE = new PostRegistryListener();

    private PostRegistryListener() {}

    protected void apply() {
        var lookup = getRegistryLookup().lookupOrThrow(GTRegistries.Keys.ORE_VEIN);

        buildVeinGenerators(lookup);
        GTOreVeins.updateLargestVeinSize(lookup);
        ServerCache.instance.oreVeinDefinitionsChanged(lookup);
        WorldGeneratorUtils.invalidateOreVeinCache();
    }

    public static void buildVeinGenerators(HolderLookup.RegistryLookup<OreVeinDefinition> registry) {
        var iterator = registry.listElements().iterator();
        while (iterator.hasNext()) {
            var definition = iterator.next();
            var veinGen = definition.value().veinGenerator();
            if (veinGen != null && definition.value().canGenerate()) {
                veinGen.build();
            }
        }
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager,
                                          ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return stage.wait(null).thenRunAsync(this::apply);
    }
}
