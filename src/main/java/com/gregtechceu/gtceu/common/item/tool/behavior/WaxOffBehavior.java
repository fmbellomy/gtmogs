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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WaxOffBehavior implements IToolBehavior<WaxOffBehavior> {

    public static final WaxOffBehavior INSTANCE = new WaxOffBehavior();
    public static final Codec<WaxOffBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, WaxOffBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected WaxOffBehavior() {/**/}

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return action == ItemAbilities.AXE_WAX_OFF;
    }

    @NotNull
    @Override
    public InteractionResult onItemUse(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        AoESymmetrical aoeDefinition = ToolHelper.getAoEDefinition(stack);

        List<BlockPos> blocks;
        // only attempt to strip if the center block is strippable
        if (isBlockUnwaxable(context)) {
            if (aoeDefinition.isZero()) {
                blocks = List.of(pos);
            } else {
                blocks = getUnwaxableBlocks(aoeDefinition, context);
                blocks.addFirst(context.getClickedPos());
            }
        } else {
            return InteractionResult.PASS;
        }

        boolean unwaxed = false;
        for (BlockPos blockPos : blocks) {
            UseOnContext posContext = new UseOnContext(level, player, context.getHand(), stack,
                    context.getHitResult().withPosition(blockPos));
            BlockState newState = getUnwaxed(level.getBlockState(blockPos), posContext);
            unwaxed |= level.setBlock(blockPos, newState, Block.UPDATE_ALL_IMMEDIATE);
            level.levelEvent(player, LevelEvent.PARTICLES_WAX_OFF, blockPos, 0);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

            ToolHelper.damageItem(stack, player);
            if (stack.isEmpty()) break;
        }

        if (unwaxed) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static List<BlockPos> getUnwaxableBlocks(AoESymmetrical aoeDefinition, UseOnContext context) {
        return ToolHelper.iterateAoE(aoeDefinition, WaxOffBehavior::isBlockUnwaxable, context);
    }

    protected static boolean isBlockUnwaxable(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        BlockState newState = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, true);
        return newState != null && newState != state;
    }

    protected BlockState getUnwaxed(BlockState state, UseOnContext context) {
        return state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext Level, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.remove_wax"));
    }

    @Override
    public ToolBehaviorType<WaxOffBehavior> getType() {
        return GTToolBehaviors.WAX_OFF;
    }
}
