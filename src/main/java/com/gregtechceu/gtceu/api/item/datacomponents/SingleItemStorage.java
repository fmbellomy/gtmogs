package com.gregtechceu.gtceu.api.item.datacomponents;

import com.google.common.base.Preconditions;
import com.gregtechceu.gtceu.utils.codec.CodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public record SingleItemStorage(ItemStack stored, long amount, long maxAmount) {

    public SingleItemStorage {
        Preconditions.checkArgument(amount <= maxAmount, "amount must be <= maxAmount!");
    }

    public SingleItemStorage(ItemStack stored, long amount) {
        this(stored, amount, Long.MAX_VALUE);
    }

    public static final Codec<ItemStack> OPTIONAL_SINGLE_ITEM_CODEC = ExtraCodecs.optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC)
            .xmap(stack -> stack.orElse(ItemStack.EMPTY),
                    stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    public static final Codec<SingleItemStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OPTIONAL_SINGLE_ITEM_CODEC.fieldOf("stored").forGetter(SingleItemStorage::stored),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(SingleItemStorage::amount),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("max_amount").forGetter(SingleItemStorage::amount)
    ).apply(instance, SingleItemStorage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SingleItemStorage> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, SingleItemStorage::stored,
            ByteBufCodecs.VAR_LONG, SingleItemStorage::amount,
            ByteBufCodecs.VAR_LONG, SingleItemStorage::maxAmount,
            SingleItemStorage::new
    );

    public static final SingleItemStorage EMPTY = new SingleItemStorage(ItemStack.EMPTY, 0);

    public SingleItemStorage withStored(ItemStack stored) {
        return new SingleItemStorage(stored, amount, maxAmount);
    }

    public SingleItemStorage withAmount(long amount) {
        return new SingleItemStorage(stored, amount, maxAmount);
    }
    
    public SingleItemStorage withMaxAmount(long maxAmount) {
        return new SingleItemStorage(stored, amount, maxAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SingleItemStorage(ItemStack otherStored, long otherAmount, long otherMax))) return false;

        return amount() == otherAmount && maxAmount() == otherMax &&
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
