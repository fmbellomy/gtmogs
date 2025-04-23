package com.gregtechceu.gtceu.core.mixins.neoforge;

import net.neoforged.neoforge.registries.BaseMappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = BaseMappedRegistry.class, remap = false)
public interface BaseMappedRegistryAccessor<T> {

    @Invoker
    void callSetSync(boolean sync);

    @Invoker
    void callSetMaxId(int maxId);
}
