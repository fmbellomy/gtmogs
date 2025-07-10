package com.gregtechceu.gtceu.integration.cctweaked;

import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.integration.cctweaked.peripherals.*;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.ForgeComputerCraftAPI;

public class CCTweakedPlugin {

    public static void init() {
        ComputerCraftAPI.registerGenericSource(new ControllablePeripheral());
        ComputerCraftAPI.registerGenericSource(new EnergyInfoPeripheral());
        ComputerCraftAPI.registerGenericSource(new WorkablePeripheral());
        ForgeComputerCraftAPI.registerGenericCapability(GTCapability.CAPABILITY_CONTROLLABLE);
        ForgeComputerCraftAPI.registerGenericCapability(GTCapability.CAPABILITY_ENERGY_INFO_PROVIDER);
        ForgeComputerCraftAPI.registerGenericCapability(GTCapability.CAPABILITY_WORKABLE);
    }
}
