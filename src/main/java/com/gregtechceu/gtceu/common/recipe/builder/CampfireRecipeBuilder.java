package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Accessors(chain = true, fluent = true)
public class CampfireRecipeBuilder extends AbstractCookingRecipeBuilder<CampfireCookingRecipe, CampfireRecipeBuilder> {

    public CampfireRecipeBuilder(@Nullable ResourceLocation id) {
        super(id, "campfire_cooking", CampfireCookingRecipe::new);
    }
}
