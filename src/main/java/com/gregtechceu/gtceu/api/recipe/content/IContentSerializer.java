package com.gregtechceu.gtceu.api.recipe.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

public interface IContentSerializer<T> {

    Gson GSON = new GsonBuilder().create();

    default void toNetwork(RegistryFriendlyByteBuf buf, T content) {
        buf.writeUtf(codec().encodeStart(
                buf.registryAccess().createSerializationContext(JsonOps.INSTANCE), content).getOrThrow().toString());
    }

    default T fromNetwork(RegistryFriendlyByteBuf buf) {
        return codec().parse(buf.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                GSON.fromJson(buf.readUtf(), JsonElement.class)).getOrThrow();
    }

    T fromJson(JsonElement json);

    JsonElement toJson(T content);

    T of(Object o);

    T defaultValue();

    @SuppressWarnings("unchecked")
    default void toNetworkContent(RegistryFriendlyByteBuf buf, Content content) {
        T inner = (T) content.getContent();
        toNetwork(buf, inner);
        buf.writeVarInt(content.chance);
        buf.writeVarInt(content.maxChance);
        buf.writeVarInt(content.tierChanceBoost);
    }

    default Content fromNetworkContent(RegistryFriendlyByteBuf buf) {
        T inner = fromNetwork(buf);
        int chance = buf.readVarInt();
        int maxChance = buf.readVarInt();
        int tierChanceBoost = buf.readVarInt();
        return new Content(inner, chance, maxChance, tierChanceBoost);
    }

    Codec<T> codec();

    default T fromJson(JsonElement json, HolderLookup.Provider provider) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
        return codec().parse(ops, json).getOrThrow(false, GTCEu.LOGGER::error);
    }

    default JsonElement toJson(T content, HolderLookup.Provider provider) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
        return codec().encodeStart(ops, content).getOrThrow(false, GTCEu.LOGGER::error);
    }

    @SuppressWarnings("unchecked")
    default JsonElement toJsonContent(Content content) {
        JsonObject json = new JsonObject();
        json.add("content", toJson((T) content.getContent()));
        json.addProperty("chance", content.chance);
        json.addProperty("maxChance", content.maxChance);
        json.addProperty("tierChanceBoost", content.tierChanceBoost);
        return json;
    }

    default Content fromJsonContent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        T inner = fromJson(jsonObject.get("content"));
        int chance = jsonObject.has("chance") ? jsonObject.get("chance").getAsInt() : ChanceLogic.getMaxChancedValue();
        int maxChance = jsonObject.has("maxChance") ? jsonObject.get("maxChance").getAsInt() :
                ChanceLogic.getMaxChancedValue();
        int tierChanceBoost = jsonObject.has("tierChanceBoost") ? jsonObject.get("tierChanceBoost").getAsInt() : 0;
        return new Content(inner, chance, maxChance, tierChanceBoost);
    }

    default Tag toNbt(T content, HolderLookup.Provider provider) {
        return JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, this.toJson(content, provider));
    }

    default Tag toNbtGeneric(Object content, HolderLookup.Provider provider) {
        return toNbt((T) content, provider);
    }

    default T fromNbt(Tag tag, HolderLookup.Provider provider) {
        var json = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tag);
        return fromJson(json, provider);
    }
}
