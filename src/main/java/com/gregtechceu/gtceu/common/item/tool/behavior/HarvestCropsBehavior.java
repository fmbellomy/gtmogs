package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;

import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.item.GTItemAbilities;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;

import com.google.common.collect.ImmutableSet;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Set;

public class HarvestCropsBehavior implements IToolBehavior<HarvestCropsBehavior> {

    public static final HarvestCropsBehavior INSTANCE = new HarvestCropsBehavior();
    public static final Codec<HarvestCropsBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, HarvestCropsBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected HarvestCropsBehavior() {/**/}

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        return action == GTItemAbilities.HOE_HARVEST;
    }

    @NotNull
    @Override
    public InteractionResult onItemUse(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        ItemStack stack = context.getItemInHand();

        AoESymmetrical aoeDefinition = ToolHelper.getAoEDefinition(stack);

        Set<BlockPos> blocks;
        if (aoeDefinition.isNone() || player == null) {
            blocks = ImmutableSet.of(pos);
        } else {
            blocks = ToolHelper.iterateAoE(stack, aoeDefinition, level, player, context.getHitResult(),
                    HarvestCropsBehavior::isBlockCrops);
            blocks.add(pos);
        }

        boolean harvested = false;
        for (BlockPos blockPos : blocks) {
            if (harvestBlockRoutine(stack, blockPos, level, player)) {
                harvested = true;
            }
        }

        return harvested ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    private static boolean isBlockCrops(ItemStack stack, Level world, Player player, BlockPos pos,
                                        @Nullable UseOnContext context) {
        if (world.getBlockState(pos.above()).isAir()) {
            Block block = world.getBlockState(pos).getBlock();
            return block instanceof CropBlock;
        }
        return false;
    }

    private static boolean harvestBlockRoutine(ItemStack stack, BlockPos pos, Level level, @Nullable Player player) {
        var blockState = level.getBlockState(pos);
        var block = blockState.getBlock();
        var cropBlock = (CropBlock) block;
        final var seed = cropBlock.getCloneItemStack(level, pos, blockState).getItem();
        if (cropBlock.isMaxAge(blockState)) {
            var drops = Block.getDrops(blockState, (ServerLevel) level, pos, null);
            var iterator = drops.listIterator();
            while (iterator.hasNext()) {
                var drop = iterator.next();
                if (drop.is(seed)) {
                    drop.shrink(1);
                    if (drop.isEmpty()) {
                        iterator.remove();
                    }
                    break;
                }
            }
            dropListOfItems(level, pos, drops);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(blockState));
            level.setBlock(pos, cropBlock.getStateForAge(0), Block.UPDATE_ALL_IMMEDIATE);
            ToolHelper.damageItem(stack, player);
            return true;
        }

        return false;
    }

    private static void dropListOfItems(Level world, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack stack : drops) {
            float f = 0.7F;
            double offX = (GTValues.RNG.nextFloat() * f) + (1.0F - f) * 0.5D;
            double offY = (GTValues.RNG.nextFloat() * f) + (1.0F - f) * 0.5D;
            double offZ = (GTValues.RNG.nextFloat() * f) + (1.0F - f) * 0.5D;
            ItemEntity entityItem = new ItemEntity(world, pos.getX() + offX, pos.getY() + offY, pos.getZ() + offZ,
                    stack);
            entityItem.setDefaultPickUpDelay();
            world.addFreshEntity(entityItem);
        }
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.crop_harvesting"));
    }

    @Override
    public ToolBehaviorType<HarvestCropsBehavior> getType() {
        return GTToolBehaviors.HARVEST_CROPS;
    }
}
