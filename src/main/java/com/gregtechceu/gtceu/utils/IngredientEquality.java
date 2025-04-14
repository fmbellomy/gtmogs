package com.gregtechceu.gtceu.utils;

import com.gregtechceu.gtceu.core.mixins.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.PartialNBTIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.crafting.StrictNBTIngredient;

import com.google.common.collect.Lists;

import java.util.*;

public class IngredientEquality {

    public static final Comparator<ItemStack> STACK_COMPARATOR = Comparator
            .comparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()));

    public static final Comparator<Ingredient.Value> INGREDIENT_VALUE_COMPARATOR = new Comparator<>() {

        @Override
        public int compare(Ingredient.Value value1, Ingredient.Value value2) {
            if (value1 instanceof Ingredient.TagValue tagValue) {
                if (!(value2 instanceof Ingredient.TagValue tagValue1)) {
                    return 1;
                }
                if (((TagValueAccessor) tagValue).getTag() != ((TagValueAccessor) tagValue1).getTag()) {
                    return 1;
                }
            } else if (value1 instanceof Ingredient.ItemValue) {
                if (!(value2 instanceof Ingredient.ItemValue)) {
                    return 1;
                }
                for (ItemStack item1 : value1.getItems()) {
                    for (ItemStack item2 : value2.getItems()) {
                        int result = STACK_COMPARATOR.compare(item1, item2);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            }
            return 0;
        }
    };

    public static final Comparator<SizedIngredient> INGREDIENT_COMPARATOR = new Comparator<>() {

        @Override
        public int compare(SizedIngredient first, SizedIngredient second) {
            if (first.get instanceof StrictNBTIngredient strict1) {
                if (second instanceof StrictNBTIngredientAccessor strict2) {
                    return strict1.test(strict2.getStack()) ? 0 : 1;
                }
                return 1;
            }
            if (first instanceof PartialNBTIngredient partial1) {
                if (second instanceof PartialNBTIngredient partial2) {
                    if (partial1.getItems().length != partial2.getItems().length)
                        return 1;
                    for (ItemStack stack : partial1.getItems()) {
                        if (!partial2.test(stack)) {
                            return 1;
                        }
                    }
                    return 0;
                }
                return 1;
            }

            if (first instanceof IntersectionIngredient intersection1) {
                if (second instanceof IntersectionIngredient intersection2) {
                    List<Ingredient> ingredients1 = Lists
                            .newArrayList((intersection1.children()));
                    List<Ingredient> ingredients2 = Lists
                            .newArrayList(intersection2.children());
                    if (ingredients1.size() != ingredients2.size()) return 1;

                    ingredients1.sort(this);
                    ingredients2.sort(this);

                    for (int i = 0; i < ingredients1.size(); ++i) {
                        Ingredient ingredient1 = ingredients1.get(i);
                        Ingredient ingredient2 = ingredients2.get(i);
                        int result = compare(ingredient1, ingredient2);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
                return 1;
            }

            if (first.ingredient().getValues().length != second.ingredient().getValues().length)
                return 1;
            Ingredient.Value[] values1 = first.ingredient().getValues();
            Ingredient.Value[] values2 = second.ingredient().getValues();
            if (values1.length != values2.length) return 1;

            Arrays.parallelSort(values1, INGREDIENT_VALUE_COMPARATOR);
            Arrays.parallelSort(values2, INGREDIENT_VALUE_COMPARATOR);

            for (int i = 0; i < values1.length; ++i) {
                Ingredient.Value value1 = values1[i];
                Ingredient.Value value2 = values2[i];
                int result = INGREDIENT_VALUE_COMPARATOR.compare(value1, value2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    };

}
