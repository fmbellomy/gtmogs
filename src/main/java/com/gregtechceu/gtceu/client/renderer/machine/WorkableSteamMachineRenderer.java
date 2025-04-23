package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IExhaustVentMachine;
import com.gregtechceu.gtceu.client.model.WorkableOverlayModel;

import com.lowdragmc.lowdraglib.client.bakedpipeline.FaceQuad;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class WorkableSteamMachineRenderer extends SteamHullMachineRenderer {

    public static final ResourceLocation VENT_OVERLAY = GTCEu.id("block/overlay/machine/overlay_steam_vent");
    protected final WorkableOverlayModel overlayModel;

    public WorkableSteamMachineRenderer(boolean isHighTier, ResourceLocation overlay) {
        super(isHighTier, GTCEu.id("block/machine/hull_machine"));
        this.overlayModel = new WorkableOverlayModel(overlay);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine,
                              Direction frontFacing, @Nullable Direction side, @NotNull RandomSource rand,
                              Direction modelFacing,
                              ModelState modelState, @NotNull ModelData data, RenderType renderType) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState, data,
                renderType);
        if (machine instanceof IWorkable workable) {
            quads.addAll(overlayModel.bakeQuads(side, modelState, workable.isActive(),
                    workable.isWorkingEnabled()));
        } else {
            quads.addAll(overlayModel.bakeQuads(side, modelState, false, false));
        }
        if (machine instanceof IExhaustVentMachine exhaustVentMachine) {
            if (side != null && exhaustVentMachine.getVentingDirection() == side && modelFacing != null) {
                quads.add(
                        FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(VENT_OVERLAY), modelState));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        super.onPrepareTextureAtlas(atlasName, register);
        if (atlasName.equals(TextureAtlas.LOCATION_BLOCKS)) {
            overlayModel.registerTextureAtlas(register);
            register.accept(VENT_OVERLAY);
        }
    }
}
