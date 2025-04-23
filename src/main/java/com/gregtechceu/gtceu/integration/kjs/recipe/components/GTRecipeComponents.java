package com.gregtechceu.gtceu.integration.kjs.recipe.components;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.recipe.GTRecipeCapabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.*;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.*;

public class GTRecipeComponents {

    public static final RecipeComponent<CompoundTag> TAG = new RecipeComponent<>() {

        @Override
        public RecipeComponentType<?> type() {
            return TAG_TYPE;
        }

        @Override
        public Codec<CompoundTag> codec() {
            return CompoundTag.CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.RAW_MAP;
        }
    };
    public static final RecipeComponentType<CompoundTag> TAG_TYPE = RecipeComponentType.unit(GTCEu.id("tag"), TAG);
    public static final RecipeComponent<ResourceLocation> RESOURCE_LOCATION = new RecipeComponent<>() {

        @Override
        public RecipeComponentType<?> type() {
            return RESOURCE_LOCATION_TYPE;
        }

        @Override
        public Codec<ResourceLocation> codec() {
            return ResourceLocation.CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.STRING;
        }
    };
    public static final RecipeComponentType<ResourceLocation> RESOURCE_LOCATION_TYPE = RecipeComponentType
            .unit(GTCEu.id("resource_location"), RESOURCE_LOCATION);
    public static final RecipeComponent<RecipeCapability<?>> RECIPE_CAPABILITY = new RecipeComponent<>() {

        @Override
        public RecipeComponentType<?> type() {
            return RECIPE_CAPABILITY_TYPE;
        }

        @Override
        public Codec<RecipeCapability<?>> codec() {
            return RecipeCapability.DIRECT_CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(RecipeCapability.class);
        }
    };
    public static final RecipeComponentType<RecipeCapability<?>> RECIPE_CAPABILITY_TYPE = RecipeComponentType
            .unit(GTCEu.id("recipe_capability"), RECIPE_CAPABILITY);
    public static final RecipeComponent<ChanceLogic> CHANCE_LOGIC = new RecipeComponent<>() {

        @Override
        public RecipeComponentType<?> type() {
            return CHANCE_LOGIC_TYPE;
        }

        @Override
        public Codec<ChanceLogic> codec() {
            return GTRegistries.CHANCE_LOGICS.byNameCodec();
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(ChanceLogic.class);
        }
    };
    public static final RecipeComponentType<ChanceLogic> CHANCE_LOGIC_TYPE = RecipeComponentType
            .unit(GTCEu.id("chance_logic"), CHANCE_LOGIC);

    public static final RecipeComponent<RecipeCondition<?>> RECIPE_CONDITION = new RecipeComponent<>() {

        @Override
        public RecipeComponentType<?> type() {
            return RECIPE_CONDITION_TYPE;
        }

        @Override
        public Codec<RecipeCondition<?>> codec() {
            return RecipeCondition.CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.of(RecipeCondition.class);
        }
    };
    public static final RecipeComponentType<RecipeCondition<?>> RECIPE_CONDITION_TYPE = RecipeComponentType
            .unit(GTCEu.id("recipe_condition"), RECIPE_CONDITION);

    public static final ContentJS<SizedIngredient> ITEM = ContentJS.create(SizedIngredientComponent.NESTED,
            GTRecipeCapabilities.ITEM);
    public static final ContentJS<SizedFluidIngredient> FLUID = ContentJS.create(SizedFluidIngredientComponent.NESTED,
            GTRecipeCapabilities.FLUID);
    public static final ContentJS<Long> EU = ContentJS.create(NumberComponent.LONG_TYPE, GTRecipeCapabilities.EU);
    public static final ContentJS<Integer> CWU = ContentJS.create(NumberComponent.INT_TYPE, GTRecipeCapabilities.CWU);

    public static final RecipeComponent<Map<RecipeCapability<?>, ChanceLogic>> CHANCE_LOGIC_MAP = new JavaMapRecipeComponent<>(
            RECIPE_CAPABILITY, CHANCE_LOGIC);

    /**
     * First in pair is in, second is out
     */
    public static final Map<RecipeCapability<?>, ContentJS<?>> VALID_CAPS = new IdentityHashMap<>();

    static {
        VALID_CAPS.put(GTRecipeCapabilities.ITEM, ITEM);
        VALID_CAPS.put(GTRecipeCapabilities.FLUID, FLUID);
        VALID_CAPS.put(GTRecipeCapabilities.EU, EU);
        VALID_CAPS.put(GTRecipeCapabilities.CWU, CWU);

        KJSRecipeKeyEvent event = new KJSRecipeKeyEvent();
        AddonFinder.getAddonList().forEach(addon -> addon.registerRecipeKeys(event));
        VALID_CAPS.putAll(event.getRegisteredKeys());
    }
}
