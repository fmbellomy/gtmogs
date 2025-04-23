package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.client.model.WorkableOverlayModel;

import com.lowdragmc.lowdraglib.client.bakedpipeline.Quad;

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
import java.util.Map;
import java.util.function.Consumer;

public class WorkableCasingMachineRenderer extends MachineRenderer {

    protected final WorkableOverlayModel overlayModel;
    protected final ResourceLocation baseCasing;

    public WorkableCasingMachineRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        this(baseCasing, workableModel, true);
    }

    public WorkableCasingMachineRenderer(ResourceLocation baseCasing, ResourceLocation workableModel, boolean tint) {
        super(tint ? GTCEu.id("block/cube/tinted/all") : GTCEu.id("block/cube/all"));
        this.overlayModel = new WorkableOverlayModel(workableModel);
        this.baseCasing = baseCasing;
        setTextureOverride(Map.of("all", baseCasing));
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
            overlayModel.bakeQuads(side, modelState, workable.isActive(), workable.isWorkingEnabled())
                    .forEach(quad -> quads.add(Quad.from(quad, reBakeOverlayQuadsOffset()).rebake()));
        } else {
            overlayModel.bakeQuads(side, modelState, false, false)
                    .forEach(quad -> quads.add(Quad.from(quad, reBakeOverlayQuadsOffset()).rebake()));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        super.onPrepareTextureAtlas(atlasName, register);
        if (atlasName.equals(TextureAtlas.LOCATION_BLOCKS)) {
            overlayModel.registerTextureAtlas(register);
        }
    }

    public float reBakeOverlayQuadsOffset() {
        return 0.004f;
    }
}
