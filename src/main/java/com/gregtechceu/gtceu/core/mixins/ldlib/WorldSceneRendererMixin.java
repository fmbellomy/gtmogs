package com.gregtechceu.gtceu.core.mixins.ldlib;

import com.lowdragmc.lowdraglib.client.scene.WorldSceneRenderer;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorldSceneRenderer.class, remap = false)
public class WorldSceneRendererMixin {

    @SuppressWarnings("ConstantValue")
    @ModifyExpressionValue(method = "renderBlocksForge",
                           at = {
                                   @At(value = "FIELD",
                                       target = "Lnet/neoforged/neoforge/client/model/data/ModelData;EMPTY:Lnet/neoforged/neoforge/client/model/data/ModelData;"),
                                   @At(value = "INVOKE",
                                       target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getModelData()Lnet/neoforged/neoforge/client/model/data/ModelData;")
                           })
    private static ModelData gtceu$getActualModelData(ModelData originalModelData,
                                                      BlockRenderDispatcher blockRenderDispatcher,
                                                      BlockState state, BlockPos pos, BlockAndTintGetter level) {
        ModelData newModelData = level.getModelData(pos);
        return newModelData != null ? newModelData : originalModelData;
    }
}
