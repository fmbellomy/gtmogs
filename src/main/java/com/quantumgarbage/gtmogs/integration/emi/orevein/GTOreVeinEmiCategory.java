package com.quantumgarbage.gtmogs.integration.emi.orevein;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.GTRegistries;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

public class GTOreVeinEmiCategory extends EmiRecipeCategory {

    public static final GTOreVeinEmiCategory CATEGORY = new GTOreVeinEmiCategory();

    public GTOreVeinEmiCategory() {
        super(GTMOGS.id("ore_vein_diagram"), EmiStack.of(Items.RAW_IRON));
    }

    public static void registerDisplays(EmiRegistry registry) {
        var fluids = Minecraft.getInstance().level.registryAccess()
                .registryOrThrow(GTRegistries.ORE_VEIN_REGISTRY);
        fluids.holders()
                .filter(ore -> ore.value().canGenerate())
                .forEach(ore -> registry.addRecipe(new GTEmiOreVein(ore)));
    }

    @Override
    public Component getName() {
        return Component.translatable("gtmogs.jei.ore_vein_diagram");
    }
}
