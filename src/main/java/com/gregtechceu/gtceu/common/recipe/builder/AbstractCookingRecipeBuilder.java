package com.gregtechceu.gtceu.common.recipe.builder;

import com.gregtechceu.gtceu.api.recipe.ingredient.ExDataComponentIngredient;

import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Accessors(chain = true, fluent = true)
public abstract class AbstractCookingRecipeBuilder<T extends AbstractCookingRecipe,
        S extends AbstractCookingRecipeBuilder<T, S>> {

    private final RecipeConstructor<T> constructor;
    protected final String folder;

    protected Ingredient input;
    protected String group;
    protected CookingBookCategory category = CookingBookCategory.MISC;

    protected ItemStack output = ItemStack.EMPTY;
    protected float experience;
    protected int cookingTime;
    protected ResourceLocation id;

    public AbstractCookingRecipeBuilder(@Nullable ResourceLocation id, String folder,
                                        RecipeConstructor<T> constructor) {
        this.id = id;
        this.folder = folder;
        this.constructor = constructor;
    }

    public S id(ResourceLocation id) {
        this.id = id;
        return self();
    }

    public S group(String group) {
        this.group = group;
        return self();
    }

    public S category(CookingBookCategory category) {
        this.category = category;
        return self();
    }

    public S experience(float experience) {
        this.experience = experience;
        return self();
    }

    public S cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return self();
    }

    public S input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public S input(TagKey<Item> tag, DataComponentPredicate components, boolean strict) {
        return input(ExDataComponentIngredient.of(strict, components, tag));
    }

    public S input(ItemStack itemStack) {
        if (!itemStack.getComponentsPatch().isEmpty()) {
            input = DataComponentIngredient.of(true, itemStack);
        } else {
            input = Ingredient.of(itemStack);
        }
        return self();
    }

    public S input(ItemLike itemLike) {
        return input(Ingredient.of(itemLike));
    }

    public S input(Ingredient ingredient) {
        input = ingredient;
        return self();
    }

    public S output(ItemStack itemStack) {
        this.output = itemStack.copy();
        return self();
    }

    public S output(ItemStack itemStack, int count) {
        this.output = itemStack.copyWithCount(count);
        return self();
    }

    protected ResourceLocation defaultId() {
        return BuiltInRegistries.ITEM.getKey(output.getItem());
    }

    private T create() {
        return constructor.create(Objects.requireNonNullElse(this.group, ""), this.category, this.input, this.output,
                this.experience, this.cookingTime);
    }

    public void save(RecipeOutput consumer) {
        var recipeId = id == null ? defaultId() : id;
        consumer.accept(recipeId.withPrefix(folder + "/"), create(), null);
    }

    @SuppressWarnings("unchecked")
    public S self() {
        return (S) this;
    }

    @FunctionalInterface
    public interface RecipeConstructor<T extends AbstractCookingRecipe> {

        T create(String group, CookingBookCategory category,
                 Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
