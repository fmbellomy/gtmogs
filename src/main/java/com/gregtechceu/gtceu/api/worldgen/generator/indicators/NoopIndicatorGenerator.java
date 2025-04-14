package com.gregtechceu.gtceu.api.worldgen.generator.indicators;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.worldgen.generator.IndicatorGenerator;
import com.gregtechceu.gtceu.api.worldgen.ores.GeneratedVeinMetadata;
import com.gregtechceu.gtceu.api.worldgen.ores.OreIndicatorPlacer;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NoopIndicatorGenerator extends IndicatorGenerator {

    public static final NoopIndicatorGenerator INSTANCE = new NoopIndicatorGenerator();
    public static final MapCodec<NoopIndicatorGenerator> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public Map<ChunkPos, OreIndicatorPlacer> generate(WorldGenLevel level, RandomSource random,
                                                      GeneratedVeinMetadata metadata) {
        // Nothing to do here
        return Collections.emptyMap();
    }

    @Override
    public int getSearchRadiusModifier(int veinRadius) {
        return 0;
    }

    @Nullable
    @Override
    public Either<BlockState, Material> block() {
        return null;
    }

    @Override
    public MapCodec<? extends IndicatorGenerator> codec() {
        return CODEC;
    }
}
