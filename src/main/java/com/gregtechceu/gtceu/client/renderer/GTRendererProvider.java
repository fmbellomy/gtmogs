package com.gregtechceu.gtceu.client.renderer;

import com.lowdragmc.lowdraglib.client.renderer.ATESRRendererProvider;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;


@OnlyIn(Dist.CLIENT)
public class GTRendererProvider extends ATESRRendererProvider<BlockEntity> {

    private static GTRendererProvider INSTANCE;

    private GTRendererProvider(BlockEntityRendererProvider.Context context) {
        // ModelBellows.INSTANCE = new ModelBellows(context);
        // ModelHungryChest.INSTANCE = new ModelHungryChest(context);
    }

    public static GTRendererProvider getOrCreate(BlockEntityRendererProvider.Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GTRendererProvider(context);
        }
        return INSTANCE;
    }

    @Nullable
    public static GTRendererProvider getInstance() {
        return INSTANCE;
    }
}
