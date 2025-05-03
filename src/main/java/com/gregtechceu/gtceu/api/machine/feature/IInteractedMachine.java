package com.gregtechceu.gtceu.api.machine.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IInteractedMachine extends IMachineFeature {

    /**
     * Basically a hook from block
     * {@link net.minecraft.world.level.block.state.BlockBehaviour#useWithoutItem(BlockState, Level, BlockPos, Player, BlockHitResult)}
     * <br>
     * Right-Click
     */
    default InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                    BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    /**
     * Basically a hook from block
     * {@link net.minecraft.world.level.block.state.BlockBehaviour#useItemOn(ItemStack, BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)}
     * <br>
     * Right-Click
     */
    default ItemInteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                                Player player, InteractionHand hand,
                                                BlockHitResult hit) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Left-Click
     * 
     * @return cancel (true) / keep (false) further processing
     */
    default boolean onLeftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        return false;
    }
}
