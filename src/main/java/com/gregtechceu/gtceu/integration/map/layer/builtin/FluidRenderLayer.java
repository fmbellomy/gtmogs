package com.gregtechceu.gtceu.integration.map.layer.builtin;

import com.gregtechceu.gtceu.api.gui.misc.ProspectorMode;
import com.gregtechceu.gtceu.integration.map.GenericMapRenderer;
import com.gregtechceu.gtceu.integration.map.layer.MapRenderLayer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

public class FluidRenderLayer extends MapRenderLayer {

    public FluidRenderLayer(String key, GenericMapRenderer renderer) {
        super(key, renderer);
    }

    public static String getId(ProspectorMode.FluidInfo vein, ChunkPos pos) {
        return "bedrock_fluids@[" + pos.x + "," + pos.z + "]";
    }

    public static Component getName(ProspectorMode.FluidInfo entry) {
        FluidStack fluidStack = new FluidStack(entry.fluid(), entry.left());
        return fluidStack.getHoverName();
    }

    public static List<Component> getTooltip(ProspectorMode.FluidInfo entry) {
        FluidStack fluidStack = new FluidStack(entry.fluid(), entry.left());
        return Collections.singletonList(((MutableComponent) fluidStack.getHoverName())
                .append(" --- %s (%s%%)".formatted(entry.yield(), entry.left())));
    }
}
