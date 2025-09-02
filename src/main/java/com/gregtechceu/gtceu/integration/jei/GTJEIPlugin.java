package com.gregtechceu.gtceu.integration.jei;

import com.gregtechceu.gtceu.GTCEu;

import com.gregtechceu.gtceu.integration.jei.orevein.GTOreVeinInfoCategory;


import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;

import mezz.jei.api.registration.*;
import org.jetbrains.annotations.NotNull;


@JeiPlugin
public class GTJEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return GTCEu.id("jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        if (!GTCEu.Mods.isJEILoaded()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new GTOreVeinInfoCategory(jeiHelpers));

    }



    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (!GTCEu.Mods.isJEILoaded()) return;
        GTOreVeinInfoCategory.registerRecipes(registration);
    }
}
