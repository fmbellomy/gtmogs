package com.gregtechceu.gtceu.core.mixins.ldlib;

import com.lowdragmc.lowdraglib.utils.DummyWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelDataManager;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.common.extensions.ILevelExtension;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DummyWorld.class, remap = false)
public abstract class DummyWorldMixin implements ILevelExtension, IBlockGetterExtension {

    @Shadow
    public abstract @NotNull Level getLevel();

    @Override
    public @Nullable ModelDataManager getModelDataManager() {
        return getLevel().getModelDataManager();
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockPos pos) {
        return getLevel().getModelData(pos);
    }
}
