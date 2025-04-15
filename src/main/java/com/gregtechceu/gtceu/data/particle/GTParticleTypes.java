package com.gregtechceu.gtceu.data.particle;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.particle.HazardParticleOptions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GTParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, GTCEu.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<HazardParticleOptions>> HAZARD_PARTICLE = PARTICLE_TYPES
            .register("hazard", () -> new ParticleType<>(false, HazardParticleOptions.DESERIALIZER) {

                @Override
                public MapCodec<HazardParticleOptions> codec() {
                    return HazardParticleOptions.CODEC;
                }
            });
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MUFFLER_PARTICLE = PARTICLE_TYPES
            .register("muffler", () -> new SimpleParticleType(false));

    public static void init(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
