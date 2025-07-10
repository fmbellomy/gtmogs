package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GrassPathBehavior implements IToolBehavior<GrassPathBehavior> {

    public static final GrassPathBehavior INSTANCE = new GrassPathBehavior();
    public static final Codec<GrassPathBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, GrassPathBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected GrassPathBehavior() {/**/}

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return action == ItemAbilities.SHOVEL_FLATTEN;
    }

    @NotNull
    @Override
    public InteractionResult onItemUse(UseOnContext context) {
        if (context.getClickedFace() == Direction.DOWN) return InteractionResult.PASS;

        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        ItemStack stack = context.getItemInHand();
        AoESymmetrical aoeDefinition = ToolHelper.getAoEDefinition(stack);

        List<BlockPos> blocks;
        // only attempt to till if the center block is tillable
        if (level.isEmptyBlock(pos.above()) && isBlockPathConvertible(context)) {
            if (aoeDefinition.isZero()) {
                blocks = List.of(pos);
            } else {
                blocks = getPathConvertibleBlocks(aoeDefinition, context);
                blocks.addFirst(context.getClickedPos());
            }
        } else {
            return InteractionResult.PASS;
        }

        boolean pathed = false;
        for (BlockPos blockPos : blocks) {
            UseOnContext posContext = new UseOnContext(level, player, hand, stack,
                    context.getHitResult().withPosition(blockPos));
            BlockState newState = getFlattened(level.getBlockState(blockPos), posContext);
            if (newState == null) continue;
            pathed |= level.setBlock(blockPos, newState, Block.UPDATE_ALL_IMMEDIATE);

            ToolHelper.damageItem(stack, player);
            if (stack.isEmpty()) break;
        }

        if (pathed) {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static List<BlockPos> getPathConvertibleBlocks(AoESymmetrical aoeDefinition, UseOnContext context) {
        return ToolHelper.iterateAoE(aoeDefinition, GrassPathBehavior::isBlockPathConvertible, context);
    }

    protected static boolean isBlockPathConvertible(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.isEmptyBlock(pos.above())) {
            BlockState state = level.getBlockState(pos);
            BlockState newState = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, true);
            return newState != null && newState != state;
        }
        return false;
    }

    protected static BlockState getFlattened(BlockState state, UseOnContext context) {
        return state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext Level, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.grass_path"));
    }

    @Override
    public ToolBehaviorType<GrassPathBehavior> getType() {
        return GTToolBehaviors.PATH;
    }
}
