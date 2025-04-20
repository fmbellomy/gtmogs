package com.gregtechceu.gtceu.core.mixins.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RegisterEvent.class, remap = false)
public interface RegisterEventAccessor {

    // man why is it private let me hack it in peace
    @Invoker("<init>")
    static RegisterEvent create(ResourceKey<? extends Registry<?>> registryKey, Registry<?> registry) {
        throw new AssertionError();
    }
}
