package com.quantumgarbage.gtmogs.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.common.capability.WorldIDSaveData;
import com.quantumgarbage.gtmogs.integration.map.ClientCacheManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
public class SPacketSendWorldID implements CustomPacketPayload {

    public static final ResourceLocation ID = GTMOGS.id("send_world_id");
    public static final Type<SPacketSendWorldID> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SPacketSendWorldID> CODEC = StreamCodec
            .ofMember(SPacketSendWorldID::encode, SPacketSendWorldID::new);

    private String worldId;

    public SPacketSendWorldID(FriendlyByteBuf buf) {
        worldId = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(WorldIDSaveData.getWorldID());
    }

    public void execute(IPayloadContext context) {
        ClientCacheManager.init(worldId);
    }

    @Override
    public @NotNull Type<SPacketSendWorldID> type() {
        return TYPE;
    }
}
