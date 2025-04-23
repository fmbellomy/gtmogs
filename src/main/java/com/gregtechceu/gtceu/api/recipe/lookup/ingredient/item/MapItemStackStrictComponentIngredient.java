package com.gregtechceu.gtceu.api.recipe.lookup.ingredient.item;

import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapItemStackStrictComponentIngredient extends MapItemStackIngredient {

    protected DataComponentIngredient componentIngredient;

    public MapItemStackStrictComponentIngredient(ItemStack s, DataComponentIngredient componentIngredient) {
        super(s);
        this.componentIngredient = componentIngredient;
    }

    @NotNull
    public static List<AbstractMapIngredient> from(@NotNull DataComponentIngredient r) {
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        r.getItems().forEach(s -> list.add(new MapItemStackStrictComponentIngredient(s, r)));
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapItemStackStrictComponentIngredient other) {
            if (this.stack.getItem() != other.stack.getItem()) {
                return false;
            }
            if (this.componentIngredient != null) {
                if (other.componentIngredient != null) {
                    var stacks1 = this.componentIngredient.getItems().toArray(ItemStack[]::new);
                    var stacks2 = other.componentIngredient.getItems().toArray(ItemStack[]::new);
                    for (ItemStack stack1 : stacks1) {
                        for (ItemStack stack2 : stacks2) {
                            if (ItemStack.isSameItemSameComponents(stack1, stack2)) return true;
                        }
                    }
                } else {
                    return this.componentIngredient.test(other.stack);
                }
            }
            if (other.componentIngredient != null) {
                return other.componentIngredient.test(this.stack);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MapItemStackStrictComponentIngredient{" + "item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) +
                "}";
    }

    @Override
    public boolean isSpecialIngredient() {
        return true;
    }
}
