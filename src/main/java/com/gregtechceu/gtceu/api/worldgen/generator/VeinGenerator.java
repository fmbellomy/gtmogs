package com.gregtechceu.gtceu.api.worldgen.generator;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.api.worldgen.ores.OreBlockPlacer;
import com.gregtechceu.gtceu.data.material.GTMaterials;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class VeinGenerator {

    public static final Codec<MapCodec<? extends VeinGenerator>> REGISTRY_CODEC = ResourceLocation.CODEC
            .flatXmap(rl -> Optional.ofNullable(WorldGeneratorUtils.VEIN_GENERATORS.get(rl))
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "No VeinGenerator with id " + rl + " registered")),
                    obj -> Optional.ofNullable(WorldGeneratorUtils.VEIN_GENERATORS.inverse().get(obj))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "VeinGenerator " + obj + " not registered")));
    public static final Codec<VeinGenerator> DIRECT_CODEC = REGISTRY_CODEC.dispatchStable(VeinGenerator::codec,
            Function.identity());

    public VeinGenerator() {}
    /**
     * @return List of [block|material, chance]
     */
    public abstract List<Map.Entry<Either<BlockState, Material>, Integer>> getAllEntries();

    public List<BlockState> getAllBlocks() {
        return getAllEntries().stream().map(entry -> entry.getKey().map(Function.identity(),
                material -> ChemicalHelper.getBlock(TagPrefix.ore, material).defaultBlockState())).toList();
    }

    public List<Material> getAllMaterials() {
        return getAllEntries().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .map(either -> either.map(state -> {
                    var matStack = ChemicalHelper.getMaterialStack(state.getBlock());
                    return matStack.material();
                }, Function.identity()))
                .filter(mat -> !mat.isNull())
                .toList();
    }

    public List<Integer> getAllChances() {
        return getAllEntries().stream().map(Map.Entry::getValue).toList();
    }

    public List<Map.Entry<Integer, Material>> getValidMaterialsChances() {
        return getAllEntries().stream()
                .filter(entry -> entry.getKey()
                        .map(state -> ChemicalHelper.getMaterialStack(state.getBlock()).material(),
                                Function.identity()) !=
                        GTMaterials.NULL)
                .map(entry -> Map.entry(entry.getValue(), entry.getKey()
                        .map(state -> ChemicalHelper.getMaterialStack(state.getBlock()).material(),
                                Function.identity())))
                .collect(Collectors.toList());
    }

    /**
     * Generate a map of all ore placers (by block position), for each block in this ore vein.
     * 
     * <p>
     * Note that, if in any way possible, this is NOT supposed to directly place any of the vein's blocks, as their
     * respective ore placers are invoked at a later time, when the chunk containing them is actually generated.
     */
    @HideFromJS
    public abstract Map<BlockPos, OreBlockPlacer> generate(WorldGenLevel level, RandomSource random,
                                                           OreVeinDefinition entry, BlockPos origin);

    @HideFromJS
    public abstract VeinGenerator build();

    public abstract VeinGenerator copy();

    public abstract MapCodec<? extends VeinGenerator> codec();
}
