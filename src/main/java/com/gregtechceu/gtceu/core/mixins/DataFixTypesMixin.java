package com.gregtechceu.gtceu.core.mixins;

import com.gregtechceu.gtceu.api.datafixer.DataFixesInternals;

import net.minecraft.util.datafix.DataFixTypes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The required {@code getExtensionInfo()} method is
 * injected with ASM in {@link GTMixinPlugin} because Mixin doesn't allow injecting non-private static methods.
 * @see IExtensibleEnum
 * @see GTMixinPlugin#postApply(String, ClassNode, String, IMixinInfo)
 */
@Mixin(DataFixTypes.class)
public class DataFixTypesMixin implements net.neoforged.fml.common.asm.enumextension.IExtensibleEnum {

    @Shadow
    @Final
    private DSL.TypeReference type;

    // ModifyReturnValue to inject our fixes *after* vanilla ones
    @ModifyReturnValue(method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
                       at = @At(value = "RETURN"))
    private <T> Dynamic<T> gtceu$injectDataFixers(Dynamic<T> value) {
        return DataFixesInternals.get().updateWithAllFixers(this.type, value);
    }
}
