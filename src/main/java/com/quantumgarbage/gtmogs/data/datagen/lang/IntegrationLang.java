package com.quantumgarbage.gtmogs.data.datagen.lang;



import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.data.worldgen.GTOreVeins;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class IntegrationLang {

    public static void init(RegistrateLangProvider provider) {
        initRecipeViewerLang(provider);
        initMinimapLang(provider);
        initOwnershipLang(provider);
    }

    /** JEI, REI, EMI */
    private static void initRecipeViewerLang(RegistrateLangProvider provider) {
        provider.add("gtceu.jei.ore_vein_diagram", "Ore Vein Diagram");
        provider.add("gtceu.jei.ore_vein_diagram.chance", "§eChance: %s§r");
        provider.add("gtceu.jei.ore_vein_diagram.spawn_range", "Spawn Range:");
        provider.add("gtceu.jei.ore_vein_diagram.weight", "Weight: %s");
        provider.add("gtceu.jei.ore_vein_diagram.dimensions", "Dimensions:");
        for (ResourceKey<OreVeinDefinition> key : GTOreVeins.ALL_KEYS) {
            ResourceLocation id = key.location();
            String name = id.getPath();
            provider.add(id.toLanguageKey("ore_vein"), RegistrateLangProvider.toEnglishName(name));
        }
    }

    private static void initMinimapLang(RegistrateLangProvider provider) {
        provider.add("gtceu.minimap.ore_vein.depleted", "Depleted");

        provider.add("message.gtceu.new_veins.amount", "Prospected %d new veins!");
        provider.add("message.gtceu.new_veins.name", "Prospected %s!");
        provider.add("button.gtceu.mark_as_depleted.name", "Mark as Depleted");
        provider.add("button.gtceu.toggle_waypoint.name", "Toggle Waypoint");

        provider.add("gtceu.journeymap.options.layers", "Prospection layers");
        provider.add("gtceu.journeymap.options.layers.ore_veins", "Show Ore Veins");
        provider.add("gtceu.journeymap.options.layers.bedrock_fluids", "Show Bedrock Fluid Veins");
        provider.add("gtceu.journeymap.options.layers.hide_depleted", "Hide Depleted Veins");
    }

    private static void initOwnershipLang(RegistrateLangProvider provider) {
        provider.add("gtceu.ownership.name.player", "Player");
        provider.add("gtceu.ownership.name.ftb", "FTB Teams");
        provider.add("gtceu.ownership.name.argonauts", "Argonauts Guild");
    }
}