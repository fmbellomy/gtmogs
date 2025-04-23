package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.gregtechceu.gtceu.client.renderer.machine.OverlayEnergyIORenderer.ENERGY_OUT_1A;

public class SimpleGeneratorMachineRenderer extends WorkableTieredHullMachineRenderer {

    public SimpleGeneratorMachineRenderer(int tier, ResourceLocation workableModel) {
        super(tier, workableModel);
    }

    @Override
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine,
                              Direction frontFacing, @Nullable Direction side, @NotNull RandomSource rand,
                              Direction modelFacing,
                              ModelState modelState, @NotNull ModelData data, RenderType renderType) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState, data,
                renderType);
        if (side == frontFacing && modelFacing != null) {
            ENERGY_OUT_1A.renderOverlay(quads, modelFacing, modelState, 2);
        }
    }
}
