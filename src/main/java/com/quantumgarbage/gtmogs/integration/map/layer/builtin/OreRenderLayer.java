package com.quantumgarbage.gtmogs.integration.map.layer.builtin;

import com.quantumgarbage.gtmogs.api.worldgen.ores.GeneratedVeinMetadata;
import com.quantumgarbage.gtmogs.config.ConfigHolder;

import com.quantumgarbage.gtmogs.integration.map.GenericMapRenderer;
import com.quantumgarbage.gtmogs.integration.map.layer.MapRenderLayer;
import com.quantumgarbage.gtmogs.integration.xei.widgets.GTOreVeinWidget;

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
            return Component.translatable("gtmogs.minimap.ore_vein.depleted");
        }
        return Component.translatable(GTOreVeinWidget.getOreName(vein.definition()));
    }


    public static List<Component> getTooltip(String name, GeneratedVeinMetadata vein) {
        final List<Component> tooltip = new ArrayList<>();
        var title = Component.literal(name);
        if (vein.depleted()) {
            title.append(" (").append(Component.translatable("gtmogs.minimap.ore_vein.depleted")).append(")");
        }
        tooltip.add(title);

        for (var filler : vein.definition().value().veinGenerator().getAllEntries()) {
                tooltip.add(Component.literal(ConfigHolder.INSTANCE.compat.minimap.oreNamePrefix)
                        .append(filler.vein().getBlock().getName()));
        }
        return tooltip;
    }
}
