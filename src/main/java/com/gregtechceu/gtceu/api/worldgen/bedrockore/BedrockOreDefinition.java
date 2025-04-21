package com.gregtechceu.gtceu.api.worldgen.bedrockore;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.worldgen.BiomeWeightModifier;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.integration.kjs.builders.worldgen.BedrockOreBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Accessors(fluent = true, chain = true)
public class BedrockOreDefinition {
    // spotless:off
    public static final MapCodec<Pair<Material, Integer>> MATERIAL = Codec
            .mapPair(GTCEuAPI.materialManager.codec().fieldOf("material"), Codec.INT.fieldOf("chance"));

    public static final Codec<BedrockOreDefinition> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("weight").forGetter(ft -> ft.weight),
                    Codec.INT.fieldOf("size").forGetter(ft -> ft.size),
                    IntProvider.POSITIVE_CODEC.fieldOf("yield").forGetter(ft -> ft.yield),
                    Codec.INT.fieldOf("depletion_amount").forGetter(ft -> ft.depletionAmount),
                    ExtraCodecs.intRange(0, 100).fieldOf("depletion_chance").forGetter(ft -> ft.depletionChance),
                    Codec.INT.fieldOf("depleted_yield").forGetter(ft -> ft.depletedYield),
                    MATERIAL.codec().listOf().fieldOf("materials").forGetter(ft -> ft.materials),
                    BiomeWeightModifier.CODEC.listOf().optionalFieldOf("weight_modifier", List.of()).forGetter(ft -> ft.originalModifiers),
                    ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimension_filter").forGetter(ft -> new ArrayList<>(ft.dimensionFilter)))
                    .apply(instance,
                            (weight, size, yield, depletionAmount, depletionChance, depletedYield, materials,
                             biomeWeightModifier, dimensionFilter) -> new BedrockOreDefinition(weight, size, yield,
                                     depletionAmount, depletionChance, depletedYield, materials, biomeWeightModifier,
                                     new HashSet<>(dimensionFilter))));
    public static final Codec<Holder<BedrockOreDefinition>> CODEC = RegistryFixedCodec.create(GTRegistries.BEDROCK_ORE_REGISTRY);
    // spotless:on
    @Getter
    @Setter
    private int weight; // weight value for determining which vein will appear
    @Getter
    @Setter
    private int size; // size in chunks
    @Getter
    @Setter
    private IntProvider yield;// the [minimum, maximum] yields
    @Getter
    @Setter
    private int depletionAmount; // amount of ore the vein gets drained by
    @Getter
    @Setter
    private int depletionChance; // the chance [0, 100] that the vein will deplete by 1
    @Getter
    @Setter
    private int depletedYield; // yield after the vein is depleted
    @Getter
    @Setter
    private List<Pair<Material, Integer>> materials; // the ores which the vein contains
    @Getter
    private BiomeWeightModifier biomeWeightModifier; // weighting of biomes
    private List<BiomeWeightModifier> originalModifiers; // weighting of biomes
    @Getter
    @Setter
    @Nullable
    public Set<ResourceKey<Level>> dimensionFilter; // filtering of dimensions

    public BedrockOreDefinition(ResourceLocation name, int size, int weight, IntProvider yield, int depletionAmount,
                                int depletionChance, int depletedYield, List<Pair<Material, Integer>> materials,
                                List<BiomeWeightModifier> originalModifiers, Set<ResourceKey<Level>> dimensionFilter) {
        this(weight, size, yield, depletionAmount, depletionChance, depletedYield, materials, originalModifiers,
                dimensionFilter);
    }

    public BedrockOreDefinition(int weight, int size, IntProvider yield, int depletionAmount, int depletionChance,
                                int depletedYield, List<Pair<Material, Integer>> materials,
                                List<BiomeWeightModifier> originalModifiers, Set<ResourceKey<Level>> dimensionFilter) {
        this.weight = weight;
        this.size = size;
        this.yield = yield;
        this.depletionAmount = depletionAmount;
        this.depletionChance = depletionChance;
        this.depletedYield = depletedYield;
        this.materials = materials;
        this.originalModifiers = originalModifiers;
        this.biomeWeightModifier = new BiomeWeightModifier(
                () -> HolderSet.direct(originalModifiers.stream().flatMap(mod -> mod.biomes.get().stream()).toList()),
                originalModifiers.stream().mapToInt(mod -> mod.addedWeight).sum()) {

            @Override
            public Integer apply(Holder<Biome> biome) {
                int mod = 0;
                for (var modifier : originalModifiers) {
                    if (modifier.biomes.get().contains(biome)) {
                        mod += modifier.apply(biome);
                    }
                }
                return mod;
            }
        };
        this.dimensionFilter = dimensionFilter;
    }

    public void setOriginalModifiers(List<BiomeWeightModifier> modifiers) {
        this.originalModifiers = modifiers;
        this.biomeWeightModifier = new BiomeWeightModifier(
                () -> HolderSet.direct(originalModifiers.stream().flatMap(mod -> mod.biomes.get().stream()).toList()),
                originalModifiers.stream().mapToInt(mod -> mod.addedWeight).sum()) {

            @Override
            public Integer apply(Holder<Biome> biome) {
                int mod = 0;
                for (var modifier : originalModifiers) {
                    if (modifier.biomes.get().contains(biome)) {
                        mod += modifier.apply(biome);
                    }
                }
                return mod;
            }
        };
    }

    public List<Integer> getAllChances() {
        return materials().stream().map(Pair::getSecond).toList();
    }

    public List<Material> getAllMaterials() {
        return materials().stream().map(Pair::getFirst).toList();
    }

    public static BedrockOreBuilder builder(ResourceLocation name) {
        return new BedrockOreBuilder(name);
    }

}
