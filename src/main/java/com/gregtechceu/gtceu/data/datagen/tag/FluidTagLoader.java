package com.gregtechceu.gtceu.data.datagen.tag;

import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.world.level.material.Fluid;

import com.tterrag.registrate.providers.RegistrateTagsProvider;

public class FluidTagLoader {

    public static void init(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
        provider.addTag(CustomTags.LIGHTER_FLUIDS).add(GTMaterials.Butane.getFluid(), GTMaterials.Propane.getFluid());
    }
}
