package com.gregtechceu.gtceu.core.mixins.neoforge;

import net.neoforged.neoforge.client.model.generators.ModelBuilder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ModelBuilder.class, remap = false)
public interface ModelBuilderAccessor {

    @Accessor("textures")
    Map<String, String> gtceu$getTextures();
}
