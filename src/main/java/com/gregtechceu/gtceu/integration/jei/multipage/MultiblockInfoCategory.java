package com.gregtechceu.gtceu.integration.jei.multipage;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.gui.widget.PatternPreviewWidget;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.machine.GTMultiMachines;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;

import lombok.Getter;
import net.minecraft.network.chat.Component;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;

public class MultiblockInfoCategory extends ModularUIRecipeCategory<MultiblockMachineDefinition> {

    public final static RecipeType<MultiblockMachineDefinition> RECIPE_TYPE = new RecipeType<>(GTCEu.id("multiblock_info"),
            MultiblockMachineDefinition.class);
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;

    public MultiblockInfoCategory(IJeiHelpers helpers) {
        super(MultiblockInfoWrapper::new);
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(160, 160);
        this.icon = helpers.getGuiHelper().createDrawableItemStack(GTMultiMachines.ELECTRIC_BLAST_FURNACE.asStack());
    }

    public static void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RECIPE_TYPE, GTRegistries.MACHINES.stream()
                .filter(MultiblockMachineDefinition.class::isInstance)
                .map(MultiblockMachineDefinition.class::cast)
                .filter(MultiblockMachineDefinition::isRenderXEIPreview)
                .toList());
    }

    @Override
    @NotNull
    public RecipeType<MultiblockMachineDefinition> getRecipeType() {
        return RECIPE_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.jei.multiblock_info");
    }
}
