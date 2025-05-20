package com.gregtechceu.gtceu.api.worldgen.generator;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.api.worldgen.ores.OreBlockPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public abstract List<VeinEntry> getAllEntries();

    public List<BlockState> getAllBlocks() {
        return getAllEntries().stream()
                .map(VeinEntry::mapToBlockState)
                .toList();
    }

    public List<Material> getAllMaterials() {
        return getAllEntries().stream()
                .sorted(Comparator.comparingInt(VeinEntry::chance))
                .map(VeinEntry::mapToMaterial)
                .filter(mat -> !mat.isNull())
                .toList();
    }

    public IntList getAllChances() {
        return IntArrayList.toList(getAllEntries().stream().mapToInt(VeinEntry::chance));
    }

    public List<ObjectIntPair<Material>> getValidMaterialsChances() {
        return getAllEntries().stream()
                .map(entry -> ObjectIntPair.of(entry.mapToMaterial(), entry.chance))
                .filter(pair -> !pair.first().isNull())
                .toList();
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

    public record VeinEntry(Either<BlockState, Material> vein, int chance) {

        public static VeinEntry ofBlock(BlockState state, int chance) {
            return new VeinEntry(Either.left(state), chance);
        }

        public static VeinEntry ofMaterial(Material mat, int chance) {
            return new VeinEntry(Either.right(mat), chance);
        }

        public <T> T map(Function<BlockState, T> left, Function<Material, T> right) {
            return vein.map(left, right);
        }

        public BlockState mapToBlockState() {
            return vein.map(Function.identity(),
                    material -> ChemicalHelper.getBlock(TagPrefix.ore, material).defaultBlockState());
        }

        public Material mapToMaterial() {
            return vein.map(state -> ChemicalHelper.getMaterialStack(state.getBlock()).material(), Function.identity());
        }
    }

    public static Stream<Either<BlockState, Material>> mapTarget(Either<List<OreConfiguration.TargetBlockState>, Material> target) {
        return target.map(tbs -> tbs.stream().map(state -> Either.left(state.state)),
                mat -> Stream.of(Either.right(mat)));
    }

    public static Stream<VeinEntry> mapTarget(Either<List<OreConfiguration.TargetBlockState>, Material> target,
                                              int weight) {
        return mapTarget(target).map(entry -> new VeinEntry(entry, weight));
    }
}
