package com.gregtechceu.gtceu.integration.kjs.builders.recipetype;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.data.recipe.GTRecipeCategories;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(chain = true, fluent = true)
public class GTRecipeCategoryBuilder extends BuilderBase<GTRecipeCategory> {

    private final transient String name;
    @Setter
    @NotNull
    private transient GTRecipeType recipeType;
    @Setter
    @Nullable
    private transient IGuiTexture icon;
    @Setter
    private transient boolean isXEIVisible;
    @Setter
    @Nullable
    private transient String langValue;

    public GTRecipeCategoryBuilder(ResourceLocation id) {
        super(id);
        name = id.getPath();
        recipeType = GTRecipeTypes.DUMMY_RECIPES;
        icon = null;
        isXEIVisible = true;
        langValue = null;
    }

    @Override
    public void generateLang(LangKubeEvent lang) {
        super.generateLang(lang);
        if (langValue != null) lang.add(get().getLanguageKey(), langValue);
        else lang.add(GTCEu.MOD_ID, get().getLanguageKey(), FormattingUtil.toEnglishName(get().name));
    }

    @Override
    public GTRecipeCategory createObject() {
        return GTRecipeCategories.register(name, recipeType)
                .setIcon(icon)
                .setXEIVisible(isXEIVisible);
    }

    public GTRecipeCategoryBuilder setCustomIcon(ResourceLocation location) {
        this.icon = new ResourceTexture(location.withPrefix("textures/").withSuffix(".png"));
        return this;
    }

    public GTRecipeCategoryBuilder setItemIcon(ItemStack... stacks) {
        this.icon = new ItemStackTexture(stacks);
        return this;
    }
}
