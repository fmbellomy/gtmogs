package com.quantumgarbage.gtmogs.integration.jei.orevein;

import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;

import net.minecraft.core.Holder;

public class GTOreVeinInfoWrapper extends ModularWrapper<GTOreVeinWidget> {

    public final Holder<OreVeinDefinition> oreDefinition;

    public GTOreVeinInfoWrapper(Holder<OreVeinDefinition> oreDefinition) {
        super(new GTOreVeinWidget(oreDefinition));
        this.oreDefinition = oreDefinition;
    }
}
