package com.gregtechceu.gtceu.common.recipe.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Accessors(chain = true, fluent = true)
public class SmokingRecipeBuilder {

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

    public SmokingRecipeBuilder(@Nullable ResourceLocation id) {
        this.id = id;
    }

    public SmokingRecipeBuilder input(TagKey<Item> itemStack) {
        return input(Ingredient.of(itemStack));
    }

    public SmokingRecipeBuilder input(ItemStack itemStack) {
        if (!itemStack.getComponentsPatch().isEmpty()) {
            input = DataComponentIngredient.of(true, itemStack);
        } else {
            input = Ingredient.of(itemStack);
        }
        return this;
    }

    public SmokingRecipeBuilder input(ItemLike itemLike) {
        return input(Ingredient.of(itemLike));
    }

    public SmokingRecipeBuilder input(Ingredient ingredient) {
        input = ingredient;
        return this;
    }

    public SmokingRecipeBuilder output(ItemStack itemStack) {
        this.output = itemStack.copy();
        return this;
    }

    public SmokingRecipeBuilder output(ItemStack itemStack, int count) {
        this.output = itemStack.copy();
        this.output.setCount(count);
        return this;
    }

    protected ResourceLocation defaultId() {
        return BuiltInRegistries.ITEM.getKey(output.getItem());
    }

    private SmokingRecipe create() {
        return new SmokingRecipe(Objects.requireNonNullElse(this.group, ""), this.category, this.input, this.output,
                this.experience, this.cookingTime);
    }

    public void save(RecipeOutput consumer) {
        var recipeId = id == null ? defaultId() : id;
        consumer.accept(recipeId.withPrefix("smoking/"), create(), null);
    }
}
