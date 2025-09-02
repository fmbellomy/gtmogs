package com.quantumgarbage.gtmogs.integration.kjs;

import com.quantumgarbage.gtmogs.integration.kjs.events.*;

import com.quantumgarbage.gtmogs.integration.kjs.events.WorldGenLayerKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface GTCEuStartupEvents {

    EventGroup GROUP = EventGroup.of("GTCEuStartupEvents");

    EventHandler WORLD_GEN_LAYERS = GROUP.startup("worldGenLayers", () -> WorldGenLayerKubeEvent.class);
}
