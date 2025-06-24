package com.gregtechceu.gtceu.common.network.packets;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.cosmetics.CapeRegistry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
public class SPacketNotifyCapeChange implements CustomPacketPayload {

    public static final ResourceLocation ID = GTCEu.id("cape_changed");
    public static final Type<SPacketNotifyCapeChange> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SPacketNotifyCapeChange> CODEC = StreamCodec
            .ofMember(SPacketNotifyCapeChange::encode, SPacketNotifyCapeChange::decode);

    private final UUID uuid;
    private final ResourceLocation cape;

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeBoolean(this.cape != null);
        if (this.cape != null) {
            buf.writeResourceLocation(this.cape);
        }
    }

    public static SPacketNotifyCapeChange decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        ResourceLocation cape = buf.readBoolean() ? buf.readResourceLocation() : null;
        return new SPacketNotifyCapeChange(uuid, cape);
    }

    public void execute(IPayloadContext handler) {
        if (handler.flow().isClientbound()) {
            CapeRegistry.giveRawCape(uuid, cape);
        }
    }

    @Override
    public @NotNull Type<SPacketNotifyCapeChange> type() {
        return TYPE;
    }
}
