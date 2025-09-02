package com.quantumgarbage.gtmogs.api.worldgen.ores;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.worldgen.IWorldGenLayer;
import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.api.worldgen.WorldGeneratorUtils;
import com.quantumgarbage.gtmogs.config.ConfigHolder;
import com.quantumgarbage.gtmogs.integration.map.cache.server.ServerCache;
import com.quantumgarbage.gtmogs.utils.GTUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Responsible for (pre)generating ore veins.<br>
 * This does not actually place any of the ore's blocks, and delegates to the applicable vein generator.
 */
public class OreGenerator {

    public record VeinConfiguration(GeneratedVeinMetadata data, RandomSource random) {

        public RandomSource newRandom() {
            return new XoroshiroRandomSource(random.nextLong());
        }
    }

    public List<GeneratedVeinMetadata> generateMetadata(final WorldGenLevel level, ChunkGenerator chunkGenerator,
                                                        ChunkPos chunkPos) {
        return createConfigs(level, chunkGenerator, chunkPos).stream()
                .map(OreGenerator::logVeinGeneration)
                .map(entry -> entry.data)
                .peek(data -> {
                    int gridX = Math.floorDiv(chunkPos.x, ConfigHolder.INSTANCE.worldgen.oreVeins.oreVeinGridSize);
                    int gridZ = Math.floorDiv(chunkPos.z, ConfigHolder.INSTANCE.worldgen.oreVeins.oreVeinGridSize);
                    ServerCache.instance.addVein(level.getLevel().dimension(), gridX, gridZ, data);
                })
                .toList();
    }

    /**
     * Generates the vein for the specified chunk metadata.<br>
     * If the chunk is not located on one of the ore vein grid's intersections, no vein will be generated.
     *
     * <p>
     * Note that depending on the configured random offset, the actual center of the generated vein may be located
     * outside the specified origin chunk.
     *
     * @return The generated vein for the specified chunk metadata.<br>
     *         {@code Optional.empty()} if no vein exists at this chunk.
     */
    public List<GeneratedVein> generateOres(WorldGenLevel level, List<GeneratedVeinMetadata> metadata,
                                            ChunkPos chunkPos) {
        return metadata.stream()
                .map(data -> new VeinConfiguration(
                        data,
                        new XoroshiroRandomSource(level.getSeed() ^ chunkPos.toLong())))
                .flatMap(config -> generateOres(config, level, chunkPos).stream())
                .toList();
    }

    public Optional<GeneratedVein> generateOres(VeinConfiguration config, WorldGenLevel level, ChunkPos chunkPos) {
        OreVeinDefinition definition = config.data.definition().value();
        Map<BlockPos, OreBlockPlacer> generatedVeins = definition.veinGenerator()
                .generate(level, config.newRandom(), definition, config.data.center());

        if (generatedVeins.isEmpty()) {
            logEmptyVein(config);
            return Optional.empty();
        }

        return Optional.of(new GeneratedVein(chunkPos, definition.layer(), generatedVeins));
    }

    private List<VeinConfiguration> createConfigs(WorldGenLevel level, ChunkGenerator generator, ChunkPos chunkPos) {
        var random = new XoroshiroRandomSource(level.getSeed() ^ chunkPos.toLong());

        return OreVeinUtil.getVeinCenter(chunkPos, random).stream()
                .flatMap(veinCenter -> getEntries(level, veinCenter, random).map(entry -> {
                    if (entry == null) return null;

                    BlockPos origin = computeVeinOrigin(level, generator, chunkPos, random, veinCenter, entry.value())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Cannot determine y coordinate for the vein at " + veinCenter));

                    return new VeinConfiguration(new GeneratedVeinMetadata(chunkPos, origin, entry), random);
                })).toList();
    }

    private Stream<Holder<OreVeinDefinition>> getEntries(WorldGenLevel level, BlockPos veinCenter,
                                                         XoroshiroRandomSource random) {
        return WorldGeneratorUtils.WORLD_GEN_LAYERS.values().stream()
                .filter(layer -> layer.isApplicableForLevel(level.getLevel().dimension()))
                .map(layer -> {
                    int quartX = QuartPos.fromBlock(veinCenter.getX());
                    int quartY = QuartPos.fromBlock(veinCenter.getY());
                    int quartZ = QuartPos.fromBlock(veinCenter.getZ());
                    return getEntry(level, level.getUncachedNoiseBiome(quartX, quartY, quartZ), random, layer);
                })
                .filter(Objects::nonNull);
    }

    @Nullable
    private Holder<OreVeinDefinition> getEntry(WorldGenLevel level, Holder<Biome> biome, RandomSource random,
                                               IWorldGenLayer layer) {
        var veins = WorldGeneratorUtils.getCachedBiomeVeins(level.getLevel(), biome).stream()
                .filter(vein -> vein.vein().value().layer().equals(layer))
                .toList();
        var randomVein = GTUtil.getRandomItem(random, veins);
        return randomVein == null ? null : randomVein.vein();
    }

    @NotNull
    private static Optional<BlockPos> computeVeinOrigin(WorldGenLevel level, ChunkGenerator generator, ChunkPos pos,
                                                        RandomSource random, BlockPos veinCenter,
                                                        OreVeinDefinition entry) {
        int layerSeed = WorldGeneratorUtils.getWorldGenLayerKey(entry.layer())
                .map(String::hashCode)
                .orElse(0);
        var layeredRandom = new XoroshiroRandomSource(random.nextLong() ^ ((long) layerSeed));

        veinCenter = OreVeinUtil.getVeinCenter(pos, layeredRandom).orElse(veinCenter);
        return entry.heightRange().getPositions(
                new PlacementContext(level, generator, Optional.empty()),
                layeredRandom, veinCenter).findFirst();
    }

    /////////////////////////////////////
    // ********* LOGGING *********//
    /////////////////////////////////////

    private static VeinConfiguration logVeinGeneration(VeinConfiguration config) {
        if (ConfigHolder.INSTANCE.dev.debugWorldgen) {
            GTMOGS.LOGGER.debug("Generating vein {} at {}",
                    config.data.definition().getKey().location(), config.data.center());
        }

        return config;
    }

    private static void logEmptyVein(VeinConfiguration config) {
        if (ConfigHolder.INSTANCE.dev.debugWorldgen) {
            GTMOGS.LOGGER.debug("No blocks generated for vein {} at {}",
                    config.data.definition().getKey().location(), config.data.center());
        }
    }
}
