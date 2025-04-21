package com.gregtechceu.gtceu.integration.rei.orevein;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.Holder;

import java.util.ArrayList;
import java.util.List;

public class GTBedrockOreDisplay extends ModularDisplay<WidgetGroup> {

    private final Holder<BedrockOreDefinition> bedrockOre;

    public GTBedrockOreDisplay(Holder<BedrockOreDefinition> bedrockOre) {
        super(() -> new GTOreVeinWidget(bedrockOre, null), GTBedrockOreDisplayCategory.CATEGORY);
        this.bedrockOre = bedrockOre;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> outputs = new ArrayList<>();
        for (Material material : bedrockOre.value().getAllMaterials()) {
            outputs.add(EntryIngredients.of(ChemicalHelper.get(TagPrefix.rawOre, material)));
        }
        return outputs;
    }
}
