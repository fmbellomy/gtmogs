package com.gregtechceu.gtceu.core.mixins;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceKey.class)
public interface ResourceKeyAccessor {

    @Invoker
    static <T> ResourceKey<T> callCreate(ResourceLocation registryName, ResourceLocation location) {
        throw new AssertionError();
    }
}
