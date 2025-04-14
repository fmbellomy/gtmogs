package com.gregtechceu.gtceu.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class AssemblyLineMachine extends WorkableElectricMultiblockMachine {

    @Accessors(fluent = true)
    @Getter
    @Persisted
    protected boolean allowCircuitSlots;

    public AssemblyLineMachine(IMachineBlockEntity holder, boolean allowCircuitSlots) {
        super(holder);
        this.allowCircuitSlots = allowCircuitSlots;
    }

    public AssemblyLineMachine(IMachineBlockEntity holder) {
        this(holder, false);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (!super.beforeWorking(recipe)) return false;

        var config = ConfigHolder.INSTANCE.machines;
        if (!config.orderedAssemblyLineItems && !config.orderedAssemblyLineFluids) return true;
        if (!checkItemInputs(recipe)) return false;

        if (!config.orderedAssemblyLineFluids) return true;
        return checkFluidInputs(recipe);
    }

    @Override
    public void onStructureFormed() {
        getDefinition().setPartSorter(Comparator.comparing(it -> multiblockPartSorter().apply(it.self().getPos())));
        super.onStructureFormed();
    }

    private Function<BlockPos, Integer> multiblockPartSorter() {
        return RelativeDirection.RIGHT.getSorter(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    private boolean checkItemInputs(@NotNull GTRecipe recipe) {
        var itemInputs = recipe.inputs.getOrDefault(ItemRecipeCapability.CAP, Collections.emptyList());
        if (itemInputs.isEmpty()) return true;
        int inputsSize = itemInputs.size();
        var itemHandlers = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP);
        if (itemHandlers.size() < inputsSize) return false;

        var itemInventory = itemHandlers.stream()
                .filter(IRecipeHandler::shouldSearchContent)
                .map(container -> container.getContents().stream()
                        .filter(ItemStack.class::isInstance)
                        .map(ItemStack.class::cast)
                        .filter(s -> !s.isEmpty())
                        .findFirst())
                .dropWhile(Optional::isEmpty)
                .limit(inputsSize)
                .map(o -> o.orElse(ItemStack.EMPTY))
                .toList();

        if (itemInventory.size() < inputsSize) return false;

        for (int i = 0; i < inputsSize; i++) {
            var itemStack = itemInventory.get(i);
            Ingredient recipeStack = ItemRecipeCapability.CAP.of(itemInputs.get(i).content);
            if (!recipeStack.test(itemStack)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkFluidInputs(@NotNull GTRecipe recipe) {
        var fluidInputs = recipe.inputs.getOrDefault(FluidRecipeCapability.CAP, Collections.emptyList());
        if (fluidInputs.isEmpty()) return true;
        int inputsSize = fluidInputs.size();
        var fluidHandlers = getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP);
        if (fluidHandlers.size() < inputsSize) return false;

        var fluidInventory = fluidHandlers.stream()
                .filter(IRecipeHandler::shouldSearchContent)
                .map(container -> container.getContents().stream()
                        .filter(FluidStack.class::isInstance)
                        .map(FluidStack.class::cast)
                        .filter(f -> !f.isEmpty())
                        .findFirst())
                .dropWhile(Optional::isEmpty)
                .limit(inputsSize)
                .map(o -> o.orElse(FluidStack.EMPTY))
                .toList();

        if (fluidInventory.size() < inputsSize) return false;

        for (int i = 0; i < inputsSize; i++) {
            var fluidStack = fluidInventory.get(i);
            FluidIngredient recipeStack = FluidRecipeCapability.CAP.of(fluidInputs.get(i).content);
            if (!recipeStack.test(fluidStack) || recipeStack.getAmount() > fluidStack.getAmount()) {
                return false;
            }
        }
        return true;
    }
}
