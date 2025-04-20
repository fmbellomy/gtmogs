package com.gregtechceu.gtceu.integration.jei.orevein;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.client.ClientInit;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;

import lombok.Getter;
import net.minecraft.network.chat.Component;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;

public class GTOreVeinInfoCategory extends ModularUIRecipeCategory<OreVeinDefinition> {

    public final static RecipeType<OreVeinDefinition> RECIPE_TYPE = new RecipeType<>(GTCEu.id("ore_vein_diagram"),
            OreVeinDefinition.class);
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;

    public GTOreVeinInfoCategory(IJeiHelpers helpers) {
        super(GTOreVeinInfoWrapper::new);
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(GTOreVeinWidget.width, 120);
        this.icon = helpers.getGuiHelper()
                .createDrawableItemStack(ChemicalHelper.get(TagPrefix.rawOre, GTMaterials.Iron));
    }

    public static void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RECIPE_TYPE, ClientInit.CLIENT_ORE_VEINS.values().stream().toList());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OreVeinDefinition definition, IFocusGroup focuses) {
        super.setRecipe(builder, definition, focuses);
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                .addItemStacks(GTOreVeinWidget.getContainedOresAndBlocks(definition));
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_LV.asStack(), RECIPE_TYPE);
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_HV.asStack(), RECIPE_TYPE);
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_LuV.asStack(), RECIPE_TYPE);
    }

    @NotNull
    @Override
    public RecipeType<OreVeinDefinition> getRecipeType() {
        return RECIPE_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.jei.ore_vein_diagram");
    }
}
