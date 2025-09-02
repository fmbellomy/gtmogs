package com.quantumgarbage.gtmogs.data.valueprovider;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.quantumgarbage.gtmogs.GTMOGS;

public class GTValueProviderTypes {

    public static final DeferredRegister<IntProviderType<?>> INT_PROVIDER_TYPE_REGISTER = DeferredRegister.create(
            Registries.INT_PROVIDER_TYPE,
            GTMOGS.MOD_ID);
    public static final DeferredRegister<FloatProviderType<?>> FLOAT_PROVIDER_TYPE_REGISTER = DeferredRegister.create(
            Registries.FLOAT_PROVIDER_TYPE,
            GTMOGS.MOD_ID);

    public static void init(IEventBus bus) {
        INT_PROVIDER_TYPE_REGISTER.register(bus);
        FLOAT_PROVIDER_TYPE_REGISTER.register(bus);
    }
}
