package com.gregtechceu.gtceu.integration.rei.orevein;

import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class GTOreVeinDisplay extends ModularDisplay<WidgetGroup> {

    private final Holder<OreVeinDefinition> oreDefinition;

    public GTOreVeinDisplay(Holder<OreVeinDefinition> oreDefinition) {
        super(() -> new GTOreVeinWidget(oreDefinition), GTOreVeinDisplayCategory.CATEGORY);
        this.oreDefinition = oreDefinition;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> ingredients = new ArrayList<>();
        for (ItemStack output : GTOreVeinWidget.getContainedOresAndBlocks(oreDefinition.value())) {
            ingredients.add(EntryIngredients.of(output));
        }
        return ingredients;
    }
}
