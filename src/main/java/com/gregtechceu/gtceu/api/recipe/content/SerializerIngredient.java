package com.gregtechceu.gtceu.api.recipe.content;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class SerializerIngredient implements IContentSerializer<SizedIngredient> {

    public static final SizedIngredient EMPTY = new SizedIngredient(Ingredient.EMPTY, 1);
    public static SerializerIngredient INSTANCE = new SerializerIngredient();

    private SerializerIngredient() {}

    @Override
    public void toNetwork(FriendlyByteBuf buf, SizedIngredient content) {
        content.toNetwork(buf);
    }

    @Override
    public SizedIngredient fromNetwork(FriendlyByteBuf buf) {
        return Ingredient.fromNetwork(buf);
    }

    @Override
    public SizedIngredient fromJson(JsonElement json) {
        return Ingredient.fromJson(json);
    }

    @Override
    public JsonElement toJson(SizedIngredient content) {
        return content.toJson();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SizedIngredient of(Object o) {
        if (o instanceof SizedIngredient ingredient) {
            return ingredient;
        } else if (o instanceof ItemStack itemStack) {
            return new SizedIngredient(Ingredient.of(itemStack), itemStack.getCount());
        } else if (o instanceof ItemLike itemLike) {
            return SizedIngredient.of(itemLike, 1);
        } else if (o instanceof TagKey tag) {
            return SizedIngredient.of(tag, 1);
        }
        return EMPTY;
    }

    @Override
    public SizedIngredient defaultValue() {
        return EMPTY;
    }

    @Override
    public Codec<SizedIngredient> codec() {
        return SizedIngredient.NESTED_CODEC;
    }
}
