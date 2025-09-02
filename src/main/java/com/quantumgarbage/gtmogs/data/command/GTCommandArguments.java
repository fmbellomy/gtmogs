package com.quantumgarbage.gtmogs.data.command;

import com.quantumgarbage.gtmogs.GTMOGS;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("unused")
public class GTCommandArguments {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister
            .create(Registries.COMMAND_ARGUMENT_TYPE, GTMOGS.MOD_ID);

    @SuppressWarnings({ "SameParameterValue", "unchecked" })
    private static <T extends ArgumentType<?>> Class<T> fixClassType(Class<? super T> type) {
        return (Class<T>) type;
    }

    public static void init(IEventBus modBus) {
        COMMAND_ARGUMENT_TYPES.register(modBus);
    }
}
