package com.gregtechceu.gtceu.api.recipe.lookup.ingredient.item;

import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapItemStackPartialComponentIngredient extends MapItemStackIngredient {

    private final DataComponentIngredient nbtIngredient;

    public MapItemStackPartialComponentIngredient(ItemStack stack, DataComponentIngredient nbtIngredient) {
        super(stack, nbtIngredient.toVanilla());
        this.nbtIngredient = nbtIngredient;
    }

    @NotNull
    public static List<AbstractMapIngredient> from(@NotNull DataComponentIngredient r) {
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        r.getItems().forEach(s -> list.add(new MapItemStackPartialComponentIngredient(s, r)));
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapItemStackPartialComponentIngredient other) {
            if (this.stack.getItem() != other.stack.getItem()) {
                return false;
            }
            if (this.nbtIngredient != null) {
                if (other.nbtIngredient != null) {
                    var thisItems = this.nbtIngredient.getItems().toArray(ItemStack[]::new);
                    var otherItems = other.nbtIngredient.getItems().toArray(ItemStack[]::new);
                    if (thisItems.length != otherItems.length)
                        return false;
                    for (ItemStack stack : thisItems) {
                        if (!other.nbtIngredient.test(stack)) {
                            return false;
                        }
                    }
                    return true;
                }
            } else if (other.nbtIngredient != null) {
                return other.nbtIngredient.test(this.stack);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MapItemStackPartialComponentIngredient{" + "item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) +
                "}";
    }

    @Override
    public boolean isSpecialIngredient() {
        return true;
    }
}
