package com.gregtechceu.gtceu.integration.jei.orevein;

import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;

public class GTOreVeinInfoWrapper extends ModularWrapper<GTOreVeinWidget> {

    public final OreVeinDefinition oreDefinition;

    public GTOreVeinInfoWrapper(OreVeinDefinition oreDefinition) {
        super(new GTOreVeinWidget(oreDefinition));
        this.oreDefinition = oreDefinition;
    }
}
