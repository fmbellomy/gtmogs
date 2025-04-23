package com.gregtechceu.gtceu.core.mixins;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.GTRegistry;
import com.gregtechceu.gtceu.core.mixins.neoforge.BaseMappedRegistryAccessor;
import com.gregtechceu.gtceu.core.mixins.neoforge.RegistryBuilderAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistryDataLoader.RegistryData.class)
public class RegistryDataMixin<T> {

    @SuppressWarnings({ "UnstableApiUsage", "unchecked" })
    @ModifyExpressionValue(method = "create", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/registries/RegistryBuilder;create()Lnet/minecraft/core/Registry;"))
    private Registry<T> gtceu$ReplaceRegistryType(Registry<T> original, @Local RegistryBuilder<T> builder) {
        if (builder instanceof RegistryBuilderAccessor<?> accessor &&
                original.key().location().getNamespace().equals(GTCEu.MOD_ID)) {
            // if it's our registry, remake it with our registry class.
            GTRegistry<T> patched = new GTRegistry<>(((RegistryBuilderAccessor<T>) builder).getRegistryKey(),
                    Lifecycle.stable(), accessor.isIntrusiveHolders());

            ((RegistryBuilderAccessor<T>) builder).getCallbacks().forEach(patched::addCallback);
            if (accessor.getMaxId() != -1)
                ((BaseMappedRegistryAccessor<T>) patched).callSetMaxId(accessor.getMaxId());
            ((BaseMappedRegistryAccessor<T>) patched).callSetSync(accessor.isSync());

            return patched;
        }
        return original;
    }
}
