package com.gregtechceu.gtceu.syncdata;

import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;

import com.lowdragmc.lowdraglib.syncdata.payload.ObjectTypedPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import org.jetbrains.annotations.Nullable;

/**
 * @author KilaBash
 * @date 2023/2/18
 * @implNote GTRecipePayload
 */
public class GTRecipePayload extends ObjectTypedPayload<GTRecipe> {

    private static RecipeManager getRecipeManager() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && Thread.currentThread() == server.getRunningThread()) {
            return server.getRecipeManager();
        } else {
            return Client.getRecipeManager();
        }
    }

    @Nullable
    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", payload.id.toString());
        tag.put("recipe",
                GTRecipeSerializer.CODEC.codec().encodeStart(NbtOps.INSTANCE, payload).result().orElse(new CompoundTag()));
        tag.putInt("parallels", payload.parallels);
        tag.putInt("ocLevel", payload.ocLevel);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag tag, HolderLookup.Provider provider) {
        if (tag instanceof CompoundTag compoundTag) {
            payload = GTRecipeSerializer.CODEC.codec().parse(NbtOps.INSTANCE, compoundTag.get("recipe")).result().orElse(null);
            if (payload != null) {
                payload.id = ResourceLocation.parse(compoundTag.getString("id"));
                payload.parallels = compoundTag.contains("parallels") ? compoundTag.getInt("parallels") : 1;
                payload.ocLevel = compoundTag.getInt("ocLevel");
            }
        }
    }

    @Override
    public void writePayload(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(this.payload.id);
        GTRecipeSerializer.toNetwork(buf, this.payload);
        buf.writeInt(this.payload.parallels);
        buf.writeInt(this.payload.ocLevel);
    }

    @Override
    public void readPayload(RegistryFriendlyByteBuf buf) {
        this.payload = GTRecipeSerializer.fromNetwork(buf);
        this.payload.parallels = buf.readInt();
        this.payload.ocLevel = buf.readInt();
    }

    static class Client {

        static RecipeManager getRecipeManager() {
            return Minecraft.getInstance().getConnection().getRecipeManager();
        }
    }
}
