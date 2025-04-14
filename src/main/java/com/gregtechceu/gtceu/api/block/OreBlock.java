package com.gregtechceu.gtceu.api.block;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.client.renderer.block.OreBlockRenderer;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.map.cache.server.ServerCache;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OreBlock extends MaterialBlock {

    public OreBlock(Properties properties, TagPrefix tagPrefix, Material material, boolean registerModel) {
        super(properties, tagPrefix, material, false);
        if (registerModel && GTCEu.isClientSide()) {
            OreBlockRenderer.create(this);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                        Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ServerCache.instance.prospectByOreMaterial(
                    level.dimension(),
                    this.material,
                    pos,
                    (ServerPlayer) player,
                    ConfigHolder.INSTANCE.compat.minimap.oreBlockProspectRange);
        }
        return InteractionResult.PASS;
    }
}
