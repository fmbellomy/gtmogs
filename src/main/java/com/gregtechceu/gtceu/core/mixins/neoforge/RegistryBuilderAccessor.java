package com.gregtechceu.gtceu.core.mixins.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RegistryBuilder.class, remap = false)
public interface RegistryBuilderAccessor<T> {

    @Accessor
    ResourceKey<? extends Registry<T>> getRegistryKey();

    @Accessor
    List<RegistryCallback<T>> getCallbacks();

    @Accessor
    boolean isIntrusiveHolders();

    @Accessor
    int getMaxId();

    @Accessor
    boolean isSync();

}
