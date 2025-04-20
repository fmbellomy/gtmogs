package com.gregtechceu.gtceu.api.registry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.worldgen.DimensionMarker;
import com.gregtechceu.gtceu.api.material.Element;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.sound.SoundEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

public final class GTRegistries {

    public static final ResourceLocation ROOT_GT_REGISTRY_NAME = GTCEu.id("root");
    public static final GTRegistry<GTRegistry<?>> ROOT = new GTRegistry<>(ROOT_GT_REGISTRY_NAME);
    // spotless:off
    public static final ResourceKey<Registry<OreVeinDefinition>> ORE_VEIN_REGISTRY = makeRegistryKey(GTCEu.id("ore_vein"));
    public static final ResourceKey<Registry<BedrockFluidDefinition>> BEDROCK_FLUID_REGISTRY = makeRegistryKey(GTCEu.id("bedrock_fluid"));
    public static final ResourceKey<Registry<BedrockOreDefinition>> BEDROCK_ORE_REGISTRY = makeRegistryKey(GTCEu.id("bedrock_ore"));

    public static final ResourceKey<Registry<Material>> MATERIAL_REGISTRY = makeRegistryKey(GTCEu.id("material"));
    public static final ResourceKey<Registry<Element>> ELEMENT_REGISTRY = makeRegistryKey(GTCEu.id("element"));
    public static final ResourceKey<Registry<MachineDefinition>> MACHINE_REGISTRY = makeRegistryKey(GTCEu.id("machine"));
    public static final ResourceKey<Registry<CoverDefinition>> COVER_REGISTRY = makeRegistryKey(GTCEu.id("cover"));

    public static final ResourceKey<Registry<GTRecipeType>> RECIPE_TYPE_REGISTRY = makeRegistryKey(GTCEu.id("recipe_type"));
    public static final ResourceKey<Registry<GTRecipeCategory>> RECIPE_CATEGORY_REGISTRY = makeRegistryKey(GTCEu.id("recipe_category"));
    public static final ResourceKey<Registry<RecipeCapability<?>>> RECIPE_CAPABILITY_REGISTRY = makeRegistryKey(GTCEu.id("recipe_capability"));
    public static final ResourceKey<Registry<RecipeConditionType<?>>> RECIPE_CONDITION_REGISTRY = makeRegistryKey(GTCEu.id("recipe_condition"));
    public static final ResourceKey<Registry<ChanceLogic>> CHANCE_LOGIC_REGISTRY = makeRegistryKey(GTCEu.id("chance_logic"));

    public static final ResourceKey<Registry<ToolBehaviorType<?>>> TOOL_BEHAVIOR_REGISTRY = makeRegistryKey(GTCEu.id("tool_behavior"));
    public static final ResourceKey<Registry<SoundEntry>> SOUND_REGISTRY = makeRegistryKey(GTCEu.id("sound"));
    public static final ResourceKey<Registry<DimensionMarker>> DIMENSION_MARKER_REGISTRY = makeRegistryKey(GTCEu.id("dimension_marker"));

    // GT Registry
    public static final GTRegistry<Material> MATERIALS = new GTRegistry<>(MATERIAL_REGISTRY);
    public static final GTRegistry<Element> ELEMENTS = new GTRegistry<>(ELEMENT_REGISTRY);
    public static final GTRegistry<MachineDefinition> MACHINES = new GTRegistry<>(MACHINE_REGISTRY);
    public static final GTRegistry<CoverDefinition> COVERS = new GTRegistry<>(COVER_REGISTRY);

    public static final GTRegistry<GTRecipeType> RECIPE_TYPES = new GTRegistry<>(RECIPE_TYPE_REGISTRY);
    public static final GTRegistry<GTRecipeCategory> RECIPE_CATEGORIES = new GTRegistry<>(RECIPE_CATEGORY_REGISTRY);
    public static final GTRegistry<RecipeCapability<?>> RECIPE_CAPABILITIES = new GTRegistry<>(RECIPE_CAPABILITY_REGISTRY);
    public static final GTRegistry<RecipeConditionType<?>> RECIPE_CONDITIONS = new GTRegistry<>(RECIPE_CONDITION_REGISTRY);
    public static final GTRegistry<ChanceLogic> CHANCE_LOGICS = new GTRegistry<>(CHANCE_LOGIC_REGISTRY);

    public static final GTRegistry<ToolBehaviorType<?>> TOOL_BEHAVIORS = new GTRegistry<>(TOOL_BEHAVIOR_REGISTRY);
    public static final GTRegistry<SoundEntry> SOUNDS = new GTRegistry<>(SOUND_REGISTRY);
    public static final GTRegistry<DimensionMarker> DIMENSION_MARKERS = new GTRegistry<>(DIMENSION_MARKER_REGISTRY);
    // spotless:on

    public static <T> ResourceKey<Registry<T>> makeRegistryKey(ResourceLocation registryId) {
        return ResourceKey.createRegistryKey(registryId);
    }

    private static final Table<Registry<?>, ResourceLocation, Object> TO_REGISTER = HashBasedTable.create();
    public static <V, T extends V> T register(Registry<V> registry, ResourceLocation name, T value) {
        TO_REGISTER.put(registry, name, value);
        return value;
    }

    // ignore the generics and hope the registered objects are still correctly typed :3
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void actuallyRegister(RegisterEvent event) {
        for (Registry reg : TO_REGISTER.rowKeySet()) {
            event.register(reg.key(), helper -> {
                TO_REGISTER.row(reg).forEach(helper::register);
            });
        }
    }

    public static void init(IEventBus eventBus) {
        eventBus.addListener(GTRegistries::actuallyRegister);
    }

    private static final RegistryAccess BLANK = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    private static RegistryAccess FROZEN = BLANK;

    /**
     * You shouldn't call it, you should probably not even look at it just to be extra safe
     *
     * @param registryAccess the new value to set to the frozen registry access
     */
    @ApiStatus.Internal
    public static void updateFrozenRegistry(RegistryAccess registryAccess) {
        FROZEN = registryAccess;
    }

    public static RegistryAccess builtinRegistry() {
        if (FROZEN == BLANK && GTCEu.isClientThread()) {
            if (Minecraft.getInstance().getConnection() != null) {
                return Minecraft.getInstance().getConnection().registryAccess();
            }
        }
        return FROZEN;
    }
}
