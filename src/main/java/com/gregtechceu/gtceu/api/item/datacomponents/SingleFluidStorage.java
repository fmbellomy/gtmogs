package com.gregtechceu.gtceu.api.item.datacomponents;

import com.gregtechceu.gtceu.utils.codec.CodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public record SingleFluidStorage(FluidStack stored, long amount) {


    public static final Codec<FluidStack> OPTIONAL_SINGLE_FLUID_CODEC = ExtraCodecs.optionalEmptyMap(FluidStack.fixedAmountCodec(1))
            .xmap(stack -> stack.orElse(FluidStack.EMPTY),
                    stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    public static final Codec<SingleFluidStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OPTIONAL_SINGLE_FLUID_CODEC.fieldOf("stored").forGetter(SingleFluidStorage::stored),
            CodecUtils.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(SingleFluidStorage::amount)
    ).apply(instance, SingleFluidStorage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SingleFluidStorage> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC, SingleFluidStorage::stored,
            ByteBufCodecs.VAR_LONG, SingleFluidStorage::amount,
            SingleFluidStorage::new
    );

    public static final SingleFluidStorage EMPTY = new SingleFluidStorage(FluidStack.EMPTY, 0);

    public SingleFluidStorage withStored(FluidStack stored) {
        return new SingleFluidStorage(stored, amount);
    }

    public SingleFluidStorage withAmount(long amount) {
        return new SingleFluidStorage(stored, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SingleFluidStorage(FluidStack otherStored, long otherAmount))) return false;

        return amount() == otherAmount && FluidStack.isSameFluidSameComponents(stored, otherStored);
    }

    @Override
    public int hashCode() {
        int result = FluidStack.hashFluidAndComponents(stored());
        result = 31 * result + Long.hashCode(amount());
        return result;
    }

}
