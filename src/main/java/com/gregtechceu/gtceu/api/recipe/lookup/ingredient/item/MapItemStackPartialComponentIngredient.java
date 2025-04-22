package com.gregtechceu.gtceu.api.recipe.lookup.ingredient.item;
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java

import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
========
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java

import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java
import net.minecraft.world.item.crafting.Ingredient;
========
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java
public class MapItemStackWeakDataComponentIngredient extends MapItemStackIngredient {

    Ingredient nbtIngredient;

    public MapItemStackWeakDataComponentIngredient(ItemStack stack, Ingredient nbtIngredient) {
        super(stack, nbtIngredient);
========
public class MapItemStackPartialComponentIngredient extends MapItemStackIngredient {

    private final DataComponentIngredient nbtIngredient;

    public MapItemStackPartialComponentIngredient(ItemStack stack, DataComponentIngredient nbtIngredient) {
        super(stack, nbtIngredient.toVanilla());
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java
        this.nbtIngredient = nbtIngredient;
    }

    @NotNull
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java
    public static List<AbstractMapIngredient> from(@NotNull Ingredient r) {
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        for (ItemStack s : r.getItems()) {
            list.add(new MapItemStackWeakDataComponentIngredient(s, r));
        }
========
    public static List<AbstractMapIngredient> from(@NotNull DataComponentIngredient r) {
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        r.getItems().forEach(s -> list.add(new MapItemStackPartialComponentIngredient(s, r)));
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java
        if (obj instanceof MapItemStackWeakDataComponentIngredient other) {
========
        if (obj instanceof MapItemStackPartialComponentIngredient other) {
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java
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
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackWeakDataComponentIngredient.java
        return "MapItemStackWeakDataComponentIngredient{" + "item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) +
                "}";
========
        return "MapItemStackPartialComponentIngredient{" + "item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "}";
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/api/recipe/lookup/ingredient/item/MapItemStackPartialComponentIngredient.java
    }

    @Override
    public boolean isSpecialIngredient() {
        return true;
    }
}
