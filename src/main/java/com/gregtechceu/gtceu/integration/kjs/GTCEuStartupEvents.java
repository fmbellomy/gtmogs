package com.gregtechceu.gtceu.integration.kjs;

import com.gregtechceu.gtceu.integration.kjs.events.*;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface GTCEuStartupEvents {

    EventGroup GROUP = EventGroup.of("GTCEuStartupEvents");

    EventHandler WORLD_GEN_LAYERS = GROUP.startup("worldGenLayers", () -> WorldGenLayerKubeEvent.class);
}
