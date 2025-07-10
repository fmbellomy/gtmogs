package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.BlastingRecipe;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Accessors(chain = true, fluent = true)
public class BlastingRecipeBuilder extends AbstractCookingRecipeBuilder<BlastingRecipe, BlastingRecipeBuilder> {

    public BlastingRecipeBuilder(@Nullable ResourceLocation id) {
        super(id, "blasting", BlastingRecipe::new);
    }
}
