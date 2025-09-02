package com.quantumgarbage.gtmogs.integration.kjs.events;

import com.quantumgarbage.gtmogs.api.worldgen.SimpleWorldGenLayer;
import com.quantumgarbage.gtmogs.integration.kjs.builders.worldgen.WorldGenLayerBuilder;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;

import java.util.function.Consumer;

public class WorldGenLayerKubeEvent implements KubeEvent {

    @Info("Create a new material icon set with the default parent.")
    public SimpleWorldGenLayer create(String name, Consumer<WorldGenLayerBuilder> consumer) {
        WorldGenLayerBuilder builder = new WorldGenLayerBuilder(name);
        consumer.accept(builder);
        return builder.build();
    }
}
