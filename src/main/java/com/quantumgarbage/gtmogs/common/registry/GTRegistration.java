package com.quantumgarbage.gtmogs.common.registry;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class GTRegistration {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GTMOGS.MOD_ID);
    static {
        GTRegistration.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    private GTRegistration() {/**/}
}
