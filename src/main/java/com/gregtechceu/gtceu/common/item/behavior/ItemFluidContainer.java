package com.gregtechceu.gtceu.common.item.behavior;

import com.gregtechceu.gtceu.api.item.component.IRecipeRemainder;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class ItemFluidContainer implements IRecipeRemainder {

    @Override
    public ItemStack getRecipeRemained(ItemStack itemStack) {
        return FluidUtil.getFluidHandler(itemStack).map(handler -> {
            var drained = handler.drain(FluidType.BUCKET_VOLUME, FluidAction.SIMULATE);
            if (drained.getAmount() != FluidType.BUCKET_VOLUME) return ItemStack.EMPTY;
            handler.drain(FluidType.BUCKET_VOLUME, FluidAction.EXECUTE);
            var copy = handler.getContainer();
            copy.getComponentsPatch().forget(type -> true);
            return copy;
        }).orElse(itemStack);
    }
}
