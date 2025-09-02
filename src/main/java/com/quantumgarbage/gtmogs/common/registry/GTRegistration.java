package com.quantumgarbage.gtmogs.common.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.registrate.GTRegistrate;

public class GTRegistration {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GTMOGS.MOD_ID);
    static {
        GTRegistration.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    private GTRegistration() {/**/}
}
