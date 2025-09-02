package com.quantumgarbage.gtmogs.data.datagen;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.data.worldgen.*;

import com.quantumgarbage.gtmogs.data.worldgen.GTDensityFunctions;
import com.quantumgarbage.gtmogs.data.worldgen.GTOreVeins;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;


import java.util.Set;

@EventBusSubscriber(modid = GTMOGS.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class GTVanillaDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var registries = event.getLookupProvider();
        if (event.includeServer()) {
            var set = Set.of(GTMOGS.MOD_ID);
            DatapackBuiltinEntriesProvider provider = generator.addProvider(true, new DatapackBuiltinEntriesProvider(
                    packOutput, registries, new RegistrySetBuilder()
                    .add(Registries.DENSITY_FUNCTION, GTDensityFunctions::bootstrap)
                    .add(GTRegistries.ORE_VEIN_REGISTRY, GTOreVeins::bootstrap),
                    set));
        }
    }
}
