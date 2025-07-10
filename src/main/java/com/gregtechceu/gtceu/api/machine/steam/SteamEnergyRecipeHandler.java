package com.gregtechceu.gtceu.api.machine.steam;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.utils.GTMath;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteamEnergyRecipeHandler implements IRecipeHandler<EnergyStack> {

    private final NotifiableFluidTank steamTank;
    private final double conversionRate; // mB steam per EU

    public SteamEnergyRecipeHandler(NotifiableFluidTank steamTank, double conversionRate) {
        this.steamTank = steamTank;
        this.conversionRate = conversionRate;
    }

    @Override
    public List<EnergyStack> handleRecipeInner(IO io, GTRecipe recipe, List<EnergyStack> left, boolean simulate) {
        long eut = left.stream().reduce(EnergyStack.EMPTY, EnergyStack::sum).getTotalEU();
        int totalSteam = GTMath.saturatedCast((long) Math.ceil(eut * conversionRate));
        if (totalSteam > 0) {
            SizedFluidIngredient steam = io == IO.IN ?
                    SizedFluidIngredient.of(GTMaterials.Steam.getFluidTag(), totalSteam) :
                    SizedFluidIngredient.of(GTMaterials.Steam.getFluid(totalSteam));
            List<SizedFluidIngredient> list = new ArrayList<>();
            list.add(steam);
            var leftSteam = steamTank.handleRecipeInner(io, recipe, list, simulate);
            if (leftSteam == null || leftSteam.isEmpty()) return null;
            eut = (long) (leftSteam.getFirst().amount() / conversionRate);
        }
        return eut <= 0 ? null : Collections.singletonList(new EnergyStack(eut));
    }

    @Override
    public @NotNull List<Object> getContents() {
        List<FluidStack> tankContents = new ArrayList<>();
        for (int i = 0; i < steamTank.getTanks(); ++i) {
            FluidStack stack = steamTank.getFluidInTank(i);
            if (!stack.isEmpty()) {
                tankContents.add(stack);
            }
        }
        long sum = tankContents.stream().mapToLong(FluidStack::getAmount).sum();
        long realSum = (long) Math.ceil(sum * conversionRate);
        return List.of(realSum);
    }

    @Override
    public double getTotalContentAmount() {
        List<FluidStack> tankContents = new ArrayList<>();
        for (int i = 0; i < steamTank.getTanks(); ++i) {
            FluidStack stack = steamTank.getFluidInTank(i);
            if (!stack.isEmpty()) {
                tankContents.add(stack);
            }
        }
        long sum = tankContents.stream().mapToLong(FluidStack::getAmount).sum();
        return (long) Math.ceil(sum * conversionRate);
    }

    @Override
    public RecipeCapability<EnergyStack> getCapability() {
        return EURecipeCapability.CAP;
    }

    public long getCapacity() {
        return steamTank.getTankCapacity(0);
    }

    public long getStored() {
        FluidStack stack = steamTank.getFluidInTank(0);
        if (stack != FluidStack.EMPTY) {
            return stack.getAmount();
        }
        return 0;
    }
}
