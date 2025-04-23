package com.gregtechceu.gtceu.api.recipe;

import com.gregtechceu.gtceu.api.tag.TagUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import org.jetbrains.annotations.Nullable;

public class RecipeUtil {

    public static int getRatioForDistillery(SizedFluidIngredient fluidInput, SizedFluidIngredient fluidOutput,
                                            @Nullable ItemStack output) {
        int[] divisors = new int[] { 2, 5, 10, 25, 50 };
        int ratio = -1;

        for (int divisor : divisors) {

            if (!isFluidStackDivisibleForDistillery(fluidInput, divisor))
                continue;

            if (!isFluidStackDivisibleForDistillery(fluidOutput, divisor))
                continue;

            if (output != null && output.getCount() % divisor != 0)
                continue;

            ratio = divisor;
        }

        return Math.max(1, ratio);
    }

    public static boolean isFluidStackDivisibleForDistillery(SizedFluidIngredient fluidStack, int divisor) {
        return fluidStack.amount() % divisor == 0 && fluidStack.amount() / divisor >= 25;
    }

    public static SizedFluidIngredient makeFluidIngredient(FluidStack stack) {
        var tagKey = TagUtil.createFluidTag(BuiltInRegistries.FLUID.getKey(stack.getFluid()).getPath());
        if (stack.isComponentsPatchEmpty()) {
            return new SizedFluidIngredient(FluidIngredient.tag(tagKey), stack.getAmount());
        } else {
            var tag = BuiltInRegistries.FLUID.getOrCreateTag(tagKey);
            return new SizedFluidIngredient(DataComponentFluidIngredient.of(true, stack.getComponents(), tag),
                    stack.getAmount());
        }
    }

    public static SizedIngredient makeSizedIngredient(ItemStack stack) {
        return new SizedIngredient(makeItemIngredient(stack), stack.getCount());
    }

    public static Ingredient makeItemIngredient(ItemStack stack) {
        if (stack.isComponentsPatchEmpty()) {
            return Ingredient.of(stack);
        } else {
            return DataComponentIngredient.of(true, stack);
        }
    }
}
