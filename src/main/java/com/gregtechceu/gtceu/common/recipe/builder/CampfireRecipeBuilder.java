package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Accessors(chain = true, fluent = true)
public class CampfireRecipeBuilder {

    private Ingredient input;
    @Setter
    protected String group;
    @Setter
    protected CookingBookCategory category = CookingBookCategory.MISC;

    private ItemStack output = ItemStack.EMPTY;
    @Setter
    private float experience;
    @Setter
    private int cookingTime;
    @Setter
    protected ResourceLocation id;

    public CampfireRecipeBuilder(@Nullable ResourceLocation id) {
        this.id = id;
    }

    public CampfireRecipeBuilder input(TagKey<Item> itemStack) {
        return input(Ingredient.of(itemStack));
    }

    public CampfireRecipeBuilder input(ItemStack itemStack) {
        if (!itemStack.isComponentsPatchEmpty()) {
            input = DataComponentIngredient.of(true, itemStack);
        } else {
            input = Ingredient.of(itemStack);
        }
        return this;
    }

    public CampfireRecipeBuilder input(ItemLike itemLike) {
        return input(Ingredient.of(itemLike));
    }

    public CampfireRecipeBuilder input(Ingredient ingredient) {
        input = ingredient;
        return this;
    }

    public CampfireRecipeBuilder output(ItemStack itemStack) {
        this.output = itemStack.copy();
        return this;
    }

    public CampfireRecipeBuilder output(ItemStack itemStack, int count) {
        this.output = itemStack.copy();
        this.output.setCount(count);
        return this;
    }

    public CampfireRecipeBuilder output(ItemStack itemStack, int count, DataComponentPatch components) {
        this.output = itemStack.copy();
        this.output.setCount(count);
        this.output.applyComponents(components);
        return this;
    }

    protected ResourceLocation defaultId() {
        return BuiltInRegistries.ITEM.getKey(output.getItem());
    }

    private CampfireCookingRecipe create() {
        return new CampfireCookingRecipe(Objects.requireNonNullElse(this.group, ""), this.category, this.input, this.output,
                this.experience, this.cookingTime);
    }

    public void save(RecipeOutput consumer) {
        var recipeId = id == null ? defaultId() : id;
        consumer.accept(recipeId.withPrefix("campfire_cooking/"), create(), null);
    }
}
