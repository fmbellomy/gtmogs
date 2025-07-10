package com.gregtechceu.gtceu.core.mixins.neoforge;

import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BakedModelWrapper.class, remap = false)
public interface BakedModelWrapperAccessor<T extends BakedModel> {

    @Accessor("originalModel")
    T gtceu$getParent();
}
