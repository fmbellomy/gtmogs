package com.gregtechceu.gtceu.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

public class GTStringUtils {

    /**
     * Better implementation of {@link ItemStack#toString()} which respects the stack-aware
     * {@link net.minecraft.world.item.Item#getDescriptionId(ItemStack)} method.
     *
     * @param stack the stack to convert
     * @return the string form of the stack
     */
    @NotNull
    public static String itemStackToString(@NotNull ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stack.getCount() + "x_" + itemId.toDebugFileName();
    }

    @NotNull
    public static String fluidStackToString(@NotNull FluidStack stack) {
        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(stack.getFluid());
        return stack.getAmount() + "x_" + fluidId.getNamespace() + "_" + fluidId.getPath();
    }
}
