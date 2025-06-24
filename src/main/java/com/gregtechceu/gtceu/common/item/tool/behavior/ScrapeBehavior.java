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
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ScrapeBehavior implements IToolBehavior<ScrapeBehavior> {

    public static final ScrapeBehavior INSTANCE = create();
    public static final Codec<ScrapeBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, ScrapeBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected ScrapeBehavior() {/**/}

    protected static ScrapeBehavior create() {
        return new ScrapeBehavior();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return action == ItemAbilities.AXE_SCRAPE;
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

        // only attempt to strip if the center block is strippable
        if (!isBlockScrapable(stack, level, player, pos, context)) {
            return InteractionResult.PASS;
        }

        Set<BlockPos> blocks;
        if (aoeDefinition.isNone() || player == null) {
            blocks = ImmutableSet.of(pos);
        } else {
            blocks = getScrapableBlocks(stack, aoeDefinition, level, player, context.getHitResult());
            blocks.add(pos);
        }

        boolean scraped = false;
        for (BlockPos blockPos : blocks) {
            UseOnContext newCtx = new UseOnContext(level, player, hand, stack,
                    context.getHitResult().withPosition(blockPos));
            scraped |= level.setBlock(blockPos, getScraped(level.getBlockState(blockPos), newCtx),
                    Block.UPDATE_ALL_IMMEDIATE);
            level.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, blockPos, 0);

            ToolHelper.damageItem(stack, player);
            if (stack.isEmpty())
                break;
        }

        if (scraped) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    public static Set<BlockPos> getScrapableBlocks(ItemStack stack, AoESymmetrical aoeDefinition, Level Level,
                                                   Player player, HitResult hitResult) {
        return ToolHelper.iterateAoE(stack, aoeDefinition, Level, player, hitResult,
                ScrapeBehavior.INSTANCE::isBlockScrapable);
    }

    protected boolean isBlockScrapable(ItemStack stack, Level level, Player player, BlockPos pos,
                                       UseOnContext context) {
        BlockState state = level.getBlockState(pos);
        BlockState newState = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, true);
        return newState != null && newState != state;
    }

    protected BlockState getScraped(BlockState unscrapedState, UseOnContext context) {
        // just assume it exists.
        BlockState newState = unscrapedState.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
        return newState != null ? newState : unscrapedState;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext Level, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.scrape"));
    }

    @Override
    public ToolBehaviorType<ScrapeBehavior> getType() {
        return GTToolBehaviors.SCRAPE;
    }
}
