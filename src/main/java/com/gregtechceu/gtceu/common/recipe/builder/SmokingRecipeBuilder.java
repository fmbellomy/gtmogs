package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SmokingRecipe;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Accessors(chain = true, fluent = true)
public class SmokingRecipeBuilder extends AbstractCookingRecipeBuilder<SmokingRecipe, SmokingRecipeBuilder> {

    public SmokingRecipeBuilder(@Nullable ResourceLocation id) {
        super(id, "smoking", SmokingRecipe::new);
    }
}
