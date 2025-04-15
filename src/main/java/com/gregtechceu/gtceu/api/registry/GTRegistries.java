package com.gregtechceu.gtceu.api.registry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.api.worldgen.DimensionMarker;
import com.gregtechceu.gtceu.api.material.Element;
import com.gregtechceu.gtceu.api.worldgen.GTOreDefinition;
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
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

public final class GTRegistries {

    // GT Registry
    public static final GTRegistry.String<Element> ELEMENTS = new GTRegistry.String<>(GTCEu.id("element"));

    public static final GTRegistry.RL<GTRecipeType> RECIPE_TYPES = new GTRegistry.RL<>(GTCEu.id("recipe_type"));
    public static final GTRegistry.RL<GTRecipeCategory> RECIPE_CATEGORIES = new GTRegistry.RL<>(
            GTCEu.id("recipe_category"));
    public static final GTRegistry.RL<CoverDefinition> COVERS = new GTRegistry.RL<>(GTCEu.id("cover"));

    public static final GTRegistry.RL<MachineDefinition> MACHINES = new GTRegistry.RL<>(GTCEu.id("machine"));
    public static final GTRegistry.String<RecipeCapability<?>> RECIPE_CAPABILITIES = new GTRegistry.String<>(
            GTCEu.id("recipe_capability"));
    public static final GTRegistry.String<RecipeConditionType<?>> RECIPE_CONDITIONS = new GTRegistry.String<>(
            GTCEu.id("recipe_condition"));
    public static final GTRegistry.RL<ToolBehaviorType<?>> TOOL_BEHAVIORS = new GTRegistry.RL<>(
            GTCEu.id("tool_behavior"));
    public static final GTRegistry.String<ChanceLogic> CHANCE_LOGICS = new GTRegistry.String<>(
            GTCEu.id("chance_logic"));
    public static final GTRegistry.RL<SoundEntry> SOUNDS = new GTRegistry.RL<>(GTCEu.id("sound"));
    public static final GTRegistry.RL<BedrockFluidDefinition> BEDROCK_FLUID_DEFINITIONS = new GTRegistry.RL<>(
            GTCEu.id("bedrock_fluid"));
    public static final GTRegistry.RL<BedrockOreDefinition> BEDROCK_ORE_DEFINITIONS = new GTRegistry.RL<>(
            GTCEu.id("bedrock_ore"));
    public static final GTRegistry.RL<GTOreDefinition> ORE_VEINS = new GTRegistry.RL<>(GTCEu.id("ore_vein"));
    public static final GTRegistry.RL<DimensionMarker> DIMENSION_MARKERS = new GTRegistry.RL<>(
            GTCEu.id("dimension_marker"));

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
