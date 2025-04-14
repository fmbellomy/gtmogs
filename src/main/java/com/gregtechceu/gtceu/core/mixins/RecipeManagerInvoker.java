package com.gregtechceu.gtceu.core.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerInvoker {

    @Invoker("byType")
    <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipeFromType(RecipeType<T> type);
}
