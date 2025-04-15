package com.gregtechceu.gtceu.data.particle;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.particle.HazardParticleOptions;
import com.gregtechceu.gtceu.common.particle.MufflerParticleOptions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
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
    public static final DeferredHolder<ParticleType<?>, ParticleType<MufflerParticleOptions>> MUFFLER_PARTICLE = PARTICLE_TYPES
            .register("muffler", () -> new ParticleType<>(false, MufflerParticleOptions.DESERIALIZER) {

                @Override
                public MapCodec<MufflerParticleOptions> codec() {
                    return MufflerParticleOptions.CODEC;
                }
            });

    public static void init(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
