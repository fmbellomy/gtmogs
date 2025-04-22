package com.gregtechceu.gtceu.core.mixins.emi;

import com.gregtechceu.gtceu.api.item.IGTTool;

import net.minecraft.world.item.ItemStack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.network.FillRecipeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FillRecipeC2SPacket.class, remap = false)
public abstract class FillRecipePacketMixin {

    @WrapOperation(method = "grabMatching",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
                            remap = true))
    private static boolean gtceu$toolBypass(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        if (stack.getItem() instanceof IGTTool) {
            return ItemStack.isSameItem(stack, other);
        }
        return original.call(stack, other);
    }
}
