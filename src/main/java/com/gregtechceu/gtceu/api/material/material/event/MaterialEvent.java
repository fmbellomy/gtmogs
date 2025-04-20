package com.gregtechceu.gtceu.api.material.material.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * Event to register and modify materials in
 * <br/>
 * Material events are fired on the MOD bus as the forge bus isn't active until all mods have loaded.
 * @deprecated use a {@link net.neoforged.neoforge.registries.RegisterEvent} instead.
 */
@Deprecated(forRemoval = true)
public class MaterialEvent extends Event implements IModBusEvent {

    public MaterialEvent() {
        super();
    }
}
