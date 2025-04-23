package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Used to allow a tool to hoe the ground
 */
public class HoeGroundBehavior implements IToolBehavior<HoeGroundBehavior> {

    public static final HoeGroundBehavior INSTANCE = new HoeGroundBehavior();
    public static final Codec<HoeGroundBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, HoeGroundBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected HoeGroundBehavior() {/**/}

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return action == ItemAbilities.HOE_TILL;
    }

    @NotNull
    @Override
    public InteractionResult onItemUse(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        ItemStack stack = context.getItemInHand();
        AoESymmetrical aoeDefinition = ToolHelper.getAoEDefinition(stack);

        // only attempt to till if the center block is tillable
        if (!isBlockTillable(stack, level, player, pos, context)) {
            return InteractionResult.PASS;
        }

        Set<BlockPos> blocks;
        if (aoeDefinition.isNone() || player == null) {
            blocks = ImmutableSet.of(pos);
        } else {
            blocks = getTillableBlocks(stack, aoeDefinition, level, player, context.getHitResult());
            blocks.add(pos);
        }

        boolean tilled = false;
        for (BlockPos blockPos : blocks) {
            BlockState state = level.getBlockState(blockPos);
            UseOnContext newCtx = new UseOnContext(level, player, hand, stack,
                    context.getHitResult().withPosition(blockPos));
            boolean didTill = tillGround(newCtx, state);
            tilled |= didTill;
            if (didTill && player != null) {
                ToolHelper.damageItem(context.getItemInHand(), context.getPlayer());
            }
            if (stack.isEmpty())
                break;
        }

        if (tilled) {
            level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    public static Set<BlockPos> getTillableBlocks(ItemStack stack, AoESymmetrical aoeDefinition, Level world,
                                                  Player player, HitResult rayTraceResult) {
        return ToolHelper.iterateAoE(stack, aoeDefinition, world, player, rayTraceResult,
                HoeGroundBehavior.INSTANCE::isBlockTillable);
    }

    protected boolean isBlockTillable(ItemStack stack, Level world, Player player, BlockPos pos, UseOnContext context) {
        BlockState state = world.getBlockState(pos);
        BlockState newState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, true);
        return newState != null && newState != state;
    }

    protected boolean tillGround(UseOnContext context, BlockState state) {
        BlockState newState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (newState != null && newState != state) {
            boolean result = context.getLevel().setBlock(context.getClickedPos(), newState, Block.UPDATE_ALL_IMMEDIATE);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(),
                    GameEvent.Context.of(context.getPlayer(), state));
            return result;
        }
        return false;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.ground_tilling"));
    }

    @Override
    public ToolBehaviorType<HoeGroundBehavior> getType() {
        return GTToolBehaviors.HOE_GROUND;
    }
}
