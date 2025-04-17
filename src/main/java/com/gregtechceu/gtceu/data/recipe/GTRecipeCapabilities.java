package com.gregtechceu.gtceu.data.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class GTRecipeCapabilities {

    public final static RecipeCapability<SizedIngredient> ITEM = ItemRecipeCapability.CAP;
    public final static RecipeCapability<SizedFluidIngredient> FLUID = FluidRecipeCapability.CAP;
    public final static RecipeCapability<BlockState> BLOCK_STATE = BlockStateRecipeCapability.CAP;
    public final static RecipeCapability<Long> EU = EURecipeCapability.CAP;
    public final static RecipeCapability<Integer> CWU = CWURecipeCapability.CAP;

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.unfreeze();

        GTRegistries.RECIPE_CAPABILITIES.register(ITEM.name, ITEM);
        GTRegistries.RECIPE_CAPABILITIES.register(FLUID.name, FLUID);
        GTRegistries.RECIPE_CAPABILITIES.register(BLOCK_STATE.name, BLOCK_STATE);
        GTRegistries.RECIPE_CAPABILITIES.register(EU.name, EU);
        GTRegistries.RECIPE_CAPABILITIES.register(CWU.name, CWU);

        ModLoader.postEvent(new GTCEuAPI.RegisterEvent(GTRegistries.RECIPE_CAPABILITIES));
        GTRegistries.RECIPE_CAPABILITIES.freeze();
    }
}
