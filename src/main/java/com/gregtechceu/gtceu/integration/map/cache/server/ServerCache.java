package com.gregtechceu.gtceu.integration.map.cache.server;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.data.worldgen.ores.GeneratedVeinMetadata;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.network.GTNetwork;
import com.gregtechceu.gtceu.common.network.packets.prospecting.SPacketProspectOre;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.map.cache.DimensionCache;
import com.gregtechceu.gtceu.integration.map.cache.WorldCache;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class ServerCache extends WorldCache {

    public static final ServerCache instance = new ServerCache();

    private final Map<ResourceKey<Level>, ServerCacheSavedData> saveData = new HashMap<>();

    public void maybeInitWorld(ServerLevel world) {
        ResourceKey<Level> dim = world.dimension();
        if (!cache.containsKey(dim)) {
            cache.put(dim, new DimensionCache());
        }
        if (!saveData.containsKey(dim)) {
            saveData.put(dim, ServerCacheSavedData.init(world, cache.get(dim)));
        }
    }

    public void invalidateWorld(ServerLevel world) {
        ResourceKey<Level> dim = world.dimension();
        cache.remove(dim);
        saveData.remove(dim);
    }

    @Override
    public boolean addVein(ResourceKey<Level> dim, int gridX, int gridZ, GeneratedVeinMetadata name) {
        boolean added = super.addVein(dim, gridX, gridZ, name);
        if (added && saveData.containsKey(dim)) {
            saveData.get(dim).setDirty();
        }
        return added;
    }

    @Override
    public void clear() {
        super.clear();
        saveData.clear();
    }

    public void prospectSurfaceRockMaterial(ResourceKey<Level> dim, Material material, BlockPos pos,
                                            ServerPlayer player) {
        prospectBySurfaceRockMaterial(dim, material, pos, player,
                ConfigHolder.INSTANCE.compat.minimap.surfaceRockProspectRange);
    }

    public void prospectBySurfaceRockMaterial(ResourceKey<Level> dim, final Material material, BlockPos pos,
                                              ServerPlayer player, int radius) {
        if (radius < 0) return;
        List<GeneratedVeinMetadata> nearbyVeins = getNearbyVeins(dim, pos, radius);
        List<GeneratedVeinMetadata> foundVeins = new ArrayList<>();
        for (GeneratedVeinMetadata nearbyVein : nearbyVeins) {
            if (nearbyVein.definition().indicatorGenerators().stream()
                    .anyMatch(generator -> generator.block() != null && Objects.requireNonNull(generator.block())
                            .map(state -> {
                                MaterialStack mat = ChemicalHelper.getMaterialStack(state.getBlock().asItem());
                                if (mat.isEmpty()) return false;
                                return mat.material() == material;
                            },
                                    mat -> mat == material))) {
                foundVeins.add(nearbyVein);
            }
        }

        GTNetwork.NETWORK.sendToPlayer(new SPacketProspectOre(dim, foundVeins), player);
    }

    public void prospectByOreMaterial(ResourceKey<Level> dim, Material material, BlockPos origin, ServerPlayer player,
                                      int radius) {
        if (radius < 0) return;
        List<GeneratedVeinMetadata> nearbyVeins = getNearbyVeins(dim, origin, radius);
        List<GeneratedVeinMetadata> foundVeins = new ArrayList<>();
        for (GeneratedVeinMetadata nearbyVein : nearbyVeins) {
            if (nearbyVein.definition().veinGenerator().getAllMaterials().contains(material)) {
                foundVeins.add(nearbyVein);
            }
        }
        GTNetwork.NETWORK.sendToPlayer(new SPacketProspectOre(dim, foundVeins), player);
    }

    public void prospectByDepositName(ResourceKey<Level> dim, String depositName, BlockPos origin, ServerPlayer player,
                                      int radius) {
        if (radius < 0) return;
        List<GeneratedVeinMetadata> nearbyVeins = getNearbyVeins(dim, origin, radius);
        List<GeneratedVeinMetadata> foundVeins = new ArrayList<>();
        for (GeneratedVeinMetadata nearbyVein : nearbyVeins) {
            if (GTRegistries.ORE_VEINS.getKey(nearbyVein.definition()).toString().equals(depositName)) {
                foundVeins.add(nearbyVein);
            }
        }
        GTNetwork.NETWORK.sendToPlayer(new SPacketProspectOre(dim, foundVeins), player);
    }

    public void prospectAllInChunk(ResourceKey<Level> dim, ChunkPos pos, ServerPlayer player) {
        List<GeneratedVeinMetadata> nearbyVeins = cache.get(dim).getVeinsInChunk(pos);
        List<GeneratedVeinMetadata> foundVeins = new ArrayList<>();
        for (GeneratedVeinMetadata nearbyVein : nearbyVeins) {
            if (cache.containsKey(dim)) {
                foundVeins.add(nearbyVein);
            }
        }
        GTNetwork.NETWORK.sendToPlayer(new SPacketProspectOre(dim, foundVeins), player);
    }

    public void removeAllInChunk(ResourceKey<Level> dim, ChunkPos pos) {
        if (cache.containsKey(dim)) {
            cache.get(dim).removeAllInChunk(pos);
        }
    }
}
