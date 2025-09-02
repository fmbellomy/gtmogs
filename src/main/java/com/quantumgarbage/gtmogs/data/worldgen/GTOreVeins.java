package com.quantumgarbage.gtmogs.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.api.worldgen.*;
import com.quantumgarbage.gtmogs.api.worldgen.generator.veins.NoopVeinGenerator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class GTOreVeins {

    /**
     * The size of the largest registered vein.
     * This becomes available after all veins have been loaded.
     */
    @Getter
    private static int largestVeinSize = 0;

    @Getter
    private static final int largestIndicatorOffset = 0;

    public static final Set<ResourceKey<OreVeinDefinition>> ALL_KEYS = new ReferenceOpenHashSet<>();

    public static final ResourceKey<OreVeinDefinition> FREAKY_VEIN = create(GTMOGS.id("freaky"));

    public static void updateLargestVeinSize(Registry<OreVeinDefinition> registry) {
        // map to average of min & max values.
        GTOreVeins.largestVeinSize = registry.stream()
                .map(OreVeinDefinition::clusterSize)
                .mapToInt(intProvider -> (intProvider.getMinValue() + intProvider.getMaxValue()) / 2)
                .max()
                .orElse(0);
    }

    public static ResourceKey<OreVeinDefinition> create(ResourceLocation id) {
        var key = ResourceKey.create(GTRegistries.ORE_VEIN_REGISTRY, id);
        ALL_KEYS.add(key);
        return key;
    }

    public static OreVeinDefinition blankOreDefinition(HolderGetter<Biome> biomeLookup) {
        return new OreVeinDefinition(
                ConstantInt.ZERO, 0, 0, IWorldGenLayer.NOWHERE, Set.of(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(0)),
                0, HolderSet.empty(), BiomeWeightModifier.EMPTY, NoopVeinGenerator.INSTANCE, biomeLookup);
    }

    private static void register(BootstrapContext<OreVeinDefinition> context,
                                 ResourceKey<OreVeinDefinition> key,
                                 Consumer<OreVeinDefinition> consumer) {
        OreVeinDefinition builder = blankOreDefinition(context.lookup(Registries.BIOME));
        consumer.accept(builder);
        context.register(key, builder);
    }

    public static void bootstrap(BootstrapContext<OreVeinDefinition> context) {
        // END
        RuleTest[] endRules = new RuleTest[] { WorldGeneratorUtils.END_ORE_REPLACEABLES };
        RuleTest[] netherRules = new RuleTest[] { new TagMatchTest(BlockTags.NETHER_CARVER_REPLACEABLES) };
        RuleTest[] stoneRules = new RuleTest[] { new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES) };
        RuleTest[] deepslateRules = new RuleTest[] { new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES) };

        register(context, FREAKY_VEIN, vein -> vein
                .clusterSize(UniformInt.of(32, 40)).density(0.95f).weight(100)
                .layer(WorldGenLayers.DEEPSLATE)
                .heightRangeUniform(-55, 80)
                .biomes(BiomeTags.IS_OVERWORLD)
                .classicVeinGenerator(generator -> generator
                        .primary(l -> l
                                .block(() -> BuiltInRegistries.BLOCK
                                        .get(ResourceLocation.fromNamespaceAndPath("minecraft", "slime_block")))
                                .size(6))
                        .secondary(l -> l
                                .block(() -> BuiltInRegistries.BLOCK
                                        .get(ResourceLocation.fromNamespaceAndPath("minecraft", "honey_block")))
                                .size(4))
                        .between(l -> l
                                .block(() -> BuiltInRegistries.BLOCK
                                        .get(ResourceLocation.fromNamespaceAndPath("minecraft", "redstone_block")))
                                .size(2))
                        .sporadic(l -> l
                                .block(() -> BuiltInRegistries.BLOCK
                                        .get(ResourceLocation.fromNamespaceAndPath("minecraft", "monster_spawner")))
                                .size(1))
                        .build()));
    }
}
