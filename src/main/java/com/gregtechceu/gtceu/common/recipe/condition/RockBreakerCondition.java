package com.gregtechceu.gtceu.common.recipe.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.data.recipe.GTRecipeConditions;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class RockBreakerCondition extends RecipeCondition<RockBreakerCondition> {

    public static final MapCodec<RockBreakerCondition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> RecipeCondition.isReverse(instance)
                    .apply(instance, RockBreakerCondition::new));

    public final static RockBreakerCondition INSTANCE = new RockBreakerCondition();

    public RockBreakerCondition(boolean isReverse) {
        super(isReverse);
    }

    @Override
    public RecipeConditionType<RockBreakerCondition> getType() {
        return GTRecipeConditions.ROCK_BREAKER;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("recipe.condition.rock_breaker.tooltip");
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        var fluidA = BuiltInRegistries.FLUID.get(ResourceLocation.parse(recipe.data.getString("fluidA")));
        var fluidB = BuiltInRegistries.FLUID.get(ResourceLocation.parse(recipe.data.getString("fluidB")));
        boolean hasFluidA = false, hasFluidB = false;
        var level = recipeLogic.machine.self().getLevel();
        var pos = recipeLogic.machine.self().getPos();
        for (Direction side : GTUtil.DIRECTIONS) {
            if (side.getAxis() != Direction.Axis.Y) {
                var fluid = level.getFluidState(pos.relative(side));
                if (fluid.getType() == fluidA) hasFluidA = true;
                if (fluid.getType() == fluidB) hasFluidB = true;
                if (hasFluidA && hasFluidB) return true;
            }
        }
        return false;
    }

    @Override
    public RecipeCondition createTemplate() {
        return new RockBreakerCondition();
    }
}
