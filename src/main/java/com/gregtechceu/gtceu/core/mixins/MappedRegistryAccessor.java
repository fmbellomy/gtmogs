package com.gregtechceu.gtceu.core.mixins;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {

    @Accessor
    default boolean isFrozen() {
        throw new AssertionError();
    }

    @Accessor
    default ObjectList<Holder.Reference<T>> getById() {
        throw new AssertionError();
    }

    @Accessor
    default Reference2IntMap<T> getToId() {
        throw new AssertionError();
    }

    @Accessor
    default Map<ResourceLocation, Holder.Reference<T>> getByLocation() {
        throw new AssertionError();
    }

    @Accessor
    default Map<ResourceKey<T>, Holder.Reference<T>> getByKey() {
        throw new AssertionError();
    }

    @Accessor
    default Map<T, Holder.Reference<T>> getByValue() {
        throw new AssertionError();
    }

    @Accessor
    default Map<ResourceKey<T>, RegistrationInfo> getRegistrationInfos() {
        throw new AssertionError();
    }
}
