package com.gregtechceu.gtceu.api.worldgen.ores;

import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.data.worldgen.GTOreVeins;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.Optional;
import java.util.function.Function;

public class OreVeinUtil {

    private OreVeinUtil() {}

    public static boolean canPlaceOre(BlockState pState, Function<BlockPos, BlockState> pAdjacentStateAccessor,
                                      RandomSource pRandom, OreVeinDefinition entry,
                                      OreConfiguration.TargetBlockState pTargetState,
                                      BlockPos pMatablePos) {
        if (!pTargetState.target.test(pState, pRandom))
            return false;
        if (shouldSkipAirCheck(pRandom, entry.discardChanceOnAirExposure()))
            return true;

        return !Feature.isAdjacentToAir(pAdjacentStateAccessor, pMatablePos);
    }

    public static boolean canPlaceOre(BlockState pState, Function<BlockPos, BlockState> pAdjacentStateAccessor,
                                      RandomSource pRandom, OreVeinDefinition entry,
                                      BlockPos pMatablePos) {
        if (!entry.layer().getTarget().test(pState, pRandom))
            return false;
        if (shouldSkipAirCheck(pRandom, entry.discardChanceOnAirExposure()))
            return true;

        return !Feature.isAdjacentToAir(pAdjacentStateAccessor, pMatablePos);
    }

    protected static boolean shouldSkipAirCheck(RandomSource pRandom, float pChance) {
        return pChance <= 0 || (!(pChance >= 1) && pRandom.nextFloat() >= pChance);
    }

    /**
     * Resolves a vein's center for the supplied chunk position.
     * 
     * <p>
     * Note that depending on the config value for the random vein offset, its actual
     * center may be outside the supplied chunk.
     * 
     * @return The origin of the vein to be generated.<br/>
     *         {@code Optional.empty()} if no vein should exist for the specified chunk.
     */
    public static Optional<BlockPos> getVeinCenter(ChunkPos chunkPos, RandomSource random) {
        int gridSize = ConfigHolder.INSTANCE.worldgen.oreVeins.oreVeinGridSize;
        int randomOffset = ConfigHolder.INSTANCE.worldgen.oreVeins.oreVeinRandomOffset;

        if (chunkPos.x % gridSize != 0 || chunkPos.z % gridSize != 0)
            return Optional.empty();

        var chunkCenter = chunkPos.getMiddleBlockPosition(0);

        if (randomOffset == 0)
            return Optional.of(chunkCenter);

        return Optional.of(chunkCenter.offset(
                random.nextInt(-randomOffset, +randomOffset),
                0,
                random.nextInt(-randomOffset, +randomOffset)));
    }

    /**
     * @return The radius (in chunks) to search for adjacent veins.<br/>
     *         Depends on the largest registered vein size, as well as the configured random vein offset.
     */
    static int getMaxVeinSearchDistance() {
        double halfVeinSize = GTOreVeins.getLargestVeinSize() / 2.0;
        int randomOffset = ConfigHolder.INSTANCE.worldgen.oreVeins.oreVeinRandomOffset;

        return (int) Math.ceil((halfVeinSize + randomOffset) / 16.0);
    }

    /**
     * @return The radius (in chunks) to search for adjacent indicators.<br/>
     *         Depends on the largest registered indicator size, as well as the configured random vein offset.
     */
    static int getMaxIndicatorSearchDistance() {
        return getMaxVeinSearchDistance() + (int) Math.ceil((double) GTOreVeins.getLargestIndicatorOffset() / 16.0);
    }
}
