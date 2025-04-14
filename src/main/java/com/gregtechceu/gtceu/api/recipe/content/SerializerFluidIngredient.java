package com.gregtechceu.gtceu.api.recipe.content;

import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mojang.serialization.JsonOps;
import lombok.experimental.ExtensionMethod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.fluids.FluidStack;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@ExtensionMethod(SizedIngredientExtensions.class)
public class SerializerFluidIngredient implements IContentSerializer<SizedFluidIngredient> {

    public static final SizedFluidIngredient EMPTY = new SizedFluidIngredient(FluidIngredient.empty(), 1);

    public static SerializerFluidIngredient INSTANCE = new SerializerFluidIngredient();

    private SerializerFluidIngredient() {}

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf, SizedFluidIngredient content) {
        SizedFluidIngredient.STREAM_CODEC.encode(buf, content);
    }

    @Override
    public SizedFluidIngredient fromNetwork(RegistryFriendlyByteBuf buf) {
        return SizedFluidIngredient.STREAM_CODEC.decode(buf);
    }

    @Override
    public SizedFluidIngredient fromJson(JsonElement json) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, GTRegistries.builtinRegistry());
        return SizedFluidIngredient.NESTED_CODEC.parse(ops, json).getOrThrow();
    }

    @Override
    public JsonElement toJson(SizedFluidIngredient content) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, GTRegistries.builtinRegistry());
        return SizedFluidIngredient.NESTED_CODEC.encodeStart(ops, content).getOrThrow();
    }

    @Override
    public SizedFluidIngredient of(Object o) {
        if (o instanceof SizedFluidIngredient ingredient) {
            return ingredient.copy();
        }
        if (o instanceof FluidStack stack) {
            return SizedFluidIngredient.of(stack.copy());
        }
        return EMPTY;
    }

    @Override
    public SizedFluidIngredient defaultValue() {
        return EMPTY;
    }

    @Override
    public Codec<SizedFluidIngredient> codec() {
        return SizedFluidIngredient.NESTED_CODEC;
    }
}
