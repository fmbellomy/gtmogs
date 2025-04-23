package com.gregtechceu.gtceu.api.item.datacomponents;

import com.gregtechceu.gtceu.utils.codec.CodecUtils;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record LargeItemContent(ItemStack stored, long amount, long maxAmount) {

    public LargeItemContent {
        Preconditions.checkArgument(amount <= maxAmount, "amount must be <= maxAmount!");
    }

    public LargeItemContent(ItemStack stored, long amount) {
        this(stored, amount, Long.MAX_VALUE);
    }

    public static final Codec<ItemStack> OPTIONAL_SINGLE_ITEM_CODEC = ExtraCodecs
            .optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC)
            .xmap(stack -> stack.orElse(ItemStack.EMPTY),
                    stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    // spotless:off
    public static final Codec<LargeItemContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OPTIONAL_SINGLE_ITEM_CODEC.fieldOf("stored").forGetter(LargeItemContent::stored),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(LargeItemContent::amount),
            CodecUtils.NON_NEGATIVE_LONG.orElse(Long.MAX_VALUE).fieldOf("max_amount").forGetter(LargeItemContent::amount)
    ).apply(instance, LargeItemContent::new));
    // spotless:on
    public static final StreamCodec<RegistryFriendlyByteBuf, LargeItemContent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, LargeItemContent::stored,
            ByteBufCodecs.VAR_LONG, LargeItemContent::amount,
            ByteBufCodecs.VAR_LONG, LargeItemContent::maxAmount,
            LargeItemContent::new);

    public static final LargeItemContent EMPTY = new LargeItemContent(ItemStack.EMPTY, 0);

    public LargeItemContent withStored(ItemStack stored) {
        return new LargeItemContent(stored, amount, maxAmount);
    }

    public LargeItemContent withAmount(long amount) {
        return new LargeItemContent(stored, amount, maxAmount);
    }

    public LargeItemContent withMaxAmount(long maxAmount) {
        return new LargeItemContent(stored, amount, maxAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LargeItemContent(ItemStack otherStored, long otherAmount, long otherMaxAmount)))
            return false;

        return amount() == otherAmount && maxAmount() == otherMaxAmount &&
                ItemStack.isSameItemSameComponents(stored, otherStored);
    }

    @Override
    public int hashCode() {
        int result = ItemStack.hashItemAndComponents(stored());
        result = 31 * result + Long.hashCode(amount());
        result = 31 * result + Long.hashCode(maxAmount());
        return result;
    }
}
