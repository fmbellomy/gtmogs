package com.gregtechceu.gtceu.api.item.datacomponents;

import com.google.common.base.Preconditions;
import com.gregtechceu.gtceu.utils.codec.CodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public record LargeFluidContent(FluidStack stored, long amount, long maxAmount) {

    public LargeFluidContent {
        Preconditions.checkArgument(amount <= maxAmount, "amount must be <= maxAmount!");
    }

    public LargeFluidContent(FluidStack stored, long amount) {
        this(stored, amount, Long.MAX_VALUE);
    }

    public static final Codec<FluidStack> OPTIONAL_SINGLE_FLUID_CODEC = ExtraCodecs.optionalEmptyMap(FluidStack.fixedAmountCodec(1))
            .xmap(stack -> stack.orElse(FluidStack.EMPTY),
                    stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    public static final Codec<LargeFluidContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OPTIONAL_SINGLE_FLUID_CODEC.fieldOf("stored").forGetter(LargeFluidContent::stored),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(LargeFluidContent::amount),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("max_amount").forGetter(LargeFluidContent::maxAmount)
    ).apply(instance, LargeFluidContent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LargeFluidContent> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC, LargeFluidContent::stored,
            ByteBufCodecs.VAR_LONG, LargeFluidContent::amount,
            ByteBufCodecs.VAR_LONG, LargeFluidContent::maxAmount,
            LargeFluidContent::new
    );

    public static final LargeFluidContent EMPTY = new LargeFluidContent(FluidStack.EMPTY, 0);

    public LargeFluidContent withStored(FluidStack stored) {
        return new LargeFluidContent(stored, amount);
    }

    public LargeFluidContent withAmount(long amount) {
        return new LargeFluidContent(stored, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LargeFluidContent(FluidStack otherStored, long otherAmount, long otherMax))) return false;

        return amount() == otherAmount && maxAmount() == otherMax &&
                FluidStack.isSameFluidSameComponents(stored, otherStored);
    }

    @Override
    public int hashCode() {
        int result = FluidStack.hashFluidAndComponents(stored());
        result = 31 * result + Long.hashCode(amount());
        result = 31 * result + Long.hashCode(maxAmount());
        return result;
    }

}
