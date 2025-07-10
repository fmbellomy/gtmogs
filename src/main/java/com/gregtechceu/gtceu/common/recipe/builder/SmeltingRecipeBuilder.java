package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import org.jetbrains.annotations.Nullable;

public class SmeltingRecipeBuilder extends AbstractCookingRecipeBuilder<SmeltingRecipe, SmeltingRecipeBuilder> {

    public SmeltingRecipeBuilder(@Nullable ResourceLocation id) {
        super(id, "smelting", SmeltingRecipe::new);
    }
}
