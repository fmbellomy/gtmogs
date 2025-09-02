package com.gregtechceu.gtceu.integration.map.layer.builtin;

import com.gregtechceu.gtceu.api.worldgen.ores.GeneratedVeinMetadata;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.gregtechceu.gtceu.integration.map.GenericMapRenderer;
import com.gregtechceu.gtceu.integration.map.layer.MapRenderLayer;
import com.gregtechceu.gtceu.integration.xei.widgets.GTOreVeinWidget;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


import java.util.ArrayList;
import java.util.List;


public class OreRenderLayer extends MapRenderLayer {

    public OreRenderLayer(String key, GenericMapRenderer renderer) {
        super(key, renderer);
    }

    public static String getId(GeneratedVeinMetadata vein) {
        BlockPos center = vein.center();
        return "ore_veins@[" + center.getX() + "," + center.getY() + "," + center.getZ() + "]";
    }

    public static MutableComponent getName(GeneratedVeinMetadata vein) {
        // noinspection ConstantValue IDK, it crashed
        if (vein == null || vein.definition() == null || vein.definition().unwrapKey().isEmpty()) {
            return Component.translatable("gtceu.minimap.ore_vein.depleted");
        }
        return Component.translatable(GTOreVeinWidget.getOreName(vein.definition()));
    }


    public static List<Component> getTooltip(String name, GeneratedVeinMetadata vein) {
        final List<Component> tooltip = new ArrayList<>();
        var title = Component.literal(name);
        if (vein.depleted()) {
            title.append(" (").append(Component.translatable("gtceu.minimap.ore_vein.depleted")).append(")");
        }
        tooltip.add(title);

        for (var filler : vein.definition().value().veinGenerator().getAllEntries()) {
                tooltip.add(Component.literal(ConfigHolder.INSTANCE.compat.minimap.oreNamePrefix)
                        .append(filler.vein().getBlock().getName()));
        }
        return tooltip;
    }
}
