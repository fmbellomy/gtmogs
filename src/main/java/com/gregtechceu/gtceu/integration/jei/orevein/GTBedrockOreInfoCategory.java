package com.gregtechceu.gtceu.integration.jei.orevein;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.client.ClientInit;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import lombok.Getter;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;

public class GTBedrockOreInfoCategory extends ModularUIRecipeCategory<BedrockOreDefinition> {

    public final static RecipeType<BedrockOreDefinition> RECIPE_TYPE = new RecipeType<>(
            GTCEu.id("bedrock_ore_diagram"), BedrockOreDefinition.class);
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;

    public GTBedrockOreInfoCategory(IJeiHelpers helpers) {
        super(GTBedrockOreInfoWrapper::new);
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(GTOreVeinWidget.width, 120);
        this.icon = helpers.getGuiHelper().createDrawableItemStack(Items.RAW_IRON.getDefaultInstance());
    }

    public static void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RECIPE_TYPE, ClientInit.CLIENT_BEDROCK_ORE_VEINS.values().stream().toList());
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_HV.asStack(), RECIPE_TYPE);
        registration.addRecipeCatalyst(GTItems.PROSPECTOR_LuV.asStack(), RECIPE_TYPE);
    }

    @NotNull
    @Override
    public RecipeType<BedrockOreDefinition> getRecipeType() {
        return RECIPE_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.jei.bedrock_ore_diagram");
    }
}
